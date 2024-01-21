package com.qkwl.common.framework.mq.dynamic.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.io.IOException;

public abstract class RabbitMQConsumer implements IDynamicConsumer {
    private volatile boolean end = false;
    private SimpleMessageListenerContainer container;
    private boolean autoAck;

    @Override
    public void setContainer(SimpleMessageListenerContainer container) {
        this.container = container;
        autoAck = container.getAcknowledgeMode().isAutoAck();
    }

    @Override
    public void shutdown() {
        end = true;
    }

    protected void autoAck(Message message, Channel channel, boolean success) throws IOException {
        if (autoAck) {
            return;
        }
        if (success) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            autoAck(message, channel, process(message, channel));
        } catch (Exception e) {
            autoAck(message, channel, false);
            throw e;
        } finally {
            if (end) {
                container.stop();
            }
        }
    }

    public abstract boolean process(Message message, Channel channel);
}
