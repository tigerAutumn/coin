package com.qkwl.initMarket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.qkwl.initMarket.service.EntrustOrderTx;

@SpringBootApplication
public class Application implements CommandLineRunner {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private EntrustOrderTx entrustServerTx;
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Override
	public void run(String... args) {
		//初始委托单
		logger.info("========初始委托单开始========");
		entrustServerTx.initMarket();
		logger.info("========初始委托单完成========");
	}
}
