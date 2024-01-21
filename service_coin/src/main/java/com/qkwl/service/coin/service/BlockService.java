package com.qkwl.service.coin.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.CoinCollect;
import com.qkwl.common.dto.coin.FPool;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.USDTCollect;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.coin.mapper.CoinCollectMapper;
import com.qkwl.service.coin.mapper.FPoolMapper;
import com.qkwl.service.coin.mapper.FUserMapper;
import com.qkwl.service.coin.mapper.FUserVirtualAddressMapper;
import com.qkwl.service.coin.mapper.FVirtualCapitalOperationMapper;
import com.qkwl.service.coin.mapper.FWalletCapitalOperationMapper;
import com.qkwl.service.coin.mapper.USDTCollectMapper;
import com.qkwl.service.coin.mapper.UserCoinWalletMapper;
import com.qkwl.service.coin.mapper.UserWalletBalanceLogMapper;
import com.qkwl.service.coin.mapper.UserWhiteListMapper;
import com.qkwl.service.coin.util.JobUtils;
import com.qkwl.service.coin.util.MQSend;
import com.qkwl.service.coin.util.WalletUtils;

@Service("blockService")
@Scope("prototype")
public class BlockService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(BlockService.class);

	@Autowired
	private FVirtualCapitalOperationMapper fVirtualCapitalOperationMapper;
	@Autowired
	private FWalletCapitalOperationMapper fWalletCapitalOperationMapper;
	@Autowired
	private FUserVirtualAddressMapper fUserVirtualAddressMapper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private FPoolMapper poolMapper;
	@Autowired
	private USDTCollectMapper usdtCollectMapper;

	@Autowired
	private JobUtils jobUtils;
	@Autowired
	private ScoreHelper scoreHelper;
	@Autowired
	private ValidateHelper validateHelper;
	
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	
	@Autowired
	private UserWhiteListMapper userWhiteListMapper;
	
	@Autowired
	private CoinCollectMapper coinCollectMapper;
	
	
	@Autowired
	private RechargeServiceTx rechargeServiceTx;
	
	/**
	 * 同步neo区块
	 * */
	public void syncNeoBlock(List<SystemCoinType> coinList) {
		if(coinList == null || coinList.size() == 0) {
			return;
		}
		SystemCoinType neo = null;
		for (SystemCoinType systemCoinType : coinList) {
			if("NEO".equals(systemCoinType.getShortName())) {
				neo = systemCoinType;
				break;
			}
		}
		if(neo == null) {
			logger.error("neo币种类型为空");
			return;
		}
		CoinDriver coinDriver = new CoinDriverFactory.Builder(neo.getCoinType(), neo.getWalletLink(), neo.getChainLink())
				.accessKey(neo.getAccessKey())
				.secretKey(neo.getSecrtKey())
				.assetId(neo.getAssetId())
				.sendAccount(neo.getEthAccount())
				.contractAccount(neo.getContractAccount())
				.shortName(neo.getShortName())
				.builder()
				.getDriver();

		if (coinDriver == null) {
			return;
		}
		//查用户地址
		List<FUserVirtualAddressDTO> selectByCoinId = fUserVirtualAddressMapper.selectByCoinId(neo.getId());
		if(selectByCoinId == null || selectByCoinId.size() == 0) {
			return;
		}
		
		Map<String, Integer> userAdressMap = selectByCoinId.stream().collect(Collectors.toMap(FUserVirtualAddressDTO::getFadderess, FUserVirtualAddressDTO::getFuid));
		
		Map<String, SystemCoinType> coinMap = coinList.stream().collect(Collectors.toMap(SystemCoinType::getContractAccount, SystemCoinType -> SystemCoinType));
		
		String string = jobUtils.get(RedisConstant.BLOCK_NUMBER + neo.getId());
		
		BigInteger bestHeight = coinDriver.getBestHeight();
		
		
		if(bestHeight == null) {
			logger.info(neo.getShortName() + "查询区块为空");
			return;
		}
		
		bestHeight = bestHeight.subtract(BigInteger.valueOf(10));
		
		logger.info(neo.getShortName() + "当前区块：" + bestHeight + ",同步区块：" + string);
		
		BigInteger blockNum = null;
		
		if(!StringUtils.isEmpty(string)) {
			blockNum = new BigInteger(string);
		}else {
			blockNum = bestHeight;
		}
		
		BigInteger i = blockNum;
		for (;;) {
			i = i.add(BigInteger.valueOf(1));
			if(bestHeight.compareTo(i) < 0) {
				break;
			}
			try {
				List<TxInfo> block = coinDriver.getBlock(i);
				if(block == null || block.size() == 0) {
					continue;
				}
				for (TxInfo txInfo : block) {
					Integer uid = userAdressMap.get(txInfo.getAddress());
					if(uid == null) {
						continue;
					}
					SystemCoinType coin = coinMap.get(txInfo.getContract());
					if(coin == null) {
						continue;
					}
					
					//最小充值数
					if(coin.getRechargeMinLimit().compareTo(txInfo.getAmount()) > 0) {
						continue;
					}
					
					// 查询交易记录是否已存在
					List<FVirtualCapitalOperationDTO> fvirtualcaptualoperations = this.fVirtualCapitalOperationMapper
							.selectByTxid(txInfo.getTxid() + "_" + txInfo.getVout());
					if (fvirtualcaptualoperations.size() > 0) {
						continue;
					}
					//如果是从币
					SystemCoinType walletCoinType = coin;
					if(coin.getIsSubCoin()) {
						try {
							walletCoinType = jobUtils.getCoinType(Integer.valueOf(coin.getLinkCoin()));
						} catch (Exception e) {
							logger.error("币种配置有误，coin："+coin.getId() + ",block:"+i);
							break;
						}
					}
					if(walletCoinType == null) {
						break;
					}
					
					
					//构造充值对象
					FVirtualCapitalOperationDTO fvirtualcaptualoperation = new FVirtualCapitalOperationDTO();
					//是否冻结
					boolean isFrozen = false;
					//如果是创新区的就去查询是否是白名单
					if(coin.getIsInnovateAreaCoin()) {
						List<UserWhiteList> selectByUserAndCoinAndType = userWhiteListMapper.selectByUserAndCoinAndType(txInfo.getUid(), coin.getId(), UserWhiteListTypeEnum.RECHARGE_OF_INNOVATION_ZONE.getCode());
						if(selectByUserAndCoinAndType == null || selectByUserAndCoinAndType.size() == 0) {
							isFrozen = true;
						}
					}
					fvirtualcaptualoperation.setFuid(uid);
					fvirtualcaptualoperation.setFamount(txInfo.getAmount());
					fvirtualcaptualoperation.setFfees(BigDecimal.ZERO);
					fvirtualcaptualoperation.setFcoinid(coin.getId());
					fvirtualcaptualoperation.setWalletCoinId(walletCoinType.getId());
					fvirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
					fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
					fvirtualcaptualoperation.setFhasowner(true);
					fvirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
					fvirtualcaptualoperation.setFblocknumber(i);
					fvirtualcaptualoperation.setFconfirmations(0);
					fvirtualcaptualoperation.setFrechargeaddress(txInfo.getAddress().trim());
					fvirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
					fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
					fvirtualcaptualoperation.setVersion(0);
					fvirtualcaptualoperation.setFsource(DataSourceEnum.WEB.getCode());
					fvirtualcaptualoperation.setFuniquenumber(txInfo.getTxid() + "_" + txInfo.getVout());
					fvirtualcaptualoperation.setIsFrozen(isFrozen);
					fvirtualcaptualoperation.setFblocknumber(i);
					//入库
					int result = this.fVirtualCapitalOperationMapper.insert(fvirtualcaptualoperation);
					if (result <= 0) {
						logger.error("Coin updateRecharge insert failed");
					}
				}
			} catch (Exception e) {
				logger.error("neo区块同步异常，block:"+i,e);
			}
		}
		
		jobUtils.setNoExpire(RedisConstant.BLOCK_NUMBER + neo.getId(),bestHeight.toString());
		
	}

	
	/**
	 * 同步区块
	 * */
	public void syncBlock(SystemCoinType coinType) {
		if(coinType == null) {
			return;
		}
		//如果是从币
		SystemCoinType walletCoinType = coinType;
		if(coinType.getIsSubCoin()) {
			try {
				walletCoinType = jobUtils.getCoinType(Integer.valueOf(coinType.getLinkCoin()));
			} catch (Exception e) {
				logger.error("币种配置有误，coin："+coinType.getId());
				return;
			}
		}
		if(walletCoinType == null) {
			return;
		}
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
				.accessKey(coinType.getAccessKey())
				.secretKey(coinType.getSecrtKey())
				.assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount())
				.contractWei(coinType.getContractWei())
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.builder()
				.getDriver();

		if (coinDriver == null) {
			return;
		}
		//查用户地址
		List<FUserVirtualAddressDTO> selectByCoinId = fUserVirtualAddressMapper.selectByCoinId(coinType.getId());
		if(selectByCoinId == null || selectByCoinId.size() == 0) {
			return;
		}
		Map<String, Integer> userAddressMap = selectByCoinId.parallelStream().collect(Collectors.toMap(FUserVirtualAddressDTO::getFadderess, FUserVirtualAddressDTO::getFuid));
		String string = jobUtils.get(RedisConstant.BLOCK_NUMBER + coinType.getId());
		BigInteger bestHeight = coinDriver.getBestHeight();
		if(bestHeight == null) {
			logger.info(coinType.getShortName() + "查询区块为空");
			return;
		}
		logger.info(coinType.getShortName() + "当前区块：" + bestHeight + ",同步区块：" + string);
		
		BigInteger blockNum = null;
		
		if(!StringUtils.isEmpty(string)) {
			blockNum = new BigInteger(string); //Integer.valueOf(string);
		}else {
			blockNum = bestHeight;
		}
		BigInteger i = blockNum;
		for (;;) {
			i = i.add(BigInteger.valueOf(1));
			if(bestHeight.compareTo(i) < 0) {
				break;
			}
			try {
				List<TxInfo> block = coinDriver.getBlock(i);
				if(block == null || block.size() == 0) {
					continue;
				}
				for (TxInfo txInfo : block) {
					Integer uid = userAddressMap.get(txInfo.getAddress());
					if(uid == null) {
						continue;
					}
					
					//最小充值数
					if(coinType.getRechargeMinLimit().compareTo(txInfo.getAmount()) > 0) {
						continue;
					}
					
					// 查询交易记录是否已存在
					List<FVirtualCapitalOperationDTO> fvirtualcaptualoperations = this.fVirtualCapitalOperationMapper.selectByTxid(txInfo.getTxid());
					if (fvirtualcaptualoperations.size() > 0) {
						continue;
					}
					
					//构造充值对象
					FVirtualCapitalOperationDTO fvirtualcaptualoperation = new FVirtualCapitalOperationDTO();
					//是否冻结
					boolean isFrozen = false;
					//如果是创新区的就去查询是否是白名单
					if(coinType.getIsInnovateAreaCoin()) {
						List<UserWhiteList> selectByUserAndCoinAndType = userWhiteListMapper.selectByUserAndCoinAndType(txInfo.getUid(), coinType.getId(), UserWhiteListTypeEnum.RECHARGE_OF_INNOVATION_ZONE.getCode());
						if(selectByUserAndCoinAndType == null || selectByUserAndCoinAndType.size() == 0) {
							isFrozen = true;
						}
					}
					
					fvirtualcaptualoperation.setFuid(uid);
					fvirtualcaptualoperation.setFamount(txInfo.getAmount());
					fvirtualcaptualoperation.setFfees(BigDecimal.ZERO);
					fvirtualcaptualoperation.setFcoinid(coinType.getId());
					fvirtualcaptualoperation.setWalletCoinId(walletCoinType.getId());
					fvirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
					fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
					fvirtualcaptualoperation.setFhasowner(true);
					fvirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
					fvirtualcaptualoperation.setFblocknumber(i);
					fvirtualcaptualoperation.setFconfirmations(0);
					fvirtualcaptualoperation.setFrechargeaddress(txInfo.getAddress().trim());
					fvirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
					fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
					fvirtualcaptualoperation.setVersion(0);
					fvirtualcaptualoperation.setFsource(DataSourceEnum.WEB.getCode());
					if(txInfo.getVout() != null) {
						fvirtualcaptualoperation.setFuniquenumber(txInfo.getTxid() + "_" + txInfo.getVout());
					}else {
						fvirtualcaptualoperation.setFuniquenumber(txInfo.getTxid());
					}
					fvirtualcaptualoperation.setIsFrozen(isFrozen);
					fvirtualcaptualoperation.setFblocknumber(i);
					//入库
					int result = this.fVirtualCapitalOperationMapper.insert(fvirtualcaptualoperation);
					if (result <= 0) {
						logger.error("Coin updateRecharge insert failed");
					}
				}
			} catch (Exception e) {
				logger.error(coinType.getShortName() + "区块同步异常，block:"+i,e);
			}
		}
		jobUtils.setNoExpire(RedisConstant.BLOCK_NUMBER + coinType.getId(),bestHeight.toString());
	}

	

}
