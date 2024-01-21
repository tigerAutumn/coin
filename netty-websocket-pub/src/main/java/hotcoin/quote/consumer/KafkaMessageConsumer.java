package hotcoin.quote.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.config.kafkaconfig.KafkaTopicConfig;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.TradeDepthVo;
import hotcoin.quote.model.wsquote.TradeDetailDTO;
import hotcoin.quote.model.wsquote.TradeDetailVo;
import hotcoin.quote.model.wsquote.TradeKlineVo;
import hotcoin.quote.parser.QuoteDataParser;
import hotcoin.quote.service.GroupSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.consumer
 * @ClassName: KafkaMessageConsumer
 * @Author: hf
 * @Description: kafka消费者
 * @Date: 2019/5/8 16:42
 * @Version: 1.0
 */
@Component
@Slf4j
public class KafkaMessageConsumer {
    @Autowired
    private GroupSendService groupSendService;
    @Autowired
    private QuoteDataParser quoteDataParser;

    /**
     * 监听深度数据
     *
     * @param content(接收json格式数据)
     */
    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_DEPTH})
    public void receiveTradeDepth(String content) {
        log.info("receive depth data->{}", content);
        try {
            JSONObject jsonObject = JSON.parseObject(content);
            Integer tradeId = jsonObject.getInteger(WsConstant.TRADE_ID);
            TradeDepthVo tradeDepthVo = jsonObject.toJavaObject(TradeDepthVo.class);
            Objects.requireNonNull(tradeDepthVo);
            groupSendService.groupSendDepthData(tradeDepthVo, tradeId);
        } catch (Exception e) {
            log.error("deal depth data from kafka fail->{}", e);
        }
    }

    /**
     * 监听K线数据
     *
     * @param content(接收json格式数据)
     */
    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_KLINE})
    public void receiveTradeKline(String content) {
        log.info("receive kline data ->{}", content);
        try {
            JSONArray jsonArray = JSON.parseArray(content);
            List<TradeKlineVo> tradeKlineVoList = jsonArray.toJavaList(TradeKlineVo.class);
            if (CollectionUtils.isEmpty(tradeKlineVoList)) {
                return;
            }
            for (TradeKlineVo item : tradeKlineVoList) {
                Integer tradeId = item.getTradeId();
                String period = item.getPeriod();
                List<?> data = item.getData();
                if (CollectionUtils.isEmpty(data)) {
                    continue;
                }
                groupSendService.groupSendKlineData(data, tradeId, period);
            }
        } catch (Exception e) {
            log.error("deal tradeKline data from kafka fail->{},data is->{}", e, content);
        }
    }


    /**
     * 监听24小时涨跌幅数据
     *
     * @param content 1:GSET交易区, 2: BTC交易区,3:ETH交易区,4:USDT交易区,5:创新区(INNOVATE_MAP)
     */
//    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_TICKERS})
//    public void receiveTradeTickers(String content) {
//        try {
//            log.info("get tickers content ->{}", content);
//            JSONArray jsonArray = JSON.parseArray(content);
//            // 获取自选数据
//            List<TradeTickersDTO> receiveList = jsonArray.toJavaList(TradeTickersDTO.class);
//            if (CollectionUtils.isEmpty(receiveList)) {
//                return;
//            }
//            // 获取涨跌幅数据
//            Map<String, List<TradeChangeTickersVo>> changeRateTickersMap = tickersComponent.
//                    getChangeRateSortTickersCompare2Cache(receiveList, TickersConstant.TICKERS_SUB_NUM);
//
//            //获取交易区数据
//            Map<String, List<TradeAreaTickersVo>> areaTickersMap = tickersComponent.getArea4PushData(receiveList);
//            log.info("deal trade 24h ticker,area data is ->{},changeData is->{}", JSON.toJSONString(areaTickersMap),
//                    JSON.toJSONString(areaTickersMap));
//
//            groupSendService.groupSendChangeAndAreaTickersData(changeRateTickersMap, areaTickersMap);
//        } catch (Exception e) {
//            log.error("deal tradeKline data from kafka fail->{}", e);
//        }
//    }

    /**
     * 监听交易详情信息(老版本从mq读取)
     *
     * @param content(接收json格式数据)
     */
    @KafkaListener(topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_DETAIL}, groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE)
    public void receiveTradeDetail(String content) {
        log.info("receive trade detail data ->{}", content);
        try {
            JSONArray jsonArray = JSON.parseArray(content);
            if (jsonArray == null || jsonArray.size() < 1) {
                return;
            }
            List<TradeDetailDTO> detailVoList = jsonArray.toJavaList(TradeDetailDTO.class);
            TradeDetailDTO detailDTO = detailVoList.get(0);
            List<TradeDetailVo> result = quoteDataParser.parserTradeDetailData(detailVoList);
            if (CollectionUtils.isEmpty(result)) {
                return;
            }
            groupSendService.groupSendTradeDetailData(result, detailDTO.getTradeId());
        } catch (Exception e) {
            log.error("process detail data from kafka fail->{}", e);
        }
    }
}
