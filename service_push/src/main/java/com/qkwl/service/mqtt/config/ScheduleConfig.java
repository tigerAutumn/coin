package com.qkwl.service.mqtt.config;

import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableAsync
public class ScheduleConfig implements SchedulingConfigurer {

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    //设定一个长度10的定时任务线程池
    taskRegistrar.setScheduler(Executors.newScheduledThreadPool(16));
  }
}
