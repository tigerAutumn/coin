package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TimeZoneConfig {
    @Value("${time-zone}")
    private String timeZone;

}
