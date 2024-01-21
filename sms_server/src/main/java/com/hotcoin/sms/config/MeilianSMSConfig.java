package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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