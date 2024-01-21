package com.qkwl.web.front.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.RequstLimit;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.constant.QuantitativeDataConsts;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@PassToken
public class OpenApiController extends JsonBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenApiController.class);

	@Autowired
	private RedisHelper redisHelper;
	
	@Autowired
    private PushService pushService;
	
	/**
     * @return
     */
	@RequstLimit(count=1000)
    @ResponseBody
    @RequestMapping("/openApi/ticker")
    public ReturnResult ticker() throws Exception {
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
        JSONArray array = new JSONArray();
        for (SystemTradeType tradeType : tradeTypeList) 
        {
			if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
				continue;
			}
        	TickerData tickerData = pushService.getTickerData(tradeType.getId());
        	JSONObject jsonObject = new JSONObject();
        	jsonObject.put("symbol", tradeType.getBuyShortName()+"_"+tradeType.getSellShortName());
	        jsonObject.put("last", tickerData.getLast()==null?0d:tickerData.getLast());
	        jsonObject.put("high", tickerData.getHigh()==null?0d:tickerData.getHigh());
	        // 深度
	        jsonObject.put("low", tickerData.getLow()==null?0d:tickerData.getLow());
	        jsonObject.put("vol", tickerData.getVol()==null?0d:tickerData.getVol());
        	array.add(jsonObject);
        }
        return ReturnResult.SUCCESS(array);
    }
	
	 // 买卖盘，最新成交 ，供外部行情网站调用
	 @RequstLimit(count=1000)
	 @ResponseBody
	 @RequestMapping(value = "/v1/market/ticker")
	 public JSONObject marketJsonsTicker() {
		JSONObject jsonObject = new JSONObject();
	 	try {
	      JSONArray jsonArray = new JSONArray();
	      List<SystemTradeType> tradeTypeShare = redisHelper.getTradeTypeShare(0);
	      
	      for (SystemTradeType systemTradeType : tradeTypeShare) {
				if(systemTradeType == null) {
					continue;
				}
				if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
					continue;
				}
				TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
				JSONObject j = new JSONObject();
		        j.put("symbol", systemTradeType.getSellShortName().toLowerCase() + "_" + systemTradeType.getBuyShortName().toLowerCase());
		        j.put("last", tickerData.getLast() == null ? 0d : tickerData.getLast());
		        j.put("high", tickerData.getHigh()== null ? 0d : tickerData.getHigh());
		        j.put("low", tickerData.getLow() == null ? 0d : tickerData.getLow());
		        j.put("vol", tickerData.getVol() == null ? 0d : tickerData.getVol());
		        j.put("buy", tickerData.getBuy() == null ? 0d: tickerData.getBuy());
		        j.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());
		        j.put("change", tickerData.getChg() == null ? 0d : tickerData.getChg());
		        jsonArray.add(j);
		  }
	      jsonObject.put("ticker", jsonArray);
	      jsonObject.put("status", "ok");
	 	} catch (Exception e) {
	 	  logger.error("访问/v1/market/ticker异常",e);
	 	  jsonObject.put("ticker", new JSONArray());
	 	  jsonObject.put("status", "error");
		}
	    jsonObject.put("timestamp", System.currentTimeMillis() / 1000);
	    return jsonObject;
	 }


	// 买卖盘，最新成交 ，供外部行情网站调用
	@RequstLimit(count=1000)
	@ResponseBody
	@RequestMapping(value = "/v1/market/ticker/{symbol}")
	public JSONObject marketJsonsTickerBySymbol(@PathVariable String symbol) {

		if(StringUtils.isBlank(symbol)){
			return this.marketJsonsTicker();
		}else{
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			Integer tradeId = QuantitativeDataConsts.SystemTradeTypeMap.get(symbol);
			if(null == tradeId) {
				List<SystemTradeType> tradeTypeShare = redisHelper.getTradeTypeShare(0);

				for (SystemTradeType systemTradeType : tradeTypeShare) {
					if (systemTradeType == null) {
						continue;
					}
					if (systemTradeType.getStatus()
							.equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
						continue;
					}
					String symbolStr =
							systemTradeType.getSellShortName().toLowerCase() + "_" + systemTradeType
									.getBuyShortName().toLowerCase();
					if(symbolStr.equalsIgnoreCase(symbol)){
						tradeId = systemTradeType.getId();
					}
					QuantitativeDataConsts.SystemTradeTypeMap
							.put(symbolStr, systemTradeType.getId());
				}
			}
			if(null == tradeId){
				jsonObject.put("ticker", new JSONArray());
				jsonObject.put("status", "error");
			}else{
				TickerData tickerData = pushService.getTickerData(tradeId);
				JSONObject j = new JSONObject();
				j.put("symbol", symbol);
				j.put("last", tickerData.getLast() == null ? 0d : tickerData.getLast());
				j.put("high", tickerData.getHigh()== null ? 0d : tickerData.getHigh());
				j.put("low", tickerData.getLow() == null ? 0d : tickerData.getLow());
				j.put("vol", tickerData.getVol() == null ? 0d : tickerData.getVol());
				j.put("buy", tickerData.getBuy() == null ? 0d: tickerData.getBuy());
				j.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());
				j.put("change", tickerData.getChg() == null ? 0d : tickerData.getChg());
				jsonArray.add(j);
				jsonObject.put("ticker", jsonArray);
				jsonObject.put("status", "ok");
			}
			jsonObject.put("timestamp", System.currentTimeMillis() / 1000);
			return jsonObject;
		}

	}
	 
	// 买卖盘，最新成交 ，供外部行情网站调用
	@RequstLimit(count=1000)
	@ResponseBody
	@RequestMapping(value = "/v1/market/ggtTicker")
	public JSONObject marketGgtTicker() {
		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray jsonArray = new JSONArray();
			List<SystemTradeType> tradeTypeShare = redisHelper.getTradeTypeShare(0);

			for (SystemTradeType systemTradeType : tradeTypeShare) {
				if (systemTradeType == null) {
					continue;
				}
				if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
					continue;
				}
				TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
				JSONObject j = new JSONObject();
				j.put("symbol", systemTradeType.getSellShortName().toLowerCase() + "_"
						+ systemTradeType.getBuyShortName().toLowerCase());
				j.put("last", tickerData.getLast() == null ? 0d : tickerData.getLast());
				j.put("high", tickerData.getHigh() == null ? 0d : tickerData.getHigh());
				j.put("low", tickerData.getLow() == null ? 0d : tickerData.getLow());
				j.put("vol", tickerData.getVol() == null ? 0d : tickerData.getVol());
				j.put("buy", tickerData.getBuy() == null ? 0d : tickerData.getBuy());
				j.put("sell", tickerData.getSell() == null ? 0d : tickerData.getSell());
				j.put("change", tickerData.getChg() == null ? 0d : tickerData.getChg());
				jsonArray.add(j);
			}
			jsonObject.put("ticker", jsonArray);
			jsonObject.put("status", "ok");
		} catch (Exception e) {
			logger.error("访问/v1/market/ticker异常", e);
			JSONArray jsonArray = new JSONArray();
			JSONObject j = new JSONObject();
			j.put("symbol", "ggt_gset");
			j.put("last", 0d);
			j.put("high", 0d);
			j.put("low", 0d);
			j.put("vol", 0d);
			j.put("buy", 0d);
			j.put("sell", 0d);
			j.put("change", 0d);
			jsonArray.add(j);
			jsonObject.put("ticker", jsonArray);
			jsonObject.put("status", "error");
		}
		jsonObject.put("timestamp", System.currentTimeMillis() / 1000);
		return jsonObject;
	}
}
