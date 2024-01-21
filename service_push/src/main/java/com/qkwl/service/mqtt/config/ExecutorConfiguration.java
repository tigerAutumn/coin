package com.qkwl.service.mqtt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class ExecutorConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfiguration.class);

    @Bean("asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {

        logger.info("start asyncServiceExecutor");

        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(4);
        //配置最大线程数
        executor.setMaxPoolSize(8);
        //线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(5);
        //配置队列大小
        executor.setQueueCapacity(40);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //执行初始化
        executor.initialize();
        return executor;
    }

}
