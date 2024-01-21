package com.qkwl.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qkwl.common.util.RespData;
import com.qkwl.web.permission.annotation.PassToken;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
@SpringBootApplication
@ImportResource(locations = "classpath:edas-hsf.xml")
public class Application extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("io.netty.allocator.type", "unpooled");
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.err.println("*************hk-web start****************"); 
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


