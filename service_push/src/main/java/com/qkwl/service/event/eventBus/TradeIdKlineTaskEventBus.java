package com.qkwl.service.event.eventBus;

import com.google.common.eventbus.AsyncEventBus;
import com.qkwl.service.event.PushEventListener;
import com.qkwl.service.event.TradeIdKlineTaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TradeIdKlineTaskEventBus {
    private static final Logger logger = LoggerFactory.getLogger(TradeIdKlineTaskEventBus.class);
    /**
     * 时间任务总线
     */
    private final static AsyncEventBus tradeIdKlineTaskEventBus = new AsyncEventBus(new ThreadPoolExecutor(4, 8, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()),
            (throwable, subscriberExceptionContext) -> {
                 logger.error(
                "Failed to dispatch event to " + subscriberExceptionContext.getSubscriberMethod() + ": " + throwable
        );});

    /**
     * 触发同步事件
     *
     * @param event
     */
    public static void post(TradeIdKlineTaskEvent event) {
        tradeIdKlineTaskEventBus.post(event);
    }

    /**
     * 注册事件处理器
     *
     * @param handler
     */
    public static void register(PushEventListener handler) {
        tradeIdKlineTaskEventBus.register(handler);
    }

    /**
     * 注销事件处理器
     *
     * @param handler
     */
    public static void unregister(PushEventListener handler) {
        tradeIdKlineTaskEventBus.unregister(handler);
    }
}
