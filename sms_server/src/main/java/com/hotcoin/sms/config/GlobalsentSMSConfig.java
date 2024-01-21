package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
