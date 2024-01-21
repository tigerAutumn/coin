package com.hotcoin.webchat.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SMSConfig {

    @Value("${sms.accessKey}")
    private String accessKey;

    @Value("${sms.secretKey}")
    private String secretKey;

    @Value("${sms.url}")
    private String url;

    @Value("${sms.accessKey.international}")
    private String internationalAccessKey;

    @Value("${sms.secretKey.international}")
    private String internationalSecretKey;

    @Value("${sms.url.international}")
    private String internationalUrl;
}
