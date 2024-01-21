package hotcoin.quote.config.kafkaconfig;

/**
 * @ProjectName: service_user
 * @Package: hotcoin.quote.config.kafkaconfig
 * @ClassName: KafkaTopicConfig
 * @Author: hf
 * @Description:
 * @Date: 2019/5/15 15:54
 * @Version: 1.0
 */
public interface KafkaTopicConfig {
    /**
     * 分组
     */
    String KAFKA_GROUP_WS_QUOTE = "WS.QUOTE";
    /**
     * topic DEPTH:深度,KLINE:K线,DETAIL:实时交易,TICKERS:涨跌幅,交易区 24H.TICKERS:24小时涨跌幅
     */
    String KAFKA_TOPIC_WS_DEPTH = "WS.QUOTE.DEPTH";
    String KAFKA_TOPIC_WS_KLINE = "WS.QUOTE.KLINE";
    String KAFKA_TOPIC_WS_DETAIL = "WS.QUOTE.DETAIL";
    String KAFKA_TOPIC_WS_TICKERS = "WS.QUOTE.TICKERS";
    String KAFKA_TOPIC_WS_24H_TICKERS = "WS.QUOTE.24H.TICKERS";

    /**
     * 一次性拉取相关topic
     */
    /**
     * kline 历史
     */
    String KAFKA_TOPIC_PULL_KLINE_HISTORY = "WS.PULL.KLINE.HISTORY";
}
