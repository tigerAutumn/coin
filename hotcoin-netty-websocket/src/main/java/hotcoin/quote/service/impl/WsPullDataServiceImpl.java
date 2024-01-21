package hotcoin.quote.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.component.RedisComponent;
import hotcoin.quote.config.kafkaconfig.KafkaTopicConfig;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.dto.TradeKlineHistoryDTO;
import hotcoin.quote.service.WsPullDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.service.impl
 * @ClassName: WsPullDataServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/8/28 19:00
 * @Version: 1.0
 */
@Slf4j
@Service
public class WsPullDataServiceImpl implements WsPullDataService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisComponent redisComponent;

    @Override
    public void pullKlineHistory(JSONObject jsonObject) {
        try {
            TradeKlineHistoryDTO dto = convert2Vo(jsonObject);
            kafkaTemplate.send(KafkaTopicConfig.KAFKA_TOPIC_PULL_KLINE_HISTORY, JSON.toJSONString(dto))
                    .addCallback(success -> log.info("send kline history pull req's data success! "), err ->
                            log.error("kafka send pull klineHistory data fail,data is ->{},err is ->{}", JSON.toJSONString(dto), err));
        } catch (Exception e) {
            log.error("error happened when convert klineHistoryDto ->{}", e);
        }
    }

    private TradeKlineHistoryDTO convert2Vo(JSONObject jsonObject) {
        String coinPair = jsonObject.getString(WsConstant.SYMBOL);
        Integer tradeId = redisComponent.getTradeId4Symbol(coinPair.toUpperCase());
        if (tradeId == null) {
            return null;
        }
        jsonObject.put(WsConstant.TRADE_ID, tradeId);
        return jsonObject.toJavaObject(TradeKlineHistoryDTO.class);
    }

}
