package com.qkwl.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qkwl.common.auth.SessionContextUtils;

/**
 * Created by muji on 2017/5/24.
 */
@Configuration
public class BeanConfig {

    @Bean
    public SessionContextUtils sessionContextUtils() {
        return new SessionContextUtils();
    }
}

