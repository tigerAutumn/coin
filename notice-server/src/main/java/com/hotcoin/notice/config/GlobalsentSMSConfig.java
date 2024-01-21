package com.hotcoin.notice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class GlobalsentSMSConfig {
    @Value("${sms.globalsent.accessKey}")
    private String accessKey;

    @Value("${sms.globalsent.url}")
    private String url;

    @Value("${sms.globalsent.callBackUrl}")
    private String callback_url;
}
