package hotcoin.quote.controller.ws;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.annotation.*;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.SubMessageHelperComponent;
import hotcoin.quote.constant.DetailConstant;
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
 * @ProjectName: finance-server
 * @Package: org.quote.controller
 * @ClassName: WsApiController
 * @Author: hf
 * @Description:
 * @Date: 2019/4/29 18:51
 * @Version: 1.0
 */
@ServerEndpoint(value = "/trade/detail")
@Component
@Slf4j
public class TradeDetailWsWsController extends BaseWsController {
    @Autowired
    private SubMessageHelperComponent subMessageHelperComponent;
    @Autowired
    private SessionComponent sessionComponent;

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, ParameterMap parameterMap) throws IOException {
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
            log.error("func onClose connection closed->{}", session.id().asLongText());
            session.close();
        }
        sessionComponent.cleanLinkContent(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws error happened->{}", throwable.getCause().getStackTrace());
        if (session == null) {
            return;
        }

   /*     if (session.isActive()) {
            log.error("func onError connection closed->{},err is->{}", session.id().asLongText(), throwable.getStackTrace());
            session.close();
        }
        sessionComponent.cleanLinkContent(session);*/

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
            log.error("detail ws error ->{}", e);
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
    public Boolean isValidSubParam(String message) {
        if (!message.contains(WsConstant.TRADE_PING) && !message.contains(DetailConstant.DETAIL_NAME)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
