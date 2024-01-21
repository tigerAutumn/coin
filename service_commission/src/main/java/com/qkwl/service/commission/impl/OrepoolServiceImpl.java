package com.qkwl.service.commission.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.activity.InnovationAreaActivity;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.orepool.OrepoolIncomeRecord;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.orepool.IOrepoolService;
import com.qkwl.common.rpc.orepool.IOrepoolWalletService;
import com.qkwl.service.commission.dao.OrepoolPlanMapper;
import com.qkwl.service.commission.dao.OrepoolRecordMapper;
import com.qkwl.service.commission.dao.UserCoinWalletMapper;
import com.qkwl.service.commission.dao.UserWalletBalanceLogMapper;
import com.qkwl.service.commission.job.OrepoolJob;

@Service("orepoolService")
public class OrepoolServiceImpl implements IOrepoolService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(OrepoolServiceImpl.class);
	
	@Autowired
	private OrepoolPlanMapper orepoolPlanMapper;
	@Autowired
	private OrepoolRecordMapper orepoolRecordMapper;
	@Autowired
	private IOrepoolWalletService orepoolWalletService;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	
	@Override
	public PageInfo<OrepoolPlan> getFixedPlanList(Integer currentPage,Integer numPerPage) 
	{
		//条件查询订单，带分页
		PageHelper.startPage(currentPage, numPerPage);
		List<OrepoolPlan> list = orepoolPlanMapper.getFixedPlanList();
		PageInfo<OrepoolPlan> pageInfo = new PageInfo<OrepoolPlan>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<OrepoolPlan> getCurrentPlanList(Integer currentPage,Integer numPerPage) 
	{
		//条件查询订单，带分页
		PageHelper.startPage(currentPage, numPerPage);
		List<OrepoolPlan> list = orepoolPlanMapper.getCurrentPlanList();
		PageInfo<OrepoolPlan> pageInfo = new PageInfo<OrepoolPlan>(list);
		return pageInfo;
	}
	
	@Override
	public Integer getPersonCount(Integer planId) {
		return orepoolRecordMapper.getPersonCount(planId);
	}
	
	@Override
	public OrepoolPlan getPlanById(Integer id) {
		return orepoolPlanMapper.getPlanById(id);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result lockPlan(Integer userId, Integer id, BigDecimal lockVolume) throws BCException {
		try {
			//查询矿池计划
			OrepoolPlan plan = orepoolPlanMapper.getPlanById(id);
			//校验
			if (plan == null) {
				return Result.failure("OrepoolServiceImpl.1");
			}
			if (plan.getStatus() == OrepoolPlanStatusEnum.forbid.getCode()) {
				return Result.failure("OrepoolServiceImpl.2");
			}
			//查询币种数量精度
			Integer countDigit = getCountDigit(plan.getCoinName());
			lockVolume = MathUtils.toScaleNum(lockVolume, countDigit);
			
			if (MathUtils.compareTo(lockVolume, plan.getMinAmount()) < 0) {
				return Result.failure("OrepoolServiceImpl.3");
			}
			//定期判断锁仓最大数量
			if (plan.getType()==OrepoolPlanTypeEnum.fixed_plan.getCode() 
					&& MathUtils.compareTo(lockVolume, plan.getMaxAmount())>0) {
				return Result.failure("OrepoolServiceImpl.4");
			}
			Date now = new Date();
			if (plan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
				if (now.before(plan.getStartTime())) {
					return Result.failure("OrepoolServiceImpl.5");
				}
				if (now.after(plan.getEndTime())) {
					return Result.failure("OrepoolServiceImpl.6");
				}
				if (MathUtils.compareTo(lockVolume, plan.getVisibleVolume()) > 0) {
					return Result.failure("OrepoolServiceImpl.7");
				}
			}
			//冻结钱包
			Result frozenResult = orepoolWalletService.orepoolFrozen(userId, plan.getLockCoinId(), lockVolume);
			if (!frozenResult.getSuccess()) {
				return frozenResult;
			}
			//创建更新记录
			if (plan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
				Map<String, Object> param = new HashMap<>();
				param.put("planId", plan.getId());
				param.put("userId", userId);
				OrepoolRecord record = orepoolRecordMapper.getRecord(param);
				if (record != null) {
					BigDecimal volume = MathUtils.add(record.getLockVolume(), lockVolume);
					if (MathUtils.compareTo(volume, plan.getMaxAmount())>0) {
						throw new BCException("OrepoolServiceImpl.4");
					}
					//更新记录
					record.setLockVolume(volume);
					record.setUpdateTime(now);
					if (orepoolRecordMapper.update(record) <= 0) {
						throw new BCException("OrepoolServiceImpl.8");
					}
					//计划更新
					plan.setVisibleVolume(MathUtils.sub(plan.getVisibleVolume(), lockVolume));
					plan.setLockVolume(MathUtils.add(plan.getLockVolume(), lockVolume));
					plan.setUpdateTime(now);
					orepoolPlanMapper.update(plan);
					//记录账户流水
					UserWalletBalanceLog log = new UserWalletBalanceLog();
					log.setUid(userId);
					log.setCoinId(plan.getLockCoinId());
					log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
					log.setChange(lockVolume);
					log.setSrcId(record.getId());
					log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
					log.setDirection(UserWalletBalanceLogDirectionEnum.out.getValue());
					log.setCreatetime(now);
					log.setCreatedatestamp(now.getTime());
					userWalletBalanceLogMapper.insert(log);
					return Result.success();
				}
			}
			OrepoolRecord orepoolRecord = new OrepoolRecord();
			orepoolRecord.setPlanId(plan.getId());
			orepoolRecord.setUserId(userId);
			orepoolRecord.setLockCoinId(plan.getLockCoinId());
			orepoolRecord.setIncomeCoinId(plan.getIncomeCoinId());
			orepoolRecord.setLockVolume(lockVolume);
			orepoolRecord.setStatus(OrepoolRecordStatusEnum.lock.getCode());
			orepoolRecord.setCreateTime(now);
			orepoolRecord.setUpdateTime(now);
			int recordId = orepoolRecordMapper.insert(orepoolRecord);
			if (recordId <= 0) {
				throw new BCException("OrepoolServiceImpl.9");
			}
			//计划更新
			if (plan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
				plan.setVisibleVolume(MathUtils.sub(plan.getVisibleVolume(), lockVolume));
			}
			plan.setLockVolume(MathUtils.add(plan.getLockVolume(), lockVolume));
			plan.setUpdateTime(now);
			orepoolPlanMapper.update(plan);
			//记录账户流水
			UserWalletBalanceLog log = new UserWalletBalanceLog();
			log.setUid(userId);
			log.setCoinId(plan.getLockCoinId());
			log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
			log.setChange(lockVolume);
			log.setSrcId(recordId);
			if (plan.getType() == OrepoolPlanTypeEnum.innovation_plan.getCode()) {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Innovation_lock.getCode());
			} else {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_lock.getCode());
			}
			log.setDirection(UserWalletBalanceLogDirectionEnum.out.getValue());
			log.setCreatetime(now);
			log.setCreatedatestamp(now.getTime());
			userWalletBalanceLogMapper.insert(log);
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("OrepoolServiceImpl.10");
		}
	}
	
	@Override
	@Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result unlockPlan(Integer userId, Integer id) throws BCException {
		try {
			//查询矿池计划
			OrepoolRecord record = orepoolRecordMapper.getRecordById(id);
			if (record == null) {
				return Result.failure("OrepoolServiceImpl.11");
			}
			if (record.getStatus() == OrepoolRecordStatusEnum.unlock.getCode()) {
				return Result.failure("OrepoolServiceImpl.12");
			}
			//查询矿池计划
			OrepoolPlan plan = orepoolPlanMapper.getPlanById(record.getPlanId());
			//校验
			if (plan == null) {
				return Result.failure("OrepoolServiceImpl.1");
			}
			if (plan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
				return Result.failure("OrepoolServiceImpl.13");
			}
			//解冻钱包
			Result unfrozenResult = orepoolWalletService.orepoolUnFrozen(userId, plan.getLockCoinId(), record.getLockVolume());
			if (!unfrozenResult.getSuccess()) {
				return unfrozenResult;
			}
			//记录更新为已解锁
			record.setStatus(OrepoolRecordStatusEnum.unlock.getCode());
			Date now = new Date();
			record.setUpdateTime(now);
			if (orepoolRecordMapper.update(record) <= 0) {
				throw new BCException("OrepoolServiceImpl.8");
			}
			//记录账户流水
			UserWalletBalanceLog log = new UserWalletBalanceLog();
			log.setUid(userId);
			log.setCoinId(record.getLockCoinId());
			log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
			log.setChange(record.getLockVolume());
			log.setSrcId(record.getId());
			if (plan.getType() == OrepoolPlanTypeEnum.innovation_plan.getCode()) {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
			} else {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
			}
			log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
			log.setCreatetime(now);
			log.setCreatedatestamp(now.getTime());
			userWalletBalanceLogMapper.insert(log);
			return Result.success();
		} catch (BCException e) {
			throw e;
		} catch (Exception e) {
			throw new BCException("OrepoolServiceImpl.14");
		}
	}
	
	@Override
	public PageInfo<OrepoolRecord> getRecordList(Integer currentPage,Integer numPerPage,Map<String, Object> param) 
	{
		//条件查询订单，带分页
		PageHelper.startPage(currentPage, numPerPage);
		List<OrepoolRecord> list = orepoolRecordMapper.getRecordList(param);
		PageInfo<OrepoolRecord> pageInfo = new PageInfo<OrepoolRecord>(list);
		return pageInfo;
	}
	
	@Override
	public BigDecimal getAccountBalance(Integer uid, Integer coinId) {
		return orepoolWalletService.getAccountBalance(uid, coinId);
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void changeFixedPlan(){
		//查询所有定期计划
		List<OrepoolPlan> planList = orepoolPlanMapper.getFixedPlan();
		for (OrepoolPlan plan : planList) {
			if (plan.getStatus() == OrepoolPlanStatusEnum.not_start.getCode()) {
				if (plan.getStartTime().before(new Date())) {
					plan.setStatus(OrepoolPlanStatusEnum.start.getCode());
					plan.setUpdateTime(new Date());
					orepoolPlanMapper.update(plan);
				}
			} else if (plan.getStatus() == OrepoolPlanStatusEnum.start.getCode()) {
				if (plan.getEndTime().before(new Date()) || MathUtils.compareTo(plan.getVisibleVolume(), BigDecimal.ZERO)==0) {
					plan.setStatus(OrepoolPlanStatusEnum.end.getCode());
					plan.setUpdateTime(new Date());
					orepoolPlanMapper.update(plan);
				}
			} else if (plan.getStatus() == OrepoolPlanStatusEnum.end.getCode()) {
				Date endTime = plan.getEndTime();
				Calendar cal = Calendar.getInstance();
				cal.setTime(endTime);
				cal.add(Calendar.DAY_OF_YEAR, plan.getLockPeriod());
				if (cal.getTime().before(new Date())) {
					plan.setStatus(OrepoolPlanStatusEnum.unlock.getCode());
					plan.setUpdateTime(new Date());
					List<OrepoolRecord> recordList = orepoolRecordMapper.getRecordByPlanId(plan.getId());
					for (OrepoolRecord record : recordList) {
						Integer countDigit = getCountDigit(record.getCoinName());
						//解冻钱包
						BigDecimal dayRate = MathUtils.div(plan.getIncomeRate(), new BigDecimal("365"));
						BigDecimal dayIncome = MathUtils.mul(dayRate, record.getLockVolume());
						BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(dayIncome, new BigDecimal(plan.getIncomePeriod()+"")), countDigit);
						orepoolWalletService.orepoolUnFrozen(record.getUserId(), plan.getLockCoinId(), record.getLockVolume());
						//解锁入账户流水
						Date date = new Date();     
						UserWalletBalanceLog log = new UserWalletBalanceLog();
						log.setUid(record.getUserId());
						log.setCoinId(record.getLockCoinId());
						log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
						log.setChange(record.getLockVolume());
						log.setSrcId(record.getId());
						log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
						log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
						log.setCreatetime(date);
						log.setCreatedatestamp(date.getTime());
						userWalletBalanceLogMapper.insert(log);
						//发放利息
						OrepoolIncomeRecord income = new OrepoolIncomeRecord();
						income.setPlanId(record.getPlanId());
						income.setUserId(record.getUserId());
						income.setLockCoinId(record.getLockCoinId());
						income.setIncomeCoinId(record.getIncomeCoinId());
						income.setLockVolume(record.getLockVolume());
						income.setIncome(amount);
						orepoolWalletService.orepoolncome(income);
						//更新记录为已解锁
						record.setStatus(OrepoolRecordStatusEnum.unlock.getCode());
						record.setUpdateTime(new Date());
						orepoolRecordMapper.update(record);
						//收益账户流水
						UserWalletBalanceLog incomeLog = new UserWalletBalanceLog();
						incomeLog.setUid(record.getUserId());
						incomeLog.setCoinId(record.getLockCoinId());
						incomeLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
						incomeLog.setChange(amount);
						incomeLog.setSrcId(record.getId());
						incomeLog.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
						incomeLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
						incomeLog.setCreatetime(date);
						incomeLog.setCreatedatestamp(date.getTime());
						userWalletBalanceLogMapper.insert(incomeLog);
					}
					orepoolPlanMapper.update(plan);
				}
			}
		}
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void payCurrentPlan(){
		//查询所有活期记录
		List<OrepoolRecord> recordList = orepoolRecordMapper.getCurrentRecord();
		Calendar cal = Calendar.getInstance();
		for (OrepoolRecord record : recordList) {
			//计息开始时间
			Date createTime = record.getCreateTime();
			cal.setTime(createTime);
			cal.add(Calendar.DAY_OF_YEAR, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date startCountTime = cal.getTime();
			//当天零点
			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String format = simpleDateFormat.format(new Date());
			try {
				cal.setTime(simpleDateFormat.parse(format));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date today = cal.getTime();
			
			Integer countDigit = getCountDigit(record.getCoinName());
			if (record.getStatus() == OrepoolRecordStatusEnum.lock.getCode()) {
				if (today.getTime() == startCountTime.getTime()) {
					//更新记录状态为计息中
					record.setStatus(OrepoolRecordStatusEnum.counting.getCode());
					record.setUpdateTime(new Date());
					orepoolRecordMapper.update(record);
				}
			} else if (record.getStatus() == OrepoolRecordStatusEnum.counting.getCode()) {
				//发放利息
				OrepoolPlan plan = orepoolPlanMapper.getPlanById(record.getPlanId());
				BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(plan.getIncomeRate(), record.getLockVolume()), countDigit);
				OrepoolIncomeRecord income = new OrepoolIncomeRecord();
				income.setPlanId(record.getPlanId());
				income.setUserId(record.getUserId());
				income.setLockCoinId(record.getLockCoinId());
				income.setIncomeCoinId(record.getIncomeCoinId());
				income.setLockVolume(record.getLockVolume());
				income.setIncome(amount);
				orepoolWalletService.orepoolncome(income);
				//收益入账户流水
				UserWalletBalanceLog log = new UserWalletBalanceLog();
				log.setUid(record.getUserId());
				log.setCoinId(record.getLockCoinId());
				log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
				log.setChange(amount);
				log.setSrcId(record.getId());
				log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
				log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
				Date date = new Date();
				log.setCreatetime(date);
				log.setCreatedatestamp(date.getTime());
				userWalletBalanceLogMapper.insert(log);
			} else if (record.getStatus() == OrepoolRecordStatusEnum.unlock.getCode()) {
				//如果在10点前解锁且创建时间在昨天以前
				Date updateTime = record.getUpdateTime();
				if (updateTime.getTime() > today.getTime() && updateTime.getTime() < (today.getTime() + 10*60*60*1000)
						&& startCountTime.getTime() < today.getTime()) {
					//发放利息
					OrepoolPlan plan = orepoolPlanMapper.getPlanById(record.getPlanId());
					BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(plan.getIncomeRate(), record.getLockVolume()), countDigit);
					OrepoolIncomeRecord income = new OrepoolIncomeRecord();
					income.setPlanId(record.getPlanId());
					income.setUserId(record.getUserId());
					income.setLockCoinId(record.getLockCoinId());
					income.setIncomeCoinId(record.getIncomeCoinId());
					income.setLockVolume(record.getLockVolume());
					income.setIncome(amount);
					income.setCreateTime(new Date());
					orepoolWalletService.orepoolncome(income);
					//收益入账户流水
					UserWalletBalanceLog log = new UserWalletBalanceLog();
					log.setUid(record.getUserId());
					log.setCoinId(record.getLockCoinId());
					log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
					log.setChange(amount);
					log.setSrcId(record.getId());
					log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_income.getCode());
					log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
					Date date = new Date();
					log.setCreatetime(date);
					log.setCreatedatestamp(date.getTime());
					userWalletBalanceLogMapper.insert(log);
				}
			}
		}
	}
	
	/**
	 * 定时发放创新区锁仓利息
	 */
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public void payInnovationPlan(){
		//查询所有活期记录
		List<OrepoolRecord> recordList = orepoolRecordMapper.getInnovationRecord();
		Calendar cal = Calendar.getInstance();
		for (OrepoolRecord record : recordList) {
			//计息开始时间
			Date createTime = record.getCreateTime();
			cal.setTime(createTime);
			cal.add(Calendar.DAY_OF_YEAR, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date startCountTime = cal.getTime();
			//当天零点
			SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String format = simpleDateFormat.format(new Date());
			try {
				cal.setTime(simpleDateFormat.parse(format));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date today = cal.getTime();
			
			Integer countDigit = getCountDigit(record.getCoinName());
			if (record.getStatus() == OrepoolRecordStatusEnum.lock.getCode()) {
				if (today.getTime() == startCountTime.getTime()) {
					//更新记录状态为计息中
					record.setStatus(OrepoolRecordStatusEnum.counting.getCode());
					record.setUpdateTime(new Date());
					orepoolRecordMapper.update(record);
				}
			} else if (record.getStatus() == OrepoolRecordStatusEnum.counting.getCode()) {
				//发放利息
				OrepoolPlan plan = orepoolPlanMapper.getPlanById(record.getPlanId());
				BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(plan.getIncomeRate(), record.getLockVolume()), countDigit);
				BigDecimal updateTotal = BigDecimal.ZERO;
				//查询钱包
        		UserCoinWallet userCoinWallet = userCoinWalletMapper.select(record.getUserId(), record.getLockCoinId());
        		if(MathUtils.sub(userCoinWallet.getDepositFrozen(), amount).compareTo(BigDecimal.ZERO)<=0) {
    				updateTotal = userCoinWallet.getDepositFrozen();
    				//更新记录状态为计息中
					record.setStatus(OrepoolRecordStatusEnum.complete.getCode());
					record.setUpdateTime(new Date());
					orepoolRecordMapper.update(record);
    			}else {
    				updateTotal = amount;
    			}
        		userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(), updateTotal));
    			userCoinWallet.setDepositFrozen(MathUtils.sub(userCoinWallet.getDepositFrozen(), updateTotal));
    			userCoinWalletMapper.update(userCoinWallet);
				
				//收益入账户流水
				UserWalletBalanceLog log = new UserWalletBalanceLog();
				log.setUid(record.getUserId());
				log.setCoinId(record.getLockCoinId());
				log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
				log.setChange(updateTotal);
				log.setSrcId(record.getId());
				log.setSrcType(UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
				log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
				Date date = new Date();
				log.setCreatetime(date);
				log.setCreatedatestamp(date.getTime());
				userWalletBalanceLogMapper.insert(log);
			} else if (record.getStatus() == OrepoolRecordStatusEnum.unlock.getCode()) {
				//如果在今天解锁且创建时间在昨天以前
				Date updateTime = record.getUpdateTime();
				if (updateTime.getTime() > today.getTime() && startCountTime.getTime() < today.getTime()) {
					//创新区释放
					OrepoolPlan plan = orepoolPlanMapper.getPlanById(record.getPlanId());
					BigDecimal amount = MathUtils.toScaleNum(MathUtils.mul(plan.getIncomeRate(), record.getLockVolume()), countDigit);
					BigDecimal updateTotal = BigDecimal.ZERO;
					//查询钱包
            		UserCoinWallet userCoinWallet = userCoinWalletMapper.select(record.getUserId(), record.getLockCoinId());
            		if(MathUtils.sub(userCoinWallet.getDepositFrozen(), amount).compareTo(BigDecimal.ZERO)<=0) {
        				updateTotal = userCoinWallet.getDepositFrozen();
        			}else {
        				updateTotal = amount;
        			}
            		userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(), updateTotal));
        			userCoinWallet.setDepositFrozen(MathUtils.sub(userCoinWallet.getDepositFrozen(), updateTotal));
        			userCoinWalletMapper.update(userCoinWallet);
        			
					//收益入账户流水
					UserWalletBalanceLog log = new UserWalletBalanceLog();
					log.setUid(record.getUserId());
					log.setCoinId(record.getLockCoinId());
					log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
					log.setChange(updateTotal);
					log.setSrcId(record.getId());
					log.setSrcType(UserWalletBalanceLogTypeEnum.Innovation_unfrozen.getCode());
					log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
					Date date = new Date();
					log.setCreatetime(date);
					log.setCreatedatestamp(date.getTime());
					userWalletBalanceLogMapper.insert(log);
				}
			}
		}
	}
	
	private Integer getCountDigit(String coinName) {
		String symbol = coinName+"_GAVC";
	    String[] symbols = symbol.split("_");
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
        SystemTradeType systemTradeType = new SystemTradeType();
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
              systemTradeType = tradeType;
            }
        }
        String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
		String[] digits = digit.split("#");
        int countDigit = Integer.valueOf(digits[1]);
		return countDigit;
	}

	@Override
	public BigDecimal getYesterdayProfit(Map<String, Object> param) {
		return userWalletBalanceLogMapper.getYesterdayProfit(param);
	}

	@Override
	public BigDecimal getTotalProfit(Map<String, Object> param) {
		return userWalletBalanceLogMapper.getTotalProfit(param);
	}
	
}
