package com.qkwl.common.framework.mq.dynamic.consumer;

import com.rabbitmq.client.AMQP.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;


public class DynamicConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DynamicConsumer.class);
    private SimpleMessageListenerContainer container;

    public DynamicConsumer(DynamicConsumerContainerFactory fac, String queueName, RabbitMQConsumer rabbitMQConsumer) throws Exception {
        SimpleMessageListenerContainer container = DynamicConsumerContainerFactory.allQueue2ContainerMap.get(queueName);
        if(null == container){
            container = fac.getObject();
        }
      //  SimpleMessageListenerContainer container = fac.getObject();
        container.setMessageListener(rabbitMQConsumer);
        Queue queue = new Queue();

        //container.setConcurrentConsumers(16);
        //container.setMaxConcurrentConsumers(16);
        container.setExclusive(true);
        this.container = container;
    }

    //启动消费者监听
    public void start() {
        container.start();
    }

    //消费者停止监听
    public void stop() {
        container.stop();
    }

    //消费者重启
    public void shutdown() {
        container.shutdown();
    }


   /* *//**
     * 用户扩展处理消息
     *//*
    public void distributionConsumerMsg(Message message, Channel channel) {
        System.out.println("MQ消息处理异常，消息ID："+message.getMessageProperties().getDeliveryTag()+"，消息体:{}"+JSON.toJSONString(message));
        logger.error("MQ消息处理异常，消息ID：{}，消息体:{}", message.getMessageProperties().getDeliveryTag(),
                JSON.toJSONString(message));

    }*/

}
