package hotcoin.quote.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.pojo.ParameterMap;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

import static hotcoin.quote.resp.CodeMsg.REQUEST_PARAM_ERROR;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: BaseWsController
 * @Author: hf
 * @Description: 基类, 提供公共的订阅成功后返回内容, 以及数据检查
 * @Date: 2019/5/7 14:50
 * @Version: 1.0
 */
@Slf4j
public abstract class BaseWsController {
    /**
     * 做公共数据检查用
     *
     * @param headers
     * @param parameterMap
     */
    public Boolean isValidPubParams(HttpHeaders headers, ParameterMap parameterMap) {
        String sub = parameterMap.getParameter("sub");
        if (StringUtils.isEmpty(sub) || !sub.contains(WsConstant.SLASH)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


    /**
     * 订阅类型检查,继承的子类需要单独实现该方法去校验数据
     *
     * @param sub
     * @return
     */
    public abstract Boolean isValidSubParam(String sub);

    /**
     * 获取ip地址
     *
     * @return
     */
    public String getAddress(Session session) {
        InetSocketAddress ipSocket = (InetSocketAddress) session.channel().remoteAddress();
        return ipSocket.getAddress().getHostAddress();
    }

    /**
     * 判断是否订阅数超过限制
     *
     * @param session
     * @return
     */
    public boolean isValidSubCount(Session session) {
      /*  String ip = getAddress(session);
        if (UserLinksComponent.isInvalidSub(ip)) {
            return true;
        }*/
        return false;
    }

    /**
     * 获取订阅或取消订阅内容
     *
     * @param session
     * @param message
     * @return
     */
    public String getSubOrUnSubContent(Session session, String message) {
        try {
            log.info("get message from client ->{}", message);
            if (StringUtils.isEmpty(message)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return null;
            }
            JSONObject jsonObject = (JSONObject) JSON.parse(message);
            String sub = jsonObject.getString(WsConstant.SUB);
            String unsub = jsonObject.getString(WsConstant.UNSUB);
            String unsubAll = jsonObject.getString(WsConstant.TRADE_UNSUB_ALL);
            if (StringUtils.isEmpty(sub) && StringUtils.isEmpty(unsub) && StringUtils.isEmpty(unsubAll)) {
                session.sendText(JSON.toJSONString(Result.error(REQUEST_PARAM_ERROR)));
                return null;
            }
            return StringUtils.isEmpty(sub) ? unsub : sub;
        } catch (Exception e) {
            log.error("get getSubOrUnSubContent error happened,message is-{},err is ->{}", message, e);
            return null;
        }
    }

}
