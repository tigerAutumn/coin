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
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.Constant;
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

@Service("coinService")
@Scope("prototype")
public class CoinService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(CoinService.class);

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
	private MQSend mqSend;
	
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	
	@Autowired
	private UserWhiteListMapper userWhiteListMapper;
	
	@Autowired
	private CoinCollectMapper coinCollectMapper;
	
	
	@Autowired
	private RechargeServiceTx rechargeServiceTx;
	

	/**
	 * 定时创建充值订单
	 * 
	 * @param coinType
	 * @throws Exception
	 */
	public void updateRecharge(SystemCoinType coinType) {
		int begin = 0;
		int step = 100;
		boolean is_continue = true;
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
				.accessKey(coinType.getAccessKey())
				.secretKey(coinType.getSecrtKey())
				.assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount())
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.walletAccount(coinType.getWalletAccount())
				.contractWei(coinType.getContractWei())
				.builder()
				.getDriver();
		if (coinDriver == null) {
			return;
		}
		List<TxInfo> txInfos;
		
		Integer walletCoinId = coinType.getId();
		if(coinType.getIsSubCoin()) {
			try {
				walletCoinId = Integer.valueOf(coinType.getLinkCoin());
			} catch (Exception e) {
				logger.error("币种配置错误coinid："+coinType.getId() ,e);
				return ;
			}
		}
		while (is_continue) {
			try {
				txInfos = coinDriver.listTransactions(step, begin);
				begin += step;
				if (txInfos == null || txInfos.size() == 0) {
					is_continue = false;
					continue;
				}
				consumeRechargeTask(txInfos,coinType,walletCoinId);
			} catch (Exception e) {
				logger.error("updateRecharge listTransactions error", e);
				is_continue = false;
				continue;
			}
		}
	}
	
	/**
	 * 处理充值
	 * 
	 * @param txInfos 
	 * @param coinType
	 * @throws Exception
	 */
	public void consumeRechargeTask(List<TxInfo> txInfos,SystemCoinType coinType,Integer walletCoinId) {
		if(txInfos == null || coinType == null) {
			return ;
		}
		//最小充值数
		BigDecimal rechargeMinLimit = coinType.getRechargeMinLimit();
		for (TxInfo txInfo : txInfos) {
			String txid = txInfo.getTxid().trim();
			Date date = Utils.getCurTimeString("2017-08-06 17:40:00");
			if (txInfo.getTime() != null && txInfo.getTime().before(date)) {
				continue;
			}
			// 处理 txid
			if (coinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())) {
				if ("BTC".equals(coinType.getShortName()) || "LTC".equals(coinType.getShortName())
						|| "BCC".equals(coinType.getShortName())) {
					txid = txid + "_" + txInfo.getVout();
				} else {
					txid = txid + "_" + txInfo.getAddress().trim();
				}
			} else if (coinType.getCoinType().equals(SystemCoinSortEnum.MULTI.getCode())) {
				txid = txid + "_" + txInfo.getVout();
			} else if (coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())) {
				txid = txid + "_" + txInfo.getAddress().trim() + "_" + coinType.getAssetId();
			} else if (coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
				txid = txid + "_" + txInfo.getAddress() + "_" + txInfo.getVout();
			}

			// 查询交易记录是否已存在
			List<FVirtualCapitalOperationDTO> fvirtualcaptualoperations = this.fVirtualCapitalOperationMapper
					.selectByTxid(txid);
			if (fvirtualcaptualoperations.size() > 0) {
				continue;
			}

			//构造充值对象
			FVirtualCapitalOperationDTO fvirtualcaptualoperation = new FVirtualCapitalOperationDTO();

			boolean hasOwner = true;
			String address = txInfo.getAddress().trim();
			Integer baseCoinId = coinType.getId();
			
			//ics及mic已停止使用
			if (coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())) {
				SystemCoinType icsCoinType = jobUtils.getCoinTypeShortName("ICS");
				if (icsCoinType == null) {
					logger.error("ICS coinType is null");
					continue;
				}
				baseCoinId = icsCoinType.getId();
			}else if (coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())) {
				SystemCoinType icsCoinType = jobUtils.getCoinTypeShortName("MIC");
				if (icsCoinType == null) {
					logger.error("MIC coinType is null");
					continue;
				}
				baseCoinId = icsCoinType.getId();
			}

			// 判断充值的用户是否存在
			if(rechargeMinLimit.compareTo(txInfo.getAmount()) > 0) {
				txInfo.setUid(null);
				
				//gxs已没有使用
			}else if (coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
				FUser fUser = userMapper.selectByPrimaryKey(txInfo.getUid());
				if (fUser == null) {
					txInfo.setUid(null);
				}
				
				//eos的充值地址只有一个，客户充值使用memo作为地址标签
			}else if (coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode()) || coinType.getCoinType().equals(SystemCoinSortEnum.BTS.getCode())) {
				if(StringUtils.isNumeric(txInfo.getMemo())) {
					try {
						Integer uid = Integer .valueOf(txInfo.getMemo());
						FUser selectByPrimaryKey = userMapper.selectByPrimaryKey(uid);
						if(selectByPrimaryKey != null) {
							txInfo.setUid(uid);
						}
					} catch (Exception e) {
						logger.error("eos.mome is no number,memeo:"+address);
					}
				}
			}else {
				FUserVirtualAddressDTO fvirtualaddresse = this.fUserVirtualAddressMapper
						.selectByCoinAndAddress(baseCoinId, address);
				if (fvirtualaddresse == null) {
					txInfo.setUid(null);
				} else {
					txInfo.setUid(fvirtualaddresse.getFuid());
				}
			}
			
			

			//是否白名单
			boolean isWhiteList = false;
			if (txInfo.getUid() != null) {
				fvirtualcaptualoperation.setFuid(txInfo.getUid());
				//如果是创新区的就去查询是否是白名单
				if(coinType.getIsInnovateAreaCoin()) {
					List<UserWhiteList> selectByUserAndCoinAndType = userWhiteListMapper.selectByUserAndCoinAndType(txInfo.getUid(), coinType.getId(), UserWhiteListTypeEnum.RECHARGE_OF_INNOVATION_ZONE.getCode());
					if(selectByUserAndCoinAndType != null && selectByUserAndCoinAndType.size() > 0) {
						isWhiteList = true;
					}
				}
			} else {
				hasOwner = false;// 没有这个地址，充错进来了？没收！
			}

			fvirtualcaptualoperation.setFamount(txInfo.getAmount());
			fvirtualcaptualoperation.setFfees(BigDecimal.ZERO);
			fvirtualcaptualoperation.setFcoinid(coinType.getId());
			fvirtualcaptualoperation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
			fvirtualcaptualoperation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
			fvirtualcaptualoperation.setFhasowner(hasOwner);
			fvirtualcaptualoperation.setFbtcfees(BigDecimal.ZERO);
			fvirtualcaptualoperation.setFblocknumber(BigInteger.valueOf(0));
			fvirtualcaptualoperation.setFconfirmations(0);
			fvirtualcaptualoperation.setFrechargeaddress(txInfo.getAddress().trim());
			fvirtualcaptualoperation.setFcreatetime(Utils.getTimestamp());
			fvirtualcaptualoperation.setFupdatetime(Utils.getTimestamp());
			fvirtualcaptualoperation.setVersion(0);
			fvirtualcaptualoperation.setFsource(DataSourceEnum.WEB.getCode());
			fvirtualcaptualoperation.setFuniquenumber(txid);
			if(isWhiteList) {
				fvirtualcaptualoperation.setIsFrozen(false);
			}else {
				fvirtualcaptualoperation.setIsFrozen(coinType.getIsInnovateAreaCoin());
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
			fvirtualcaptualoperation.setWalletCoinId(walletCoinId);
			
			//入库
			int result = this.fVirtualCapitalOperationMapper.insert(fvirtualcaptualoperation);
			if (result <= 0) {
				logger.error("Coin updateRecharge insert failed");
			}
		}
	}
	
	
	

	/**
	 * 定时刷新确认数
	 * 
	 * @param coinType
	 * @throws Exception
	 */
	public void updateCoinCome(SystemCoinType coinType) throws Exception {
		int coinid = coinType.getId();

		String accesskey = coinType.getAccessKey();
		String secretkey = coinType.getSecrtKey();
		String walletLink = coinType.getWalletLink();
		String chainLink = coinType.getChainLink();

		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), walletLink, chainLink)
				.accessKey(accesskey)
				.secretKey(secretkey)
				.assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount())
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.walletAccount(coinType.getWalletAccount())
				.contractWei(coinType.getContractWei())
				.builder()
				.getDriver();

		if (coinDriver == null) {
			return;
		}
		List<FVirtualCapitalOperationDTO> fVirtualCapitalOperations = fVirtualCapitalOperationMapper.seletcGoing(coinid,
				coinType.getConfirmations());

		// 遍历
		for (FVirtualCapitalOperationDTO fvirtualcaptualoperation : fVirtualCapitalOperations) {
			if (fvirtualcaptualoperation == null) {
				continue;
			}
			// 确认数
			int Confirmations = 0;
			// 充值数量
			BigDecimal Amount = BigDecimal.ZERO;
			// txid处理，btc类要去调后面的"_"及后面的数字，ruby和neo需要留下，bts类需要吧block添加进去
			String txid = fvirtualcaptualoperation.getFuniquenumber();
			if(coinType.getCoinType() == SystemCoinSortEnum.BTC.getCode()){
				String[] txids = txid.split("_");
				txid = txids[0];
			}else if(coinType.getCoinType() == SystemCoinSortEnum.BTS.getCode()){
				txid = txid + "_" + fvirtualcaptualoperation.getFblocknumber();
			}
			
			if (coinType.getCoinType().equals(SystemCoinSortEnum.ETH.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.MOAC.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.FOD.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.VCC.getCode())) {
				// 获取区块高度并更新
				if (fvirtualcaptualoperation.getFblocknumber().compareTo(BigInteger.valueOf(0)) <= 0) {
					TxInfo etcInfo = null;
					try {
						etcInfo = coinDriver.getTransaction(txid);
					} catch (Exception e) {
						logger.error("查询区块高度异常，" + SystemCoinSortEnum.getShortNameCode(coinType.getCoinType()) +"coin:"+coinid + "txid:"+txid , e);
					}
					if (etcInfo != null) {
						fvirtualcaptualoperation.setFblocknumber(etcInfo.getBlockNumber());
						if (this.fVirtualCapitalOperationMapper.updateByPrimaryKey(fvirtualcaptualoperation) <= 0) {
							logger.error("updatate key failed : " + etcInfo.getBlockNumber());
							throw new Exception();
						}
					}
					continue;
				}
				// 获取确认数,数量
				BigInteger blockNumberCreate = fvirtualcaptualoperation.getFblocknumber();
				Confirmations = coinDriver.getBestHeight().subtract(blockNumberCreate).intValue();
				Amount = fvirtualcaptualoperation.getFamount();
			}  else if (coinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.WICC.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.USDT.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.RUBY.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.NEO.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.BTS.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.MULTI.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.CHAIN33.getCode())
					) {
				TxInfo btcInfo = coinDriver.getTransaction(txid);
				if (btcInfo == null) {
					continue;
				}
				Confirmations = btcInfo.getConfirmations();
				Amount = btcInfo.getAmount();
			} 
			//ics,mic,etp,gxs,目前都没有用了
			else if (coinType.getCoinType().equals(SystemCoinSortEnum.ICS.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.MIC.getCode())) {
				TxInfo btcInfo = coinDriver.getTransaction(txid);
				if (btcInfo == null) {
					continue;
				}
				// 资产类型，3小企股转账，5其它资产转账
				if (!btcInfo.getType().equals(3) && !btcInfo.getType().equals(5)) {
					continue;
				}
				Confirmations = btcInfo.getConfirmations();
				Amount = btcInfo.getAmount();
			}else if (coinType.getCoinType().equals(SystemCoinSortEnum.ETP.getCode())
					|| coinType.getCoinType().equals(SystemCoinSortEnum.GXS.getCode())) {
				BigInteger height = coinDriver.getBestHeight();
				Confirmations = height.subtract(fvirtualcaptualoperation.getFblocknumber()).intValue();
				Amount = fvirtualcaptualoperation.getFamount();
			}
			
			
			
			//如果确认数到了
			if (Confirmations > 0 && Confirmations > fvirtualcaptualoperation.getFconfirmations()) {
				try {
					/*if(Amount != null && BigDecimal.ZERO.compareTo(Amount) != 0) {
						fvirtualcaptualoperation.setFamount(Amount);
					}*/
					rechargeServiceTx.rechargeArrivalAccount(fvirtualcaptualoperation, Confirmations, coinType);
					// 如果是usdt就记录下哪个地址有钱
					if (coinType.getCoinType().equals(SystemCoinSortEnum.USDT.getCode())
							|| coinType.getCoinType().equals(SystemCoinSortEnum.NEO.getCode())
							|| coinType.getCoinType().equals(SystemCoinSortEnum.MULTI.getCode())
							) {
						CoinCollect cc = new CoinCollect();
						Date date = new Date();
						cc.setAddress(fvirtualcaptualoperation.getFrechargeaddress());
						cc.setCoinId(coinType.getId());
						cc.setStatus(CoinCollectStatusEnum.UNCOLLECT.getCode());
						cc.setCreatetime(date);
						cc.setUpdatetime(date);
						coinCollectMapper.insert(cc);
					}
				} catch (Exception e) {
					logger.error("处理充值入帐异常,id:"+ fvirtualcaptualoperation.getFid(),e);
					continue;
				}
			}
		}
	}

	/**
	 * 定时将维基币收集到主账号
	 * @param coinType 
	 */
	public void updateWICCCoinCollect(SystemCoinType coinType) {
		// 查询所有转账
		/*
		 * 轮询 1如果钱包没有钱就返回 2判断钱包是否已经注册 3.没注册进行注册
		 * 4.转账（转账到本地钱包的账户用sendtoaddress不需要手续费，但无法将金额完全转出，用sendtoaddresswithfee可以完全转出，
		 * 但要扣手续费，故使用sendtoaddress进行转账）
		 */
		String accesskey = coinType.getAccessKey();
		String secretkey = coinType.getSecrtKey();
		String walletLink = coinType.getWalletLink();
		String chainLink = coinType.getChainLink();
		String ethAccount = coinType.getEthAccount();

		// 注册手续费
		BigDecimal registfee = new BigDecimal("0.01");
		// 转账手续费
		BigDecimal transactionfee = new BigDecimal("0.0001");

		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), walletLink, chainLink)
				.accessKey(accesskey)
				.secretKey(secretkey)
				.assetId(coinType.getAssetId())
				.sendAccount(ethAccount)
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.builder().getDriver();

		if (coinDriver == null) {
			return;
		}
		try {
			JSONArray listaddress = coinDriver.listaddress();
			Iterator<Object> iterator = listaddress.iterator();
			while (iterator.hasNext()) {
				JSONObject next = (JSONObject) iterator.next();
				BigDecimal balance = (BigDecimal) next.get("balance");
				if (balance.compareTo(transactionfee) <= 0) {
					continue;
				}
				String addr = (String) next.get("addr");
				if (coinType.getEthAccount().equals(addr)) {
					continue;
				}
				String regid = (String) next.get("regid");
				if (StringUtils.isEmpty(regid.trim())) {
					String hash = coinDriver.registaddress(addr, registfee);
					if (StringUtils.isEmpty(hash)) {
						logger.error("地址 " + addr + " 激活失败");
						continue;
					}
					logger.info("地址 " + addr + " 激活,hash:" + hash);
					BigDecimal subtract = balance.subtract(registfee);
					if (subtract.compareTo(transactionfee) <= 0) {
						continue;
					}
					subtract = subtract.subtract(transactionfee);
					coinDriver.sendToAddress(addr, ethAccount, subtract, "", transactionfee);
				} else {
					if (balance.compareTo(transactionfee) <= 0) {
						continue;
					}
					BigDecimal subtract = balance.subtract(transactionfee);
					coinDriver.sendToAddress(addr, ethAccount, subtract, "", transactionfee);
				}
			}
		} catch (Exception e) {
			logger.error("coinService.updateWICCCoinCollect 异常", e);
		}
	}
	
