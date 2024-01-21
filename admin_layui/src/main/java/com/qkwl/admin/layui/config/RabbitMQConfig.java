package com.qkwl.admin.layui.config;

import com.qkwl.admin.layui.component.CancelOrderSender;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String springRabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int springRabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String springRabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String springRabbitmqPassword;

    @Value("${spring.rabbitmq.order_virtual_host}")
    private String ordervirtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private boolean mqPublisherConfirms;

    @Value("${spring.rabbitmq.publisher-returns}")
    private boolean mqPublisherReturns;

    @Value("${spring.rabbitmq.channelCashSize}")
    private int mqchannelCashSize;

    @Value("${spring.rabbitmq.channelCheckoutTimeout}")
    private long mqChannelCheckoutTimeout;

    @Value("${spring.rabbitmq.connectionCacheSize}")
    private int mqConnectionCacheSize;

    @Value("${spring.rabbitmq.connectionLimit}")
    private int mqConnectionLimit;


    @Bean(name="orderConnectionFactory")
    @Primary
    public ConnectionFactory orderConnectionFactory(){
        CachingConnectionFactory cachingConnectionFactory=new CachingConnectionFactory();
        cachingConnectionFactory.setHost(springRabbitmqHost);
        cachingConnectionFactory.setPort(springRabbitmqPort);
        cachingConnectionFactory.setUsername(springRabbitmqUsername);
        cachingConnectionFactory.setPassword(springRabbitmqPassword);
        cachingConnectionFactory.setPublisherConfirms(mqPublisherConfirms);
        cachingConnectionFactory.setPublisherReturns(mqPublisherReturns);
        cachingConnectionFactory.setVirtualHost(ordervirtualHost);
        cachingConnectionFactory.setCacheMode(CacheMode.CHANNEL);
        cachingConnectionFactory.setChannelCacheSize(mqchannelCashSize);
        //单位毫秒
        cachingConnectionFactory.setChannelCheckoutTimeout(mqChannelCheckoutTimeout);
        return cachingConnectionFactory;
    }

    @Bean(name="orderRabbitAdmin")
    @Primary
    public RabbitAdmin orderRabbitAdmin(){
        RabbitAdmin rabbitAdmin =  new RabbitAdmin(orderConnectionFactory());
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean(name="orderFactory")
    @Primary
    public SimpleRabbitListenerContainerFactory orderFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
    @Qualifier("orderConnectionFactory") ConnectionFactory connectionFactory) {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            return factory;
    }

    @Bean
    @Scope("prototype")
    public MessageProperties messageProperties(){
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        return messageProperties;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(@Qualifier("orderConnectionFactory") ConnectionFactory orderConnectionFactory){
        return new RabbitTemplate(orderConnectionFactory);
    }

    @Bean
    public CancelOrderSender cancelOrderSender(RabbitTemplate rabbitTemplate){
        return  new CancelOrderSender(rabbitTemplate);
    }


    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(orderConnectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
