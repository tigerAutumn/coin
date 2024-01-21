package com.qkwl.common.framework.mq.dynamic.productor;

import com.qkwl.common.framework.mq.dynamic.consumer.DynamicConsumer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

//@Component
public class DynamicProductorContainer {

  /**
   * 用于存放全局消费者
   */
  public final static Map<String, DynamicConsumer> dynamicTopicConsumerContainer= new ConcurrentHashMap<>();

  /**
   * 用于存放全局消费者
   */
  public final static Map<String, DynamicConsumer> dynamicDirectTopicConsumerContainer= new ConcurrentHashMap<>();

  /**
   * 用于存放全局消费者
   */
  public final static Map<String, DynamicConsumer> dynamicFanoutConsumerContainer= new ConcurrentHashMap<>();
}
