package com.qkwl.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.permission.annotation.PassToken;

@RestController
@SpringBootApplication
@ImportResource(locations = "classpath:edas-hsf.xml")
public class Application extends SpringBootServletInitializer{

    @RequestMapping("/")
    @PassToken
    @ResponseBody
    public ReturnResult index(HttpServletResponse response) throws IOException {
        return ReturnResult.SUCCESS(I18NUtils.getString("Application.0"));
    }

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

