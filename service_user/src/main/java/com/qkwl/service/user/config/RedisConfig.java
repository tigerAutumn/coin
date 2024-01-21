package com.qkwl.service.user.config;

import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.pre.PreValidationHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.framework.validate.ValidationCheckHelper;
import com.qkwl.common.properties.RedisProperties;
import com.qkwl.common.redis.JedisPoolConfig;
import com.qkwl.common.redis.MemCache;
import com.qkwl.service.user.utils.UserLimitHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ReflectionUtils;

import redis.clients.jedis.JedisPool;

/**
 * @Author jany
 * @Date 17-4-20
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean
    public JedisPoolConfig jedisPoolConfig(RedisProperties redisProperties) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getPool().getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWaitMillis());
        jedisPoolConfig.setTestWhileIdle(redisProperties.getPool().isTestWhileIdle());
        jedisPoolConfig.setTestOnBorrow(redisProperties.getPool().isTestOnBorrow());
        jedisPoolConfig.setTestOnReturn(redisProperties.getPool().isTestOnReturn());
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jdisPool(JedisPoolConfig jedisPoolConfig, RedisProperties redisProperties) {
        return new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(),
                redisProperties.getTimeout(), redisProperties.getPassword());
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

    @Bean
    public LimitHelper limitHelper(MemCache memCache){
        LimitHelper limitHelper = new LimitHelper();
        limitHelper.setMemCache(memCache);
        return limitHelper;
    }

    @Bean
    public UserLimitHelper userLimitHelper(MemCache memCache) {
        UserLimitHelper userLimitHelper = new UserLimitHelper();
        userLimitHelper.setMemCache(memCache);
        return userLimitHelper;
    }

    @Bean
    public PreValidationHelper preValidationHelper(){
        return new PreValidationHelper();
    }

    @Bean
    public ValidationCheckHelper validationCheckHelper(LimitHelper limitHelper, ValidateHelper validateHelper){
        ValidationCheckHelper validationCheckHelper = new ValidationCheckHelper();
        validationCheckHelper.setLimitHelper(limitHelper);
        validationCheckHelper.setValidateHelper(validateHelper);
        return validationCheckHelper;
    }
    
    
    
    @Bean(destroyMethod = "shutdown")
    @Primary
    public RedissonClient redisson(RedisProperties redisProperties) throws IOException {
        Config config = new Config();
            String prefix = "redis://";
            config.useSingleServer()
                .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setConnectTimeout(redisProperties.getTimeout())
                .setPassword(redisProperties.getPassword());
        return Redisson.create(config);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
