package com.qkwl.service.otc.config;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.qkwl.common.framework.mq.OtcSendHelper;
import com.qkwl.common.mq.MQTopic;
import com.qkwl.common.properties.MQProperties;

import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author jany
 * @Date 17-4-20
 */
@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class MQConfig {

    @Bean
    public MQTopic mqTopic() {
        return new MQTopic();
    }
    
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean otcProducer(MQProperties mqProperties) {
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.setProperty("AccessKey", mqProperties.getAccessKey());
        properties.setProperty("SecretKey", mqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
        properties.setProperty("ProducerId", mqProperties.getPid().getOtc());
        producerBean.setProperties(properties);
        return producerBean;
    }
    
    @Bean
    public OtcSendHelper otcSendHelper(ProducerBean otcProducer) {
    	OtcSendHelper otcSendHelper = new OtcSendHelper();
    	otcSendHelper.setOtcHelperProducer(otcProducer);
        return otcSendHelper;
    }
}
