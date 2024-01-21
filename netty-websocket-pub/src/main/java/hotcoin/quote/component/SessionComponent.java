package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.SubResult;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: netty-ws
 * @Package: org.quote.utils
 * @ClassName: SessionComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/4/30 10:50
 * @Version: 1.0
 */
@Slf4j
@Component
public class SessionComponent {
    @Autowired
    private UserLinksComponent userLinksComponent;
    private static ConcurrentHashMap<String, Session> onlineSessions = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, Session> getOnlineSessions() {
        return onlineSessions;
    }

    public  void setOnlineSessions(Session session) {
        String sessionId = session.id().asLongText();
        onlineSessions.put(sessionId, session);
        log.info("save session success ,key is ->{}", sessionId);
    }

    public  void removeOnlineSession(Session session) {
        if (session == null || onlineSessions.isEmpty()) {
            return;
        }
        String sessionId = session.id().asLongText();
        onlineSessions.remove(sessionId);
    }

    /**
     * 存储session
     *
     * @param session
     */
    public void setSession2Local(Session session) {
        log.error("linked success ,Session id ->{}", session.id().asLongText());
        session.sendText(JSON.toJSONString(SubResult.optSuccess()));
        setOnlineSessions(session);
    }
    /**
     * 清除订阅关系
     *
     * @param session
     */
    public void cleanLinkContent(Session session) {
        userLinksComponent.removeUser(session.id().asLongText());
        removeOnlineSession(session);
    }

    public void dealPubEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    cleanLinkContent(session);
                    if (session.isActive()) {
                        session.close();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
