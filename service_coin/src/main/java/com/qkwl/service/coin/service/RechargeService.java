package com.qkwl.service.coin.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.coin.TxInfo;
import com.qkwl.common.dto.Enum.CoinCollectStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.USDTCollectStatusEnum;
import com.qkwl.common.dto.capital.FUserVirtualAddressDTO;
import com.qkwl.common.dto.coin.CoinCollect;
import com.qkwl.common.dto.coin.CoinCollectHistory;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.USDTCollect;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.service.coin.mapper.CoinCollectHistoryMapper;
import com.qkwl.service.coin.mapper.CoinCollectMapper;
import com.qkwl.service.coin.mapper.FUserMapper;
import com.qkwl.service.coin.mapper.FUserVirtualAddressMapper;
import com.qkwl.service.coin.mapper.FVirtualCapitalOperationMapper;
import com.qkwl.service.coin.mapper.FWalletCapitalOperationMapper;
import com.qkwl.service.coin.mapper.UserCoinWalletMapper;
import com.qkwl.service.coin.util.JobUtils;
import com.qkwl.service.coin.util.MQSend;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service("rechargeService")
@Scope("prototype")
public class RechargeService {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(RechargeService.class);

	@Autowired
	private FVirtualCapitalOperationMapper fVirtualCapitalOperationMapper;
	@Autowired
	private FWalletCapitalOperationMapper fWalletCapitalOperationMapper;
	@Autowired
	private FUserVirtualAddressMapper fUserVirtualAddressMapper;
	@Autowired
	private FUserMapper userMapper;
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
	private RechargeServiceTx rechargeServiceTx;
	
	@Autowired
	private CoinCollectHistoryMapper coinCollectHistoryMapper;

	/**
	 * 创建充值订单
	 * 
	 * @param coinType
	 * @throws Exception
	 */
	public void updateRechargeByCoinAddress(SystemCoinType coinType) {
		SystemCoinType baseCoinType = null; 
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
				.accessKey(coinType.getAccessKey())
				.secretKey(coinType.getSecrtKey())
				.assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount())
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.builder()
				.getDriver();

		if (coinDriver == null) {
			return;
		}
		if(coinType.getCoinType() == SystemCoinSortEnum.BWT.getCode()) {
			baseCoinType = jobUtils.getCoinType("BWT");
		}else {
			baseCoinType = coinType;
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
		
		
		//查询所有充值地址
		List<FUserVirtualAddressDTO> selectByCoinId = fUserVirtualAddressMapper.selectByCoinId(baseCoinType.getId());
		
		for (FUserVirtualAddressDTO fUserVirtualAddressDTO : selectByCoinId) {
			//到链上查询这个地址的充值交易
			List<TxInfo> listTransactionsByAddress = coinDriver.listTransactionsByAddress(1000, 1, fUserVirtualAddressDTO.getFadderess());
			if(listTransactionsByAddress == null || listTransactionsByAddress.size() == 0 ) {
				continue;
			}
			for (TxInfo txInfo : listTransactionsByAddress) {
				try {
					rechargeServiceTx.handleRechargeTask(fUserVirtualAddressDTO,txInfo,coinType,walletCoinType);
				} catch (Exception e) {
					logger.error("处理充值异常",e);
				}
				
			}
		}
	}
	
	
	
