package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.constant.DepthConstant;
import hotcoin.quote.constant.DetailConstant;
import hotcoin.quote.constant.KlineConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static hotcoin.quote.model.CodeMsg.REQUEST_PARAM_ERROR;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.component
 * @ClassName: SubMessageHelperComponent
 * @Author: hf
 * @Description: 消息订阅关系component
 * @Date: 2019/7/3 20:07
 * @Version: 1.0
 */
@Component
@Slf4j
public class SubMessageHelperComponent {
    @Autowired
    private SessionComponent sessionComponent;
    @Autowired
    private UserLinksComponent userLinksComponent;

    /**
     * 公共数据处理
     *
     * @param message
     */
    public void pubMessageDeal(Session session, String message) {
        log.info("get message from client ->{}", message);
        JSONObject jsonObject = (JSONObject) JSON.parse(message);
        String sub = jsonObject.getString(WsConstant.SUB);
        String unsub = jsonObject.getString(WsConstant.UNSUB);
        String unsubAll = jsonObject.getString(WsConstant.TRADE_UNSUB_ALL);
        if (jsonObject.containsKey(WsConstant.TRADE_PING)) {
            return;
        }
        if (StringUtils.isEmpty(sub) && StringUtils.isEmpty(unsub) && StringUtils.isEmpty(unsubAll)) {
            session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
            return;
        }
        if (!StringUtils.isEmpty(unsubAll)) {
            unSubAllOpt(session);
            session.sendText(JSON.toJSONString(Result.success(null, WsConstant.TRADE_UNSUB_ALL_MSG)));
            return;
        }
        String temp = StringUtils.isEmpty(sub) ? unsub : sub;
        if (isValidPubSubParam(temp)) {
            session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
            return;
        }
        if (!StringUtils.isEmpty(sub)) {
            sub = sub.toLowerCase();
            subOpt(session, sub);
            session.sendText(JSON.toJSONString(Result.success(null, sub)));
        }
        if (!StringUtils.isEmpty(unsub)) {
            unsub = unsub.toLowerCase();
            unSubOpt(session, unsub);
            session.sendText(JSON.toJSONString(Result.success(null, unsub)));
        }
    }


    public Boolean isValidPubSubParam(String sub) {
        sub = sub.toLowerCase();
        boolean b = !sub.contains(DetailConstant.DETAIL_NAME) && !sub.contains(DepthConstant.DEPTH_NAME)
                && !sub.contains(KlineConstant.KLINE_NAME) && !sub.contains(WsConstant.TRADE_TICKERS);
        if (b) {
            return Boolean.TRUE;
        }
        if (sub.contains(KlineConstant.KLINE_NAME) && sub.contains(WsConstant.SYMBOL_SPOT)) {
            String[] strArr = sub.split("[.]");
            String period = strArr[strArr.length - 1];
            if (Arrays.asList(KlineConstant.KLINE_PERIOD_ARR).contains(period)) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 保存订阅内容
     *
     * @param session
     * @param sub
     */
    public void subOpt(Session session, String sub) {
        String sessionId = session.id().asLongText();

        Session value = sessionComponent.getOnlineSessions().get(sessionId);
        if (value != null) {
            userLinksComponent.setUserSubPair(sessionId, sub);
        }
    }

    /**
     * 取消订阅内容
     *
     * @param session
     * @param pair
     */
    public void unSubOpt(Session session, String pair) {
        String sessionId = session.id().asLongText();
        Session value = sessionComponent.getOnlineSessions().get(sessionId);
        if (value != null) {
            userLinksComponent.removeUserSubPair(sessionId, pair);
        }
    }

    /**
     * 取消所以订阅
     *
     * @param session
     */
    public void unSubAllOpt(Session session) {
        String sessionId = session.id().asLongText();
        Session value = sessionComponent.getOnlineSessions().get(sessionId);
        if (value != null) {
            userLinksComponent.removeUserAllSub(sessionId);
        }
    }

    /**
     * @param message 订阅消息收到的内容
     * @param content 订阅或者取消字段:sub / unsub
     * @param session
     */
    public void subOrUnSub(String message, String content, Session session) {
        if (message.contains(WsConstant.SUB)) {
            subOpt(session, content);
            session.sendText(JSON.toJSONString(Result.success(null, content)));
        } else {
            unSubOpt(session, content);
            session.sendText(JSON.toJSONString(Result.success(null, content)));
        }
    }
}
