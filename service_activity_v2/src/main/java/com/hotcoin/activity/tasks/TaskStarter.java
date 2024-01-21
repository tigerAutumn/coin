package com.hotcoin.activity.tasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.tasks
 * @ClassName: PeriodTaskStarter
 * @Author: hf
 * @Description:
 * @Date: 2019/6/12 11:59
 * @Version: 1.0
 */
@Slf4j
@Component
public class TaskStarter {
    @Autowired
    private UserRegisterActivityTask userRegisterActivityTask;
    @Autowired
    private UserRechargeActivityTask userRechargeActivityTask;
    @Autowired
    private UserTradeActivityTask userTradeActivityTask;
    @Autowired
    private GiveCandyTask giveCandyTask;

    @PostConstruct
    public void init() {
        executor.scheduleAtFixedRate(new RegisterActivityTask(), 0, 20, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new GiveCandyActivityTask(), 0, 20, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new RechargeActivityTask(), 0, 4, TimeUnit.HOURS);
        executor.scheduleAtFixedRate(new TradeActivityTask(), 0, 4, TimeUnit.HOURS);
    }

    public class RegisterActivityTask extends TimerTask {
        @Override
        public void run() {
            userRegisterActivityTask.runRegisterActivityTask();
        }
    }

    public class RechargeActivityTask extends TimerTask {
        @Override
        public void run() {
            userRechargeActivityTask.runRechargeActivityTask();
        }
    }

    public class TradeActivityTask extends TimerTask {
        @Override
        public void run() {
            userTradeActivityTask.runTradeActivityTask();
        }
    }

    public class GiveCandyActivityTask extends TimerTask {
        @Override
        public void run() {
            giveCandyTask.runGiveCandyActivityTask();
        }
    }

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(8);
}
