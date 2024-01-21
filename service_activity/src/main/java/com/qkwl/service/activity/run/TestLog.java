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
@Component("testLog")
public class TestLog {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TestLog.class);

    @Scheduled(cron = "0 0/30 * * * ?")
    public void runSettlement() {
    	logger.info(">>>>半个小时闹钟");
    }

}
