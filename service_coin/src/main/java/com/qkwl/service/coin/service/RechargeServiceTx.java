package com.qkwl.service.coin.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.Enum.CapitalOperationInOutTypeEnum;
import com.qkwl.common.dto.Enum.CapitalOperationInStatus;
import com.qkwl.common.dto.Enum.CoinCollectStatusEnum;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.USDTCollectStatusEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.Enum.UserWhiteListTypeEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.CoinCollect;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.USDTCollect;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.service.coin.mapper.CoinCollectMapper;
import com.qkwl.service.coin.mapper.FPoolMapper;
import com.qkwl.service.coin.mapper.FUserExtendMapper;
import com.qkwl.service.coin.mapper.FUserMapper;
import com.qkwl.service.coin.mapper.FUserVirtualAddressMapper;
import com.qkwl.service.coin.mapper.FVirtualCapitalOperationMapper;
import com.qkwl.service.coin.mapper.FWalletCapitalOperationMapper;
import com.qkwl.service.coin.mapper.OrepoolRecordMapper;
import com.qkwl.service.coin.mapper.USDTCollectMapper;
import com.qkwl.service.coin.mapper.UserCoinWalletMapper;
import com.qkwl.service.coin.mapper.UserWalletBalanceLogMapper;
import com.qkwl.service.coin.mapper.UserWhiteListMapper;
import com.qkwl.service.coin.util.JobUtils;
import com.qkwl.service.coin.util.MQSend;
import com.qkwl.service.coin.util.WalletUtils;

@Service("rechargeServiceTx")
@Scope("prototype")
public class RechargeServiceTx {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(RechargeServiceTx.class);

	@Autowired
	private FVirtualCapitalOperationMapper fVirtualCapitalOperationMapper;
	@Autowired
	private FWalletCapitalOperationMapper fWalletCapitalOperationMapper;
	@Autowired
	private FUserVirtualAddressMapper fUserVirtualAddressMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private FUserExtendMapper userExtendMapper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;

	@Autowired
	private JobUtils jobUtils;
	@Autowired
	private ScoreHelper scoreHelper;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private MQSend mqSend;
	
	@Autowired
	private CoinCollectMapper coinCollectMapper;
	
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
	private WalletUtils walletUtils;
	@Autowired
	private UserWhiteListMapper userWhiteListMapper;
	@Autowired
	private OrepoolRecordMapper orepoolRecordMapper;

	
	/**
	 * 处理充值(根据地址的充值)
	 * 
	 * @param fUserVirtualAddressDTO
	 * @param txInfo
	 * @param coinType
	 * @param walletCoinType 
	 * @throws Exception
	 */
	@Transactional(value="xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//@Transactional( isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void handleRechargeTask(FUserVirtualAddressDTO fUserVirtualAddressDTO ,TxInfo txInfo,SystemCoinType coinType, SystemCoinType walletCoinType) throws Exception {
		List<FVirtualCapitalOperationDTO> fvirtualcaptualoperations = this.fVirtualCapitalOperationMapper.selectByTxid(txInfo.getTxid());
		//如果充值记录已存在就返回
		if (fvirtualcaptualoperations != null && fvirtualcaptualoperations.size() > 0) {
			return;
		}
		//最小充值数
		BigDecimal rechargeMinLimit = coinType.getRechargeMinLimit();
		//这个地址有币就记录下来 updateCollect方法会进行收集
		CoinCollect cc = new CoinCollect();
		Date date = new Date();
		cc.setAddress(fUserVirtualAddressDTO.getFadderess());
		cc.setSecret(fUserVirtualAddressDTO.getSecret());
		cc.setCoinId(coinType.getId());
		cc.setStatus(CoinCollectStatusEnum.UNCOLLECT.getCode());
		cc.setCreatetime(date);
		cc.setUpdatetime(date);
		int insert = coinCollectMapper.insert(cc);
		if(insert <= 0) {
			logger.error("");
		}
		
		//用于判断是否充值成功
		boolean isRecharge = true;
		if(rechargeMinLimit.compareTo(txInfo.getAmount()) > 0) {
			isRecharge = false;
		}
		
		//如果是创新区的就去查询是否是白名单
		boolean isWhiteList = false;
		if(coinType.getIsInnovateAreaCoin()) {
			List<UserWhiteList> selectByUserAndCoinAndType = userWhiteListMapper.selectByUserAndCoinAndType(fUserVirtualAddressDTO.getFuid(), coinType.getId(), UserWhiteListTypeEnum.RECHARGE_OF_INNOVATION_ZONE.getCode());
			if(selectByUserAndCoinAndType != null && selectByUserAndCoinAndType.size() > 0) {
				isWhiteList = true;
			}
		}
		
		//构造充值记录对象
		FVirtualCapitalOperationDTO fvirtualcaptualoperation = new FVirtualCapitalOperationDTO();
		fvirtualcaptualoperation.setFamount(txInfo.getAmount());
		fvirtualcaptualoperation.setFfees(BigDecimal.ZERO);
		fvirtualcaptualoperation.setFcoinid(coinType.getId());
		fvirtualcaptualoperation.setWalletCoinId(walletCoinType.getId());
		fvirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
		fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);
		fvirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
		fvirtualcaptualoperation.setFblocknumber(BigInteger.valueOf(0));
		fvirtualcaptualoperation.setFconfirmations(0);
		fvirtualcaptualoperation.setFrechargeaddress(txInfo.getAddress().trim());
		fvirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
		fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
		fvirtualcaptualoperation.setVersion(0);
		fvirtualcaptualoperation.setFsource(DataSourceEnum.WEB.getCode());
		fvirtualcaptualoperation.setFuniquenumber(txInfo.getTxid());
		fvirtualcaptualoperation.setFhasowner(isRecharge);
		if(isWhiteList) {
			fvirtualcaptualoperation.setIsFrozen(false);
		}else {
			fvirtualcaptualoperation.setIsFrozen(coinType.getIsInnovateAreaCoin());
		}
		
