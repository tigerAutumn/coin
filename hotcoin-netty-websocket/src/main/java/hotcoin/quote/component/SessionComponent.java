package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.SubResult;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
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

    public void setOnlineSessions(Session session) {
        String sessionId = session.id().asLongText();
        onlineSessions.put(sessionId, session);
        log.info("save session success ,key is ->{}", sessionId);
    }

    public void removeOnlineSession(Session session) {
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
        try {
            setOnlineSessions(session);
            log.info("linked success ,Session id ->{}", session.id().asLongText());
            session.sendText(JSON.toJSONString(SubResult.optSuccess()));
        } catch (Exception e) {
            log.error("linked fail ,error is ->{}", e);
            session.sendText(JSON.toJSONString(SubResult.optError()));
        }
    }

    /**
     * 清除订阅关系
     *
     * @param session
     */
    public void cleanLinkContent(Session session) {
        if (session == null) {
            return;
        }
        userLinksComponent.removeUser(session.id().asLongText());
        removeOnlineSession(session);
    }

    /**
     * 删除失效session
     */
    public void cleanInValidSessions() {
        if (MapUtils.isEmpty(onlineSessions)) {
            return;
        }
        for (Map.Entry<String, Session> item : onlineSessions.entrySet()) {
            if (!item.getValue().isActive()) {
                cleanLinkContent(item.getValue());
            }
        }
    }

    public void dealPubEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    cleanLinkContent(session);
                    if (session.isActive()) {
                        log.info("package ping make connection closed");
                        session.close();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
