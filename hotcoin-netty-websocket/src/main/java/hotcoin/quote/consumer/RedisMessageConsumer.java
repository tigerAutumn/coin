package hotcoin.quote.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.listener
 * @ClassName: RedisMessageListener
 * @Author: hf
 * @Description: 接收深度消息
 * @Date: 2019/5/7 17:05
 * @Version: 1.0
 */
@Slf4j
@Component
public class RedisMessageConsumer extends JedisPubSub {
    public RedisMessageConsumer() {
    }

    @Override
    public void onMessage(String channel, String message) {
        // 所有消息都会进入,需要匹配去分发
        log.info("------------------channel is ---->{},message is ---->{}", channel, message);
        JSONObject jsonObj= (JSONObject) JSON.parse(message);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        super.onSubscribe(channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        super.onUnsubscribe(channel, subscribedChannels);
    }
}


