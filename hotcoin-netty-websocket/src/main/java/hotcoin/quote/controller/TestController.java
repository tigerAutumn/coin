package hotcoin.quote.controller;

import hotcoin.quote.component.SessionComponent;
import hotcoin.quote.component.UserLinksComponent;
import hotcoin.quote.model.wsquote.vo.TradeDepthVo;
import hotcoin.quote.pojo.Session;
import hotcoin.quote.resp.Result;
import hotcoin.quote.service.GroupSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller.web
 * @ClassName: TestController
 * @Author: hf
 * @Description:
 * @Date: 2019/11/11 10:22
 * @Version: 1.0
 */
@RestController
@Slf4j
public class TestController {
    @Autowired
    private GroupSendService groupSendService;
    @Autowired
    private UserLinksComponent userLinksComponent;
    @Autowired
    private SessionComponent sessionComponent;

    /**
     * 健康检查
     *
     * @return
     */
    @GetMapping("/handleSend")
    public Result<Void> handleSend(Integer tradeId) {
        TradeDepthVo tradeDepthVo = new TradeDepthVo();
        tradeDepthVo.setCny(String.valueOf(Math.random()));
        tradeDepthVo.setAsks(new ArrayList<>());
        tradeDepthVo.setBids(new ArrayList<>());
        tradeDepthVo.setLast(String.valueOf(Math.random()));
        tradeDepthVo.setOpen(String.valueOf(Math.random()));
        if (tradeId == null) {
            tradeId = 700008;
        }
        groupSendService.groupSendDepthData(tradeDepthVo, tradeId);
        return new Result<>();
    }

    @GetMapping("/getUserInfo")
    public Result userInfo() {
        ConcurrentHashMap<String, ConcurrentSkipListSet<String>> map = userLinksComponent.getUserSubPairsCacheMap();
        return new Result<>(map);
    }

    @GetMapping("/getSessionInfo")
    public Result sessionInfo() {
        ConcurrentHashMap<String, Session> map = sessionComponent.getOnlineSessions();
        return new Result<>(map);
    }

    @GetMapping("/close")
    public Result<String> closeClient() {
        ConcurrentHashMap<String, Session> sessionConcurrentHashMap = sessionComponent.getOnlineSessions();
        for (Session session : sessionConcurrentHashMap.values()) {
            session.close();
        }
        return new Result<>();
    }

    @GetMapping("/closeAndCleanSub")
    public Result<String> closeAndCleanSub() {
        ConcurrentHashMap<String, Session> sessionConcurrentHashMap = sessionComponent.getOnlineSessions();
        for (Session session : sessionConcurrentHashMap.values()) {
            session.close();
            sessionComponent.cleanLinkContent(session);
        }
        return new Result<>();
    }
}
