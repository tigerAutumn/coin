package com.hotcoin.notice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class MeilianSMSConfig {

    @Value("${sms.meilian.userName}")
    private String userName;

    @Value("${sms.meilian.password}")
    private String password;

    @Value("${sms.meilian.url}")
    private String url;

    @Value("${sms.meilian.apikey}")
    private String apikey;
}