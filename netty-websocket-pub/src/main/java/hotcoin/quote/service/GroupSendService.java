package hotcoin.quote.service;

import hotcoin.quote.model.wsquote.*;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.service
 * @ClassName: GroupSendService
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 10:18
 * @Version: 1.0
 */
public interface GroupSendService {
    /**
     * 群发交易深度信息
     *
     * @param data
     * @param tradeId
     */
    void groupSendDepthData(TradeDepthVo data, Integer tradeId);

    /**
     * 群发K线信息
     *
     * @param data
     * @param tradeId
     * @param period
     */
    void groupSendKlineData(List<?> data, Integer tradeId, String period);

    /**
     * 群发交易详情信息
     *
     * @param data
     * @param tradeId
     */
    void groupSendTradeDetailData(List<TradeDetailVo> data, Integer tradeId);


    /**
     * 群发24小时涨跌幅数据,分区数据,单个,多个交易对数据(自选,多个币对订阅)
     *
     * @param changeRateData
     * @param areaData
     */
    void groupSendChangeAndAreaTickersData(Map<String, List<TradeChangeTickersVo>> changeRateData, Map<String, List<TradeAreaTickersVo>> areaData);

    void groupSendTickersData(TradeTickerVo tradeTickerVo);
}
