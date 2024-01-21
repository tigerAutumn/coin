package com.qkwl.web.front.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.ExchangeRateResp;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.coin.SystemTradeTypeVO;
import com.qkwl.common.dto.news.FArticle;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.web.FSystemLan;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


@Controller
@ApiIgnore
public class FrontMarketJsonController extends JsonBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(FrontMarketJsonController.class);

    @Autowired
    private RedisHelper redisHelper;
    
    @Autowired
    private PushService pushService;

    //获取K线数据
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/kline/fullperiod",method = {RequestMethod.GET,RequestMethod.POST})
    public String fullperiod(
            @RequestParam(required = true) int step,
            @RequestParam(required = true) int symbol
    ){
        JSONArray result = pushService.getKlineJson(symbol, step / 60);
        if (result != null) {
            return result.clone().toString();
        }
        return "[[]]"; 
    }

    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/kline/fulldepth",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult fulldepth(
            @RequestParam(required = false, defaultValue = "0") int step,
            @RequestParam(required = false, defaultValue = "0") int symbol
    ) {
        JSONObject jsonObject = new JSONObject();
        JSONObject returnObject = new JSONObject();
        if (symbol == 0) {
            return ReturnResult.FAILUER(I18NUtils.getString("FrontMarketJsonController.1")); 
        }
        jsonObject.put("bids", getBuyDepth(symbol));
        jsonObject.put("asks", getSellDepth(symbol));
        jsonObject.put("date", Utils.getTimestamp().getTime() / 1000); 
        returnObject.put("depth", jsonObject); 
        if (step > 0) {
            JSONObject periodobject = new JSONObject();
            periodobject.put("marketFrom", symbol); 
            periodobject.put("coinVol", symbol); 
            periodobject.put("type", step); 
            periodobject.put("data", getLastKlineJson(symbol, step / 60));
            returnObject.put("period", periodobject); 
        }
        return ReturnResult.SUCCESS(returnObject);
    }

    /**
     * 买深度
     *
     * @param tradeId
     */
    private  JSONArray getBuyDepth(Integer tradeId) {
        // 买深度
        String buyDepthStr = redisHelper.getRedisData(RedisConstant.BUYDEPTH_KEY + tradeId);
        if (buyDepthStr == null || buyDepthStr.isEmpty()) {
            return new JSONArray();
        } else {
            JSONArray tmpArray = JSON.parseArray(buyDepthStr);
            // 数据过滤
            JSONArray buyDepth = new JSONArray();
            for (Object object : tmpArray) {
                JSONArray array = JSON.parseArray(object.toString());
                if (Double.valueOf(array.get(1).toString()) > 0d) {
                    buyDepth.add(array);
                }
            }
            return buyDepth;
        }
    }

    /**
     * 卖深度
     * @param tradeId
     * @return
     */
    private JSONArray getSellDepth(Integer tradeId){
        // 卖深度
        String sellDepthStr = redisHelper.getRedisData(RedisConstant.SELLDEPTH_KEY + tradeId);
        if (sellDepthStr == null || sellDepthStr.isEmpty()) {
            return new JSONArray();
        } else {
            JSONArray tmpArray = JSON.parseArray(sellDepthStr);
            // 数据过滤
            JSONArray sellDepth = new JSONArray();
            for (Object object : tmpArray) {
                JSONArray array = JSON.parseArray(object.toString());
                if (Double.valueOf(array.get(1).toString()) > 0d) {
                    sellDepth.add(array);
                }
            }
            return sellDepth;
        }
    }

    /**
     * lastkline
     * @param tradeId
     * @param stepid
     * @return
     */
    private String getLastKlineJson(Integer tradeId, Integer stepid){
        String klineStr = redisHelper.getRedisData(RedisConstant.LASTKLINE_KEY + tradeId + "_" + stepid);
        if(org.apache.commons.lang3.StringUtils.isBlank(klineStr)){
            klineStr ="[]";
        }
        return klineStr;
    }

    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/kline/lastperiod",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult lastPeriod(
            @RequestParam(required = false, defaultValue = "0") int step,
            @RequestParam(required = false, defaultValue = "0") int symbol
    ){
        if (symbol == 0) {
            return ReturnResult.FAILUER(I18NUtils.getString("FrontMarketJsonController.11")); 
        }
        return ReturnResult.SUCCESS(pushService.getLastKlineJson(symbol, step / 60));
    }
    
    @ResponseBody
    @ApiOperation("")
	@GetMapping(value="/market/rate")
    public ReturnResult exchangeRate() {
	        String cnyValue = getCNYValue();
	        if (!TextUtils.isEmpty(cnyValue)) {
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.put("CNY", cnyValue); 
	            return ReturnResult.SUCCESS(jsonObject);
	        }
        return ReturnResult.FAILUER(I18NUtils.getString("FrontMarketJsonController.14")); 
    }
    
    
    @ResponseBody
    @ApiOperation("获取汇率")
    @PassToken
	@GetMapping("/market/exchangeRate")
    public RespData<List<ExchangeRateResp>> exchangeRateInfo() {
    	List<ExchangeRateResp> list = getExchangeRate();
	    return RespData.ok(list);
    }
    

    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/trademarket_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult tradeMarket(
            @RequestParam(value = "symbol", required = false, defaultValue = "0") Integer symbol,
            @RequestParam(value = "type", required = false, defaultValue = "1") Integer tradeType,
            @RequestParam(value = "sb", required = false, defaultValue = "btc_usdt") String sellBuy,
            @RequestParam(value = "limit", required = false, defaultValue = "0") Integer limit
    ){
        FSystemLan systemLan = redisHelper.getLanguageType(I18NUtils.getCurrentLang());
        limit = limit < 0 ? 0 : limit;
        limit = limit > 1 ? 1 : limit;
        JSONObject jsonObject = new JSONObject();
        SystemTradeType systemTradeType = redisHelper.getTradeType(symbol, WebConstant.BCAgentId);
        if (systemTradeType == null || systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())
                || !systemTradeType.getIsShare()) {
            systemTradeType = redisHelper.getTradeTypeFirst(tradeType, WebConstant.BCAgentId);
            if (systemTradeType == null) {
                return ReturnResult.FAILUER(""); 
            }
            symbol = systemTradeType.getId();
        }
        // 小数位处理(默认价格2位，数量4位)
        String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit(); 
        String[] digits = digit.split("#"); 
        jsonObject.put("cnyDigit", Integer.valueOf(digits[0])); 
        jsonObject.put("coinDigit", Integer.valueOf(digits[1])); 
        FUser fuser = getCurrentUserInfoByToken();
        if (fuser != null) {
            jsonObject.put("isTelephoneBind", fuser.getFistelephonebind()); 
            jsonObject.put("tradePassword", fuser.getFtradepassword() == null); 
            jsonObject.put("needTradePasswd", redisHelper.getNeedTradePassword(fuser.getFid())); 
            jsonObject.put("login", true); 
        } else {
            jsonObject.put("login", false); 
        }

        //现有的交易区
        Map<Integer, Object> typeMap = new LinkedHashMap<>();

        //交易区对应的交易对
        Map<Integer, List<SystemTradeTypeVO>> tradeTypeListMap = new LinkedHashMap<>();

        //所有交易对
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);

        for (SystemTradeTypeEnum typeEnum : SystemTradeTypeEnum.values()) {
            typeMap.put(typeEnum.getCode(), typeEnum.getSymbol());
            List<SystemTradeType> tempTradeTypeList = new ArrayList<>();
            for (SystemTradeType stt : tradeTypeList) {
                if (stt.getType() == typeEnum.getCode()) {
                    stt.setBuyShortName(stt.getBuyShortName().toLowerCase());
                    stt.setSellShortName(stt.getSellShortName().toLowerCase());
                    tempTradeTypeList.add(stt);
                }
            }
            tradeTypeListMap.put(typeEnum.getCode(), ModelMapperUtils.mapper(tempTradeTypeList, SystemTradeTypeVO.class));
        }

        //List<SystemTradeTypeVO> mapper = ModelMapperUtils.mapper(redisHelper.getTradeTypeList(WebConstant.BCAgentId), SystemTradeTypeVO.class);
        redisHelper.getCoinInfo(systemTradeType.getSellCoinId(),super.getLanEnum().getCode()+"");

        //英文的交易名称
        sellBuy = sellBuy.toLowerCase();
        //交易 卖买
        jsonObject.put("sb", sellBuy); 
        //交易 卖方
        jsonObject.put("sell", sellBuy.split("_")[0]);  //$NON-NLS-2$
        //交易 买方
        jsonObject.put("buy", sellBuy.split("_")[1]);  //$NON-NLS-2$
        jsonObject.put("typeMap", typeMap); 
        jsonObject.put("type", tradeType); 
        jsonObject.put("tradeType", ModelMapperUtils.mapper(systemTradeType, SystemTradeTypeVO.class)); 
        List<FArticle> farticles = redisHelper.getArticles(systemLan.getFid() ,2, 2, 1, WebConstant.BCAgentId);
        jsonObject.put("article", farticles);    //公告 

        jsonObject.put("symbol", symbol); 
        jsonObject.put("tradeTypeListMap", tradeTypeListMap); 
        jsonObject.put("isPlatformStatus", systemTradeType.getStatus() == SystemTradeStatusEnum.NORMAL.getCode()); 
        jsonObject.put("limit", limit); 
        jsonObject.put("coinInfo",redisHelper.getCoinInfo(systemTradeType.getSellCoinId(),super.getLanEnum().getCode()+""));  //$NON-NLS-2$
        return ReturnResult.SUCCESS(jsonObject);

    }
}
