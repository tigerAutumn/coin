package hotcoin.quote.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.utils
 * @ClassName: UserLinksComponent
 * @Author: hf
 * @Description: 用于存储用户订阅币对
 * @Date: 2019/5/28 15:47
 * @Version: 1.0
 */
@Slf4j
@Component
public class UserLinksComponent {
    /**
     * key :sessionId,value :币对set
     */
    private static ConcurrentHashMap<String, ConcurrentSkipListSet<String>> userSubPairsCacheMap = new ConcurrentHashMap<>();

    public  ConcurrentHashMap<String, ConcurrentSkipListSet<String>> getUserSubPairsCacheMap() {
        return userSubPairsCacheMap;
    }

    /**
     * 用户订阅币对保存
     *
     * @param sessionId
     * @param pair:depth.btc_usdt
     */
    public  void setUserSubPair(String sessionId, String pair) {
        if (userSubPairsCacheMap == null) {
            return;
        }
        ConcurrentSkipListSet<String> linkedSet = userSubPairsCacheMap.get(sessionId);
        if (linkedSet != null && !linkedSet.isEmpty()) {
            linkedSet.add(pair);
        } else {
            linkedSet = new ConcurrentSkipListSet();
            linkedSet.add(pair);
        }
        userSubPairsCacheMap.put(sessionId, linkedSet);
    }

    /**
     * 取消币对订阅
     * @param sessionId
     * @param pair
     */
    public  void removeUserSubPair(String sessionId, String pair) {
        if (userSubPairsCacheMap == null || userSubPairsCacheMap.isEmpty()) {
            return;
        }
        ConcurrentSkipListSet<String> linkedSet = userSubPairsCacheMap.get(sessionId);
        if (linkedSet != null && !linkedSet.isEmpty()) {
            linkedSet.remove(pair);
        }
    }

    /**
     * 取消所有订阅
     * @param sessionId
     */
    public  void removeUserAllSub(String sessionId) {
        if (userSubPairsCacheMap == null || userSubPairsCacheMap.isEmpty()) {
            return;
        }
        userSubPairsCacheMap.put(sessionId, new ConcurrentSkipListSet<>());
    }

    /**
     * 删除用户session关系
     *
     * @param sessionId
     */
    public  void removeUser(String sessionId) {
        if (userSubPairsCacheMap == null || userSubPairsCacheMap.isEmpty()) {
            return;
        }
        userSubPairsCacheMap.remove(sessionId);
    }
}
