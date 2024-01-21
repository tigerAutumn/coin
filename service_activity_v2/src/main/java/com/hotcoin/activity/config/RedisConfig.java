package com.hotcoin.activity.config;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author jany
 * @Date 17-4-20
 */

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.pool.minIdle}")
    private int minIdle;

    @Value("${redis.pool.maxIdle}")
    private int maxIdle;

    @Value("${redis.pool.maxTotal}")
    private int maxTotal;

    @Value("${redis.pool.maxWaitMillis}")
    private int maxWaitMillis;

    @Value("${redis.pool.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${redis.pool.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${redis.pool.testOnReturn}")
    private boolean testOnReturn;

    @Value("${redis.pool.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${redis.pool.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${redis.pool.numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;


    @Bean
    public JedisPool redisPoolFactory() throws Exception {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        if (jedisPool == null) {
            log.error("redisPoolFactory init fail!");
            throw new RuntimeException();
        }
        return jedisPool;
    }

    @Bean
    public MemCache memCache(JedisPool jedisPool) {
        MemCache memCache = new MemCache();
        memCache.setJedisPool(jedisPool);
        return memCache;
    }

    @Bean
    public RedisHelper redisHelper(MemCache memCache) {
        RedisHelper redisHelper = new RedisHelper();
        redisHelper.setMemCache(memCache);
        return redisHelper;
    }
}
