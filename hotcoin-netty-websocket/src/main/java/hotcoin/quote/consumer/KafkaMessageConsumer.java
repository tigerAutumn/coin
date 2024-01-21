package hotcoin.quote.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.component.AsyncSendTickersComponent;
import hotcoin.quote.config.kafkaconfig.KafkaTopicConfig;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.dto.TradeTickersDTO;
import hotcoin.quote.model.wsquote.vo.TradeDepthVo;
import hotcoin.quote.model.wsquote.vo.TradeDetailVo;
import hotcoin.quote.model.wsquote.vo.TradeKlineVo;
import hotcoin.quote.model.wsquote.vo.TradeTickerVo;
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
    private QuoteDataParser quoteDataParser;

    @Autowired
    private GroupSendService groupSendService;
    @Autowired
    private AsyncSendTickersComponent asyncSendTickersComponent;

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
            quoteDataParser.parserDepthData(tradeId, tradeDepthVo);
            groupSendService.groupSendDepthData(tradeDepthVo, tradeId);
        } catch (Exception e) {
            log.error("deal depth data from kafka fail->{},data is->{}", e, content);
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
    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_TICKERS})
    public void receiveTradeTickers(String content) {
        log.info("get tickers content ->{}", content);
        try {
            JSONArray jsonArray = JSON.parseArray(content);
            // 获取自选数据
            List<TradeTickersDTO> receiveList = jsonArray.toJavaList(TradeTickersDTO.class);
            if (CollectionUtils.isEmpty(receiveList)) {
                return;
            }
            asyncSendTickersComponent.asyncGroupSendHandle(receiveList);
        } catch (Exception e) {
            log.error("deal trade 24h ticker from kafka fail->{},data is->{}", e, content);
        }
    }

    /**
     * 监听交易详情信息(老版本从mq读取)
     *
     * @param content(接收json格式数据)
     */
    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_DETAIL})
    public void receiveTradeDetail(String content) {
        log.info("receive trade detail data ->{}", content);
        try {
            List<TradeDetailVo> detailVoList = JSON.parseArray(content).toJavaList(TradeDetailVo.class);
            if (CollectionUtils.isEmpty(detailVoList)) {
                return;
            }
            Integer tradeId = detailVoList.get(0).getTradeId();
            groupSendService.groupSendTradeDetailData(detailVoList, tradeId);
        } catch (Exception e) {
            log.error("deal detail data from kafka fail->{},data is->{}", e, content);
        }
    }

    @KafkaListener(groupId = KafkaTopicConfig.KAFKA_GROUP_WS_QUOTE, topics = {KafkaTopicConfig.KAFKA_TOPIC_WS_24H_TICKERS})
    public void receiveTrade24hTickers(String content) {
        log.info("receive trade 24h tickers's data ->{}", content);
        try {
            List<TradeTickerVo> trade24hTickerList = JSON.parseArray(content).toJavaList(TradeTickerVo.class);
            if (CollectionUtils.isEmpty(trade24hTickerList)) {
                return;
            }
            for (TradeTickerVo item : trade24hTickerList) {
                groupSendService.groupSend24hTickersData(item);
            }
        } catch (Exception e) {
            log.error("process 24h tickers's data from kafka fail->{},data is->{}", e, content);
        }
    }
}
