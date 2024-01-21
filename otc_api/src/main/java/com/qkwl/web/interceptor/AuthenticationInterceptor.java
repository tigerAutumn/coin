package com.qkwl.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.web.exception.LoginException;
import com.qkwl.web.exception.SignSecurityPermissionException;
import com.qkwl.web.permission.Enum.URLTypeEnum;
import com.qkwl.web.permission.PermissionCheckUtil;
import com.qkwl.web.permission.annotation.APIPermission;
import com.qkwl.web.permission.annotation.APISignPermission;
import com.qkwl.web.permission.annotation.PassToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        /*if(uri.length()>1) {
            logger.error("uri=:{}", uri); //$NON-NLS-1$
        }*/

        httpServletRequest.setCharacterEncoding("utf-8"); //$NON-NLS-1$
        httpServletResponse.setContentType("application/json;charset=UTF-8"); //$NON-NLS-1$
        //跨域设置
        //通过在响应 header 中设置 ‘*’ 来允许来自所有域的跨域请求访问。
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*"); //$NON-NLS-1$ //$NON-NLS-2$
        //通过对 Credentials 参数的设置，就可以保持跨域 Ajax 时的 Cookie
        //设置了Allow-Credentials，Allow-Origin就不能为*,需要指明具体的url域
        //httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        //请求方式
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*"); //$NON-NLS-1$ //$NON-NLS-2$
        //（预检请求）的返回结果（即 Access-Control-Allow-Methods 和Access-Control-Allow-Headers 提供的信息） 可以被缓存多久
        httpServletResponse.setHeader("Access-Control-Max-Age", "86400"); //$NON-NLS-1$ //$NON-NLS-2$
        //首部字段用于预检请求的响应。其指明了实际请求中允许携带的首部字段
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*"); //$NON-NLS-1$ //$NON-NLS-2$
        httpServletResponse.setHeader("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "platform"); //$NON-NLS-1$ //$NON-NLS-2$

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
        //检查有没有需要用户权限的注解
        /*
        //JWT TOKEN 验证方法
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("401");
                }

                User user = userService.findUserById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    throw new RuntimeException("401");
                }

                return true;
            }
        }
         */
        // 如果标记了注解，则判断权限
        if (method.isAnnotationPresent(APIPermission.class)) {
            APIPermission apiPermission = method.getAnnotation(APIPermission.class);
            URLTypeEnum urlTypeEnum = apiPermission.urlType();
            if (urlTypeEnum.urlName.equals(URLTypeEnum.API_URL.urlName)) {
                if (!PermissionCheckUtil.checkSign(httpServletRequest, false)) {
                    throw new SignSecurityPermissionException("sign faile"); //$NON-NLS-1$
                }
                return true;
            }
        }
        //String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        /*
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证

                String token = checkToken(httpServletRequest,redisHelper);
                resetTokenExpireTime(redisHelper, token);
                return true;
            }
        }
        */
        String token = checkToken(httpServletRequest,redisHelper);

        // 如果标记了注解，则判断权限
        if (method.isAnnotationPresent(APISignPermission.class)) {
            APISignPermission apiSignPermission = method.getAnnotation(APISignPermission.class);
            URLTypeEnum urlTypeEnum = apiSignPermission.urlType();
            if (urlTypeEnum.urlName.equals(URLTypeEnum.APISIGN_URL.urlName)) {

                if (!PermissionCheckUtil.checkSign(httpServletRequest, true)) {
                    throw new SignSecurityPermissionException("sign faile"); //$NON-NLS-1$
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
            logger.info("前台获取token为空----" + token); //$NON-NLS-1$
           // ReturnResult rst = ReturnResult.FAILUER(401, "登录已失效，请重新登录!（code：-1000）");
            throw new LoginException(400, I18NUtils.getString("AuthenticationInterceptor.18")); //$NON-NLS-1$
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
    }

}
