package com.qkwl.service.validate.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.qkwl.common.properties.NoticeProperties;

@Configuration
@EnableConfigurationProperties(NoticeProperties.class)
public class NoticeConfig {

}
