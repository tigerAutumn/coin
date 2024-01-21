package com.qkwl.common.framework.mq.dynamic;

import com.qkwl.common.framework.mq.dynamic.consumer.DynamicConsumer;
import com.qkwl.common.framework.mq.dynamic.consumer.DynamicConsumerContainerFactory;
import com.qkwl.common.framework.mq.dynamic.consumer.RabbitMQConsumer;
import com.qkwl.common.framework.mq.dynamic.productor.DynamicProductor;
import com.qkwl.common.framework.mq.dynamic.productor.DynamicProductorContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

public class ConsumerGenerate {


    /**
     * 创建DirectExchange消费者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicConsumer genDirectConsumer(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                                    String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                                    boolean autoAck, RabbitMQConsumer rabbitMQConsumer) throws Exception {
        DynamicConsumerContainerFactory fac =
                DynamicConsumerContainerFactory.builder().directExchange(exchangeName).queue(queueName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicConsumer(fac, queueName,rabbitMQConsumer);
    }


    /**
     * 创建Topic消费者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicConsumer genTopicConsumer(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                              String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                              boolean autoAck,RabbitMQConsumer rabbitMQConsumer) throws Exception {
        DynamicConsumerContainerFactory fac =
                DynamicConsumerContainerFactory.builder().topicExchange(exchangeName).queue(queueName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicConsumer(fac, queueName,rabbitMQConsumer);
    }


    /**
     * 创建Fanout消费者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicConsumer genFanoutConsumer(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                              String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                              boolean autoAck,RabbitMQConsumer rabbitMQConsumer) throws Exception {
        DynamicConsumerContainerFactory fac =
                DynamicConsumerContainerFactory.builder().fanoutExchange(exchangeName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicConsumer(fac, queueName,rabbitMQConsumer);
    }



    /**
     * 创建Direct生产者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicProductor getDirectProductor(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                                      String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                                      boolean autoAck) throws Exception {
        DynamicProductorContainerFactory fac =
                DynamicProductorContainerFactory.builder().directExchange(exchangeName).queue(queueName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicProductor(fac, queueName);
    }


    /**
     * 创建Topic生产者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicProductor getTopicProductor(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                                      String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                                      boolean autoAck) throws Exception {
        DynamicProductorContainerFactory fac =
                DynamicProductorContainerFactory.builder().topicExchange(exchangeName).queue(queueName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicProductor(fac, queueName);
    }


    /**
     * 创建Fanout生产者
     *
     * @param connectionFactory
     * @param rabbitAdmin
     * @param exchangeName
     * @param queueName
     * @param routingKey
     * @param autoDelete
     * @param durable
     * @param autoAck
     * @return
     * @throws Exception
     */
    public static DynamicProductor getFanoutProductor(ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin,
                                                     String exchangeName, String queueName, String routingKey, boolean autoDelete, boolean durable,
                                                     boolean autoAck) throws Exception {
        DynamicProductorContainerFactory fac =
                DynamicProductorContainerFactory.builder().fanoutExchange(exchangeName).queue(queueName).autoDeleted(autoDelete)
                        .autoAck(autoAck).durable(durable).routingKey(routingKey).rabbitAdmin(rabbitAdmin)
                        .connectionFactory(connectionFactory).build();
        return new DynamicProductor(fac, queueName);
    }

}
