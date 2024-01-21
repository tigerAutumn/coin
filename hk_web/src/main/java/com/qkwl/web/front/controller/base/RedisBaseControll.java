package com.qkwl.web.front.controller.base;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qkwl.common.controller.BaseController;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.Utils;

public class RedisBaseControll extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisBaseControll.class);

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private PushService pushService;
    

    public FUser getCurrentUserInfoByToken() {
			
        String webToken = sessionContextUtils.getContextToken("token"); 
        if(StringUtils.isNotEmpty(webToken)) {
        	return redisHelper.getCurrentUserInfoByToken(webToken);
        }
      
        String appToken = sessionContextUtils.getContextApiToken();
        if(StringUtils.isNotEmpty(appToken)) {
        	return redisHelper.getCurrentUserInfoByToken(appToken);
        }
		return null;
    }

    public FUser getCurrentUserInfoByApiToken(){
        String token = sessionContextUtils.getContextApiToken();
        return redisHelper.getCurrentUserInfoByToken(token);
    }

    public BigDecimal getLastPrice(int coinid) {
        TickerData tickerData = pushService.getTickerData(coinid);
        return tickerData.getLast();
    }

    public void deleteUserInfo() {
        String token = sessionContextUtils.getContextToken("token"); 
        redisHelper.deleteUserInfo(token);
    }
    
    public void deleteApiUserInfo() {
    	String token = sessionContextUtils.getContextApiToken();
        redisHelper.deleteUserInfo(token);
    }

    public void updateUserInfo(FUser user) {
        String webToken = sessionContextUtils.getContextToken("token"); 
        if(StringUtils.isNotEmpty(webToken)) {
        	redisHelper.updateUserInfo(webToken, user);
        }
        
        String appToken = sessionContextUtils.getContextApiToken();
        if(StringUtils.isNotEmpty(appToken)) {
        	redisHelper.updateUserInfo(appToken, user);
        	if (appToken.startsWith(RedisConstant.APP_LOGIN_TOTAL_KEY)) {
            	String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
                String md5Account = MD5Util.md5(MD5Util.md5(appToken) + md5AccountId);
                String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_"; 
                String keyname = accountKeyInfo + md5Account;
                redisHelper.expire(keyname, Constant.EXPIRETIME);
    		}
        }
    }
    
    public void updateApiUserInfo(FUser user) {
    	String token = sessionContextUtils.getContextApiToken();
        redisHelper.updateUserInfo(token, user);
        if (token.startsWith(RedisConstant.APP_LOGIN_TOTAL_KEY)) {
        	String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
            String md5Account = MD5Util.md5(MD5Util.md5(token) + md5AccountId);
            String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_"; 
            String keyname = accountKeyInfo + md5Account;
            redisHelper.expire(keyname, Constant.EXPIRETIME);
		}
    }

    public String setRedisData(String token, Object restInfo) {
        String redisKey = Utils.UUID();
        sessionContextUtils.addContextToken(token, redisKey);
        redisHelper.setRedisData(redisKey, restInfo);
        return redisKey;
    }

    public void deletRedisData(String token) {
        String redisKey = sessionContextUtils.getContextToken(token);
        redisHelper.deletRedisData(redisKey);
    }

    public String getRedisData(String token) {
        String redisKey = sessionContextUtils.getContextToken(token);
        return redisHelper.getRedisData(redisKey);
    }
}