/*	*//**
	 * 定时将usdt收集到主账号
	 * @param coinType
	 *//*
	public void updateUSDTCoinCollect(SystemCoinType coinType,SystemCoinType coinTypebtc) {
		
		logger.debug("开始收集usdt  ======>");

		// 查询还没进行收集的地址（由于交易提交后，钱包还未能查询到到账的钱，大概不到半个钟后，交易被确认了，就能查询到钱，故查询一小时前的转账地址）
		// 查询地址余额

		
		 * //收集 
		 * 如果成功，更新状态，结束 
		 * 如果失败，充值btc，更新表状态，等待下次收集，结束
		 
		String accesskey = coinType.getAccessKey();
		String secretkey = coinType.getSecrtKey();
		String walletLink = coinType.getWalletLink();
		String chainLink = coinType.getChainLink();
		String ethAccount = coinType.getEthAccount();
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), walletLink, chainLink)
				.accessKey(accesskey)
				.secretKey(secretkey)
				.assetId(coinType.getAssetId())
				.sendAccount(ethAccount)
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.builder().getDriver();
		boolean issend = true;
		BigDecimal transferNeedBtc = null;
		BigDecimal riskManagement = null;
		BigDecimal minimumCollection = null;
		try {
			// 转账一些btc去进行收集
			transferNeedBtc = coinDriver.estimatesmartfee(10);
			
			//风控阈值
			String systemArgs = jobUtils.getSystemArgs(ArgsConstant.USDTRiskManagement);
			if(!StringUtils.isEmpty(systemArgs)) {
				riskManagement = new BigDecimal(systemArgs);
			}else {
				riskManagement = new BigDecimal(0.0002);
			}
			//usdt最小收集数
			String systemArgs2 = jobUtils.getSystemArgs(ArgsConstant.USDTMinimumCollection);
			if(!StringUtils.isEmpty(systemArgs2)) {
				minimumCollection = new BigDecimal(systemArgs2);
			}else {
				minimumCollection = new BigDecimal(5);
			}
			if(transferNeedBtc == null) {
				return;
			}
			if(transferNeedBtc.compareTo(riskManagement) > 0) {
				issend = false;
			}
		} catch (Exception e) {
			logger.error("updateUSDTCoinCollect 异常",e);
		}
		
		
		String accesskeybtc = coinTypebtc.getAccessKey();
		String secretkeybtc = coinTypebtc.getSecrtKey();
		String btcwallet = coinTypebtc.getWalletLink();
		String bctchain = coinTypebtc.getChainLink();
		String ethAccountbtc = coinTypebtc.getEthAccount();
		// get CoinDriver
		CoinDriver coinDriverbtc = new CoinDriverFactory.Builder(coinTypebtc.getCoinType(), btcwallet, bctchain)
				.accessKey(accesskeybtc)
				.secretKey(secretkeybtc)
				.assetId(coinTypebtc.getAssetId())
				.sendAccount(ethAccountbtc)
				.contractAccount(coinTypebtc.getCoinTypeName())
				.shortName(coinTypebtc.getShortName())
				.builder().getDriver();
		if (coinDriver == null) {
			return;
		}
		int begin = 0;
		int step = 100;
		boolean is_continue = true;

		while (is_continue) {
			try {
				//查询15分钟前未进行收集的usdt地址
				List<USDTCollect> selectUnCollectlist = usdtCollectMapper.selectUnCollectList(begin, step);
				if (selectUnCollectlist.size() <= step) {
					is_continue = false;
				}
				
				for (USDTCollect usdtCollect : selectUnCollectlist) {
					logger.info("开始收集地址："+usdtCollect.getAddress());
					String address = usdtCollect.getAddress();
					BigDecimal balance = coinDriver.getBalance(address);
					if(balance == null) {
						continue;
					}
					if(balance.compareTo(minimumCollection) <= 0) {
						usdtCollect.setUpdatetime(Utils.getTimestamp());
						usdtCollect.setStatus(USDTCollectStatusEnum.FINISHED.getCode());
						if(usdtCollectMapper.updateByPrimaryKey(usdtCollect) == 0) {
							logger.error("usdtcollect 更改状态失败，id："+ usdtCollect.getId());
						}
						continue;
					}
					logger.info("开始转账，address："+ address + "余额" + balance);
					JSONObject sendToAddress = coinDriver.sendToAddress(address, ethAccount, balance , "", BigDecimal.ZERO, 31);
					if(sendToAddress == null) {
						logger.info("usdtcollect 发送usdt失败，返回空，{id："+ usdtCollect.getId()+",address:"+address+",ethAccount"+ethAccount+",balance:"+balance+"}");
						continue;
					}
					String string = sendToAddress.getString("error");
					//发送成功
					if(StringUtils.isEmpty(string) || "null".equals(string)) {
						logger.info("address:"+address + ",转账成功");
						usdtCollect.setUpdatetime(Utils.getTimestamp());
						usdtCollect.setStatus(USDTCollectStatusEnum.FINISHED.getCode());
						if(usdtCollectMapper.updateByPrimaryKey(usdtCollect) == 0) {
							logger.error("usdtcollect 更改状态失败，id："+ usdtCollect.getId());
						}
						continue;
					}
					if(usdtCollect.getIsrechargebtc()) {
						logger.error("usdt地址已充值btc，但转账失败，id："+ usdtCollect.getId());
						continue;
					}
					if(!issend) {
						continue;
					}
					String sendToAddress2 = coinDriverbtc.sendToAddress(address, transferNeedBtc, "", new BigDecimal("0.00001"));
					if(!StringUtils.isEmpty(sendToAddress2)) {
						usdtCollect.setIsrechargebtc(true);
						usdtCollect.setRechargebtc(transferNeedBtc);
						usdtCollect.setUpdatetime(Utils.getTimestamp());
						if(usdtCollectMapper.updateByPrimaryKey(usdtCollect) == 0) {
							logger.error("usdtcollect 更改状态失败，id："+ usdtCollect.getId());
						}
					}
				}
			} catch (Exception e) {
				logger.error("updateUSDTCoinCollect异常",e);
			}
			
		}
		logger.debug("usdt收集结束");
	}*/

	
	//修正fod地址
	//@PostConstruct
	public void updateFODAddress() {
		logger.info("开始修改用户fod地址");
		List<FUserVirtualAddressDTO> useraddressList = fUserVirtualAddressMapper.selectByCoinId(28);
		for(FUserVirtualAddressDTO f:useraddressList) {
			String address = f.getFadderess();
			if(!address.startsWith("0x")) {
				continue;
			}
			address = CoinCommentUtils.FODEncode(address);
			f.setFadderess(address);
			fUserVirtualAddressMapper.updateAdressByPrimaryKey(f);
		}
		logger.info("开始修改fod地址");
		List<FPool> poolList = poolMapper.selectByCoinId(28);
		for(FPool f:poolList) {
			String address = f.getFaddress();
			if(!address.startsWith("0x")) {
				continue;
			}
			address = CoinCommentUtils.FODEncode(address);
			f.setFaddress(address);
			poolMapper.updateAdressByPrimaryKey(f);
		}
		logger.info("修改fod地址结束");
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
