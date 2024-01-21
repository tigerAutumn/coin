package com.qkwl.service.coin.util;

import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;


/**
 * 工具类
 *
 * @author jany
 */
@Component("utils")
public class JobUtils {

    @Autowired
    private RedisHelper redisHelper;
    
    public String get(String key) {
    	return redisHelper.get(key);
    }
    
    public void setNoExpire(String key,String value) {
    	redisHelper.setNoExpire(key, value);
    }

    public List<SystemCoinType> getCoinTypeList() {
        List<SystemCoinType> coinTypes = redisHelper.getCoinTypeIsRechargeList(SystemCoinTypeEnum.COIN.getCode());
        return coinTypes;
    }
    public SystemCoinType getCoinType(String shortName) {
        SystemCoinType coinType = redisHelper.getCoinTypeShortName(shortName);
        return coinType;
    }
    
    public SystemCoinType getCoinTypeShortName(String shortName) {
        return redisHelper.getCoinTypeShortNameSystem(shortName);
    }

    public String getSystemArgs(String key) {
        return redisHelper.getSystemArgs(key);
    }

    public BigDecimal getLastPrice(Integer coinId) {
    	Integer tradeId = null;
    	List<SystemTradeType> allTradeTypeList = redisHelper.getTradeTypeList(0);
    	if(allTradeTypeList == null || allTradeTypeList.size() == 0) {
    		return BigDecimal.ZERO;
    	}
    	for (SystemTradeType systemTradeType : allTradeTypeList) {
    		if(systemTradeType.getBuyCoinId() == coinId) {
    			tradeId = systemTradeType.getId();
    			break;
    		}
		}
    	if(tradeId == null) {
    		return BigDecimal.ZERO;
    	}
        return redisHelper.getLastPrice(tradeId);
    }
	public SystemCoinType getCoinType(Integer valueOf) {
		return redisHelper.getCoinType(valueOf);
	}
    
}
