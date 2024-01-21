package com.qkwl.service.mqtt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.mq.MQTTHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.service.event.*;
import com.qkwl.service.event.eventBus.*;
import com.qkwl.service.mqtt.config.MqttProperties;
import com.qkwl.service.mqtt.task.*;
import com.qkwl.service.push.run.AutoMarket;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class MqttJob {

	private static final Logger logger = LoggerFactory.getLogger(MqttJob.class);

//	@Autowired
//	private MQTTHelper mqttHelper;
	//MQTT的数据大小限制 30个一组
	public final static int index = 30;
    //深度图的条数限制50
    public final static int depthSize = 50;
	
	@Autowired
	protected MemCache memCache;
	
	@Autowired
	private RedisHelper redisHelper;

	@Autowired
	private SystemTradeTypeJob systemTradeTypeJob;


	//@Scheduled(cron="*/2 * * * * ?")
	/*
	@Scheduled(fixedRate = 1*1000)
	public void work() throws MqttException {
	    systemTradeTypeJob.initTradeIdMap();
		//卖端数据推送

		MKTInfoTaskEvent GSET_mktInfoTask = new MKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map,mqttHelper.getGavcTopicProducer(),mqttProperties.getTopic().getGsetTopic());
		MKTInfoTaskEventBus.post(GSET_mktInfoTask);
		logger.info("MKTInfoTaskEvent send");
		MKTInfoTaskEvent BTC_mktInfoTask = new MKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP,mqttHelper.getBtcTopicProducer(),mqttProperties.getTopic().getBtcTopic());
		MKTInfoTaskEventBus.post(BTC_mktInfoTask);
		MKTInfoTaskEvent ETH_mktInfoTask = new MKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP,mqttHelper.getEthTopicProducer(),mqttProperties.getTopic().getEthTopic());
		MKTInfoTaskEventBus.post(ETH_mktInfoTask);
		MKTInfoTaskEvent USDT_mktInfoTask = new MKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP,mqttHelper.getUsdtTopicProducer(),mqttProperties.getTopic().getUsdtTopic());
		MKTInfoTaskEventBus.post(USDT_mktInfoTask);
		MKTInfoTaskEvent INNOVATE_mktInfoTask = new MKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP,mqttHelper.getInnovateTopicProducer(),mqttProperties.getTopic().getInnovateTopic());
		MKTInfoTaskEventBus.post(INNOVATE_mktInfoTask);

		WebDetailTaskEvent GSET_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map);
		WebDetailTaskEventBus.post(GSET_webDetailTask);
		logger.info("WebDetailTaskEvent send");
		WebDetailTaskEvent BTC_webDetailTask  = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP);
		WebDetailTaskEventBus.post(BTC_webDetailTask);
		WebDetailTaskEvent ETH_webDetailTask  = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP);
		WebDetailTaskEventBus.post(ETH_webDetailTask);
		WebDetailTaskEvent USDT_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP);
		WebDetailTaskEventBus.post(USDT_webDetailTask);
		WebDetailTaskEvent INNOVATE_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP);
		WebDetailTaskEventBus.post(INNOVATE_webDetailTask);

		//买端数据推送
		DetailTaskEvent GSET_DetailTask = new DetailTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map,mqttHelper.getMktTopicProducer(),mqttProperties.getTopic().getMktTopic());
		DetailTaskEventBus.post(GSET_DetailTask);
		logger.info("DetailTaskEvent send");
		DetailTaskEvent BTC_DetailTask  = new DetailTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP,mqttHelper.getMktTopicProducer(),mqttProperties.getTopic().getMktTopic());
		DetailTaskEventBus.post(BTC_DetailTask);
		DetailTaskEvent ETH_DetailTask  = new DetailTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP,mqttHelper.getMktTopicProducer(),mqttProperties.getTopic().getMktTopic());
		DetailTaskEventBus.post(ETH_DetailTask);
		DetailTaskEvent USDT_DetailTask = new DetailTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP,mqttHelper.getMktTopicProducer(),mqttProperties.getTopic().getMktTopic());
		DetailTaskEventBus.post(USDT_DetailTask);
		DetailTaskEvent INNOVATE_DetailTask = new DetailTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP,mqttHelper.getMktTopicProducer(),mqttProperties.getTopic().getMktTopic());
		DetailTaskEventBus.post(INNOVATE_DetailTask);
		//logger.info("web执行时间：{} s",(System.currentTimeMillis()-startTime));

		WebMKTInfoTaskEvent GSET_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map,mqttHelper.getGavcTopicProducer(),mqttProperties.getTopic().getWebGsetTopic());
		WebMKTInfoTaskEventBus.post(GSET_webMktInfoTask);
		logger.info("WebMKTInfoTaskEvent send");
		WebMKTInfoTaskEvent BTC_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP,mqttHelper.getBtcTopicProducer(),mqttProperties.getTopic().getWebBtcTopic());
		WebMKTInfoTaskEventBus.post(BTC_webMktInfoTask);
		WebMKTInfoTaskEvent ETH_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP,mqttHelper.getEthTopicProducer(),mqttProperties.getTopic().getWebEthTopic());
		WebMKTInfoTaskEventBus.post(ETH_webMktInfoTask);
		WebMKTInfoTaskEvent USDT_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP,mqttHelper.getUsdtTopicProducer(),mqttProperties.getTopic().getWebUsdtTopic());
		WebMKTInfoTaskEventBus.post(USDT_webMktInfoTask);
		WebMKTInfoTaskEvent INNOVATE_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP,mqttHelper.getInnovateTopicProducer(),mqttProperties.getTopic().getWebInnovateTopic());
		WebMKTInfoTaskEventBus.post(INNOVATE_webMktInfoTask);
	}
	*/

  @Scheduled(fixedDelay = 300)
  public void gsetTradeIdLineTask() throws MqttException {
    if (!AutoMarket.klineInitFlag) {
      return;
    }
    systemTradeTypeJob.initTradeIdMap();
    TradeIdKlineTaskEvent gset_tradeIdKlineTask = new TradeIdKlineTaskEvent(SystemTradeTypeJob.GAVC_Map);
    TradeIdKlineTaskEventBus.post(gset_tradeIdKlineTask);
  }

  @Scheduled(fixedDelay = 300)
  public void usdtTradeIdLineTask() throws MqttException {
    if (!AutoMarket.klineInitFlag) {
      return;
    }
    systemTradeTypeJob.initTradeIdMap();
    TradeIdKlineTaskEvent usdt_tradeIdKlineTask = new TradeIdKlineTaskEvent(SystemTradeTypeJob.USDT_MAP);
    TradeIdKlineTaskEventBus.post(usdt_tradeIdKlineTask);
  }

  @Scheduled(fixedDelay = 300)
  public void btcTradeIdLineTask() throws MqttException {
    if (!AutoMarket.klineInitFlag) {
      return;
    }
    systemTradeTypeJob.initTradeIdMap();
    TradeIdKlineTaskEvent btc_tradeIdKlineTask = new TradeIdKlineTaskEvent(SystemTradeTypeJob.BTC_MAP);
    TradeIdKlineTaskEventBus.post(btc_tradeIdKlineTask);
  }

  @Scheduled(fixedDelay = 300)
  public void ethTradeIdLineTask() throws MqttException {
    if (!AutoMarket.klineInitFlag) {
      return;
    }
    systemTradeTypeJob.initTradeIdMap();
    TradeIdKlineTaskEvent eth_tradeIdKlineTask = new TradeIdKlineTaskEvent(SystemTradeTypeJob.ETH_MAP);
    TradeIdKlineTaskEventBus.post(eth_tradeIdKlineTask);
    TradeIdKlineTaskEvent innovate_tradeIdKlineTask = new TradeIdKlineTaskEvent(SystemTradeTypeJob.INNOVATE_MAP);
    TradeIdKlineTaskEventBus.post(innovate_tradeIdKlineTask);
  }

	/*@Scheduled(fixedDelay = 300)
	public void ethTradeIdLineTask() throws MqttException {

		//String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + 0);
		//JSONObject obj = JSON.parseObject(coins);
		//JSONArray tradeTypeList = obj.getJSONArray("extObject");
		//sendMKTInfo(coins);
		//sendDetail(coins);

		//long startTime=System.currentTimeMillis();
		if(!AutoMarket.klineInitFlag){
			return;
		}
		systemTradeTypeJob.initTradeIdMap();
		TradeIdKlineTaskEvent gset_tradeIdKlineTask = new TradeIdKlineTaskEvent(redisHelper, SystemTradeTypeJob.GAVC_Map);
		//gset_tradeIdKlineTask.run();
		TradeIdKlineTaskEventBus.post(gset_tradeIdKlineTask);
		TradeIdKlineTaskEvent btc_tradeIdKlineTask = new TradeIdKlineTaskEvent(redisHelper, SystemTradeTypeJob.BTC_MAP);
		TradeIdKlineTaskEventBus.post(btc_tradeIdKlineTask);
		TradeIdKlineTaskEvent eth_tradeIdKlineTask = new TradeIdKlineTaskEvent(redisHelper, SystemTradeTypeJob.ETH_MAP);
		TradeIdKlineTaskEventBus.post(eth_tradeIdKlineTask);
		TradeIdKlineTaskEvent usdt_tradeIdKlineTask = new TradeIdKlineTaskEvent(redisHelper, SystemTradeTypeJob.USDT_MAP);
		TradeIdKlineTaskEventBus.post(usdt_tradeIdKlineTask);
		TradeIdKlineTaskEvent innovate_tradeIdKlineTask = new TradeIdKlineTaskEvent(redisHelper, SystemTradeTypeJob.INNOVATE_MAP);
		TradeIdKlineTaskEventBus.post(innovate_tradeIdKlineTask);
	}*/

	//@Scheduled(cron="*/2 * * * * ?")
/*	@Scheduled(fixedRate = 1*1000)
	public void work1() throws MqttException {
		//String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + 0);
		systemTradeTypeJob.initTradeIdMap();
		//行情
		//sendWebMKTInfo(coins);
		//币币交易详情
		//sendWebDetail(coins);
		//实时交易
		//long startTime=System.currentTimeMillis();
		WebMKTInfoTaskEvent GSET_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map,mqttHelper.getGavcTopicProducer(),mqttProperties.getTopic().getWebGsetTopic());
		WebMKTInfoTaskEventBus.post(GSET_webMktInfoTask);
		WebMKTInfoTaskEvent BTC_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP,mqttHelper.getBtcTopicProducer(),mqttProperties.getTopic().getWebBtcTopic());
		WebMKTInfoTaskEventBus.post(BTC_webMktInfoTask);
		WebMKTInfoTaskEvent ETH_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP,mqttHelper.getEthTopicProducer(),mqttProperties.getTopic().getWebEthTopic());
		WebMKTInfoTaskEventBus.post(ETH_webMktInfoTask);
		WebMKTInfoTaskEvent USDT_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP,mqttHelper.getUsdtTopicProducer(),mqttProperties.getTopic().getWebUsdtTopic());
		WebMKTInfoTaskEventBus.post(USDT_webMktInfoTask);
		WebMKTInfoTaskEvent INNOVATE_webMktInfoTask = new WebMKTInfoTaskEvent(memCache,redisHelper,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP,mqttHelper.getInnovateTopicProducer(),mqttProperties.getTopic().getWebInnovateTopic());
		WebMKTInfoTaskEventBus.post(INNOVATE_webMktInfoTask);


		WebDetailTaskEvent GSET_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.GAVC_Map);
		WebDetailTaskEventBus.post(GSET_webDetailTask);
		WebDetailTaskEvent BTC_webDetailTask  = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.BTC_MAP);
		WebDetailTaskEventBus.post(BTC_webDetailTask);
		WebDetailTaskEvent ETH_webDetailTask  = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.ETH_MAP);
		WebDetailTaskEventBus.post(ETH_webDetailTask);
		WebDetailTaskEvent USDT_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.USDT_MAP);
		WebDetailTaskEventBus.post(USDT_webDetailTask);
		WebDetailTaskEvent INNOVATE_webDetailTask = new WebDetailTaskEvent(memCache,mqttHelper,mqttProperties,SystemTradeTypeJob.INNOVATE_MAP);
		WebDetailTaskEventBus.post(INNOVATE_webDetailTask);
		//logger.info("交易执行时间：{} s",(System.currentTimeMillis()-startTime));
	}*/

	public BigDecimal getCny(int tradeId,String p_newStr) {
		//取BTC/GSET交易对价格计算
		String tradeTicker = memCache.get(RedisConstant.TICKERE_KEY + tradeId);
		JSONObject tradeTickerJson = JSONObject.parseObject(tradeTicker).getJSONObject("extObject");
		String  lastStr = tradeTickerJson.getString("last");
		//BTC/GSET交易对最新价格
		BigDecimal cny = new BigDecimal(lastStr);
		
		//当前交易对最新价格
		BigDecimal p_new = new BigDecimal(p_newStr);
		BigDecimal money = MathUtils.mul(p_new, cny);
		BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
		return newMoney;
	}
	
	public BigDecimal getCnyByCoinId(int buyCoinId,int sellCoinId,String p_newStr) {
		SystemCoinType coinType = new SystemCoinType();
		if(buyCoinId == 9) {
			coinType = redisHelper.getCoinType(sellCoinId);
			return new BigDecimal(p_newStr);
		}else {
			//取BTC/GSET交易对价格计算
			coinType = redisHelper.getCoinType(buyCoinId);
		}

		String symbol = coinType.getShortName()+"_GAVC";
		String[] symbols = symbol.split("_");
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
        SystemTradeType systemTradeType = new SystemTradeType();
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
            	systemTradeType = tradeType;
            }
        }
		
		//BTC/GSET交易对最新价格
		BigDecimal cny = redisHelper.getLastPrice(systemTradeType.getId());
		
		//当前交易对最新价格
		BigDecimal p_new = new BigDecimal(p_newStr);
		BigDecimal money = MathUtils.mul(p_new, cny);
		BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
		return newMoney;
	}
	
	public void sort(List<Object> list) {
		Collections.sort(list, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				JSONArray o1Array = JSON.parseArray(o1.toString());
				JSONArray o2Array = JSON.parseArray(o2.toString());
            	//o1单价
            	BigDecimal o1Price = new BigDecimal(o1Array.get(0).toString());
            	//o2单价
            	BigDecimal o2Price = new BigDecimal(o2Array.get(0).toString());
		            	
            	if(o1Price.compareTo(o2Price)<0) {
            		return 1;
            	}else if(o1Price.compareTo(o2Price)==0) {
            		return 0;
            	}else {
            		return -1;
            	}
			}
		});
	}
}
