package com.qkwl.service.entrust;

import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@EnableTransactionManagement
@ImportResource(locations = "classpath:edas-hsf.xml")
@MapperScan({"com.qkwl.service.common.mapper","com.qkwl.service.entrust.dao"})
public class Application extends SpringBootServletInitializer {

	@RequestMapping("/")
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
}
