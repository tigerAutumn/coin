package com.qkwl.common.framework.mq.dynamic.consumer;

import com.qkwl.common.framework.mq.RabbitMqFastJsonConverter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.ClassUtils;

@Data
@Builder
public class DynamicConsumerContainerFactory implements FactoryBean<SimpleMessageListenerContainer> {

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


  /**
   * 所有的队列监听容器MAP
   */
  public final static ConcurrentHashMap<String, SimpleMessageListenerContainer> allQueue2ContainerMap = new ConcurrentHashMap<>();

    // 消费方
    private RabbitMQConsumer consumer;
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
    public SimpleMessageListenerContainer getObject() throws Exception {
        check();

        Queue queue = buildQueue();
        Exchange exchange = buildExchange();
        Binding binding = buildBinding(queue,exchange);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setAmqpAdmin(rabbitAdmin);
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queue);
        container.setPrefetchCount(1);
        container.setConcurrentConsumers(concurrentNum == null ? 1 : concurrentNum);
        container.setAcknowledgeMode(autoAck ? AcknowledgeMode.AUTO : AcknowledgeMode.MANUAL);
        container.setMessageConverter(new RabbitMqFastJsonConverter());
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor= new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setThreadNamePrefix( "MQAsyncExe-"+queue.getName());
        container.setTaskExecutor(simpleAsyncTaskExecutor);

        if (null != consumer) {
            container.setMessageListener(consumer);
        }
        allQueue2ContainerMap.putIfAbsent(StringUtils.trim(queue.getName()), container);
        return container;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleMessageListenerContainer.class;
    }
}
