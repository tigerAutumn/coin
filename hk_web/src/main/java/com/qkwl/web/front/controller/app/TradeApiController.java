package com.qkwl.web.front.controller.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.qkwl.web.utils.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserFavoriteTrade;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserFavoriteService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.config.MqttProperties;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 订单相关接口，包括：下单、撤单、获取订单列表、当前订单、历史订单等。
 */
@Controller
@ApiIgnore
public class TradeApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(TradeApiController.class);

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private IUserWalletService userWalletService;

    @Autowired
    private IUserService userService;
    
    @Autowired
    private MqttProperties mqttProperties;
    
    @Autowired
   	private IUserFavoriteService userFavoriteService;
    /**
     * 交易区
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PassToken
    @ApiOperation("交易区")
	@RequestMapping(value="/v1/market/area",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeMarket(){
        SystemTradeTypeEnum[] values = SystemTradeTypeEnum.values();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.length; i++) {
            SystemTradeTypeEnum trade =  values[i];
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",trade.getCode()); 
            jsonObject.put("name",trade.getSymbol()); 
            jsonArray.add(i,jsonObject);
        }
        return ReturnResult.SUCCESS(jsonArray);
    }
    
    /**
     * 交易区
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PassToken
    @ApiOperation("交易区")
	@RequestMapping(value="/v1/market/areas",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeMarkets(){
    	try {
            String marketAreasOrder = redisHelper.getSystemArgs("market_areas_order");

            String[] marketAreasOrderArray = null;
            if (org.apache.commons.lang3.StringUtils.isBlank(marketAreasOrder)) {
                marketAreasOrderArray = Stream.of(SystemTradeTypeNewEnum.values())
                        .map(e -> String.valueOf(e.getCode())).toArray(String[]::new);
            } else {
                marketAreasOrderArray = marketAreasOrder.split(",");
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < marketAreasOrderArray.length; i++) {
                SystemTradeTypeNewEnum trade =
                        SystemTradeTypeNewEnum.getEnum(Integer.valueOf(marketAreasOrderArray[i].trim()));
                if (trade == null) {
                    continue;
                }

                JSONObject jsonObject = null;
                switch (trade) {
                    case BTC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getBtcTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case ETH:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getEthTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case GAVC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getGsetTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case INNOVATION_AREA:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getInnovateTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case USDT:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getUsdtTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case FAVORITE:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonArray.add(jsonObject);
                    default:
                        break;
                }


            }
            return ReturnResult.SUCCESS(jsonArray);
		} catch (Exception e) {
			logger.error("请求 /v1/market/areas 错误" + e); 
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.10")); 
		}
        
    }
    
    /**
     * 主板交易区(app改版)
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PassToken
    @ApiOperation("主板交易区(app改版)")
	@RequestMapping(value="/v2/market/areas",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeMainMarkets(){
    	try {
            String marketAreasOrder = redisHelper.getSystemArgs("market_areas_order");

            String[] marketAreasOrderArray = null;
            if (org.apache.commons.lang3.StringUtils.isBlank(marketAreasOrder)) {
                marketAreasOrderArray = Stream.of(SystemTradeTypeNewEnum.values())
                        .map(e -> String.valueOf(e.getCode())).toArray(String[]::new);
            } else {
                marketAreasOrderArray = marketAreasOrder.split(",");
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < marketAreasOrderArray.length; i++) {
                SystemTradeTypeNewEnum trade =
                        SystemTradeTypeNewEnum.getEnum(Integer.valueOf(marketAreasOrderArray[i].trim()));
                if (trade == null) {
                    continue;
                }

                JSONObject jsonObject = null;
                switch (trade) {
                    case BTC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getBtcTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case ETH:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getEthTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case GAVC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getGsetTopic());
                        jsonArray.add(jsonObject);
                        break;
                    case USDT:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name",I18NUtils.getString("SystemTradeTypeNewEnum."+trade.getCode()));
                        jsonObject.put("topic", mqttProperties.getUsdtTopic());
                        jsonArray.add(jsonObject);
                        break;
                    default:
                        break;
                }
            }
            return ReturnResult.SUCCESS(jsonArray);
		} catch (Exception e) {
			logger.error("请求 /v2/market/areas 错误" + e); 
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.24")); 
		}
        
    }
    
    /**
     * 获取交易区的交易对
     *
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PassToken
    @ApiOperation("获取交易区的交易对")
	@RequestMapping(value="/v1/market/list",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeMarket(
            @RequestParam(value = "symbol", required = false, defaultValue = "0") Integer code
    ){
    	try {
/*    		if(code == SystemTradeTypeNewEnum.FAVORITE.getCode()) {
        		FUser userInfo = getCurrentUserInfoByToken();
        		if(userInfo != null && !StringUtils.isEmpty(userInfo.getfFavoriteTradeList())) {
        			JSONArray parseArray = JSON.parseArray(userInfo.getfFavoriteTradeList());
        			List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeShareByTradeIds(parseArray, WebConstant.BCAgentId);
        			return ReturnResult.SUCCESS(tradeTypeSort);
        		}else {
        			List<SystemTradeType> tradeTypeSort = new ArrayList<>();
        			SystemTradeType tradeTypeFirst = redisHelper.getTradeTypeFirst(SystemTradeTypeEnum.BTC.getCode(), WebConstant.BCAgentId);
        			if(tradeTypeFirst != null) {
        				tradeTypeSort.add(tradeTypeFirst);
        			}
        			SystemTradeType tradeTypeFirst2 = redisHelper.getTradeTypeFirst(SystemTradeTypeEnum.ETH.getCode(), WebConstant.BCAgentId);
        			if(tradeTypeFirst2 != null) {
        				tradeTypeSort.add(tradeTypeFirst2);
        			}
        			SystemTradeType tradeTypeFirst3 = redisHelper.getTradeTypeFirst(SystemTradeTypeEnum.GSET.getCode(), WebConstant.BCAgentId);
        			if(tradeTypeFirst3 != null) {
        				tradeTypeSort.add(tradeTypeFirst3);
        			}
        			return ReturnResult.SUCCESS(tradeTypeSort);
        		}
        	}*/
            //所有交易对
            List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeSort(code, WebConstant.BCAgentId);
            JSONArray array= new JSONArray();
            for(SystemTradeType object : tradeTypeSort) {
            	if(object != null) {
            		String jsonStr = JSON.toJSONString(object);
            		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            		jsonObject.put("tradeId", object.getId()); 
            		array.add(jsonObject);
            	}
            }
            return ReturnResult.SUCCESS(array);
		} catch (Exception e) {
			logger.error("请求 /v1/market/list 错误" + e.getMessage()); 
			return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.27")); 
		}
    }
    
    // app自选币种
    @ApiOperation("")
	@RequestMapping(value="/v1/collect/list",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ReturnResult MarketJsons(
            @RequestParam(value = "symbol", required = false, defaultValue = "0") Integer code) {
    	
    	if(code == SystemTradeTypeNewEnum.FAVORITE.getCode()) {
    		FUser userInfo = getUser();
    		FUserFavoriteTrade fUserFavoriteTrade = userFavoriteService.selectByUid(userInfo.getFid());
    		String getfFavoriteTradeList  = null;
			if(fUserFavoriteTrade != null) {
				getfFavoriteTradeList = fUserFavoriteTrade.getFfavoritetradelist();
			}
			
    		if(userInfo != null && !StringUtils.isEmpty(getfFavoriteTradeList)) {
                JSONArray parseArray = JSON.parseArray(getfFavoriteTradeList);
                List<SystemTradeType> tradeTypeSort =
                        redisHelper.getTradeTypeShareByTradeIds(parseArray, WebConstant.BCAgentId);
                for (SystemTradeType systemTradeType : tradeTypeSort) {
                    systemTradeType.setBuySymbol(systemTradeType.getBuyShortName());
                    systemTradeType.setSellSymbol(systemTradeType.getSellShortName());
                    if (ApiUtils.isTradeOpen(systemTradeType)) {
                        systemTradeType.setIsOpen("1");
                    }
                    else {
                        systemTradeType.setIsOpen("0");
                    }
                }
                return ReturnResult.SUCCESS(tradeTypeSort);
    		}else {
    			//如果是空的则返回空的币种列表
    			List<SystemTradeType> tradeTypeSort = new ArrayList<>();
    			return ReturnResult.SUCCESS(tradeTypeSort);
    		}
    	}
    	List<SystemTradeType> tradeTypeSort = new ArrayList<>();
		return ReturnResult.SUCCESS(tradeTypeSort);
    }

    //获取用户资产
    @ResponseBody
    @ApiOperation("获取用户资产")
	@RequestMapping(value="/v1/market/userassets",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult UserAssets(
            @RequestParam(required = false, defaultValue = "0") Integer tradeid
    ){
        JSONObject jsonObject = new JSONObject();
        JSONObject buyCoin = new JSONObject();
        JSONObject sellCoin = new JSONObject();
        FUser fuser = super.getCurrentUserInfoByApiToken();
        if (null == fuser) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("TradeApiController.28"));
        }
        SystemTradeType tradeType = redisHelper.getTradeType(tradeid, WebConstant.BCAgentId);
        if (tradeType == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.29")); 
        }
        UserCoinWallet buyuserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getBuyCoinId());
        UserCoinWallet selluserWallet = userWalletService.getUserCoinWallet(fuser.getFid(), tradeType.getSellCoinId());
        FUserScore userScore = this.userService.selectUserScoreById(fuser.getFid());
        if (buyuserWallet == null || selluserWallet == null || userScore == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.30")); 
        }
        jsonObject.put("score", userScore.getFscore()); 
        buyCoin.put("id", tradeType.getBuyCoinId()); 
        buyCoin.put("total", buyuserWallet.getTotal()); 
        buyCoin.put("frozen", buyuserWallet.getFrozen()); 
        buyCoin.put("borrow", buyuserWallet.getBorrow()); 
        jsonObject.put("buyCoin", buyCoin); 
        sellCoin.put("id", tradeType.getSellCoinId()); 
        sellCoin.put("total", selluserWallet.getTotal()); 
        sellCoin.put("frozen", selluserWallet.getFrozen()); 
        sellCoin.put("borrow", selluserWallet.getBorrow()); 
        jsonObject.put("sellCoin", sellCoin); 
        return ReturnResult.SUCCESS(jsonObject);
    }


}
