package hotcoin.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.framework.mq.RabbitMqFastJsonConverter;
import com.qkwl.common.framework.mq.dynamic.CorrelationData;
import com.qkwl.common.framework.mq.dynamic.RabbitMQMessage;
import com.qkwl.common.framework.mq.dynamic.RabbitMQProductor;
import com.qkwl.common.mq.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 临时使用的orderSender的代码
 */
@Slf4j
public class CancelOrderSender implements RabbitMQProductor {

    private int mqRetryCount = 0;

    private RabbitTemplate rabbitTemplate;

    private  CorrelationData correlationData;

    public CancelOrderSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(new RabbitMqFastJsonConverter());
        this.rabbitTemplate.setConfirmCallback(this::confirm);
        this.rabbitTemplate.setReturnCallback(this::returnedMessage);
        this.rabbitTemplate.setMandatory(true);
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    /**
     * 发送MQ消息,异步
     *
     * @param message      发送消息体
     */
    @Override
    public void sendAsync(RabbitMQMessage message) {
        Assert.notNull(message, "message 消息体不能为NULL");
        // 获取CorrelationData对象
        CorrelationData correlationData = this.correlationData(message, message.getMessageId());
        String exchangeName = RabbitMQConstant.ORDER_EXCHANGE_PRE+message.getTradeId();
        correlationData.setExchange(exchangeName);
        String routingKey = RabbitMQConstant.ORDER_ROUTINGKEY_PRE+message.getTradeId()+RabbitMQConstant.TOPIC_SUF;
        correlationData.setRoutingKey(routingKey);
        correlationData.setMessage(message);
        this.setCorrelationData(correlationData);

        log.info("发送MQ消息，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{}",
                correlationData.getId(), JSON.toJSONString(message), exchangeName, routingKey);
        // 发送消息
        this.convertAndSend(exchangeName, routingKey, message, correlationData);
    }


    /**
     * 用于实现消息发送到RabbitMQ交换器后接收ack回调。
     * 如果消息发送确认失败就进行重试。
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(org.springframework.amqp.rabbit.connection.CorrelationData correlationData, boolean ack, String cause) {
        log.info(" 回调id:" + correlationData);
        CorrelationData correlationDataExtends = null;
        if (correlationData instanceof CorrelationData) {
            correlationDataExtends = (CorrelationData)correlationData;
        }

        if (!ack) {
            if (correlationDataExtends != null) {
                if (correlationDataExtends.getRetryCount() < this.mqRetryCount) {
                    log.info("MQ消息发送失败，消息重发，消息ID：{}，重发次数：{}，消息体:{}", new Object[]{correlationDataExtends.getId(), correlationDataExtends.getRetryCount(), JSON.toJSONString(correlationDataExtends.getMessage())});
                    correlationDataExtends.setRetryCount(correlationDataExtends.getRetryCount() + 1);
                    convertAndSend(correlationDataExtends.getExchange(), correlationDataExtends.getRoutingKey(), correlationDataExtends.getMessage(), correlationDataExtends);
                } else {
                    log.error("MQ消息重发失败，消息ID：{}，消息体:{},cause:{}", correlationData.getId(), JSON.toJSONString(correlationDataExtends.getMessage()),cause);
                }
            }
        } else {
            log.info("消息发送成功,消息ID:{}", correlationData.getId());
        }

    }

    @Override
    public CorrelationData getCorrelationData() {
        return this.correlationData;
    }

    public void setCorrelationData(final CorrelationData correlationData) {
        this.correlationData = correlationData;
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
     * 用于实现消息发送到RabbitMQ交换器，但无相应队列与交换器绑定时的回调。
     * 在脑裂的情况下会出现这种情况。
     */
    @Override
    public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
        Object msg = rabbitTemplate.getMessageConverter().fromMessage(message);
        log.error("MQ消息发送失败，replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，消息体:{}", new Object[]{replyCode, replyText, exchange, routingKey, JSON.toJSONString(msg)});
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
            log.error("MQ消息发送异常，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{},e:{}",
                    correlationData.getId(), JSON.toJSONString(message), exchange, routingKey, e);

        }
    }

}