	/**
	 * 充地址上收集币到提现地址
	 * 
	 * @param coinType
	 * @throws Exception
	 */
	public void updateCollect(SystemCoinType coinType) {
		
		List<CoinCollect> selectByCoinidAndStatus = coinCollectMapper.selectByCoinidAndStatus(coinType.getId(), CoinCollectStatusEnum.UNCOLLECT.getCode());
		
		if(selectByCoinidAndStatus == null || selectByCoinidAndStatus.size() <= 0) {
			return;
		}
		
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
				.accessKey(coinType.getAccessKey())
				.secretKey(coinType.getSecrtKey())
				.assetId(coinType.getAssetId())
				.sendAccount(coinType.getEthAccount())
				.contractAccount(coinType.getContractAccount())
				.shortName(coinType.getShortName())
				.builder()
				.getDriver();
		for (CoinCollect coinCollect : selectByCoinidAndStatus) {
			//查询余额
			BigDecimal balance = coinDriver.getBalance(coinCollect.getAddress());
			if(balance == null) {
				continue;
			}
			//bwt 激活时转入12bwt
			if("BWT".equals(coinType.getShortName().toUpperCase())) {
				balance = MathUtils.sub(balance, new BigDecimal(12));
			}else if(coinType.getCoinType().equals(SystemCoinSortEnum.TCP.getCode())) {
				//扣下转账手续费
				balance = MathUtils.sub(balance, new BigDecimal(0.01));
			}
			
			if(BigDecimal.ZERO.compareTo(balance) < 0) {
				//如果地址有钱就进行收集
				String hash = coinDriver.sendToAddress(coinCollect.getAddress(), coinType.getEthAccount(), balance, null, null, coinCollect.getSecret());
				if(StringUtils.isEmpty(hash)) {
					logger.error("收集失败，address"+coinCollect.getAddress());
					continue;
				}
			}
			finishCoinCollect(coinCollect);
		}
	}
	
	
	/**
	 * 定时将usdt收集到主账号
	 * @param coinType
	 */
	public void updateUSDTCoinCollect(SystemCoinType coinType,SystemCoinType coinTypebtc) {
		logger.info("开始收集usdt  ======>");
		//请求外部接口查询最优手续费
		BigDecimal satoshiPerByte = new BigDecimal(80);
		BigDecimal fastestFee = null;
        BigDecimal halfHourFee = null;
        BigDecimal hourFee = null;
		try {
			OkHttpClient client = new OkHttpClient.Builder()
	                .connectTimeout(10, TimeUnit.SECONDS)
	                .writeTimeout(10, TimeUnit.SECONDS)
	                .readTimeout(10, TimeUnit.SECONDS)
	                .build();
	        Response response = client.newCall(new Request.Builder()
				        .url("https://bitcoinfees.earn.com/api/v1/fees/recommended")
				        .build())
				        .execute();
	        String string = response.body().string();
	        logger.info("手续费"+string);
	        //返回：{"fastestFee":86,"halfHourFee":76,"hourFee":38}
	        //fastestFee ：最低费用（每个字节的satoshis），目前将导致最快的交易确认（通常为0到1个块延迟）。
	        //halfHourFee ：最低费用（每个字节的satoshis）将在半小时内确认交易（概率为90％）。
	        //hourFee ：最低费用（每个字节的satoshis）将在一小时内确认交易（概率为90％）。
	        fastestFee = JSON.parseObject(string).getBigDecimal("fastestFee");
	        halfHourFee = JSON.parseObject(string).getBigDecimal("halfHourFee");
	        hourFee = JSON.parseObject(string).getBigDecimal("hourFee");
		} catch (Exception e) {
			logger.error("请求手续费接口异常",e);
		} 

		// 查询还没进行收集的地址
		// 查询地址余额

		/*
		 * //收集 
		 * 如果成功，更新状态，结束 
		 * 如果失败，等待下次收集，结束
		 */
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
		BigDecimal minimumCollection = new BigDecimal(5);
		BigDecimal riskManagement = BigDecimal.valueOf(0.0005);
		try {
			//usdt最小收集数
			String systemArgs2 = jobUtils.getSystemArgs(ArgsConstant.USDTMinimumCollection);
			if(!StringUtils.isEmpty(systemArgs2)) {
				minimumCollection = new BigDecimal(systemArgs2);
			}
			//usdt风控
			String USDTRiskManagement = jobUtils.getSystemArgs(ArgsConstant.USDTRiskManagement);
			if(!StringUtils.isEmpty(USDTRiskManagement)) {
				riskManagement = new BigDecimal(USDTRiskManagement);
				if(fastestFee != null) {
					//根据每笔交易大概408byte计算，选择合适的satoshiPerByte
					BigDecimal div = MathUtils.div(riskManagement, BigDecimal.valueOf(0.00000408));
					if(div.compareTo(fastestFee) >= 0) {
						satoshiPerByte = fastestFee;
					}else if(div.compareTo(halfHourFee) >= 0){
						satoshiPerByte = fastestFee;
					}else if(div.compareTo(hourFee) >= 0){
						satoshiPerByte = fastestFee;
					}else {
						logger.error("当前手续费太高，风控："+div.setScale(2, BigDecimal.ROUND_HALF_UP)+"不收集");
						return ;
					}
				}
			}
		} catch (Exception e) {
			logger.error("updateUSDTCoinCollect 异常",e);
		}
		
		//查询已经有区块确认的地址
		List<CoinCollect> selectByCoinidAndStatus = coinCollectMapper.selectByCoinidAndStatus(coinType.getId(), CoinCollectStatusEnum.UNCOLLECT.getCode());
		
		if(selectByCoinidAndStatus == null || selectByCoinidAndStatus.size() == 0) {
			return;
		}
		
		for (CoinCollect usdtCollect : selectByCoinidAndStatus) {
			try {
				//logger.info("开始收集地址："+usdtCollect.getAddress());
				String address = usdtCollect.getAddress();
				BigDecimal balance = coinDriver.getBalance(address);
				if(balance == null) {
					continue;
				}
				if(balance.compareTo(minimumCollection) <= 0) {
					finishCoinCollect(usdtCollect);
					continue;
				}
				logger.info("开始转账，address："+ address + "余额" + balance);
				
				ReturnResult usdtsendToAddress = coinDriver.sendToAddress(address, ethAccount, balance , null, riskManagement, 31,satoshiPerByte);
				
				if(usdtsendToAddress.getCode() == 200) {
					logger.info("address:"+address + ",转账成功:"+usdtsendToAddress.getMsg());
					finishCoinCollect(usdtCollect);
					continue;
				}else {
					logger.info("转账失败，address："+ address + ","+usdtsendToAddress.getMsg());
				}
				
				/*JSONObject sendToAddress = null;
				if(sendToAddress == null) {
					logger.info("usdtcollect 发送usdt失败，返回空，{id："+ usdtCollect.getId()+",address:"+address+",ethAccount"+ethAccount+",balance:"+balance+"}");
					continue;
				}
				String string = sendToAddress.getString("error");
				//发送成功
				if(StringUtils.isEmpty(string) || "null".equals(string)) {
					logger.info("address:"+address + ",转账成功");
					finishCoinCollect(usdtCollect);
					continue;
				}*/
				
				/*if(usdtCollect.getIsRechargeFuelCoin()) {
					logger.error("usdt地址已充值btc，但转账失败，id："+ usdtCollect.getId());
					continue;
				}
				String sendToAddress2 = coinDriverbtc.sendToAddress(address, transferNeedBtc, "", new BigDecimal("0.00001"));
				if(!StringUtils.isEmpty(sendToAddress2)) {
					usdtCollect.setIsRechargeFuelCoin(true);
					usdtCollect.setRechargeFuelCoin(transferNeedBtc);
					usdtCollect.setUpdatetime(Utils.getTimestamp());
					if(coinCollectMapper.updateByPrimaryKey(usdtCollect) == 0) {
						logger.error("usdtcollect 更改状态失败，id："+ usdtCollect.getId());
					}
				}*/
			} catch (Exception e) {
				logger.error("updateUSDTCoinCollect异常",e);
			}
		}
		
	}
	
	
	/**
	 * 删除记录，入历史表
	 * 
	 * @param coinCollect
	 * @return
	 */
	public void finishCoinCollect(CoinCollect coinCollect) {
		coinCollect.setUpdatetime(Utils.getTimestamp());
		coinCollect.setStatus(USDTCollectStatusEnum.FINISHED.getCode());
		CoinCollectHistory convert = PojoConvertUtil.convert(coinCollect, CoinCollectHistory.class);
		coinCollectMapper.deleteByAddress(coinCollect.getAddress(),coinCollect.getCoinId());
		coinCollectHistoryMapper.insert(convert);
	}
}
