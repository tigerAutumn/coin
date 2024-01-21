package hotcoin.quote.controller.ws;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.annotation.*;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.SubMessageHelperComponent;
import hotcoin.quote.controller.BaseWsController;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import hotcoin.quote.resp.SubResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static hotcoin.quote.model.CodeMsg.REQUEST_PARAM_ERROR;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: TradeMultipleWsController
 * @Author: hf
 * @Description: 综合接口, 所有订阅走该接口
 * @Date: 2019/6/17 14:00
 * @Version: 1.0
 */
@Slf4j
@Component
@ServerEndpoint(value = "/trade/multiple")
public class TradeMultipleWsController extends BaseWsController {
    @Autowired
    private SubMessageHelperComponent subMessageHelperComponent;
    @Autowired
    private SessionComponent sessionComponent;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        if (session == null) {
            session.sendText(JSON.toJSONString(SubResult.optError()));
            session.close();
        }
        sessionComponent.setSession2Local(session);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        if (session != null) {
            sessionComponent.cleanLinkContent(session);
            if (session.isActive()) {
                session.close();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws error happened->{}", throwable.getMessage());
        if (session != null) {
            sessionComponent.cleanLinkContent(session);
            if (session.isActive()) {
                session.close();
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            if (StringUtils.isEmpty(message)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return;
            }
            subMessageHelperComponent.pubMessageDeal(session, message);
        } catch (Exception e) {
            log.error("multiple controller convert message fail->{}", e);
            session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
        }

    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {

    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        sessionComponent.dealPubEvent(session, evt);
    }

    @Override
    public Boolean isValidSubParam(String sub) {
        return Boolean.FALSE;
    }
}
