package com.qkwl.service.activity.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.activity.InnovationAreaActivity;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.DateUtils;
import com.qkwl.service.activity.dao.FEntrustHistoryMapper;
import com.qkwl.service.activity.dao.FEntrustMapper;
import com.qkwl.service.activity.dao.InnovationAreaActivityMapper;
import com.qkwl.service.activity.dao.OrepoolPlanMapper;
import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import com.qkwl.service.activity.dao.UserWalletBalanceLogMapper;
import com.qkwl.service.activity.mq.KafkaSend;

@Service
public class AutoEntrustHistoryService {
	private static final Logger logger = LoggerFactory.getLogger(AutoEntrustHistoryService.class);
	
	 /**
     * 历史每次读取委单条数
     */
    private static final int HISTORY_MAX = 50;
    
    private static final int TRY_TIMES = 3;
	
	@Autowired
    private FEntrustMapper fEntrustMapper;
	@Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
	private InnovationAreaActivityMapper innovationAreaActivityMapper;
	@Autowired
	private OrepoolPlanMapper orepoolPlanMapper;
	
	@Autowired
	private KafkaSend kafkaSend;
	
	
	public List<FEntrust> getHistoryEntrust(int ftradeid){
		return fEntrustMapper.selectHistoryEntrust(ftradeid, HISTORY_MAX);
	}
	
