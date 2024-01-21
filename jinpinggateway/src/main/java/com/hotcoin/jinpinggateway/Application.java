package com.hotcoin.jinpinggateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qkwl.common.util.RespData;

@SpringBootApplication
@EnableZuulProxy
@ImportResource(locations = "classpath:edas-hsf.xml")
@RestController
public class Application  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    public RespData<Void> health(){
    	return RespData.ok();
    }
    
}

