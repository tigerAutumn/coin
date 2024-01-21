package hotcoin.quote.controller.ws;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.annotation.*;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.SubMessageHelperComponent;
import hotcoin.quote.constant.DepthConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.controller.BaseWsController;
import hotcoin.quote.pojo.ParameterMap;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import hotcoin.quote.resp.SubResult;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static hotcoin.quote.resp.CodeMsg.REQUEST_PARAM_ERROR;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: TradeDepthWsWsController
 * @Author: hf
 * @Description:
 * @Date: 2019/5/7 14:48
 * @Version: 1.0
 */
@Component
@Slf4j
@ServerEndpoint(value = "/trade/depth")
public class TradeDepthWsWsController extends BaseWsController {
    @Autowired
    private SubMessageHelperComponent subMessageHelperComponent;
    @Autowired
    private SessionComponent sessionComponent;

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, ParameterMap parameterMap) throws Exception {
        if (session == null) {
            session.sendText(JSON.toJSONString(SubResult.optError()));
            session.close();
            return;
        }
        sessionComponent.setSession2Local(session);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        if (session == null) {
            return;
        }
        if (session.isActive()) {
            session.close();
        }
        sessionComponent.cleanLinkContent(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws error happened->{}", throwable.getCause().getStackTrace());
//        if (session != null) {
//            if (session.isActive()) {
//                session.close();
//            }
//            sessionComponent.cleanLinkContent(session);
//        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            if (StringUtils.isEmpty(message)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return;
            }
            if (isValidSubParam(message)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return;
            }
            subMessageHelperComponent.processPubMessage(session, message);
        } catch (Exception e) {
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

    /**
     * @param message
     * @return
     */
    @Override
    public Boolean isValidSubParam(String message) {
        if (!message.contains(WsConstant.TRADE_PING) && !message.contains(DepthConstant.DEPTH_NAME)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
