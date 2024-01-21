package com.qkwl.common.framework.mq.dynamic.productor;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.FactoryBean;

@Data
@Builder
public class DynamicProductorContainerFactory implements FactoryBean<RabbitMQProductor> {

    private String directExchange;
    private String topicExchange;
    private String fanoutExchange;

    private String queue;
    private String routingKey;


    private Boolean autoDeleted;
    private Boolean durable;
    private Boolean autoAck;

    private ConnectionFactory connectionFactory;
    private RabbitAdmin rabbitAdmin;
    private Integer concurrentNum;

    private Exchange exchange;


    private Exchange buildExchange() {
        if (null != directExchange) {
           // exchangeType = Exchang;
            return new DirectExchange(directExchange);
        } else if (null != topicExchange) {
           // exchangeType = ExchangeType.TOPIC;
            return new TopicExchange(topicExchange);
        } else if (null != fanoutExchange) {
           // exchangeType = ExchangeType.FANOUT;
            return new FanoutExchange(fanoutExchange);
        } else {
            if (StringUtils.isEmpty(routingKey)) {
                throw new IllegalArgumentException("defaultExchange's routingKey should not be null!");
            }
           // exchangeType = ExchangeType.DEFAULT;
            return new DirectExchange("");
        }
    }

    private Binding buildBinding(Queue queue, Exchange exchange) {
        if (null != directExchange) {
            // exchangeType = Exchang;
            return bindDirect(queue,(DirectExchange) exchange,routingKey);
        } else if (null != topicExchange) {
            return bindTopic(queue,(TopicExchange) exchange,routingKey);
        } else if (null != fanoutExchange) {
            return bindFanout(queue,(FanoutExchange) exchange);
        } else {
            if (StringUtils.isEmpty(routingKey)) {
                throw new IllegalArgumentException("defaultExchange's routingKey should not be null!");
            }
            // exchangeType = ExchangeType.DEFAULT;
            return bindDirect(queue,(DirectExchange) exchange,routingKey);
        }
    }


    private Queue buildQueue() {
        if (StringUtils.isEmpty(queue)) {
            throw new IllegalArgumentException("queue name should not be null!");
        }

        return new Queue(queue, durable == null ? false : durable, false, autoDeleted == null ? true : autoDeleted);
    }


    /**
     * 绑定一个队列到一个匹配型交换器使用一个DirectExchange
     * @param queue
     * @param exchange
     * @param routingKey
     */
    private Binding bindDirect(Queue queue ,DirectExchange exchange,String routingKey){
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    /**
     * 绑定一个队列到一个匹配型交换器使用一个topicExchange
     * @param queue
     * @param topicExchange
     * @param routingKey
     */
    private Binding bindTopic(Queue queue,TopicExchange topicExchange,String routingKey){
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }

    /**
     * 绑定一个队列到一个匹配型交换器使用一个FanoutExchange
     * @param queue
     * @param exchange
     */
    private Binding bindFanout(Queue queue ,FanoutExchange exchange){
       return BindingBuilder.bind(queue).to(exchange);
    }


    private void check() {
        if (null == rabbitAdmin || null == connectionFactory) {
            throw new IllegalArgumentException("rabbitAdmin and connectionFactory should not be null!");
        }
    }


    @Override
    public RabbitMQProductor getObject() throws Exception {
        check();

        Queue queue = buildQueue();
        Exchange exchange = buildExchange();
        Binding binding = buildBinding(queue,exchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        RabbitMQProductor rabbitMQProductor = new RabbitMQProductor(rabbitTemplate);
        return rabbitMQProductor;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleMessageListenerContainer.class;
    }
}