		if(isRecharge) {
			fvirtualcaptualoperation.setFuid(fUserVirtualAddressDTO.getFuid());
		}
		if(!StringUtils.isEmpty(txInfo.getMemo())) {
			fvirtualcaptualoperation.setMemo(txInfo.getMemo());
		}
		if (txInfo.getBlockNumber() != null) {
			fvirtualcaptualoperation.setFblocknumber(txInfo.getBlockNumber());
		}
		if (txInfo.getTime() != null) {
			fvirtualcaptualoperation.setTxTime(txInfo.getTime());
		}
		//充值记录入库
		int result = this.fVirtualCapitalOperationMapper.insert(fvirtualcaptualoperation);
		if (result <= 0) {
			logger.error("Coin updateRecharge insert failed");
			throw new BCException("充值记录入库失败");
		}
		
		//如果充值数量不够的话就没收了
		if(!isRecharge) {
			return;
		} 
		
		//如果是创新区而且不是白名单的
		if(coinType.getIsInnovateAreaCoin() && !isWhiteList) {
			if(!walletUtils.change(fUserVirtualAddressDTO.getFuid(), coinType.getId(), WalletUtils.depositFrozen_add, txInfo.getAmount())) {
				throw new BCException("更新钱包失败");
			}
			//入流水表
			UserWalletBalanceLog u = new UserWalletBalanceLog();
			u.setCoinId(coinType.getId());
			u.setUid(fvirtualcaptualoperation.getFuid());
			u.setFieldId(UserWalletBalanceLogFieldEnum.deposit_frozen.getValue());
			u.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
			u.setSrcType(UserWalletBalanceLogTypeEnum.Recharge_freezing.getCode());
			u.setChange(fvirtualcaptualoperation.getFamount());
			u.setSrcId(fvirtualcaptualoperation.getFid());
			u.setCreatetime(date);
			userWalletBalanceLogMapper.insert(u);
		}else {
			if(!walletUtils.change(fUserVirtualAddressDTO.getFuid(), coinType.getId(), WalletUtils.total_add, txInfo.getAmount())) {
				throw new BCException("更新钱包失败");
			}
		}
		boolean isFirstRecharge = isFirstCharge(fUserVirtualAddressDTO.getFuid());
		
		BigDecimal last = jobUtils.getLastPrice(fUserVirtualAddressDTO.getFcoinid());
		BigDecimal amount = MathUtils.mul(txInfo.getAmount(), last);

		scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), amount,
				ScoreTypeEnum.RECHARGE.getCode(),
				"充值" + coinType.getShortName() + ":" + fvirtualcaptualoperation.getFamount());
		// 首次充值奖励
		if (isFirstRecharge) {
			scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), BigDecimal.ZERO,
					ScoreTypeEnum.FIRSTCHARGE.getCode(),
					ScoreTypeEnum.FIRSTCHARGE.getValue().toString());
		}
		mqSend.SendUserAction(fvirtualcaptualoperation.getFagentid(),
				fvirtualcaptualoperation.getFuid(), LogUserActionEnum.COIN_RECHARGE,
				fvirtualcaptualoperation.getFcoinid(), 0, fvirtualcaptualoperation.getFamount());
		
		FUser user = userMapper.selectByPrimaryKey(fvirtualcaptualoperation.getFuid());
		FUserExtend userExtend = userExtendMapper.selectByUid(fvirtualcaptualoperation.getFuid());
		Integer langCode = 1;
		if (userExtend != null) {
			langCode = LocaleEnum.getCodeByName(userExtend.getLanguage());
		}
		// 发送短信
		if (user.getFistelephonebind()) {
			try {
				validateHelper.smsCoinToAccount(user.getFareacode(), user.getFtelephone(), user.getFplatform(),
						BusinessTypeEnum.SMS_CHARGE_TO_ACCOUNT.getCode(), amount, coinType.getShortName(),
						langCode);
			} catch (Exception e) {
				logger.error("smsCoinToAccound err");
				e.printStackTrace();
			}
		}
		
		// 风控短信
		if (fvirtualcaptualoperation.getFamount().compareTo(coinType.getRiskNum()) >= 0) {
			String riskphone = jobUtils.getSystemArgs(ArgsConstant.RISKPHONE);
			String[] riskphones = riskphone.split("#");
			if (riskphones.length > 0) {
				FUser fuser = userMapper.selectByPrimaryKey(fvirtualcaptualoperation.getFuid());
				for (String string : riskphones) {
					try {
						validateHelper.smsRiskManage(fuser.getFloginname(), string,
								PlatformEnum.BC.getCode(), BusinessTypeEnum.SMS_RISKMANAGE.getCode(),
								"充值", fvirtualcaptualoperation.getFamount(), coinType.getName());
					} catch (Exception e) {
						logger.error("handleRechargeTask riskphones err");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 充值到账（确认数到了，到账）
	 * @param fvirtualcaptualoperation 充值记录
	 * @param confirmations 确认数
	 * @param coinType 币种类型
	 * @throws BCException 
	 * @throws Exception
	 */
	@Transactional(value="xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//@Transactional( isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void rechargeArrivalAccount(FVirtualCapitalOperationDTO fvirtualcaptualoperation,Integer confirmations, SystemCoinType coinType) throws BCException {
		fvirtualcaptualoperation.setFconfirmations(confirmations);
		fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
		// 确认状态
		if (fvirtualcaptualoperation.getFstatus() != VirtualCapitalOperationInStatusEnum.SUCCESS) {
			if (confirmations >= coinType.getConfirmations()) {
				if(fvirtualcaptualoperation.getIsFrozen()) {
					if(!walletUtils.change(fvirtualcaptualoperation.getFuid(), fvirtualcaptualoperation.getWalletCoinId(), WalletUtils.depositFrozen_add, fvirtualcaptualoperation.getFamount())) {
						throw new BCException("更改钱包错误");
					}
					//修改用户创新区锁仓记录
					OrepoolRecord record = new OrepoolRecord();
					record.setUserId(fvirtualcaptualoperation.getFuid());
					record.setLockCoinId(fvirtualcaptualoperation.getFcoinid());
					List<OrepoolRecord> innovationRecordList = orepoolRecordMapper.getInnovationRecord(record);
					for (OrepoolRecord orepoolRecord : innovationRecordList) {
						orepoolRecord.setStatus(OrepoolRecordStatusEnum.counting.getCode());
						orepoolRecord.setUpdateTime(new Date());
						orepoolRecordMapper.update(record);
					}
				}else{
					if(!walletUtils.change(fvirtualcaptualoperation.getFuid(), fvirtualcaptualoperation.getWalletCoinId(), WalletUtils.total_add, fvirtualcaptualoperation.getFamount())) {
						throw new BCException("更改钱包错误");
					}
				}
				boolean isFirstRecharge = isFirstCharge(fvirtualcaptualoperation.getFuid());
				// 更新订单
				fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.SUCCESS);
				if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
					throw new BCException();
				}
				
				FUser user = userMapper.selectByPrimaryKey(fvirtualcaptualoperation.getFuid());
				FUserExtend userExtend = userExtendMapper.selectByUid(fvirtualcaptualoperation.getFuid());
				Integer langCode = 1;
				if (userExtend != null) {
					langCode = LocaleEnum.getCodeByName(userExtend.getLanguage());
				}
				// 发送短信
				if (user.getFistelephonebind()) {
					try {
						validateHelper.smsCoinToAccount(user.getFareacode(), user.getFtelephone(), user.getFplatform(),
								BusinessTypeEnum.SMS_CHARGE_TO_ACCOUNT.getCode(), fvirtualcaptualoperation.getFamount(), coinType.getShortName(),
								langCode);
					} catch (Exception e) {
						logger.error("smsCoinToAccound err");
						e.printStackTrace();
					}
				}
				
				try {
					BigDecimal last = jobUtils.getLastPrice(coinType.getId());
					BigDecimal amount = MathUtils.mul(fvirtualcaptualoperation.getFamount(), last);

					scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), amount,
							ScoreTypeEnum.RECHARGE.getCode(),
							"充值" + coinType.getShortName() + ":" + fvirtualcaptualoperation.getFamount());
					// 首次充值奖励
					if (isFirstRecharge) {
						scoreHelper.SendUserScore(fvirtualcaptualoperation.getFuid(), BigDecimal.ZERO,
								ScoreTypeEnum.FIRSTCHARGE.getCode(),
								ScoreTypeEnum.FIRSTCHARGE.getValue().toString());
					}
					mqSend.SendUserAction(fvirtualcaptualoperation.getFagentid(),
							fvirtualcaptualoperation.getFuid(), LogUserActionEnum.COIN_RECHARGE,
							fvirtualcaptualoperation.getFcoinid(), 0, fvirtualcaptualoperation.getFamount());
					// 风控短信
					if (fvirtualcaptualoperation.getFamount().compareTo(coinType.getRiskNum()) >= 0) {
						String riskphone = jobUtils.getSystemArgs(ArgsConstant.RISKPHONE);
						String[] riskphones = riskphone.split("#");
						if (riskphones.length > 0) {
							FUser fuser = userMapper.selectByPrimaryKey(fvirtualcaptualoperation.getFuid());
							for (String string : riskphones) {
								try {
									validateHelper.smsRiskManage(fuser.getFloginname(), string,
											PlatformEnum.BC.getCode(), BusinessTypeEnum.SMS_RISKMANAGE.getCode(),
											"充值", fvirtualcaptualoperation.getFamount(), coinType.getName());
								} catch (Exception e) {
									logger.error("updateCoinCome riskphones err: {}", e);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("mq发送异常",e);
				}
			} else {
				if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
					throw new BCException("更改充值记录失败");
				}
			}
		} else {
			if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
				throw new BCException("更改充值记录失败");
			}
		}
	}
	
	
	
	
	/**
	 * 首次充值判断
	 * 
	 * @param fuid
	 * @return
	 */
	public boolean isFirstCharge(int fuid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fuid", fuid);
		map.put("finouttype", CapitalOperationInOutTypeEnum.IN.getCode());
		map.put("fstatus", CapitalOperationInStatus.Come);
		int countCny = fWalletCapitalOperationMapper.countWalletCapitalOperation(map);
		if (countCny > 0) {
			return false;
		}

		map.clear();
		map.put("fuid", fuid);
		map.put("ftype", VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
		map.put("fstatus", VirtualCapitalOperationInStatusEnum.SUCCESS);
		int countCoin = fVirtualCapitalOperationMapper.countVirtualCapitalOperation(map);

		return (countCny + countCoin) <= 0;
	}

}
