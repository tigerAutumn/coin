//package com.qkwl.service.mqtt.config;
//
//import com.aliyun.openservices.ons.api.MessageListener;
//import com.aliyun.openservices.ons.api.PropertyKeyConst;
//import com.aliyun.openservices.ons.api.bean.ConsumerBean;
//import com.aliyun.openservices.ons.api.bean.ProducerBean;
//import com.aliyun.openservices.ons.api.bean.Subscription;
//import com.qkwl.common.framework.mq.MQTTHelper;
//import com.qkwl.common.mq.MQTopic;
//import com.qkwl.common.properties.MQProperties;
//import com.qkwl.service.umeng.mq.MQUmengOtcListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * @Author yuanchangjian
// * @Date 2018年12月21日
// */
//@Configuration
//@EnableConfigurationProperties(MQProperties.class)
//public class MQConfig {
//	@Autowired
//	private MqttProperties mqttProperties;
//
//	@Bean
//    public MQTTHelper mqttHelper() {
//        return new MQTTHelper();
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean ethTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getEthTopic());
//        producerBean.setProperties(properties);
//        mqttHelper.setEthTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean gavcTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getGsetTopic());
//        producerBean.setProperties(properties);
//        mqttHelper.setGavcTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean btcTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getBtcTopic());
//        producerBean.setProperties(properties);
//        mqttHelper.setBtcTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean usdtTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getUsdtTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setUsdtTopicProducer(producerBean);
//        return producerBean;
//    }
//
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean mktTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getMktTopic());
//        producerBean.setProperties(properties);
//        mqttHelper.setMktTopicProducer(producerBean);
//        return producerBean;
//    }
//
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webEthTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebEthTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebEthTopicProducer(producerBean);
//        return producerBean;
//    }
//
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webGavcTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebGsetTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebGavcTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webBtcTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebBtcTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebBtcTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webMktTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebMktTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebMktTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webRealTimeTradeBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebRealTimeTrade());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebRealTimeTradeProducer(producerBean);
//        return producerBean;
//    }
//
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webUsdtTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebUsdtTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebUsdtTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean tickerRankingAscTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getTickerRankingAscTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setTickerRankingAscTopicProducer(producerBean);
//        return producerBean;
//    }
//
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean tickerRankingDescTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getTickerRankingDescTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setTickerRankingDescTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean
//    public MQTopic mqTopic() {
//        return new MQTopic();
//    }
//
//    @Bean
//    public MQUmengOtcListener otcListener() {
//        return new MQUmengOtcListener();
//    }
//
//    @Bean
//    public Map<Subscription, MessageListener> otcSubscription(MQUmengOtcListener otcListener) {
//        Subscription subscription = new Subscription();
//        subscription.setTopic(MQTopic.OTC);
//        subscription.setExpression("*");
//        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
//        subscriptionTable.put(subscription, otcListener);
//        return subscriptionTable;
//    }
//
//   @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ConsumerBean otcConsumer(MQProperties mqProperties, Map<Subscription, MessageListener> otcSubscription) {
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqProperties.getAccessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqProperties.getOnsAddr());
//        properties.setProperty(PropertyKeyConst.GROUP_ID,mqProperties.getPid().getOtc());
//        properties.setProperty("ConsumerId", mqProperties.getCid().getOtc());
//        ConsumerBean consumerBean = new ConsumerBean();
//        consumerBean.setProperties(properties);
//        consumerBean.setSubscriptionTable(otcSubscription);
//        return consumerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean innovateTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getInnovateTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setInnovateTopicProducer(producerBean);
//        return producerBean;
//    }
//
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public ProducerBean webInnovateTopicBean(MQTTHelper mqttHelper) {
//        ProducerBean producerBean = new ProducerBean();
//        Properties properties = new Properties();
//        properties.setProperty(PropertyKeyConst.AccessKey, mqttProperties.getAcessKey());
//        properties.setProperty(PropertyKeyConst.SecretKey, mqttProperties.getSecretKey());
//        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, mqttProperties.getUrl());
//        properties.setProperty("ProducerId", mqttProperties.getPid().getWebInnovateTopic());
//        producerBean.setProperties(properties);
//
//        mqttHelper.setWebInnovateTopicProducer(producerBean);
//        return producerBean;
//    }
//}
