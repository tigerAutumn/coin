package hotcoin.quote.controller.ws;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.annotation.*;
import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.SubMessageHelperComponent;
import hotcoin.quote.constant.KlineConstant;
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
import java.util.Arrays;

import static hotcoin.quote.resp.CodeMsg.REQUEST_PARAM_ERROR;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: TradeKlineWsController
 * @Author: hf
 * @Description:
 * @Date: 2019/5/23 9:37
 * @Version: 1.0
 */
@ServerEndpoint(value = "/trade/kline")
@Component
@Slf4j
public class TradeKlineWsController extends BaseWsController {
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
            session.close();
        }
        sessionComponent.cleanLinkContent(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws kline error happened->{}", throwable.getMessage());
        if (session == null) {
            return;
        }

       /* if (session.isActive()) {
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
            if (!message.contains(WsConstant.TRADE_PING) && !message.contains(KlineConstant.KLINE_NAME)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return;
            }
            subMessageHelperComponent.processPubMessage(session, message);
        } catch (Exception e) {
            log.error("kline err->{}", e);
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
        if (sub.contains(KlineConstant.KLINE_NAME) && sub.contains(WsConstant.SYMBOL_SPOT)) {
            String[] strArr = sub.split("[.]");
            String period = strArr[strArr.length - 1];
            if (Arrays.asList(KlineConstant.KLINE_PERIOD_ARR).contains(period)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
