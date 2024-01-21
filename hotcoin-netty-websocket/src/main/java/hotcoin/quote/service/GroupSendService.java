package hotcoin.quote.service;

import hotcoin.quote.model.wsquote.vo.*;

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
     * 群发24小时涨跌幅(也可作为自选数据)
     *
     * @param tradeTickerVo
     */
    void groupSend24hTickersData(TradeTickerVo tradeTickerVo);

    /**
     * 群发明星币
     */
    void groupSendStarCoinData();

    /**
     * 群发成交额榜
     */
    void groupSendAmountRankData();

    /**
     * 群发涨跌幅
     */

    void groupSendChangeRateData(Map<String, List<TradeChangeTickersVo>> changeRateData);

    /**
     * 群发交易区数据
     */
    void groupSendTradeAreaData(Map<String, List<TradeAreaTickersVo>> areaData);


}
