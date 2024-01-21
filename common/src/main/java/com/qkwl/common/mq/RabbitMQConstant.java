package com.qkwl.common.mq;

public interface RabbitMQConstant {

    public static final String ORDER_EXCHANGE_PRE = "order.exchange.topic.";
    public static final String ORDER_ROUTINGKEY_PRE = "order.topic.";
    public static final String ORDER_QUEUE_PRE = "order.topic.";

    public static final String ENTRUSTSTATE_EXCHANGE_PRE = "entruststate.exchange.topic.";
    public static final String ENTRUSTSTATE_ROUTINGKEY_PRE = "entruststate.topic.";
    public static final String MARKET_QUEUE_PRE = "market.topic.";
    public static final String WSS_QUEUE_PRE = "wss.topic.";

    public static final String TOPIC_SUF = ".#";

    public static final String ENTRUST_EXCHANGE_PRE = "entrust.exchange.topic.";
    public static final String ENTRUST_ROUTINGKEY_PRE = "entrust.topic.";
    public static final String ENTRUST_QUEUE_PRE = "entrust.topic.";

    public static final String CLOSE_POSITION_EXCHANGE = "exchange.topic.close_position";
    public static final String CLOSE_POSITION_ROUTINGKEY = "topic.close_position.#";
    public static final String CLOSE_POSITION_QUEUE = "topic.close_position.queue";
}
