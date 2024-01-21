package com.qkwl.service.activity.run;

import com.qkwl.service.activity.impl.UserLogService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志定时统计
 *
 * @author TT
 */
@Component("autoLog")
public class AutoLog {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoLog.class);

    @Autowired
    private UserLogService userLogService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void runSettlement() {
    	logger.info("--------> 开始统计用户存币量  <--------------");
        try {
            // 定时器更新
            userLogService.upCoinJob();
        } catch (Exception e) {
            logger.info("----> autoclog failed upCoinJob",e);
        }
        logger.info("--------> 用户存币量统计结束  <--------------");
        logger.info("--------> 开始统计用户交易量  <--------------");
        try {
            // 定时器更新
            userLogService.upTradeJob();
        } catch (Exception e) {
            logger.info("----> autoclog failed upTradeJob",e);
        }
        logger.info("--------> 用户交易量统计结束  <--------------");
    }

}
