package com.qkwl.common.framework.mq.dynamic;


import com.qkwl.common.framework.mq.dynamic.consumer.DynamicConsumer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomizeDynamicConsumerContainer{

    /**
     * 用于存放全局消费者
     */
    public static final Map<String, DynamicConsumer> dynamicConsumerContainer= new ConcurrentHashMap<>();
}
