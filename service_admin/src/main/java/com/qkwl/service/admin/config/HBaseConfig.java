package com.qkwl.service.admin.config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qkwl.service.admin.bc.utils.HBaseUtils;

@Configuration
@EnableConfigurationProperties(HBaseProperties.class)
public class HBaseConfig {

    private final HBaseProperties properties;

    public HBaseConfig(HBaseProperties properties) {
        this.properties = properties;
    }

    @Bean
    public HBaseUtils hbaseUtils() {
    	HBaseUtils hBaseUtils = new HBaseUtils(properties);
        return hBaseUtils;
    }
    
}