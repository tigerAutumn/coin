package com.qkwl.service.event.eventListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.service.event.PushEventListener;
import com.qkwl.service.event.TradeIdLastKlineEvent;
import com.qkwl.service.event.eventBus.TradeIdLastKlineEventBus;
import com.qkwl.service.push.run.AutoMarket;
import com.qkwl.service.util.JodaTimeUtil;

import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeIdLastKlineEventListener implements PushEventListener {
    private static final Logger logger = LoggerFactory.getLogger(TradeIdLastKlineEventListener.class);

    @Autowired
    RedisHelper redisHelper;

    @PostConstruct
    public void register() {
        TradeIdLastKlineEventBus.register(this);

    }

    @Subscribe
    @AllowConcurrentEvents
    public void lastklineUpdate(TradeIdLastKlineEvent event) {
        Integer tradeId = event.getTradeId();
        initLastKline(tradeId);
    }

    /**
     * 初始化最后一条K线
     *
     * @param tradeId
     */
    public void initLastKline(Integer tradeId) {


        ConcurrentHashMap<Integer, JSONArray> lastLlineJsonMap = AutoMarket.lastKlineJson.get(tradeId);
        if (lastLlineJsonMap == null) {
            lastLlineJsonMap = new ConcurrentHashMap<Integer, JSONArray>();
            AutoMarket.lastKlineJson.put(tradeId, lastLlineJsonMap);

        }
//        if (tradeId.compareTo(900094)>0 ) {
//            logger.error("tradeid:{} process lastkline......",tradeId);
//        }
        for (int i : AutoMarket.TIME_KIND) {
            // lastKline
            String lastKlineStr = redisHelper.getRedisData(RedisConstant.LASTKLINE_KEY + tradeId + "_" + i);
            try {
                JSONArray lastLlineJson = new JSONArray();
                long currentTs = new DateTime().withMillisOfSecond(0).minusSeconds(2).getMillis();
                if (lastKlineStr == null || lastKlineStr.isEmpty()) {
                    /*SystemTradeType sytemTradeType = SystemTradeTypeJob.SystemTradeTypeMap.get(tradeId);
                    BigDecimal openPrice = BigDecimal.ZERO;
                    if(null != sytemTradeType){
                       openPrice = sytemTradeType.getOpenPrice();
                    }
                    String paddingLastLineJson = "[[" + currentTs + "," + openPrice + "," + openPrice + "," + openPrice + "," + openPrice + "," + 0 + "]]";
                    lastLlineJson = JSON.parseArray({"excludeEndDateTime":"2019-09-05","includeBeginDateTime":"2019-07-18"});
                    */
                    lastLlineJsonMap.put(i,lastLlineJson);
//                    if (tradeId.compareTo(900094)>0 ) {
//                        logger.error("tradeid:{} lastKlineStr is {} 为空.....",tradeId,i);
//                    }

                } else {
                    lastLlineJson = JSON.parseArray(lastKlineStr);
                    if(lastLlineJson.size()==0){
                        /*SystemTradeType sytemTradeType = SystemTradeTypeJob.SystemTradeTypeMap.get(tradeId);
                        BigDecimal openPrice = BigDecimal.ZERO;
                        if(null != sytemTradeType){
                            openPrice = sytemTradeType.getOpenPrice();
                        }
                        String paddingLastLineJson = "[[" + currentTs + "," + openPrice + "," + openPrice + "," + openPrice + "," + openPrice + "," + 0 + "]]";
                        lastLlineJson = JSON.parseArray(paddingLastLineJson);*/
//                        if (tradeId.compareTo(900094)>0 ) {
//                            logger.error("tradeid:{} lastLlineJson.size == 0.....",tradeId);
//                        }
                        lastLlineJsonMap.put(i,lastLlineJson);
                    }else {
                        JSONArray lastKlineJsonStr = JSONArray
                            .parseArray(lastLlineJson.get(0).toString());
                        Long lastKlineTs = Long.valueOf(lastKlineJsonStr.get(0).toString());
                        DateTime openNew = JodaTimeUtil.isOpenNewPeriod(i, currentTs, lastKlineTs);
                        if (currentTs - lastKlineTs > i * 60 * 1_000L) {
                            String shouPrice = lastKlineJsonStr.get(4).toString();
                            String paddingLastLineJson =
                                "[[" + openNew.getMillis() + "," + shouPrice + "," + shouPrice + ","
                                    + shouPrice + "," + shouPrice + "," + 0 + "]]";
                            lastLlineJson = JSON.parseArray(paddingLastLineJson);
                        }
//                        if (tradeId.compareTo(900094)>0 ) {
//                            logger.error("tradeid:{} lastLlineJson :{}",tradeId,JSON.toJSONString(lastLlineJson));
//                        }
                        lastLlineJsonMap.put(i, lastLlineJson);
                    }
                }
                AutoMarket.lastKlineJson.put(tradeId, lastLlineJsonMap);
                // 增量更新KLine
                if (AutoMarket.KlineJson.get(tradeId) == null) {
                    AutoMarket.KlineJson.put(tradeId, new ConcurrentHashMap<Integer, JSONArray>());
                }
                ConcurrentHashMap<Integer, JSONArray> klineJsonMap = AutoMarket.KlineJson.get(tradeId);
                String  klineStr = redisHelper.getRedisData(RedisConstant.KLINE_KEY + tradeId + "_" + i);
                if (klineStr == null || klineStr.isEmpty()) {
                    logger.debug("kline timekind {} is not in redis.  replace with lastkline {}",i,lastLlineJson);
//                    if (tradeId.compareTo(900094)>0 ) {
//                        logger.error("tradeid {}'s kline timekind {} is not in redis.  replace with lastkline {}",tradeId,i,lastLlineJson);
//                    }
                    klineJsonMap.put(i, lastLlineJson);
                } else {
                    try {
                        JSONArray klineJson = JSON.parseArray(klineStr);
                        if(klineJson.size()==0){
                            klineJsonMap.put(i, lastLlineJson);
                        }else {
                            JSONArray klineTemp1 = JSONArray
                                .parseArray(klineJson.get(klineJson.size() - 1).toString());
                            JSONArray klineTemp2 = JSONArray
                                .parseArray(lastLlineJson.get(0).toString());
                            if (klineTemp1.get(0).toString().equals(klineTemp2.get(0).toString())) {
                                klineJson.remove(klineJson.size() - 1);
                                klineJson.add(klineTemp2);
                            } else {
                                klineJson.add(klineTemp2);
                            }
                            klineJsonMap.put(i, klineJson);
                        }

                    }catch (Exception ex){
                        logger.error("initLastKline ex :{}",ex);
                    }
                }

//                if (tradeId.compareTo(900094)>0 ) {
//                    logger.error("增量k线更新  tradeid {} klinemap.size={}", tradeId,klineJsonMap.size());
//                }
                AutoMarket.KlineJson.put(tradeId, klineJsonMap);
                /*
                if (klineJsonMap == null) {
                    klineJsonMap = new ConcurrentHashMap<Integer, JSONArray>();
                    klineJsonMap.put(i, new JSONArray());
                    AutoMarket.KlineJson.put(tradeId, klineJsonMap);
                } else {
                    JSONArray klineJson = klineJsonMap.get(i);
                    if (klineJson == null) {
                        klineJsonMap.put(i, new JSONArray());
                    } else if (klineJson.size() >= 1 && lastLlineJson != null) {
                        JSONArray klineTemp1 = JSONArray.parseArray(klineJson.get(klineJson.size() - 1).toString());
                        JSONArray klineTemp2 = JSONArray.parseArray(lastLlineJson.get(0).toString());
                        if (klineTemp1.get(0).toString().equals(klineTemp2.get(0).toString())) {
                            klineJson.remove(klineJson.size() - 1);
                            klineJson.add(klineTemp2);
                        } else {
                            if (Long.valueOf(klineTemp2.get(0).toString()) - Long.valueOf(klineTemp1.get(0).toString()) > (i * 60 * 1000)) {
                                String klineStr = redisHelper.getRedisData(RedisConstant.KLINE_KEY + tradeId + "_" + i);
                                if (klineStr == null || klineStr.isEmpty()) {
                                    logger.error("获取tradeId:{},i:{} redis KLINE_KEY is null",tradeId,i);
                                    klineJsonMap.put(i, new JSONArray());
                                } else {
                                    klineJsonMap.put(i, JSON.parseArray(klineStr));
                                }
                                AutoMarket.KlineJson.put(tradeId, klineJsonMap);
                            } else {
                                klineJson.add(klineTemp2);
                            }
                        }
                        // 大于指定条数移除
                        if (klineJson.size() > AutoMarket.MAX_LEN) {
                            klineJson.remove(0);
                        }
                    } else if (klineJson.size() == 0) {
                        String klineStr = redisHelper.getRedisData(RedisConstant.KLINE_KEY + tradeId + "_" + i);
                        if (klineStr == null || klineStr.isEmpty()) {
                            klineJsonMap.put(i, new JSONArray());
                        } else {
                            klineJsonMap.put(i, JSON.parseArray(klineStr));
                        }
                        AutoMarket.KlineJson.put(tradeId, klineJsonMap);
                    }
                 }
                */
            } catch (Exception e) {
                logger.error("initLastKline error tradeId:"+tradeId+" e:{}",e);
            }
        }
     }

}
