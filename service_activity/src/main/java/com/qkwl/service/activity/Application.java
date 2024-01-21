package com.qkwl.service.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

/**
 * Created by wangchen on 2017-05-23.
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
