package com.qkwl.service.event;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class TradeIdKlineTaskEvent implements Serializable {

    private ConcurrentHashMap<Integer, SystemTradeType> tradeMap;

    public TradeIdKlineTaskEvent(ConcurrentHashMap<Integer, SystemTradeType> tradeMap) {
        this.tradeMap = tradeMap;
    }

    public ConcurrentHashMap<Integer, SystemTradeType> getTradeMap() {
        return tradeMap;
    }

    public void setTradeMap(ConcurrentHashMap<Integer, SystemTradeType> tradeMap) {
        this.tradeMap = tradeMap;
    }
}
