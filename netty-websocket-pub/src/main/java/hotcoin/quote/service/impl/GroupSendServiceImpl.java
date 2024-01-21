package hotcoin.quote.service.impl;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.component.RedisComponent;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.UserLinksComponent;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.*;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import hotcoin.quote.service.GroupSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.service.impl
 * @ClassName: GroupSendServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 10:21
 * @Version: 1.0
 */
@Service
@Slf4j
public class GroupSendServiceImpl implements GroupSendService {
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private SessionComponent sessionComponent;
    @Autowired
    private UserLinksComponent userLinksComponent;

    @Override
    public void groupSendDepthData(TradeDepthVo data, Integer tradeId) {
        log.info("group send depth tradeId ->{},data->{}", tradeId, JSON.toJSONString(data));
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (sessions == null || sessions.isEmpty()) {
            log.info("group send DepthData fail,because no client exist!");
            return;
        }
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name by tradeId->{}", tradeId);
            return;
        }
        try {
            for (String key : sessions.keySet()) {
                ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
                if (subTopicSet == null || subTopicSet.isEmpty()) {
                    continue;
                }
                if (isMatchDepth(subTopicSet, coinPair)) {
                    String subKey = String.format(WsConstant.TRADE_DEPTH_STR, coinPair.toLowerCase());
                    sessions.get(key).sendText(JSON.toJSONString(Result.success(data, subKey)));
                    log.info("send tradeDepth message success,coinPair is ->{}", coinPair);
                }
            }
        } catch (Exception e) {
            log.error("send tradeDepth'Data fail ->{}", e);
        }
    }

    @Override
    public void groupSendKlineData(List<?> data, Integer tradeId, String period) {
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (sessions == null || sessions.isEmpty()) {
            log.info("group send Kline Data fail,because no client exist!");
            return;
        }
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name about kline by tradeId:->{}", tradeId);
            return;
        }
        try {
            for (String key : sessions.keySet()) {
                ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
                if (subTopicSet == null || subTopicSet.isEmpty()) {
                    continue;
                }
                if (isMatchKline(subTopicSet, coinPair, period)) {
                    String subKey = String.format(WsConstant.TRADE_KLINE_STR, coinPair.toLowerCase(), period);
                    sessions.get(key).sendText(JSON.toJSONString(Result.success(data, subKey)));
                    log.info("send kline message success,coinPair is->{},period is->{}", coinPair, period);
                }
            }
        } catch (Exception e) {
            log.error("send kline Data fail ->{}", e);
        }
    }

    @Override
    public void groupSendTradeDetailData(List<TradeDetailVo> data, Integer tradeId) {
        log.info("group send detail data start,tradeId is:[{}],data :[{}] ->{}", tradeId, JSON.toJSONString(data));
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (sessions == null || sessions.isEmpty()) {
            log.info("group send TradeDetail fail,because because no client exist!");
            return;
        }
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name by tradeId:->{}", tradeId);
            return;
        }
        try {
            for (String key : sessions.keySet()) {
                ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
                if (subTopicSet == null || subTopicSet.isEmpty()) {
                    continue;
                }
                if (isMatchDetail(subTopicSet, coinPair)) {
                    String subKey = String.format(WsConstant.TRADE_DETAIL_STR, coinPair.toLowerCase());
                    sessions.get(key).sendText(JSON.toJSONString(Result.success(data, subKey)));
                    log.info("send tradeDetail message success,coinPair is->{},data is->{}", coinPair, JSON.toJSONString(data));
                }
            }
        } catch (Exception e) {
            log.error(" send TradeDetail'Data fail ->{}", e);
        }
    }

    @Override
    public void groupSendChangeAndAreaTickersData(Map<String, List<TradeChangeTickersVo>> changeRateData, Map<String, List<TradeAreaTickersVo>>
            areaData) {
        log.info("group Send Tickers changeRateData ->{},areaData->{}", JSON.toJSONString(changeRateData),
                JSON.toJSONString(areaData));
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (MapUtils.isEmpty(sessions)) {
            log.info("group send 24h tickers fail,because because no client exist!");
            return;
        }
        try {
            for (String key : sessions.keySet()) {
                ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
                if (subTopicSet == null || subTopicSet.isEmpty()) {
                    continue;
                }
                // 交易区数据推送
                if (!MapUtils.isEmpty(areaData)) {
                    for (String area : areaData.keySet()) {
                        if (!isMatchAreaTickers(subTopicSet, area)) {
                            continue;
                        }
                        String subKey = String.format(WsConstant.TRADE_TICKERS_AREA_STR, area.toLowerCase());
                        sessions.get(key).sendText(JSON.toJSONString(Result.success(areaData.get(area), subKey)));
                        log.info("send tickers about area message success,area is->{},data is->{}", area, JSON.toJSONString(areaData.get(area)));
                    }
                }

                // 涨跌幅数据(涨幅top 10和跌幅 top 10推送)推送
                if (!MapUtils.isEmpty(changeRateData)) {
                    List<TradeChangeTickersVo> upDataList = changeRateData.get(TickersConstant.TICKERS_UP_KEY);
                    List<TradeChangeTickersVo> downDataList = changeRateData.get(TickersConstant.TICKERS_DOWN_KEY);
                    if (!CollectionUtils.isEmpty(upDataList) && isMatchUpTickers(subTopicSet)) {
                        sessions.get(key).sendText(JSON.toJSONString(Result.success(upDataList, WsConstant.TRADE_TICKERS_UP_STR)));
                        log.info("send tickers up data success,data is->{}", JSON.toJSONString(upDataList));
                    }
                    if (!CollectionUtils.isEmpty(downDataList) && isMatchDownTickers(subTopicSet)) {
                        sessions.get(key).sendText(JSON.toJSONString(Result.success(downDataList, WsConstant.TRADE_TICKERS_DOWN_STR)));
                        log.info("send tickers down data success,data is->{}", JSON.toJSONString(downDataList));
                    }
                }
            }
        } catch (Exception e) {
            log.error("send 24h tickers'Data fail ->{}", e);
        }
    }

    @Override
    public void groupSendTickersData(TradeTickerVo tradeTickerVo) {

    }


    /**
     * @param subTopic eg: market.btc_usdt.trade.detail
     * @param coinPair 从redis取到的币对信息
     * @return
     */
    private boolean isMatchDetail(ConcurrentSkipListSet<String> subTopic, String coinPair) {
        return subTopic.contains(String.format(WsConstant.TRADE_DETAIL_STR, coinPair.toLowerCase()));
    }

    /**
     * @param subTopic eg: market.btc_usdt.trade.depth
     * @param coinPair 从redis取到的币对信息
     * @return
     */
    private boolean isMatchDepth(ConcurrentSkipListSet<String> subTopic, String coinPair) {
        return subTopic.contains(String.format(WsConstant.TRADE_DEPTH_STR, coinPair.toLowerCase()));
    }

    /**
     * @param subTopic eg: market.btc_usdt.kline.1m
     * @param coinPair 从redis取到的币对信息
     * @param period   粒度,1m,5m
     * @return
     */
    private boolean isMatchKline(ConcurrentSkipListSet<String> subTopic, String coinPair, String period) {
        return subTopic.contains(String.format(WsConstant.TRADE_KLINE_STR, coinPair.toLowerCase(), period));
    }

    /**
     * 取到的涨跌幅信息(按照分区订阅)
     *
     * @param subTopic eg: market.btc_area.trade.tickers
     * @param area     分区
     * @return
     */
    private boolean isMatchAreaTickers(ConcurrentSkipListSet<String> subTopic, String area) {
        return subTopic.contains(String.format(WsConstant.TRADE_TICKERS_AREA_STR, area.toLowerCase()));
    }


    /**
     * 涨幅
     *
     * @param subTopic eg: market.up.trade.tickers
     * @return
     */
    private boolean isMatchUpTickers(ConcurrentSkipListSet<String> subTopic) {
        return subTopic.contains(WsConstant.TRADE_TICKERS_UP_STR);
    }

    /**
     * 跌幅
     *
     * @param subTopic eg: market.down.trade.tickers
     * @return
     */
    private boolean isMatchDownTickers(ConcurrentSkipListSet<String> subTopic) {
        return subTopic.contains(WsConstant.TRADE_TICKERS_DOWN_STR);
    }

    /**
     * 取到的涨跌幅信息(返回少数几个字段)
     *
     * @param subTopic eg: market.btc_usdt.single.ticker
     * @param pair     币对
     * @return
     */
    private boolean isMatchSingleTickers(ConcurrentSkipListSet<String> subTopic, String pair) {
        return subTopic.contains(String.format(WsConstant.TRADE_SINGLE_TICKER_STR, pair.toLowerCase()));
    }
}
