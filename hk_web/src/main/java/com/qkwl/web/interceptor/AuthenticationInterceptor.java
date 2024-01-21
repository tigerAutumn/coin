package com.qkwl.web.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.permission.PermissionCheckUtil;
import com.qkwl.web.permission.Enum.URLTypeEnum;
import com.qkwl.web.permission.annotation.APIPermission;
import com.qkwl.web.permission.annotation.APISignPermission;
import com.qkwl.web.permission.annotation.PassToken;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {

        //展示维护页面
        String updatingPrompt = redisHelper.getByKey(ArgsConstant.UPDATING_PROMPT);
        if (updatingPrompt != null) {
            String[] split = updatingPrompt.split(",");
            String isUpdating = split[0];
            //系统是否升级中
            if ("1".equals(isUpdating)) {
                //是否在白名单
                String updatingWhiteList = redisHelper.getSystemArgs(ArgsConstant.UPDATING_WHITE_LIST);
                if (updatingWhiteList != null) {
                    String[] whiteList = updatingWhiteList.split("#");
                    //白名单里面不包含
                    String ip = HttpRequestUtils.getIPAddress(httpServletRequest);
                    logger.info("=================ip:"+ip);
                    if(!Arrays.asList(whiteList).contains(ip)) {
                        ReturnResult rst = ReturnResult.FAILUER(ReturnResult.FAILURE_SYSTEM_UPDATING, "系统升级中!");
                        httpServletResponse.getWriter().write(JSON.toJSONString(rst));
                        return false;
                    }
                }
            }
        }
 
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) object;
        
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        
        
        // 如果方法上的注解为空 则获取类的注解
           Class<?> declaringClass = handlerMethod.getMethod().getDeclaringClass();
           if (declaringClass.isAnnotationPresent(PassToken.class)) {
             PassToken passToken = declaringClass.getAnnotation(PassToken.class);
             if (passToken.required()) {
                 return true;
             }
         }
        
        

        // 如果标记了注解，则判断权限
        if (method.isAnnotationPresent(APIPermission.class)) {
            APIPermission apiPermission = method.getAnnotation(APIPermission.class);
            URLTypeEnum urlTypeEnum = apiPermission.urlType();
            if (urlTypeEnum.urlName.equals(URLTypeEnum.API_URL.urlName)) {
                if (!PermissionCheckUtil.checkSign(httpServletRequest, false)) {
                    throw new BizException(ErrorCodeEnum.SIGN_FAILED); 
                }
                return true;
            }
        }
        String token = checkToken(httpServletRequest,redisHelper);

        // 如果标记了注解，则判断权限
        if (method.isAnnotationPresent(APISignPermission.class)) {
            APISignPermission apiSignPermission = method.getAnnotation(APISignPermission.class);
            URLTypeEnum urlTypeEnum = apiSignPermission.urlType();
            if (urlTypeEnum.urlName.equals(URLTypeEnum.APISIGN_URL.urlName)) {

                if (!PermissionCheckUtil.checkSign(httpServletRequest, true)) {
                	 throw new BizException(ErrorCodeEnum.SIGN_FAILED); 
                }

                resetTokenExpireTime(redisHelper, token);
            }
        }
        return true;
    }

    @NotNull
    private String checkToken(HttpServletRequest httpServletRequest,RedisHelper redisHelper) {
        String token = PermissionCheckUtil.getToken(httpServletRequest,redisHelper);
        if (StringUtils.isEmpty(token)) {
            logger.info("前台获取token为空----" + token); 
            throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
        }
        return token;
    }

    private void resetTokenExpireTime(RedisHelper redisHelper, String token) {
        String json = redisHelper.get(token);
        RedisObject obj = JSON.parseObject(json, RedisObject.class);
        Object resultStr = obj.getExtObject();
        FUser fuser = JSON.parseObject(resultStr.toString(), FUser.class);
        RedisObject newobj = new RedisObject();
        newobj.setExtObject(fuser);
        redisHelper.set(token, newobj, Constant.EXPIRETIME);
        //更新签名过期时间
        String md5AccountId = MD5Util.md5(String.valueOf(fuser.getFid()));
        String md5Account = MD5Util.md5(MD5Util.md5(token) + md5AccountId);
        String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_"; 
        String keyname = accountKeyInfo + md5Account;
        redisHelper.expire(keyname, Constant.EXPIRETIME);
    }

}
