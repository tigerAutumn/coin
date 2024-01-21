package com.qkwl.service.coin.controller;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.coin.service.RechargePushService;



@Controller
public class CoinController {
	private static final Logger logger = LoggerFactory.getLogger(CoinController.class);
	
	@Autowired
    private RedisHelper redisHelper;
	
	@Autowired
	private RechargePushService rechargePushService;
	
    @ResponseBody
	@RequestMapping("/v1/coin/recharge")
    public ReturnResult recharge(@RequestBody(required = true) JSONObject depositRecordVo) {
    	if(depositRecordVo == null) {
    		return ReturnResult.FAILUER("充值记录为空");
    	}
    	try {
    		String symbol = depositRecordVo.getString("symbol");
        	SystemCoinType coinType = redisHelper.getCoinTypeShortName(symbol);
    		boolean handlingRechargePush = rechargePushService.handlingRechargePush(coinType, createRecord(coinType, depositRecordVo));
    		if(handlingRechargePush) {
    			return ReturnResult.SUCCESS();
    		}else {
    			return ReturnResult.FAILUER("入库失败");
    		}
		} catch (Exception e) {
			if(e instanceof BCException) {
				if(((BCException) e).getCode() != null) {
					return ReturnResult.FAILUER(((BCException) e).getCode(), e.getMessage());
				}
				return ReturnResult.FAILUER(e.getMessage());
			}else {
				logger.error("充值处理异常,param:"+depositRecordVo.toJSONString(),e);
			}
			return ReturnResult.FAILUER(ReturnResult.FAILURE_SYSTEM_UPDATING,"系统异常");
		}
    }

    
    @ResponseBody
	@RequestMapping("/v1/coin/confirmRecharge")
    public ReturnResult confirmRecharge(@RequestBody(required = true) JSONObject depositRecordVo) {
    	if(depositRecordVo == null) {
    		return ReturnResult.FAILUER("充值记录为空");
    	}
    	try {
    		String symbol = depositRecordVo.getString("symbol");
        	SystemCoinType coinType = redisHelper.getCoinTypeShortName(symbol);
    		boolean handlingRechargePush = rechargePushService.handlingConfirmRecharge(coinType, createRecord(coinType, depositRecordVo));
    		if(handlingRechargePush) {
    			return ReturnResult.SUCCESS();
    		}else {
    			return ReturnResult.FAILUER("入库失败");
    		}
		} catch (Exception e) {
			if(e instanceof BCException) {
				if(((BCException) e).getCode() != null) {
					return ReturnResult.FAILUER(((BCException) e).getCode(), e.getMessage());
				}
				return ReturnResult.FAILUER(e.getMessage());
			}else {
				logger.error("充值处理异常,param:"+depositRecordVo.toJSONString(),e);
			}
			return ReturnResult.FAILUER(ReturnResult.FAILURE_SYSTEM_UPDATING,"系统异常");
		}
    }
    
    
    private FVirtualCapitalOperationDTO createRecord(SystemCoinType coinType,JSONObject depositRecordVo) throws BCException {
    	if(coinType == null) {
    		throw new BCException(ReturnResult.FAULURE_USER_NOT_LOGIN,"币种不存在");
    	}
    	//不使用新的推送方式
    	if(!coinType.getUseNewWay()) {
    		throw new BCException(ReturnResult.FAULURE_PARAMTER_REQUIRED,"该币种没有开启钱包机充值推送");
    	}
    	if(!coinType.getIsRecharge()) {
    		throw new BCException(ReturnResult.FAULURE_USER_NOT_LOGIN,"当前币种不允许充值");
    	}
    	
    	//用于查询地址
    	SystemCoinType coinTypeByCoinSort = redisHelper.getCoinTypeByCoinSort(coinType.getId());
    	if(coinTypeByCoinSort == null) {
    		throw new BCException("基础币种不存在");
    	}
    	SystemCoinType walletCoinType = coinType;
    	if(coinType.getIsSubCoin()) {
    		try {
    			walletCoinType = redisHelper.getCoinType(Integer.valueOf(coinType.getLinkCoin()));
    			if(walletCoinType == null) {
    				throw new BCException("后台币种配置错误");
    			}
			} catch (Exception e) {
				logger.error("充值推送异常,coinType:"+coinType.getId(),e);
				throw new BCException("后台币种配置错误");
			}
    	}
    	
    	String address = depositRecordVo.getString("to");
    	if(StringUtils.isEmpty(address)) {
    		throw new BCException("充币地址不存在");
    	}
    	String hash = depositRecordVo.getString("hash");
    	if(StringUtils.isEmpty(hash)) {
    		throw new BCException("充值hash不存在");
    	}
    	BigDecimal amount = depositRecordVo.getBigDecimal("amount");
    	if(amount == null || BigDecimal.ZERO.compareTo(amount) >= 0) {
    		throw new BCException("充值金额不存在或小于0");
    	}
    	amount = amount.setScale(MathUtils.DEF_DIV_SCALE,BigDecimal.ROUND_HALF_UP) ;
    	
    	BigInteger blockNumber = depositRecordVo.getBigInteger("blockNumber");
    	if(blockNumber == null) {
    		throw new BCException("区块高度不存在");
    	}
    	Integer confirmNumber = depositRecordVo.getInteger("confirmNumber");
    	if(confirmNumber == null) {
    		confirmNumber = 0;
    	}
    	String memo = depositRecordVo.getString("memo");
    	
    	//fod特殊处理
    	if(coinType.getCoinType().equals(SystemCoinSortEnum.FOD.getCode())) {
    		address = CoinCommentUtils.FODEncode(address);
    		hash = CoinCommentUtils.FODEncode(hash);
    	}else if(coinType.getCoinType().equals(SystemCoinSortEnum.BTC.getCode()) || coinType.getCoinType().equals(SystemCoinSortEnum.SERO.getCode())) {
    		//比特币类的特殊处理
    		Integer txIndex = depositRecordVo.getInteger("txIndex");
    		if(txIndex == null) {
    			throw new BCException("交易地址索引不能为空");
    		}
    		hash = hash + "_" + txIndex;
    	}
    	
    	//eos及bts的特殊处理
    	if(coinType.getIsUseMemo() && StringUtils.isEmpty(memo)) {
    		throw new BCException("地址标签不能为空");
    	}
    	
    	FVirtualCapitalOperationDTO fvco = new FVirtualCapitalOperationDTO();
    	fvco.setFamount(amount);
    	fvco.setFblocknumber(blockNumber);
    	fvco.setFcoinid(coinType.getId());
    	fvco.setFconfirmations(confirmNumber);
    	fvco.setFuniquenumber(hash);
    	fvco.setMemo(memo);
    	Date date = new Date();
    	fvco.setFcreatetime(date);
    	fvco.setFupdatetime(date);
    	fvco.setVersion(0);
    	fvco.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
    	fvco.setFfees(BigDecimal.ZERO);
    	fvco.setFbtcfees(BigDecimal.ZERO);
    	fvco.setAddressCoinid(coinTypeByCoinSort.getId());
    	fvco.setWalletCoinId(walletCoinType.getId());
    	fvco.setFrechargeaddress(address);
    	fvco.setFplatform(PlatformEnum.BC.getCode());
    	fvco.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
    	return fvco;
    }

}
