package com.qkwl.service.entrust.config;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;

/**
 * @FileName MyCacheManager.java
 * @Description:
 *
 * @Date 2016年10月17日
 * @author o-jinfeng.huang
 * @version 1.0
 * 
 */
@Component
public class MyCacheManager {
	
    private static final Logger logger = LoggerFactory.getLogger(MyCacheManager.class);
    
	@SuppressWarnings("rawtypes")
	private  Map<String,Map> caches=new ConcurrentHashMap<>();
	
	@Autowired
	private RedisHelper redisHelper;
	
	public <K, V> Map<K, V> getCache(String cacheName){
		return caches.computeIfAbsent(cacheName, k->new ConcurrentHashMap<>());
	}
	
	
	@PostConstruct
	public void refresh() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		        public void run() {
		        	logger.error("清空内存");
		        	caches.forEach((k,v)->v.clear());
		        }
		}, 0 , 60000);
	}
	
	
    public Integer getTradeId(String symbol) {
    	Map<String, Integer> cache = this.getCache("symbol_tradeId");
        Integer tradeId =cache.get(symbol.trim().toLowerCase());
        if(null == tradeId){
            List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
            if(CollectionUtils.isEmpty(tradeTypeList)){
                return null;
            }
            for (SystemTradeType tradeType : tradeTypeList) {
                if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                    continue;
                }
                String sellShortName = tradeType.getSellShortName().toLowerCase().trim();
                String buyShortName = tradeType.getBuyShortName().toLowerCase().trim();
                String symbolsTrade = sellShortName+"_"+buyShortName;
                cache.put(symbolsTrade,tradeType.getId());
            }
            tradeId = cache.get(symbol.trim().toLowerCase());
        }
        return tradeId;
    }
	
	
}