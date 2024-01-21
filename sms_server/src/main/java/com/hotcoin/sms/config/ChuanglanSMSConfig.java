package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ChuanglanSMSConfig {

    @Value("${sms.chuanglan.accessKey}")
    private String accessKey;

    @Value("${sms.chuanglan.secretKey}")
    private String secretKey;

    @Value("${sms.chuanglan.url}")
    private String url;

    @Value("${sms.chuanglan.accessKey.international}")
    private String internationalAccessKey;

    @Value("${sms.chuanglan.secretKey.international}")
    private String internationalSecretKey;

    @Value("${sms.chuanglan.url.international}")
    private String internationalUrl;
}
