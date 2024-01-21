package com.hotcoin.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ServiceUploadApplication {
	
	
	public static void main(String[] args) {
		SpringApplication.run(ServiceUploadApplication.class, args);
	}
    
    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    public String health(){
    	return "yes";
    }
}
