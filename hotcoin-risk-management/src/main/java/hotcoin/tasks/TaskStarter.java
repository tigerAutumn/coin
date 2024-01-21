package hotcoin.tasks;

import hotcoin.component.HttpClientComponent;
import hotcoin.component.RabbitMqSendComponent;
import hotcoin.model.po.SystemRiskManagementPo;
import hotcoin.service.RealTimeTradeStatService;
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
    private RealTimeTradeStatService realTimeTradeStatService;

    @Autowired
    private HttpClientComponent httpClientComponent;
    @Autowired
    private RabbitMqSendComponent rabbitMqSendComponent;
    @PostConstruct
    public void init() {
       executor.scheduleAtFixedRate(new RealTimeTradeStatTask(), 0, 1, TimeUnit.SECONDS);
    }

    public class RealTimeTradeStatTask extends TimerTask {
        @Override
        public void run() {
            realTimeTradeStatService.processWalletInfo();
        }
    }

    public class TestMQSendTask extends TimerTask {
        @Override
        public void run() {
            log.error("start test tasks----------------------------------------------------");
            SystemRiskManagementPo riskManagementPo = new SystemRiskManagementPo();
            riskManagementPo.setRechargeFunds(200.0);
            riskManagementPo.setTelephone("1");
            riskManagementPo.setCapitalWarningLine(20.0);
            riskManagementPo.setDebitTimes(3.0);
            riskManagementPo.setCoinId(9);
            riskManagementPo.setUserStatus(1);
            rabbitMqSendComponent.sendCloseoutMQAction(riskManagementPo,9);
            //  rocketMqSendComponent.sendCancelOrderAction(70009, mqEntrust);
            //realTimeTradeStatService.test(1600078, 9);
        }
    }

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
}
