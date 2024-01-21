package com.qkwl.web.front.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.capital.CoinOperationOrderDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserCapitalService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class ETCController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ETCController.class);

    @Autowired
    private IUserCapitalService userCapitalService;

    @Autowired
    private RedisHelper redisHelper;

    /**
     * etc充值回调
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/coin/etc/recharge",method = {RequestMethod.GET,RequestMethod.POST})
    public String etcRecharge(
            @RequestParam(required = false, defaultValue = "") String txhash,
            @RequestParam(required = false, defaultValue = "") String to,
            @RequestParam(required = false, defaultValue = "") String value,
            @RequestParam(required = false, defaultValue = "") String sign,
            @RequestParam(required = false, defaultValue = "") String symbol) {

        //判断参数
        if ("".equals(txhash) || "".equals(to) || "".equals(value) || "".equals(sign) || "".equals(symbol)) {  //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            return ReturnMsg(10001, I18NUtils.getString("ETCController.0")); 
        }
        SystemCoinType coinType = redisHelper.getCoinTypeShortNameSystem(symbol.toUpperCase());
        if (coinType == null) {
            return ReturnMsg(10001, I18NUtils.getString("ETCController.6")); 
        }
        Integer walletCoinId = coinType.getId();
        if(coinType.getIsSubCoin()) {
        	try {
        		walletCoinId = Integer.valueOf(coinType.getLinkCoin());
			} catch (Exception e) {
				logger.error("充值推送异常",e);
				return ReturnMsg(10001, I18NUtils.getString("ETCController.6")); 
			}
        }
        SystemCoinType baseCoinType = null;
        if (coinType.getShortName().equals("ETC")) { 
            baseCoinType = coinType;
        } else if (coinType.getCoinType().equals(SystemCoinSortEnum.ETC.getCode())) {
            baseCoinType = redisHelper.getCoinTypeShortName("ETC"); 
        }
        if (baseCoinType == null) {
            return ReturnMsg(10001, I18NUtils.getString("ETCController.9")); 
        }
        //验证签名
        try {
            String signVerify = txhash;
            // get CoinDriver
            CoinDriver coinDriver = new CoinDriverFactory.Builder(coinType.getCoinType(), coinType.getWalletLink(), coinType.getChainLink())
                    .builder()
                    .getDriver();
            if (coinDriver == null) {
                return ReturnMsg(10001, I18NUtils.getString("ETCController.10")); 
            }
            signVerify = coinDriver.getETCSHA3(signVerify);
            if (!signVerify.equals(sign)) {
                return ReturnMsg(10001, I18NUtils.getString("ETCController.11")); 
            }
        } catch (Exception e) {
            logger.error("etcRecharge sign error，symbol：{},e:{}", symbol,e); 
            return ReturnMsg(10001, I18NUtils.getString("ETCController.13")); 
        }
        BigDecimal amount = new BigDecimal(value);
        
        //最小充值数
        if(coinType.getRechargeMinLimit().compareTo(amount) > 0 ) {
        	return ReturnMsg(10000, I18NUtils.getString("ETCController.14")); 
        }
        
        Boolean risk = coinType.getRiskNum() != null && coinType.getRiskNum().compareTo(BigDecimal.ZERO) > 0 && coinType.getRiskNum().compareTo(amount) <= 0;
        CoinOperationOrderDTO order = new CoinOperationOrderDTO();
        order.setAddress(to);
        order.setTxId(txhash);
        order.setBaseCoinId(baseCoinType.getId());
        order.setCoinId(coinType.getId());
        order.setAmount(amount);
        order.setRisk(risk);
        order.setCoinName(coinType.getShortName());
        order.setOperationType(VirtualCapitalOperationTypeEnum.COIN_IN);
        order.setDataSource(DataSourceEnum.WEB);
        order.setPlatform(PlatformEnum.BC);
        order.setIp(""); 
        order.setIsFrozen(coinType.getIsInnovateAreaCoin());
        order.setWalletCoinId(walletCoinId);
        try {
            Result result = userCapitalService.createCoinOperationOrder(order);
            if (!result.getSuccess()) {
                return ReturnMsg(10001, I18NUtils.getString("ETCController.16")); 
            }
        } catch (Exception e) {
            logger.error("etcRecharge 服务器错误，symbol：{},e:{}", symbol,e); 
            return ReturnMsg(10001, I18NUtils.getString("ETCController.18")); 
        }

        return ReturnMsg(10000, I18NUtils.getString("ETCController.19")); 
    }

    /**
     * 返回参数封装
     */
    private String ReturnMsg(int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resultCode", code); 
        jsonObject.put("resultMsg", msg); 
        return jsonObject.toString();
    }
}
