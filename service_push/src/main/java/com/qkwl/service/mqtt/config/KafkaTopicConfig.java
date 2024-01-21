package com.qkwl.service.mqtt.config;

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
     * topic
     */
    String KAFKA_TOPIC_WS_DEPTH = "WS.QUOTE.DEPTH";
    String KAFKA_TOPIC_WS_KLINE = "WS.QUOTE.KLINE";
    String KAFKA_TOPIC_WS_DETAIL = "WS.QUOTE.DETAIL";
}
