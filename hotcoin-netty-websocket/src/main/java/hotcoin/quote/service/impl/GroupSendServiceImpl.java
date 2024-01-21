package hotcoin.quote.service.impl;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.component.RedisComponent;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.TickersComponent;
import hotcoin.quote.component.UserLinksComponent;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.vo.*;
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
    @Autowired
    private TickersComponent tickersComponent;

    @Override
    public void groupSendDepthData(TradeDepthVo data, Integer tradeId) {
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name by tradeId->{}", tradeId);
            return;
        }
        sendData(data, WsConstant.TRADE_DEPTH_STR, coinPair, null, null);
    }

    @Override
    public void groupSendKlineData(List<?> data, Integer tradeId, String period) {
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name about kline by tradeId:->{}", tradeId);
            return;
        }
        sendData(data, WsConstant.TRADE_KLINE_STR, coinPair, period, null);

    }

    @Override
    public void groupSendTradeDetailData(List<TradeDetailVo> data, Integer tradeId) {
        String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
        if (StringUtils.isEmpty(coinPair)) {
            log.error("can not find the coinPair name by tradeId:->{}", tradeId);
            return;
        }
        sendData(data, WsConstant.TRADE_DETAIL_STR, coinPair, null, null);
    }

    @Override
    public void groupSend24hTickersData(TradeTickerVo data) {
        String coinPair = data.getSellShortName() + WsConstant.SLASH + data.getBuyShortName();
        sendData(data, WsConstant.TRADE_24H_TICKER_STR, coinPair, null, null);
    }

    @Override
    public void groupSendStarCoinData() {
        List<TradeStarCoinVo> list = redisComponent.getStarCoinsList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        sendData(list, WsConstant.TRADE_STAR_COIN_STR, null, null, null);
    }

    @Override
    public void groupSendAmountRankData() {
        List<TradeAmountRankVo> list = tickersComponent.getAmountRankList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        sendData(list, WsConstant.TRADE_AMOUNT_RANK_STR, null, null, null);
    }

    @Override
    public void groupSendChangeRateData(Map<String, List<TradeChangeTickersVo>> changeRateData) {
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (MapUtils.isEmpty(sessions)) {
            log.info("group send 24h tickers fail,because because no client exist!");
            return;
        }

        for (String key : sessions.keySet()) {
            ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
            if (subTopicSet == null || subTopicSet.isEmpty()) {
                continue;
            }

            // 涨跌幅数据(涨幅top 10和跌幅 top 10推送)推送
            List<TradeChangeTickersVo> upDataList = changeRateData.get(TickersConstant.TICKERS_UP_KEY);
            if (!CollectionUtils.isEmpty(upDataList) && isSubscribed(subTopicSet, WsConstant.TRADE_TICKERS_UP_STR, null, null, null)) {
                sessions.get(key).sendText(JSON.toJSONString(Result.success(upDataList, WsConstant.TRADE_TICKERS_UP_STR)));
                log.info("send tickers up data success,data is->{}", JSON.toJSONString(upDataList));
            }

            List<TradeChangeTickersVo> downDataList = changeRateData.get(TickersConstant.TICKERS_DOWN_KEY);
            if (!CollectionUtils.isEmpty(downDataList) && isSubscribed(subTopicSet, WsConstant.TRADE_TICKERS_DOWN_STR, null, null, null)) {
                sessions.get(key).sendText(JSON.toJSONString(Result.success(downDataList, WsConstant.TRADE_TICKERS_DOWN_STR)));
                log.info("send tickers down data success,data is->{}", JSON.toJSONString(downDataList));
            }
        }
    }

    @Override
    public void groupSendTradeAreaData(Map<String, List<TradeAreaTickersVo>> areaData) {
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (MapUtils.isEmpty(sessions)) {
            log.info("group send 24h tickers fail,because because no client exist!");
            return;
        }
        for (String key : sessions.keySet()) {
            ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
            if (subTopicSet == null || subTopicSet.isEmpty()) {
                continue;
            }
            for (String area : areaData.keySet()) {
                if (!isSubscribed(subTopicSet, WsConstant.TRADE_TICKERS_AREA_STR, null, null, area)) {
                    continue;
                }
                String subKey = String.format(WsConstant.TRADE_TICKERS_AREA_STR, area.toLowerCase());
                sessions.get(key).sendText(JSON.toJSONString(Result.success(areaData.get(area), subKey)));
            }
        }
    }

    /**
     * @param subTopicStr
     * @param coinPair
     * @param period
     * @param area
     * @return
     */
    private String getSubKey(String subTopicStr, String coinPair, String period, String area) {
        switch (subTopicStr) {
            case WsConstant.TRADE_STAR_COIN_STR:
                return WsConstant.TRADE_STAR_COIN_STR;

            case WsConstant.TRADE_AMOUNT_RANK_STR:
                return WsConstant.TRADE_AMOUNT_RANK_STR;

            case WsConstant.TRADE_24H_TICKER_STR:
                return String.format(WsConstant.TRADE_24H_TICKER_STR, coinPair.toLowerCase());

            case WsConstant.TRADE_TICKERS_DOWN_STR:
                return WsConstant.TRADE_TICKERS_DOWN_STR;

            case WsConstant.TRADE_TICKERS_UP_STR:
                return WsConstant.TRADE_TICKERS_UP_STR;

            case WsConstant.TRADE_KLINE_STR:
                return String.format(WsConstant.TRADE_KLINE_STR, coinPair.toLowerCase(), period);

            case WsConstant.TRADE_DEPTH_STR:
                return String.format(WsConstant.TRADE_DEPTH_STR, coinPair.toLowerCase());

            case WsConstant.TRADE_DETAIL_STR:
                return String.format(WsConstant.TRADE_DETAIL_STR, coinPair.toLowerCase());

            case WsConstant.TRADE_TICKERS_AREA_STR:
                return String.format(WsConstant.TRADE_TICKERS_AREA_STR, area.toLowerCase());
            default:
                return "";
        }
    }

    /**
     * 判断是否订阅
     *
     * @param subTopicSet
     * @param subTopicStr
     * @param coinPair
     * @param period
     * @param area
     * @return
     */
    private boolean isSubscribed(ConcurrentSkipListSet<String> subTopicSet, String subTopicStr, String coinPair, String period, String area) {
        switch (subTopicStr) {
            case WsConstant.TRADE_STAR_COIN_STR:
                return subTopicSet.contains(WsConstant.TRADE_STAR_COIN_STR);

            case WsConstant.TRADE_AMOUNT_RANK_STR:
                return subTopicSet.contains(WsConstant.TRADE_AMOUNT_RANK_STR);

            case WsConstant.TRADE_24H_TICKER_STR:
                return subTopicSet.contains(String.format(WsConstant.TRADE_24H_TICKER_STR, coinPair.toLowerCase()));

            case WsConstant.TRADE_TICKERS_DOWN_STR:
                return subTopicSet.contains(WsConstant.TRADE_TICKERS_DOWN_STR);

            case WsConstant.TRADE_TICKERS_UP_STR:
                return subTopicSet.contains(WsConstant.TRADE_TICKERS_UP_STR);

            case WsConstant.TRADE_KLINE_STR:
                return subTopicSet.contains(String.format(WsConstant.TRADE_KLINE_STR, coinPair.toLowerCase(), period));

            case WsConstant.TRADE_DEPTH_STR:
                return subTopicSet.contains(String.format(WsConstant.TRADE_DEPTH_STR, coinPair.toLowerCase()));

            case WsConstant.TRADE_DETAIL_STR:
                return subTopicSet.contains(String.format(WsConstant.TRADE_DETAIL_STR, coinPair.toLowerCase()));

            case WsConstant.TRADE_TICKERS_AREA_STR:
                return subTopicSet.contains(String.format(WsConstant.TRADE_TICKERS_AREA_STR, area.toLowerCase()));
            default:
                return false;
        }
    }


    /**
     * 发送数据
     *
     * @param data
     * @param subTopicStr
     * @param coinPair
     * @param period
     * @param area
     */
    private void sendData(Object data, String subTopicStr, String coinPair, String period, String area) {
        Map<String, Session> sessions = sessionComponent.getOnlineSessions();
        if (sessions == null || sessions.isEmpty()) {
            log.info("group send  data fail,because no client exist!");
            return;
        }
        for (String key : sessions.keySet()) {
            ConcurrentSkipListSet<String> subTopicSet = userLinksComponent.getUserSubPairsCacheMap().get(key);
            if (subTopicSet == null || subTopicSet.isEmpty()) {
                continue;
            }
            if (isSubscribed(subTopicSet, subTopicStr, coinPair, period, area)) {
                String subKey = getSubKey(subTopicStr, coinPair, period, area);
                sessions.get(key).sendText(JSON.toJSONString(Result.success(data, subKey)));
                log.info("send data success data is->{}", JSON.toJSONString(data));
            }
        }
    }
}
