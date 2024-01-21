package com.qkwl.admin.layui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.qkwl.admin.layui.dialects.decimal.DecimalDialects;
import com.qkwl.admin.layui.dialects.order.OrderDialects;

/**
 * Thymelea自定义方言配置
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public DecimalDialects decimalformat() {
        return new DecimalDialects();
    }

    @Bean
    public OrderDialects orderDialects() {
        return new OrderDialects();
    }

}
