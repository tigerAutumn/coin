package com.hotcoin.notice.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration // 声明配置类
@EnableAsync // 开启异步任务支持
public class TaskPoolConfig  {

	 @Bean("sendSmsExecutor")
     public Executor taskExecutor() {
         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         //线程池维护线程的最少数量
         executor.setCorePoolSize(50);
         //线程池维护线程的最大数量
         executor.setMaxPoolSize(100);
         //缓存队列
         executor.setQueueCapacity(200);
         //允许的空闲时间
         executor.setKeepAliveSeconds(200);
         executor.setThreadNamePrefix("sendSmsExecutor-");
         //对拒绝task的处理策略
         executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
         return executor;
     }

}
