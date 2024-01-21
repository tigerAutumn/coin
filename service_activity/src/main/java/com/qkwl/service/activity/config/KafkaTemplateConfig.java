package com.qkwl.service.activity.config;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.config.kafkaconfig
 * @ClassName: KafkaTemplateConfig
 * @Author: hwj
 * @Description:
 * @Date: 2019/5/20
 * @Version: 1.0
 */
@Configuration
public class KafkaTemplateConfig {
	
	@Value("${kafka.server}")
	private String kafkaSever;
	
	@Value("${kafka.topic.entrust_history}")
	private String topic;
	
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
    	KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
    	kafkaTemplate.setDefaultTopic(topic);
        return kafkaTemplate;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(16);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSever);
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        //props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 200);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }


/*    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>(16);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.220:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "WS.QUOTE");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG,"WS.QUOTE");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }*/
}