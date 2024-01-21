package com.qkwl.common.framework.mq.dynamic.productor;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.framework.mq.dynamic.CorrelationData;
import com.qkwl.common.framework.mq.dynamic.RabbitMQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
public class RabbitMQProductor implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Value("${mq.retry.count}")
    private int mqRetryCount = 0;

    private RabbitTemplate rabbitTemplate;

    public RabbitMQProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(this::confirm);
        this.rabbitTemplate.setReturnCallback(this::returnedMessage);
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    }

    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public RabbitAdmin createAdmin() {
        return new RabbitAdmin(this.rabbitTemplate.getConnectionFactory());
    }

    public void bindTopicQueue(String topicExchangeName,String queueName, String routingKey) {
        RabbitAdmin admin = createAdmin();
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        TopicExchange topicExchange = new TopicExchange(topicExchangeName);
        admin.declareExchange(topicExchange);
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
        admin.declareBinding(binding);
        log.info("创建队列名：{},创建routingKey：{}", queueName, routingKey);
        // admin.setAutoStartup(true);
    }

    public void bindDirectQueue(String directExchangeName,String queueName, String routingKey) {
        RabbitAdmin admin = createAdmin();
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        DirectExchange directExchange = new DirectExchange(directExchangeName);
        admin.declareExchange(directExchange);
        Binding binding = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
        admin.declareBinding(binding);
        log.info("创建队列名：{},创建routingKey：{}", queueName, routingKey);
        // admin.setAutoStartup(true);
    }


    public void bindFanoutQueue(String fanoutExchangeName,String queueName, String routingKey) {
        RabbitAdmin admin = createAdmin();
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        FanoutExchange fanoutExchange = new FanoutExchange(fanoutExchangeName);
        admin.declareExchange(fanoutExchange);
        Binding binding = BindingBuilder.bind(queue).to(fanoutExchange);
        admin.declareBinding(binding);
        log.info("创建队列名：{},创建routingKey：{}", queueName, routingKey);
        // admin.setAutoStartup(true);
    }

    /**
     * 发送MQ消息,异步
     *
     * @param exchangeName 交换机名称
     * @param routingKey   路由名称
     * @param message      发送消息体
     */
    public void sendMessage(String exchangeName, String routingKey, final RabbitMQMessage message) {
        Assert.notNull(message, "message 消息体不能为NULL");
        Assert.notNull(exchangeName, "exchangeName 不能为NULL");
        Assert.notNull(routingKey, "routingKey 不能为NULL");
        // 获取CorrelationData对象
        CorrelationData correlationData = this.correlationData(message, message.getMessageId());
        correlationData.setExchange(exchangeName);
        correlationData.setRoutingKey(routingKey);
        correlationData.setMessage(message);

        log.info("发送MQ消息，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{}",
                correlationData.getId(), JSON.toJSONString(message), exchangeName, routingKey);
        // 发送消息
           this.convertAndSend(exchangeName, routingKey, message, correlationData);
    }

   /*
    public void sendAndReceiveMessage(String exchangeName, String routingKey, MarketMQMessage message) {
        Assert.notNull(message, "message 消息体不能为NULL");
        Assert.notNull(exchangeName, "exchangeName 不能为NULL");
        Assert.notNull(routingKey, "routingKey 不能为NULL");
        // 获取CorrelationData对象
        CorrelationData correlationData = this.correlationData(message,message.getMessageId());
        correlationData.setExchange(exchangeName);
        correlationData.setRoutingKey(routingKey);
        correlationData.setMessage(message);

        log.info("发送MQ消息，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{}",
                correlationData.getId(), JSON.toJSONString(message), exchangeName, routingKey);
        rabbitTemplate.convertSendAndReceive(exchangeName, routingKey, message,correlationData);
    }
    */

    @Override
    public void confirm(org.springframework.amqp.rabbit.connection.CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(" 回调id:" + correlationData);
        CorrelationData correlationDataExtends = null;
        if (correlationData instanceof CorrelationData) {
            correlationDataExtends = (CorrelationData) correlationData;
           /* if (correlationDataExtends.getMdcContainer() != null) {
                // 日志链路跟踪
                MDC.setContextMap(correlationDataExtends.getMdcContainer());
            }*/
        }

        // 消息回调确认失败处理
        if (!ack) {
            if (correlationDataExtends != null) {
                //消息发送失败,就进行重试，重试过后还不能成功就记录到数据库
                if (correlationDataExtends.getRetryCount() < mqRetryCount) {
                    log.info("MQ消息发送失败，消息重发，消息ID：{}，重发次数：{}，消息体:{}", correlationDataExtends.getId(),
                            correlationDataExtends.getRetryCount(), JSON.toJSONString(correlationDataExtends.getMessage()));

                    // 将重试次数加一
                    correlationDataExtends.setRetryCount(correlationDataExtends.getRetryCount() + 1);

                    // 重发发消息
                    this.convertAndSend(correlationDataExtends.getExchange(), correlationDataExtends.getRoutingKey(),
                            correlationDataExtends.getMessage(), correlationDataExtends);
                } else {
                    //消息重试发送失败,将消息放到数据库等待补发
                    log.error("MQ消息重发失败，消息ID：{}，消息体:{}", correlationData.getId(),
                            JSON.toJSONString(correlationDataExtends.getMessage()));
                }
            }
        } else {
            log.info("消息发送成功,消息ID:{}", correlationData.getId());
        }
    }

    /**
     * 用于实现消息发送到RabbitMQ交换器后接收ack回调。
     * 如果消息发送确认失败就进行重试。
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
  /*  @Override
    public void confirm(org.springframework.amqp.rabbit.connection.CorrelationData correlationData, boolean ack, String cause) {
        CorrelationData correlationDataExtends = null;
        if (correlationData instanceof CorrelationData) {
            correlationDataExtends = (CorrelationData) correlationData;
            if (correlationDataExtends.getMdcContainer() != null) {
                // 日志链路跟踪
                MDC.setContextMap(correlationDataExtends.getMdcContainer());
            }
        }

        // 消息回调确认失败处理
        if (!ack) {
            if (correlationDataExtends != null) {
                //消息发送失败,就进行重试，重试过后还不能成功就记录到数据库
                if (correlationDataExtends.getRetryCount() < mqRetryCount) {
                    log.info("MQ消息发送失败，消息重发，消息ID：{}，重发次数：{}，消息体:{}", correlationDataExtends.getId(),
                            correlationDataExtends.getRetryCount(), JSON.toJSONString(correlationDataExtends.getMessage()));

                    // 将重试次数加一
                    correlationDataExtends.setRetryCount(correlationDataExtends.getRetryCount() + 1);

                    // 重发发消息
                    this.convertAndSend(correlationDataExtends.getExchange(), correlationDataExtends.getRoutingKey(),
                            correlationDataExtends.getMessage(), correlationDataExtends);
                } else {
                    //消息重试发送失败,将消息放到数据库等待补发
                    log.error("MQ消息重发失败，消息ID：{}，消息体:{}", correlationData.getId(),
                            JSON.toJSONString(correlationDataExtends.getMessage()));

                  *//*  alertService.postAlert(MetricNameEnum.SYSTEM_INTERNAL_EXCEPTION, SystemTypeEnum.MQ.name(),
                            correlationDataExtends.getExchange(), null);*//*
                }
            }
        } else {
            log.info("消息发送成功,消息ID:{}", correlationData.getId());
        }
    }*/

    /**
     * 用于实现消息发送到RabbitMQ交换器，但无相应队列与交换器绑定时的回调。
     * 在脑裂的情况下会出现这种情况。
     */

    @Override
    public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {

        // 反序列化消息
        Object msg = rabbitTemplate.getMessageConverter().fromMessage(message);
       /* if (msg instanceof com.space.rabbitmq.dynamic.Message) {
            // 日志链路跟踪
            MDC.setContextMap(((com.space.rabbitmq.dynamic.Message) msg).getMdcContainer());
        }
*/
        log.error("MQ消息发送失败，replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，消息体:{}",
                replyCode, replyText, exchange, routingKey, JSON.toJSONString(msg));

        //alertService.postAlert(MetricNameEnum.SYSTEM_INTERNAL_EXCEPTION, SystemTypeEnum.MQ.name(), exchange, null);
    }

    /**
     * 消息相关数据（消息ID）
     *
     * @param message   消息体
     * @param messageId 消息ID
     * @return
     */
    private CorrelationData correlationData(Object message, String messageId) {
        // 消息ID默认使用UUID
        if (StringUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }
        return new CorrelationData(messageId, message);
    }

    /**
     * 发送消息
     *
     * @param exchange        交换机名称
     * @param routingKey      路由key
     * @param message         消息内容
     * @param correlationData 消息相关数据（消息ID）
     */
    public void convertAndSend(String exchange, String routingKey, final Object message, CorrelationData correlationData) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
        } catch (Exception e) {
            log.error("MQ消息发送异常，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{}",
                    correlationData.getId(), JSON.toJSONString(message), exchange, routingKey, e);

            // alertService.postAlert(MetricNameEnum.SYSTEM_INTERNAL_EXCEPTION, SystemTypeEnum.MQ.name(), exchange, null);
        }
    }


}
