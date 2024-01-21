package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SMSTemplateConfig {

    @Value("${smsTemplate.otc.zh}")
    private String otcZhMapStr;

    @Value("${smsTemplate.otc.en}")
    private String otcEnMapStr;

    @Value("${smsTemplate.otc.ko}")
    private String otcKOMapStr;
}
