package com.qkwl.service.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

/**
 * Created by wangchen on 2017-05-23.
 */

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@ImportResource(locations = "classpath:edas-hsf.xml")
public class Application extends SpringBootServletInitializer {

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
       return application.sources(Application.class);
   }

   public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
   }

}

