package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.admin.IAdminOrepoolService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.OrepoolPlanMapper;
import com.qkwl.service.admin.bc.dao.OrepoolRecordMapper;
import com.qkwl.service.admin.bc.dao.UserWalletBalanceLogMapper;

@Service("adminOrepoolService")
public class AdminOrepoolServiceImpl implements IAdminOrepoolService {

	private static final Logger logger = LoggerFactory.getLogger(AdminOrepoolServiceImpl.class);
	
	@Autowired
	private OrepoolPlanMapper orepoolPlanMapper;
	@Autowired
	private OrepoolRecordMapper orepoolRecordMapper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
	private AdminOrepoolServiceImplTX adminOrepoolServiceImplTX;
	
	@Override
	public Pagination<OrepoolPlan> selectPlanPageList(Pagination<OrepoolPlan> pageParam, OrepoolPlan orepoolPlan) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总计划数
		int count = orepoolPlanMapper.countPlanListByParam(map);
		if(count > 0) {
			// 查询计划列表
			List<OrepoolPlan> planList = orepoolPlanMapper.getPlanPageList(map);
			// 设置返回数据
			pageParam.setData(planList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public Pagination<OrepoolRecord> selectRecordPageList(Pagination<OrepoolRecord> pageParam,
			OrepoolRecord orepoolRecord) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		map.put("userId", orepoolRecord.getUserId());
		map.put("lockCoinId", orepoolRecord.getLockCoinId());
		// 查询总记录数
		int count = orepoolRecordMapper.countRecordListByParam(map);
		if(count > 0) {
			// 查询记录列表
			List<OrepoolRecord> recordList = orepoolRecordMapper.getRecordPageList(map);
			for (OrepoolRecord element : recordList) {
				if (element.getStatus() != OrepoolRecordStatusEnum.unlock.getCode()) {
					element.setProfit(BigDecimal.ZERO);
				} else {
					if (element.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
						BigDecimal dayRate = MathUtils.div(element.getIncomeRate(), new BigDecimal("365"));
						BigDecimal dayIncome = MathUtils.mul(dayRate, element.getLockVolume());
						element.setProfit(MathUtils.mul(dayIncome, new BigDecimal(element.getIncomePeriod()+"")));
					} else {
						element.setProfit(MathUtils.mul(element.getIncomeRate(), element.getLockVolume()));
					}
				}
			}
			// 设置返回数据
			pageParam.setData(recordList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public OrepoolPlan selectPlanById(Integer id) {
		return orepoolPlanMapper.selectByPrimaryKey(id);
	}

	@Override
	public ReturnResult deletePlan(OrepoolPlan orepoolPlan) {
		try {
			//查询是否存在锁定的记录
			int countLockRecord = orepoolRecordMapper.countLockRecord(orepoolPlan.getId());
			if (countLockRecord > 0) {
				return ReturnResult.SUCCESS("请先解锁！");
			}
			//删除法币
			orepoolPlanMapper.deleteByPrimaryKey(orepoolPlan.getId());
			return ReturnResult.SUCCESS("删除计划成功！");
		} catch (Exception e) {
			logger.error("删除法币异常，id："+orepoolPlan.getId(),e);
			return ReturnResult.FAILUER("删除计划失败！");
		}
	}
	
	@Override
	public ReturnResult unlockPlan(OrepoolPlan orepoolPlan) throws Exception {
		//查询计划对应的所有记录
		List<OrepoolRecord> recordList = orepoolRecordMapper.selectLockRecord(orepoolPlan.getId());
		for (OrepoolRecord record : recordList) {
			//解冻钱包
			Result result = orepoolUnFrozen(record.getUserId(), orepoolPlan.getLockCoinId(), record.getLockVolume());
			if (result.getCode() != 200) {
				return ReturnResult.FAILUER("解冻钱包失败，记录id:"+record.getId()+"；失败原因："+result.getMsg());
			}
			//解锁入账户流水
			Date date = new Date();     
			UserWalletBalanceLog log = new UserWalletBalanceLog();
			log.setUid(record.getUserId());
			log.setCoinId(record.getLockCoinId());
			log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
			log.setChange(record.getLockVolume());
			log.setSrcId(record.getId());
			if (orepoolPlan.getType() == OrepoolPlanTypeEnum.current_plan.getCode()) {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Orepool_unlock.getCode());
			} else {
				log.setSrcType(UserWalletBalanceLogTypeEnum.Innovation_unlock.getCode());
			}
			log.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
			log.setCreatetime(date);
			log.setCreatedatestamp(date.getTime());
			userWalletBalanceLogMapper.insert(log);
			//更新记录为已解锁
			record.setStatus(OrepoolRecordStatusEnum.unlock.getCode());
			record.setUpdateTime(new Date());
			orepoolRecordMapper.update(record);
		}
		orepoolPlan.setStatus(OrepoolPlanStatusEnum.unlock.getCode());
		orepoolPlan.setUpdateTime(new Date());
		orepoolPlanMapper.update(orepoolPlan);
		return ReturnResult.SUCCESS("解锁计划成功！");
	}
	
	public Result orepoolUnFrozen(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
        	return adminOrepoolServiceImplTX.orepoolUnFrozenImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
	 
	@Override
	public ReturnResult insertPlan(OrepoolPlan orepoolPlan) {
		try {
			//删除法币
			orepoolPlanMapper.insert(orepoolPlan);
			return ReturnResult.SUCCESS("新增计划成功！");
		} catch (Exception e) {
			logger.error("新增计划异常，id："+orepoolPlan.getId(),e);
			return ReturnResult.FAILUER("新增计划失败！");
		}
	}

	@Override
	public ReturnResult openOrepoolPlan(OrepoolPlan orepoolPlan) {
		try {
			//更新佣金表状态
			orepoolPlan.setStatus(OrepoolPlanStatusEnum.not_start.getCode());
			orepoolPlan.setUpdateTime(new Date());
			orepoolPlanMapper.update(orepoolPlan);
			return ReturnResult.SUCCESS("矿池计划开启成功！");
		} catch (Exception e) {
			logger.error("禁止发放异常，id："+orepoolPlan.getId(),e);
			return ReturnResult.FAILUER("矿池计划开启失败！");
		}
	}

	@Override
	public ReturnResult forbidOrepoolPlan(OrepoolPlan orepoolPlan) {
		try {
			//更新佣金表状态
			orepoolPlan.setStatus(OrepoolPlanStatusEnum.forbid.getCode());
			orepoolPlan.setUpdateTime(new Date());
			orepoolPlanMapper.update(orepoolPlan);
			return ReturnResult.SUCCESS("矿池计划禁用成功！");
		} catch (Exception e) {
			logger.error("禁止发放异常，id："+orepoolPlan.getId(),e);
			return ReturnResult.FAILUER("矿池计划禁用失败！");
		}
	};
	
	@Override
	public ReturnResult updateOrepoolPlan(OrepoolPlan orepoolPlan) {
		try {
			//更新锁仓计划
			orepoolPlanMapper.update(orepoolPlan);
			return ReturnResult.SUCCESS("修改锁仓计划成功！");
		} catch (Exception e) {
			logger.error("修改锁仓计划异常，id："+orepoolPlan.getId(),e);
			return ReturnResult.FAILUER("修改锁仓计划失败！");
		}
	}
}
