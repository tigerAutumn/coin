package com.qkwl.common.framework.mq.dynamic;

import org.slf4j.MDC;
import org.springframework.amqp.core.MessageProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * MQ消息的父类消息体
 *
 * @author yuhao.wang
 */

public class RabbitMQMessage implements Serializable {
    private static final long serialVersionUID = -4731326195678504565L;

    /**
     * MDC容器
     * 获取父线程MDC中的内容，做日志链路
     */
    private Map<String, String> mdcContainer = MDC.getCopyOfContextMap();

    /**
     * 消息ID(消息的唯一标示)
     */
    private String messageId;

    private Object message;

    private String tradeId;

    private MessageProperties messageProperties;

    public RabbitMQMessage(String messageId, Object message, String tradeId, MessageProperties messageProperties) {
        this.messageId = messageId;
        this.message = message;
        this.tradeId = tradeId;
        this.messageProperties = messageProperties;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public MessageProperties getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(MessageProperties messageProperties) {
        this.messageProperties = messageProperties;
    }
}
