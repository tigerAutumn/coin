package hotcoin.quote.listener;

import hotcoin.quote.consumer.RedisMessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.tasks
 * @ClassName: RedisMqListener
 * @Author: hf
 * @Description: 监听深度, Kline线信息
 * @Date: 2019/5/8 10:28
 * @Version: 1.0
 */
@Slf4j
@Component
public class RedisMqListener extends Thread {
    @Autowired
    private JedisPool redisPoolFactory;

    @Autowired
    private RedisMessageConsumer redisMessageConsumer;

    @Override
    public void run() {
        /**
         *  监听深度,Kline线信息
         */
        Jedis jedis = null;
        try {
            jedis = redisPoolFactory.getResource();
            jedis.subscribe(redisMessageConsumer, "DEPTH");
        } catch (Exception e) {
            log.error("get redis connection fail", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
}}
