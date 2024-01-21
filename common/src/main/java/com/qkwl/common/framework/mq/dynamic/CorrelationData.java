package com.qkwl.common.framework.mq.dynamic;

import lombok.Data;

@Data
public class CorrelationData extends org.springframework.amqp.rabbit.connection.CorrelationData {


    /**
     * MDC容器
     * 获取父线程MDC中的内容，做日志链路
     */
    //private Map<String, String> mdcContainer = MDC.getCopyOfContextMap();

    /**
     * 消息体
     */
    private volatile Object message;

    /**
     * 交换机名称
     */
    private String exchange;

    /**
     * 路由key
     */
    private String routingKey;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    public CorrelationData(String id) {
        super(id);
    }

    public CorrelationData(String id, Object data) {
        this(id);
        this.message = data;
    }
}
