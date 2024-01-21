package com.qkwl.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qkwl.common.util.RespData;
import com.qkwl.web.permission.annotation.PassToken;

@RestController
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@ImportResource(locations = "classpath:edas-hsf.xml")
public class Application extends SpringBootServletInitializer{


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    @PassToken
    public RespData<Void> health(){
    	return RespData.ok();
    }
}
