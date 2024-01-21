package com.qkwl.service.event.eventListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.service.event.PushEventListener;
import com.qkwl.service.event.TradeIdKlineTaskEvent;
import com.qkwl.service.event.TradeIdLastKlineEvent;
import com.qkwl.service.event.eventBus.TradeIdKlineTaskEventBus;
import com.qkwl.service.event.eventBus.TradeIdLastKlineEventBus;
import com.qkwl.service.push.run.AutoMarket;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class TradeIdKlineTaskEventListener implements PushEventListener {
    private static final Logger logger = LoggerFactory.getLogger(TradeIdKlineTaskEventListener.class);

    @Autowired
    RedisHelper redisHelper;

    @PostConstruct
    public void register(){
        TradeIdKlineTaskEventBus.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void klineUpdate(TradeIdKlineTaskEvent event) {
        logger.info("TradeIdKlineTaskEvent event ");

        ConcurrentHashMap<Integer, SystemTradeType> tradeMap = event.getTradeMap();
        Date curDay = new Date(System.currentTimeMillis());
        curDay.setHours(0);
        curDay.setMinutes(0);
        curDay.setSeconds(0);
        for (Map.Entry<Integer, SystemTradeType> entry :tradeMap.entrySet()) {
            Integer tradeId = entry.getKey();

            if(null != entry.getValue().getListedDateTime() &&
                    entry.getValue().getListedDateTime().compareTo(curDay)>0 ) {
                logger.error("TradeIdKlineTaskEvent event tradeId:{} type :{} ", tradeId,entry.getValue().getType());
            }
            SystemTradeType tradeType = entry.getValue();
            Integer status = tradeType.getStatus();
            // 实时行情
            initTicker(tradeId,redisHelper);
            // IndexKline
            initIndexKline(tradeId,redisHelper);
            // 平台撮合执行
            if(status.equals(SystemTradeStatusEnum.NORMAL.getCode()) ) {
                // 深度
                initDepth(tradeId,redisHelper);
                // 最新成交
                initSuccess(tradeId,redisHelper);
                // LastKline
                //initLastKline(tradeId,redisHelper);
                if (tradeId.compareTo(900094)>0 ) {
                    logger.error("tradeid:{} before post TradeIdLastKlineEvent event!!!! ", tradeId);
                }
                TradeIdLastKlineEvent tradeIdLastKlineEvent = new TradeIdLastKlineEvent(tradeId);
                TradeIdLastKlineEventBus.post(tradeIdLastKlineEvent);
            }
        }
    }

    /**
     * 初始化深度
     *
     * @param tradeId
     */
    private void initDepth(Integer tradeId,RedisHelper redisHelper) {
        // 买深度
        String buyDepthStr = redisHelper.getRedisData(RedisConstant.BUYDEPTH_KEY + tradeId);
        if (buyDepthStr == null || buyDepthStr.isEmpty()) {
            AutoMarket.BuyDepthJson.put(tradeId, new JSONArray());
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
            AutoMarket.BuyDepthJson.put(tradeId, buyDepth);
        }
        // 卖深度
        String sellDepthStr = redisHelper.getRedisData(RedisConstant.SELLDEPTH_KEY + tradeId);
        if (sellDepthStr == null || sellDepthStr.isEmpty()) {
            AutoMarket.SellDepthJson.put(tradeId, new JSONArray());
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
            AutoMarket.SellDepthJson.put(tradeId, sellDepth);
        }
    }


    /**
     * 初始化实时行情
     *
     * @param tradeId
     */
    public void initTicker(Integer tradeId,RedisHelper redisHelper) {
        // 行情
        String tickerStr = redisHelper.getRedisData(RedisConstant.TICKERE_KEY + tradeId);
        TickerData tickerData = new TickerData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        if (null != tickerStr && !tickerStr.isEmpty()) {
            tickerData = JSON.parseObject(tickerStr, TickerData.class);
        }
       /* logger.error("tickerData:{}",JSON.toJSONString(tickerData));
        String last_price_buy_sell = redisHelper.getRedisData(RedisConstant.LAST_PRICE_BUY_SELL+tradeId);
        if (null != last_price_buy_sell && !last_price_buy_sell.isEmpty()) {
            Map map = JSON.parseObject(last_price_buy_sell, Map.class);
            logger.error("tradeId :{},tickerDate last_price_buy_sell:{}, map:{}",tradeId,last_price_buy_sell,JSON.toJSONString(map));
            String buy = String.valueOf(map.get("b"));
            String sell = String.valueOf(map.get("s"));
            tickerData.setBuy(new BigDecimal(buy));
            tickerData.setSell(new BigDecimal(sell));
        }*/
        AutoMarket.TickerJson.put(tradeId, tickerData);
    }

    /**
     * 初始化最新成交
     *
     * @param tradeId
     */
    public void initSuccess(Integer tradeId,RedisHelper redisHelper) {
        String successStr = redisHelper.getRedisData(RedisConstant.SUCCESSENTRUST_KEY + tradeId);
        if (successStr == null || successStr.isEmpty()) {
            AutoMarket.SuccessJson.put(tradeId, new JSONArray());
        } else {
            AutoMarket.SuccessJson.put(tradeId, JSON.parseArray(successStr));
        }
    }

    /**
     * 初始化首页行情
     *
     * @param tradeId
     */
    public void initIndexKline(Integer tradeId,RedisHelper redisHelper) {
        String indexKlineStr = redisHelper.getRedisData(RedisConstant.THREETICKERE_KEY + tradeId);
        if (indexKlineStr == null || indexKlineStr.isEmpty()) {
            AutoMarket.indexKlineJson.put(tradeId, new JSONArray());
        } else {
            AutoMarket.indexKlineJson.put(tradeId, JSON.parseArray(indexKlineStr));
        }
    }
}
