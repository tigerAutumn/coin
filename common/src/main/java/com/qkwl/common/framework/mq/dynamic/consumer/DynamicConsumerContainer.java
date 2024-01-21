package com.qkwl.common.framework.mq.dynamic.consumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

//@Component
public class DynamicConsumerContainer{

  /**
   * 用于存放全局消费者
   */
  public final Map<String, DynamicConsumer> dynamicTopicConsumerContainer= new ConcurrentHashMap<>();

  /**
   * 用于存放全局消费者
   */
  public final Map<String, DynamicConsumer> dynamicDirectTopicConsumerContainer= new ConcurrentHashMap<>();

  /**
   * 用于存放全局消费者
   */
  public final Map<String, DynamicConsumer> dynamicFanoutConsumerContainer= new ConcurrentHashMap<>();
}