	 /**
     * 更新委单历史
     * @param fEntrust 委单
     * @return true
     * @throws Exception 执行异常
     */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public synchronized boolean updateMatchHistoryBatch(List<FEntrust> fEntrusts) throws Exception {
        // history
    	List<FEntrustHistory> historyList = new ArrayList<>();
    	for(FEntrust fEntrust : fEntrusts) {
    		FEntrustHistory fEntrustHistory = CopyHistory(fEntrust);
    		historyList.add(fEntrustHistory);
    	}
    	
        int result = entrustHistoryMapper.insertBatch(historyList);
        if (result <= 0) {
            throw new Exception();
        }
        if (fEntrustMapper.deleteByfIdBatch(fEntrusts) <= 0) {
            throw new Exception();
        }
        return true;
    }
    
    
    /**
     * 创新区分红
     * 分红比例，用户该币种冻结钱包的数量将按照该币种在创新区的用户买入数量来释放，用户每次买入成功后，都需进行释放，释放规则为：冻结释放数量=（买入数量*冻结释放比例）
     * @param fEntrust 委单
     * @return true
     * @throws Exception 执行异常
     */
    @Transactional(value="xaTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean innovationAreaDividend(List<FEntrust> fEntrusts) throws Exception {
        // history
    	List<FEntrustHistory> historyList = new ArrayList<>();
    	for(FEntrust fEntrust : fEntrusts) {
			SystemCoinType systemCoinType = redisHelper.getCoinType(fEntrust.getFsellcoinid());
			//如果是创新区需要释放的币
			if(systemCoinType.getIsInnovateAreaCoin()) {
				//判断是否为创新区存币币种
				int planCount = orepoolPlanMapper.getInnovationPlanByCoinId(systemCoinType.getId());
				if (planCount == 0) {
					//冻结释放数量=（买入数量*冻结释放比例）
		    		if(fEntrust.getFtype().equals(EntrustTypeEnum.BUY.getCode())) {
	    	    		
	    				//冻结释放比例
	        			BigDecimal rate = systemCoinType.getReleaseLockingRatio();
	        			
	        			//单日总冻结释放比例
	        			BigDecimal limitRate = systemCoinType.getDayReleaseRatio();
	        			
	        			//成交数量
//	            		BigDecimal count = MathUtils.sub(fEntrust.getFcount(), fEntrust.getFleftcount());
	        			
	        			//非对手单的成交数量
		    			BigDecimal count = BigDecimal.ZERO;
		    			List<FEntrustLog> sellerEntrust = fEntrustMapper.selectSellerEntrust(fEntrust.getFid());
		    			for (FEntrustLog fEntrustLog : sellerEntrust) {
							if (fEntrustLog.getFuid().intValue() != fEntrust.getFuid().intValue()) {
								count = MathUtils.add(count, fEntrustLog.getFcount());
							}
						}
	            		
	            		if(count.compareTo(BigDecimal.ZERO)>0) {
	                		//释放数量
	                		BigDecimal total = MathUtils.mul(count, rate);
	                		int trytimes = 0;
	                		while (true) {
	                			//查询钱包
	                    		UserCoinWallet userCoinWallet = userCoinWalletMapper.select(fEntrust.getFuid(), fEntrust.getFsellcoinid());
	                    		//单日释放总量上限
	                    		BigDecimal limitTotal = MathUtils.mul(userCoinWallet.getDepositFrozenTotal(), limitRate);
	                    		//更新数量
	                    		BigDecimal updateTotal = BigDecimal.ZERO;
	                    		//查询释放总量
	                    		Map<String, Object> param = new HashMap<>();
	                    		param.put("uid", fEntrust.getFuid());
	                    		param.put("coinId", fEntrust.getFsellcoinid());
	                    		param.put("srcType", UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
	                    		param.put("direction", UserWalletBalanceLogDirectionEnum.in.getValue());
	                    		//查询当日释放总量
	                    		param.put("start", DateUtils.format(DateUtils.getCurrentDay(), "yyyy-MM-dd"));
	                    		param.put("end", DateUtils.format(DateUtils.getFutureDayDate(1), "yyyy-MM-dd"));
	                    		BigDecimal limitCurrentTotal = userWalletBalanceLogMapper.getFrozenTotalAmount(param);
	                    		//先判断是否全部解冻完
	                    		if(userCoinWallet.getDepositFrozen().compareTo(BigDecimal.ZERO)==0) {
	                    			break;
	                    		}
	                    		
	                    		//判断当日释放总量+当前释放量,如果大于当日释放总量就释放没有超出的。
	                    		if(MathUtils.add(total, limitCurrentTotal).compareTo(limitTotal) >= 0) {
	                    			//计算超出的
	                    			BigDecimal exceededTotal = MathUtils.sub(MathUtils.add(total, limitCurrentTotal), limitTotal);
	                    			BigDecimal avalibleTotal = MathUtils.sub(total, exceededTotal);
	                    			if(avalibleTotal.compareTo(BigDecimal.ZERO)<=0) {
	                    				break;
	                    			}else {
	                        			//查询是否需要奖励
	                    				Map<String, Object> awardParam = new HashMap<>();
	                    				awardParam.put("coinId", fEntrust.getFsellcoinid());
	                    				awardParam.put("status", 1);
	                    				List<InnovationAreaActivity> list = innovationAreaActivityMapper.listInnovationActivity(awardParam);
	                    				InnovationAreaActivity innovationAreaActivity = new InnovationAreaActivity();
	                    				if(list != null && list.size() > 0) {
	                    					innovationAreaActivity = list.get(0);
	                    					feeReward(fEntrust, innovationAreaActivity,userCoinWallet);
	                    				}
	                    				
	                        			logger.info("userCoinWallet.getTotal："+userCoinWallet.getTotal());
	                        			//如果钱包可释放的数量小于计算后的释放量则直接释放钱包所有的币
	                        			if(MathUtils.sub(userCoinWallet.getDepositFrozen(), avalibleTotal).compareTo(BigDecimal.ZERO)<=0) {
	                        				updateTotal = userCoinWallet.getDepositFrozen();
	                        			}else {
	                        				updateTotal = avalibleTotal;
	                        			}
	                        			userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(), updateTotal));
	                        			userCoinWallet.setDepositFrozen(MathUtils.sub(userCoinWallet.getDepositFrozen(), updateTotal));
	                        			
	                        			if(userCoinWalletMapper.update(userCoinWallet) == 0) {
	                        				if (trytimes == TRY_TIMES) {
	                        			        throw new BCException("更改失败");
	                        		        }
	                        		        trytimes = trytimes +1;
	                        		        try {
	                        					Thread.sleep(10);
	                        				} catch (InterruptedException e) {
	                        					logger.error("innovationAreaDividend执行异常",e);
	                        					Thread.currentThread().interrupt();
	                        				}
	                        			}else {
	                    					logger.info("写日志");
	                    					Map<String, Object> awardParamLog = new HashMap<>();
	                    					awardParamLog.put("coinId", fEntrust.getFsellcoinid());
	                    					awardParamLog.put("status", 1);
	                        				List<InnovationAreaActivity> listLog = innovationAreaActivityMapper.listInnovationActivity(awardParam);
	                        				InnovationAreaActivity innovationAreaActivityLog = new InnovationAreaActivity();
	                        				if(list != null && list.size() > 0) {
	                        					innovationAreaActivityLog = listLog.get(0);
	                        					feeRewardLog(fEntrust, innovationAreaActivityLog);
	                        				}
	                        				
	                        				//记录钱包操作日志
	                            			UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
	                            			userWalletBalanceLog.setCoinId(fEntrust.getFsellcoinid());
	                            			userWalletBalanceLog.setChange(updateTotal);
	                            			userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
	                            			userWalletBalanceLog.setCreatetime(new Date());
	                            			userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
	                            			userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
	                            			userWalletBalanceLog.setSrcId(fEntrust.getFid().intValue());
	                            			userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
	                            			userWalletBalanceLog.setUid(fEntrust.getFuid());
	                            			
	                            			if (this.userWalletBalanceLogMapper.insert(userWalletBalanceLog)<= 0) {
	                            				logger.error("创新区解冻----用户账户钱包新增日志出错uid = " + userCoinWallet.getUid());
	                                            throw new Exception("创新区解冻----用户账户钱包新增日志出错uid = " + userCoinWallet.getUid());
	                                        }
	                            			break;
	                        			}
	                    			}
	                    		} else {
	                    			
	                    			//查询是否需要奖励
	                				Map<String, Object> awardParam = new HashMap<>();
	                				awardParam.put("coinId", fEntrust.getFsellcoinid());
	                				awardParam.put("status", 1);
	                				List<InnovationAreaActivity> list = innovationAreaActivityMapper.listInnovationActivity(awardParam);
	                				InnovationAreaActivity innovationAreaActivity = new InnovationAreaActivity();
	                				if(list != null && list.size() > 0) {
	                					innovationAreaActivity = list.get(0);
	                					feeReward(fEntrust, innovationAreaActivity,userCoinWallet);
	                				}
	                    			
	                    			logger.info("userCoinWallet.getTotal："+userCoinWallet.getTotal());
	                    			//如果钱包可释放的数量小于计算后的释放量则直接释放钱包所有的币
	                    			if(MathUtils.sub(userCoinWallet.getDepositFrozen(), total).compareTo(BigDecimal.ZERO)<=0) {
	                    				updateTotal = userCoinWallet.getDepositFrozen();
	                    			}else {
	                    				updateTotal = total;
	                    			}
	                    			userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(), updateTotal));
	                    			userCoinWallet.setDepositFrozen(MathUtils.sub(userCoinWallet.getDepositFrozen(), updateTotal));
	                    			
	                    			if(userCoinWalletMapper.update(userCoinWallet) == 0) {
	                    				if (trytimes == TRY_TIMES) {
	                    			        throw new BCException("更改失败");
	                    		        }
	                    		        trytimes = trytimes +1;
	                    		        try {
	                    					Thread.sleep(10);
	                    				} catch (InterruptedException e) {
	                    					logger.error("innovationAreaDividend执行异常",e);
	                    					Thread.currentThread().interrupt();
	                    				}
	                    			}else {
	                					logger.info("写日志");
	                    				
	                					Map<String, Object> awardParamLog = new HashMap<>();
	                					awardParamLog.put("coinId", fEntrust.getFsellcoinid());
	                					awardParamLog.put("status", 1);
	                    				List<InnovationAreaActivity> listLog = innovationAreaActivityMapper.listInnovationActivity(awardParam);
	                    				InnovationAreaActivity innovationAreaActivityLog = new InnovationAreaActivity();
	                    				if(list != null && list.size() > 0) {
	                    					innovationAreaActivityLog = listLog.get(0);
	                    					feeRewardLog(fEntrust, innovationAreaActivityLog);
	                    				}
	                    				
	                    				//记录钱包操作日志
	                        			UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
	                        			userWalletBalanceLog.setCoinId(fEntrust.getFsellcoinid());
	                        			userWalletBalanceLog.setChange(updateTotal);
	                        			userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
	                        			userWalletBalanceLog.setCreatetime(new Date());
	                        			userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
	                        			userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
	                        			userWalletBalanceLog.setSrcId(fEntrust.getFid().intValue());
	                        			userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Freezing_of_Innovation_Zone.getCode());
	                        			userWalletBalanceLog.setUid(fEntrust.getFuid());
	                        			
	                        			if (this.userWalletBalanceLogMapper.insert(userWalletBalanceLog)<= 0) {
	                        				logger.error("创新区解冻----用户账户钱包新增日志出错uid = " + userCoinWallet.getUid());
	                                        throw new Exception("创新区解冻----用户账户钱包新增日志出错uid = " + userCoinWallet.getUid());
	                                    }
	                        			break;
	                    			}
	                    		}
	                		}
	            		}
	    			}
				}
				
    		}
    		//logger.error("fEntrust:{}", JSON.toJSONString(fEntrust));
    		FEntrustHistory fEntrustHistory = CopyHistory(fEntrust);
			//logger.error("fEntrustHistory:{}", JSON.toJSONString(fEntrustHistory));
    		historyList.add(fEntrustHistory);
    	}
    	
        int result = entrustHistoryMapper.insertBatch(historyList);
        if (result <= 0) {
            throw new Exception();
        }
        if (fEntrustMapper.deleteByfIdBatch(fEntrusts) <= 0) {
            throw new Exception();
        }
        
        kafkaSend.put(historyList);
        
        return true;
    }
    
    private void feeReward(FEntrust entrust,InnovationAreaActivity innovationAreaActivity,UserCoinWallet userCoinWallet) throws BCException {
    	//首先判断是否在活动时间内
    	if(new Date().before(innovationAreaActivity.getStartTime())) {
    		logger.info("小于开始时间");
    		return; 
    	}
    	
    	if(new Date().after(innovationAreaActivity.getEndTime())) {
    		logger.info("大于结束时间");
    		return;
    	}
    	
    	if(innovationAreaActivity.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
    		logger.info("余额等于零");
    		return;
    	}
    	
    	//返手续费
    	//成交数量
//    	BigDecimal count = MathUtils.sub(entrust.getFcount(), entrust.getFleftcount());
    	
    	//非对手单的成交数量
		BigDecimal count = BigDecimal.ZERO;
		List<FEntrustLog> sellerEntrust = fEntrustMapper.selectSellerEntrust(entrust.getFid());
		for (FEntrustLog fEntrustLog : sellerEntrust) {
			if (fEntrustLog.getFuid().intValue() != entrust.getFuid().intValue()) {
				count = MathUtils.add(count, fEntrustLog.getFcount());
			}
		}
    	BigDecimal reward = MathUtils.mul(count, innovationAreaActivity.getRate());
    	
    	//如果奖励金额大于奖池余额则直接返还所有奖池余额给用户
    	if(reward.compareTo(innovationAreaActivity.getBalance())>=0) {
    		logger.info("可返余额全部释放");
    		reward = innovationAreaActivity.getBalance();
    	}
    	
    	userCoinWallet.setTotal(MathUtils.add(userCoinWallet.getTotal(), reward));
    	
    	innovationAreaActivity.setBalance(MathUtils.sub(innovationAreaActivity.getBalance(), reward));
    }
    
    private void feeRewardLog(FEntrust entrust,InnovationAreaActivity innovationAreaActivity) {
    	//返手续费
    	//成交数量
//    	BigDecimal count = MathUtils.sub(entrust.getFcount(), entrust.getFleftcount());
    	
    	//非对手单的成交数量
		BigDecimal count = BigDecimal.ZERO;
		List<FEntrustLog> sellerEntrust = fEntrustMapper.selectSellerEntrust(entrust.getFid());
		for (FEntrustLog fEntrustLog : sellerEntrust) {
			if (fEntrustLog.getFuid().intValue() != entrust.getFuid().intValue()) {
				count = MathUtils.add(count, fEntrustLog.getFcount());
			}
		}
    	BigDecimal reward = MathUtils.mul(count, innovationAreaActivity.getRate());
    	
    	//如果奖励金额大于奖池余额则直接返还所有奖池余额给用户
    	if(reward.compareTo(innovationAreaActivity.getBalance())>=0) {
    		logger.info("可返余额全部释放");
    		reward = innovationAreaActivity.getBalance();
    	}
    	innovationAreaActivity.setBalance(MathUtils.sub(innovationAreaActivity.getBalance(), reward));
    	
    	//更新余额
    	logger.info("更新余额");
    	innovationAreaActivityMapper.updateByPrimaryKeySelective(innovationAreaActivity);
    	
    	//记录钱包操作日志
    	logger.info("记录日志");
		UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
		userWalletBalanceLog.setCoinId(entrust.getFsellcoinid());
		userWalletBalanceLog.setChange(reward);
		userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
		userWalletBalanceLog.setCreatetime(new Date());
		userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
		userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
		userWalletBalanceLog.setSrcId(entrust.getFid().intValue());
		userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
		userWalletBalanceLog.setUid(entrust.getFuid());
		this.userWalletBalanceLogMapper.insert(userWalletBalanceLog);
    }
    
    /**
     * 历史委单复制
     * @param fEntrust 原始委单
     * @return 历史委单
     */
    private synchronized FEntrustHistory CopyHistory(FEntrust fEntrust) {
        FEntrustHistory fEntrustHistory = new FEntrustHistory();
        fEntrustHistory.setFentrustid(fEntrust.getFid());
        fEntrustHistory.setFbuycoinid(fEntrust.getFbuycoinid());
        fEntrustHistory.setFsellcoinid(fEntrust.getFsellcoinid());
        fEntrustHistory.setFuid(fEntrust.getFuid());
        fEntrustHistory.setFtradeid(fEntrust.getFtradeid());
        fEntrustHistory.setFstatus(fEntrust.getFstatus());
        fEntrustHistory.setFtype(fEntrust.getFtype());
        fEntrustHistory.setFmatchtype(fEntrust.getFmatchtype());
        fEntrustHistory.setFlast(fEntrust.getFlast());
        fEntrustHistory.setFprize(fEntrust.getFprize());
        fEntrustHistory.setFcount(fEntrust.getFcount());
        fEntrustHistory.setFamount(fEntrust.getFamount());
        fEntrustHistory.setFsuccessamount(fEntrust.getFsuccessamount());
        fEntrustHistory.setFleftcount(fEntrust.getFleftcount());
        fEntrustHistory.setFleftfees(fEntrust.getFleftfees());
        fEntrustHistory.setFfees(fEntrust.getFfees());
        fEntrustHistory.setFsource(fEntrust.getFsource());
        fEntrustHistory.setFhuobientrustid(fEntrust.getFhuobientrustid());
        fEntrustHistory.setFhuobiaccountid(fEntrust.getFhuobiaccountid());
        fEntrustHistory.setFlastupdattime(fEntrust.getFlastupdattime());
        fEntrustHistory.setFcreatetime(fEntrust.getFcreatetime());
        fEntrustHistory.setFagentid(fEntrust.getFagentid());
        if (fEntrust.getFfunds() == null ) {
			fEntrustHistory.setFfunds(BigDecimal.ZERO);
			fEntrustHistory.setFleftfunds(BigDecimal.ZERO);
		}
		else {
			fEntrustHistory.setFfunds(fEntrust.getFfunds());
			fEntrustHistory.setFleftfunds(fEntrust.getFleftfunds());
		}
        return fEntrustHistory;
    }
}
