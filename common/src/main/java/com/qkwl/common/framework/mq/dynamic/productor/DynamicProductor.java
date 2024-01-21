package com.qkwl.common.framework.mq.dynamic.productor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DynamicProductor {
    private static final Logger logger = LoggerFactory.getLogger(DynamicProductor.class);

    private RabbitMQProductor sender;

    public DynamicProductor(DynamicProductorContainerFactory fac, String name) throws Exception {
        RabbitMQProductor sender = fac.getObject();
        this.sender = sender;
    }

    public RabbitMQProductor getSender() {
        return sender;
    }

    public void setSender(RabbitMQProductor sender) {
        this.sender = sender;
    }
}
