package com.qkwl.web.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.RequstLimit;
import com.qkwl.web.permission.annotation.PassToken;

@Component
public class APIAuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(APIAuthenticationInterceptor.class);

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
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
                checkeLimit(method,httpServletRequest);
                return true;
            }
        }
        
        
        // 如果方法上的注解为空 则获取类的注解
           Class<?> declaringClass = handlerMethod.getMethod().getDeclaringClass();
           if (declaringClass.isAnnotationPresent(PassToken.class)) {
             PassToken passToken = declaringClass.getAnnotation(PassToken.class);
             if (passToken.required()) {
                 checkeLimit(method,httpServletRequest);
                 return true;
             }
         }


        checkeLimit(method,httpServletRequest);
        return true;
    }


    private boolean checkeLimit(Method method,HttpServletRequest httpServletRequest){
        if (method.isAnnotationPresent(RequstLimit.class)) {
            RequstLimit requstLimit = method.getAnnotation(RequstLimit.class);
            String ip = HttpRequestUtils.getIPAddress(httpServletRequest);
            String url = httpServletRequest.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat(ip);
            // 最大数
            int maxCount = requstLimit.count();
            key += "_";
            long count = redisHelper.getIncrByKey(key);
            if(count == 1) {
                redisHelper.set(key, "1" , 60);
            }

            if (count > maxCount) {
                logger.info("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数["
                        + maxCount + "]");
                // 超过次数，权限拒绝访问，访问太频繁！
                return false;
            }
        }
        return true;
    }

}
