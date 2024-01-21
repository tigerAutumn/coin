package com.qkwl.service.admin.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration // 声明配置类
@EnableAsync // 开启异步任务支持
public class TaskPoolConfig  {

	 @Bean("taskExecutor")
     public Executor taskExecutor() {
         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
         executor.setCorePoolSize(20);
         executor.setMaxPoolSize(50);
         executor.setQueueCapacity(200);
         executor.setKeepAliveSeconds(60);
         executor.setThreadNamePrefix("taskExecutor-");
         executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
         return executor;
     }

}
