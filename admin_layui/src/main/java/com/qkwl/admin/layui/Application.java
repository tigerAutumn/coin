package com.qkwl.admin.layui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.qkwl.common.auth.SessionContextUtils;

@ImportResource(locations = "classpath:edas-hsf.xml")
@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer {

  @Bean
  public SessionContextUtils sessionContextUtils() {
      return new SessionContextUtils();
  }
   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
       return application.sources(Application.class);
   }

   public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
       System.err.println("*************hk-admin_layui start****************"); 
   }

}
