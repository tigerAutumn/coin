package com.qkwl.service.capital.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeStatusEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.capital.RedEnvelopeRecord;
import com.qkwl.common.dto.capital.RedEnvelopeResp;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IRedEnvelopeService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.util.UUIDUtils;
import com.qkwl.service.capital.dao.FUserMapper;
import com.qkwl.service.capital.dao.RedEnvelopeMapper;
import com.qkwl.service.capital.dao.RedEnvelopeRecordMapper;
import com.qkwl.service.capital.dao.UserWalletBalanceLogMapper;

@Service("redEnvelopeService")
public class RedEnvelopeServiceImpl implements IRedEnvelopeService {

	private static final Logger logger = LoggerFactory.getLogger(RedEnvelopeServiceImpl.class);
	
	@Autowired
	private RedEnvelopeMapper redEnvelopeMapper;
	@Autowired
	private RedEnvelopeRecordMapper redEnvelopeRecordMapper;
	@Autowired
	private IUserWalletService userWalletService;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
	private RedisHelper redisHelper;
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public String send(RedEnvelope redEnvelope) {
		String redEnvelopeNo = UUIDUtils.get32UUID();
		redEnvelope.setRedEnvelopeNo(redEnvelopeNo);
		redEnvelope.setStatus(RedEnvelopeStatusEnum.NORECEIVE.getCode());
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);
//		calendar.add(calendar.MINUTE, 5);
		date=calendar.getTime();
		redEnvelope.setOverdueTime(date);
		// 查询币种名称
		SystemCoinType coinTypeSystem = redisHelper.getCoinTypeSystem(redEnvelope.getCoinId());
		redEnvelope.setCoinName(coinTypeSystem.getShortName());
		redEnvelopeMapper.insert(redEnvelope);
		
		// 扣除用户可用到冻结
		Result frozenResult = userWalletService.total2Frozen(redEnvelope.getUid(), redEnvelope.getCoinId(), redEnvelope.getAmount());
		if (!frozenResult.getSuccess()) {
			logger.error("红包冻结失败,uid:{}", redEnvelope.getUid());
			throw new BizException(ErrorCodeEnum.FROZEN_FAILED);
		}
		
		Date now = new Date();
		//记录账户流水
		UserWalletBalanceLog log = new UserWalletBalanceLog();
		log.setUid(redEnvelope.getUid());
		log.setCoinId(redEnvelope.getCoinId());
		log.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
		log.setChange(redEnvelope.getAmount());
		log.setSrcId(redEnvelope.getId());
		log.setSrcType(UserWalletBalanceLogTypeEnum.SEND_RED_ENVELOPE.getCode());
		log.setDirection(UserWalletBalanceLogDirectionEnum.out.getValue());
		log.setCreatetime(now);
		log.setCreatedatestamp(now.getTime());
		userWalletBalanceLogMapper.insert(log);
		
