package hotcoin.quote.tasks;

import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.service.GroupSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Author: hf
 * @Description:
 * @Date: 2019/9/10 11:59
 * @Version: 1.0
 */
@Slf4j
@Component
public class TaskStarter {
    @Autowired
    private GroupSendService groupSendService;
    @Autowired
    private SessionComponent sessionComponent;

    @PostConstruct
    public void init() {
        new Timer().schedule(new StarCoinTask(), 0, 400);
        new Timer().schedule(new CleanInvalidSessions(), 0, 1000 * 60 * 60);
    }

    /**
     * 定时推送明星币
     */
    public class StarCoinTask extends TimerTask {
        @Override
        public void run() {
            groupSendService.groupSendStarCoinData();
        }
    }

    /**
     * 清除无效session
     */
    public class CleanInvalidSessions extends TimerTask {
        @Override
        public void run() {
            sessionComponent.cleanInValidSessions();
        }
    }
}
