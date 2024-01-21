package hotcoin.utils;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 工具类
 *
 * @author jany
 */
@Component("utils")
public class JobUtils {
    public static final int AGENTID = 0;
    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private MemCache memCache;

    public List<SystemTradeType> getTradeTypeList() {
        // 获取交易列表
        List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(AGENTID);
        return tradeTypes;
    }

    public SystemTradeType getTradeType(int tradeId) {
        SystemTradeType tradeType = redisHelper.getTradeType(tradeId, AGENTID);
        return tradeType;
    }

    public String getRedisData(int db, String key) {
        return memCache.get(db, key);
    }

    public void setRedisData(String key, String value) {
        memCache.setNoExpire(key, value);
    }

    public void setRedisData(int db, String key, String value, int expireSeconds) {
        memCache.set(db, key, value, expireSeconds);
    }

    public void deleteByKey(int db,String key){
        memCache.delete(db,key);
    }

}