		RedEnvelopeRecord record = new RedEnvelopeRecord();
		record.setRedEnvelopeNo(redEnvelopeNo);
		record.setCoinId(redEnvelope.getCoinId());
		record.setCoinName(coinTypeSystem.getShortName());
		if (RedEnvelopeTypeEnum.NORMAL.getCode().equals(redEnvelope.getType())) {
			BigDecimal amount = MathUtils.div(redEnvelope.getAmount(), new BigDecimal(redEnvelope.getCount()));
			record.setAmount(amount);
			for (int i = 0; i < redEnvelope.getCount(); i++) {
				redEnvelopeRecordMapper.insert(record);
			}
		} else {
			// 转化为整数
			String str = redEnvelope.getAmount().toString();
			String[] split = str.split("\\.");
			Integer digitCount = 0;
			if (split.length > 1) {
				digitCount = split[1].length();
			}
			BigDecimal digit = BigDecimal.ONE;
			for (int i = 0; i < digitCount; i++) {
				digit = MathUtils.mul(digit, BigDecimal.TEN);
			}
			BigDecimal amount = MathUtils.mul(redEnvelope.getAmount(), digit);
			// 随机红包生成
			List<BigDecimal> randomMoney = getRandomMoney(amount.doubleValue(), redEnvelope.getCount());

			for (BigDecimal money : randomMoney) {
				record.setAmount(MathUtils.div(money, digit));
				redEnvelopeRecordMapper.insert(record);
			}
		}
		return redEnvelopeNo;
	}

	public List<BigDecimal> getRandomMoney(Double amount,Integer person){
		
		List<BigDecimal> redEnvelopeList = new ArrayList<>();
        BigDecimal amount1 = new BigDecimal(amount);

        //计算出随机数分布值
        amount1 = amount1.multiply(BigDecimal.valueOf(100));

        Set set = new HashSet();
        ArrayList<Integer> list = new ArrayList();
        list.add(0,0);
        Random r = new Random();
        for (int i=0;i<person-1;i++){
            //防止重复点
            while (true){
                Integer money = r.nextInt(amount1.intValue());
                boolean isContain = set.contains(money);
                if (isContain == false){
                    set.add(money);
                    list.add(money);
                    break;
                }
            }
        }
        list.add(person,amount1.intValue());

        //排序
        Collections.sort(list, Collections.reverseOrder());

        //根据比例计算金额
        BigDecimal count = new BigDecimal(0);
        for(int i=0;i<list.size()-1;i++){
            if (i==list.size()-2){
                BigDecimal a = amount1.divide(BigDecimal.valueOf(100)).subtract(count);
                redEnvelopeList.add(a);
                return redEnvelopeList;
            }
            double gap = list.get(i) - list.get(i+1);
            DecimalFormat df = new DecimalFormat("0.00");

            String mon = df.format(new BigDecimal(gap/amount * (amount/100)));
            BigDecimal redEnvelope = new BigDecimal(mon);
            redEnvelopeList.add(redEnvelope);
            count = count.add(BigDecimal.valueOf(Double.parseDouble(mon)));
        }
		return redEnvelopeList;
    }
	
	@Override
	public RedEnvelope getDetail(String redEnvelopeNo) {
		return redEnvelopeMapper.selectByRedEnvelopeNo(redEnvelopeNo);
	}

	@Override
	public int getIsReceived(RedEnvelopeRecord record) {
		List<RedEnvelopeRecord> list = redEnvelopeRecordMapper.selectByRedEnvelopeNo(record);
		if (list == null || list.isEmpty()) {
			return 0;
		} else {
			return 1;
		}
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public RedEnvelopeResp receive(Integer uid, String redEnvelopeNo) {
		RedEnvelopeResp resp = new RedEnvelopeResp();
		RedEnvelope redEnvelope = redEnvelopeMapper.selectByRedEnvelopeNo(redEnvelopeNo);
		resp.setSenderNickname(redEnvelope.getNickname());
		resp.setSenderPhoto(redEnvelope.getPhoto());
		resp.setContent(redEnvelope.getContent());
		resp.setType(redEnvelope.getType());
		resp.setCount(redEnvelope.getCount());
		resp.setStatus(RedEnvelopeStatusEnum.NORECEIVE.getCode());
		resp.setCoinId(redEnvelope.getCoinId());
		resp.setCoinName(redEnvelope.getCoinName());
		
		// 查询已发放红包
		RedEnvelopeRecord param = new RedEnvelopeRecord();
		param.setRedEnvelopeNo(redEnvelopeNo);
		param.setIsReceived(1);
		List<RedEnvelopeRecord> receivedList = redEnvelopeRecordMapper.selectByRedEnvelopeNo(param);
		Integer receiveCount = receivedList.size();
		
		// 判断用户是否领取过
		param.setUid(uid);
		List<RedEnvelopeRecord> userReceivedList = redEnvelopeRecordMapper.selectByRedEnvelopeNo(param);
		if (userReceivedList != null && !userReceivedList.isEmpty()) {
			RedEnvelopeRecord redEnvelopeRecord = userReceivedList.get(0);
			resp.setLoginname(redEnvelopeRecord.getLoginName());
			resp.setAmount(redEnvelopeRecord.getAmount());
		}
		
		if (redEnvelope.getOverdueTime().before(new Date())) {
			// 红包已过期
			resp.setStatus(RedEnvelopeStatusEnum.OVERDUE.getCode());
		} else if (redEnvelope.getCount().equals(receiveCount)) {
			// 红包已发完
			Date createTime = receivedList.get(receivedList.size()-1).getCreateTime();
			Date receiveTime = receivedList.get(receivedList.size()-1).getReceiveTime();
			resp.setTime(receiveTime.getTime() - createTime.getTime());
			resp.setStatus(RedEnvelopeStatusEnum.RECEIVED.getCode());
		} else if (userReceivedList == null || userReceivedList.isEmpty()) {
			// 用户未领取
			param.setUid(null);
			param.setIsReceived(0);
			List<RedEnvelopeRecord> noReceiveList = redEnvelopeRecordMapper.selectByRedEnvelopeNo(param);
			
			RedEnvelopeRecord record = noReceiveList.get(0);
			Date now = new Date();
			record.setReceiveTime(now);
			record.setUid(uid);
			record.setIsReceived(1);
			// 查询用户
			FUser user = userMapper.selectByPrimaryKey(uid);
			record.setNickname(user.getFnickname());
			record.setPhoto(user.getPhoto());
			record.setLoginName(user.getFloginname());
			resp.setLoginname(user.getFloginname());
			
			receivedList.add(record);
			receiveCount += 1;
			resp.setAmount(record.getAmount());
			if (noReceiveList.size() == 1) {
				//只剩最后一个红包
				resp.setTime(now.getTime() - redEnvelope.getCreateTime().getTime());
				resp.setStatus(RedEnvelopeStatusEnum.RECEIVED.getCode());
				redEnvelope.setStatus(RedEnvelopeStatusEnum.RECEIVED.getCode());
				
				// 手气最佳红包
				if (RedEnvelopeTypeEnum.RANDOM.getCode().equals(redEnvelope.getType())) {
					int mostId = 0;
					BigDecimal mostAmount = receivedList.get(0).getAmount();
					for (int i = 1; i < receivedList.size(); i++) {
						if (mostAmount.compareTo(receivedList.get(i).getAmount()) < 0) {
							mostId = i;
							mostAmount = receivedList.get(i).getAmount();
						}
					}
					receivedList.get(mostId).setIsMost(1);
					redEnvelopeRecordMapper.updateIsMost(receivedList.get(mostId));
				}
			}
			redEnvelopeRecordMapper.updateReceived(record);
			
			Result addTotalResult = userWalletService.addTotal(record.getUid(), redEnvelope.getCoinId(), record.getAmount());
			if (!addTotalResult.getSuccess()) {
				logger.error("红包增加可用失败,uid:{}", redEnvelope.getId());
				throw new BizException(ErrorCodeEnum.ADD_TOTAL_FAILED);
			}
			
			// 领取红包流水
			UserWalletBalanceLog addTotalLog = new UserWalletBalanceLog();
			addTotalLog.setUid(record.getUid());
			addTotalLog.setCoinId(record.getCoinId());
			addTotalLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
			addTotalLog.setChange(record.getAmount());
			addTotalLog.setSrcId(record.getId());
			addTotalLog.setSrcType(UserWalletBalanceLogTypeEnum.RECEIVE_RED_ENVELOPE.getCode());
			addTotalLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
			addTotalLog.setCreatetime(now);
			addTotalLog.setCreatedatestamp(now.getTime());
			userWalletBalanceLogMapper.insert(addTotalLog);
			
			Result subFrozenResult = userWalletService.subFrozen(redEnvelope.getUid(), redEnvelope.getCoinId(), record.getAmount());
			if (!subFrozenResult.getSuccess()) {
				logger.error("红包扣除冻结失败,uid:{}", redEnvelope.getId());
				throw new BizException(ErrorCodeEnum.SUB_FROZEN_FAILED);
			}
			
			// 领取红包流水
			UserWalletBalanceLog subFrozenLog = new UserWalletBalanceLog();
			subFrozenLog.setUid(redEnvelope.getUid());
			subFrozenLog.setCoinId(record.getCoinId());
			subFrozenLog.setFieldId(UserWalletBalanceLogFieldEnum.frozen.getValue());
			subFrozenLog.setChange(record.getAmount());
			subFrozenLog.setSrcId(record.getId());
			subFrozenLog.setSrcType(UserWalletBalanceLogTypeEnum.DEDUCT_RED_ENVELOPE.getCode());
			subFrozenLog.setDirection(UserWalletBalanceLogDirectionEnum.out.getValue());
			subFrozenLog.setCreatetime(now);
			subFrozenLog.setCreatedatestamp(now.getTime());
			userWalletBalanceLogMapper.insert(subFrozenLog);
			
			redEnvelope.setReceiveAmount(MathUtils.add(redEnvelope.getReceiveAmount(), record.getAmount()));
			redEnvelope.setReceiveCount(receiveCount);
			redEnvelopeMapper.updateStatus(redEnvelope);
		}
		Collections.reverse(receivedList);
		resp.setList(receivedList);
		resp.setReceivedCount(receiveCount);
		return resp;
	}
	
	@Override
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public RedEnvelopeResp getReceiveDetail(String redEnvelopeNo) {
		RedEnvelopeResp resp = new RedEnvelopeResp();
		RedEnvelope redEnvelope = redEnvelopeMapper.selectByRedEnvelopeNo(redEnvelopeNo);
		resp.setSenderNickname(redEnvelope.getNickname());
		resp.setSenderPhoto(redEnvelope.getPhoto());
		resp.setContent(redEnvelope.getContent());
		resp.setType(redEnvelope.getType());
		resp.setStatus(RedEnvelopeStatusEnum.NORECEIVE.getCode());
		resp.setCount(redEnvelope.getCount());
		resp.setReceivedCount(redEnvelope.getReceiveCount());
		// 查询已发放红包
		RedEnvelopeRecord param = new RedEnvelopeRecord();
		param.setRedEnvelopeNo(redEnvelopeNo);
		param.setIsReceived(1);
		List<RedEnvelopeRecord> receivedList = redEnvelopeRecordMapper.selectByRedEnvelopeNo(param);
		Collections.reverse(receivedList);
		resp.setList(receivedList);
		if (redEnvelope.getOverdueTime().before(new Date())) {
			resp.setStatus(RedEnvelopeStatusEnum.OVERDUE.getCode());
		} else if (redEnvelope.getCount().equals(redEnvelope.getReceiveCount())) {
			//红包已发完
			Date createTime = receivedList.get(receivedList.size()-1).getCreateTime();
			Date receiveTime = receivedList.get(receivedList.size()-1).getReceiveTime();
			resp.setTime(receiveTime.getTime() - createTime.getTime());
			resp.setStatus(RedEnvelopeStatusEnum.RECEIVED.getCode());
		}
		return resp;
	}

	@Override
	public Pagination<RedEnvelopeRecord> getRecordList(Pagination<RedEnvelopeRecord> pageParam, RedEnvelopeRecord redEnvelopeRecord) {
		// 组装查询条件数据 
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("uid", redEnvelopeRecord.getUid());
		map.put("receiveTime", redEnvelopeRecord.getReceiveTime());
		// 查询广告总数
		int count = redEnvelopeRecordMapper.countRecordList(map);
		
		if(count > 0) {
			// 查询广告列表
			List<RedEnvelopeRecord> recordList = redEnvelopeRecordMapper.getRecordList(map);
			// 设置返回数据
			pageParam.setData(recordList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public BigDecimal getRecordTotal(RedEnvelopeRecord redEnvelopeRecord) {
		List<UserCoinWallet> userCoinWallets = redEnvelopeRecordMapper.getRecordTotal(redEnvelopeRecord);
		BigDecimal totalAssets = getTotalAssets(userCoinWallets);
        BigDecimal btcPrice = redisHelper.getLastPrice(8);
        BigDecimal btcAssets = MathUtils.div(totalAssets, btcPrice);
		return btcAssets;
	}
	
	@Override
	public int getBestCount(RedEnvelopeRecord redEnvelopeRecord) {
		return redEnvelopeRecordMapper.getBestCount(redEnvelopeRecord);
	}

	@Override
	public Pagination<RedEnvelope> getEnvelopeList(Pagination<RedEnvelope> pageParam, RedEnvelope redEnvelope) {
		// 组装查询条件数据 
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("uid", redEnvelope.getUid());
		map.put("createTime", redEnvelope.getCreateTime());
		// 查询广告总数
		int count = redEnvelopeMapper.countEnvelopeList(map);
		
		if(count > 0) {
			// 查询广告列表
			List<RedEnvelope> advertList = redEnvelopeMapper.getEnvelopeList(map);
			// 设置返回数据
			pageParam.setData(advertList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public BigDecimal getEnvelopeTotal(RedEnvelope redEnvelope) {
		List<UserCoinWallet> userCoinWallets = redEnvelopeMapper.getEnvelopeTotal(redEnvelope);
		BigDecimal totalAssets = getTotalAssets(userCoinWallets);
        BigDecimal btcPrice = redisHelper.getLastPrice(8);
        BigDecimal btcAssets = MathUtils.div(totalAssets, btcPrice);
		return btcAssets;
	}
	
	public BigDecimal getTotalAssets(List<UserCoinWallet> coinWallets) {
    	BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal tmp = BigDecimal.ZERO;
        List<SystemTradeType> GAVCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.GAVC.getCode(), 0);
        List<SystemTradeType> BTCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.BTC.getCode(), 0);
        List<SystemTradeType> ETHTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.ETH.getCode(), 0);
        List<SystemTradeType> USDTTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.USDT.getCode(), 0);
    	for (UserCoinWallet coinWallet : coinWallets) {
    		//币种为GAVC时
            if (coinWallet.getCoinId() == 9) {
                totalAssets = MathUtils.add(totalAssets, coinWallet.getTotal());
                continue;
            }
            
    		//GAVC交易区
    		BigDecimal GAVCAssets = getAssets(coinWallet, GAVCTypeList, 9);
    		if (MathUtils.compareTo(GAVCAssets, BigDecimal.ZERO) != 0) {
    			tmp = GAVCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//BTC交易区
    		BigDecimal BTCAssets = getAssets(coinWallet, BTCTypeList, 1);
    		if (MathUtils.compareTo(BTCAssets, BigDecimal.ZERO) != 0) {
    			tmp = BTCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//ETH交易区
    		BigDecimal ETHAssets = getAssets(coinWallet, ETHTypeList, 4);
    		if (MathUtils.compareTo(ETHAssets, BigDecimal.ZERO) != 0) {
    			tmp = ETHAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
            //USDT交易区
            BigDecimal USDTAssets = getAssets(coinWallet, USDTTypeList, 52);
            if (MathUtils.compareTo(USDTAssets, BigDecimal.ZERO) != 0) {
            	tmp = USDTAssets;
            	totalAssets = MathUtils.add(totalAssets, tmp);
            	continue;
            }
    	}
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }
	
	/**
     * 获取币种对应交易区的资产
     * @param coinWallet
     * @param coinId
     * @return
     */
    private BigDecimal getAssets(UserCoinWallet coinWallet, List<SystemTradeType> tradeTypeList, int coinId) {
    	BigDecimal price = BigDecimal.ZERO;
    	BigDecimal assets = BigDecimal.ZERO;
    	Integer tradeId;
    	for (SystemTradeType systemTradeType : tradeTypeList) {
			if (systemTradeType.getSellCoinId().equals(coinWallet.getCoinId()) && systemTradeType.getBuyCoinId().equals(coinId)) {
                price = redisHelper.getLastPrice(systemTradeType.getId());
                //买方币种不为GAVC时
                if (systemTradeType.getBuyCoinId() != 9) {
                	Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(0);
                	//其他区币种转化为gavc卖方币种
        			tradeId = trades.get(systemTradeType.getBuyCoinId());
        			if (tradeId == null) {
        				continue;
					}
        			BigDecimal lastPrice = redisHelper.getLastPrice(tradeId);
        			price = MathUtils.mul(lastPrice, price);
				}
                assets = MathUtils.mul(coinWallet.getTotal(), price);
                break;
			}
		}
    	return assets;
    }
}
