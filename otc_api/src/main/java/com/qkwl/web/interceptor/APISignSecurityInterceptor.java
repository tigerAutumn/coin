package com.qkwl.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.exception.APISecurityPermissionException;
import com.qkwl.web.exception.LoginException;
import com.qkwl.web.permission.Enum.URLTypeEnum;
import com.qkwl.web.permission.PermissionCheckUtil;
import com.qkwl.web.permission.annotation.APISignPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author blueriver
 * @description 权限拦截器
 * @date 2017/11/17
 * @since 1.0
 */
@Component
public class APISignSecurityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(APISignSecurityInterceptor.class);


    @Autowired
    private RedisHelper redisHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 验证权限
        if (this.hasPermission(request,response,handler)) {
            return true;
        }
        //  null == request.getHeader("x-requested-with") TODO 暂时用这个来判断是否为ajax请求
        // 如果没有权限 则抛403异常 springboot会处理，跳转到 /error/403 页面
        throw new APISecurityPermissionException(502,I18NUtils.getString("APISignSecurityInterceptor.0")); //$NON-NLS-1$
    }

    /**
     * 是否有权限
     *
     * @param handler
     * @return
     */
    private boolean hasPermission(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            APISignPermission apiSignPermission = handlerMethod.getMethod().getAnnotation(APISignPermission.class);
            // 如果方法上的注解为空 则获取类的注解
            if (null == apiSignPermission) {
                apiSignPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(APISignPermission.class);
            }
            // 如果标记了注解，则判断权限
            if (null != apiSignPermission && null !=apiSignPermission.urlType()) {
                URLTypeEnum urlTypeEnum = apiSignPermission.urlType();
                if (urlTypeEnum.urlName.equals(URLTypeEnum.APISIGN_URL.urlName)) {
                    String token = PermissionCheckUtil.getToken(request,redisHelper);
                    if (StringUtils.isEmpty(token)) {
                        logger.info("前台获取token为空----"+token); //$NON-NLS-1$
                        ReturnResult rst = ReturnResult.FAILUER(401, I18NUtils.getString("APISignSecurityInterceptor.2")); //$NON-NLS-1$
                        throw new LoginException(400,I18NUtils.getString("APISignSecurityInterceptor.3")); //$NON-NLS-1$
                    }
                    if (!PermissionCheckUtil.checkSign(request, true)) {
                        ReturnResult returnResult = ReturnResult.FAILUER(ReturnResult.FAILURE_SIGN_ERROR, I18NUtils.getString("APISignSecurityInterceptor.4")); //$NON-NLS-1$
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Signature", request.getParameter("Signature")); //$NON-NLS-1$ //$NON-NLS-2$
                        returnResult.setData(jsonObject);
                        // response.getWriter().write(JSON.toJSONString(returnResult));
                        PermissionCheckUtil.returnJson(response,returnResult);
                        return false;
                    }

                    BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                    RedisHelper redisHelper = (RedisHelper) factory.getBean("redisHelper"); //$NON-NLS-1$
                    String json = redisHelper.get(token);
                    RedisObject obj = JSON.parseObject(json, RedisObject.class);
                    Object resultStr = obj.getExtObject();
                    FUser fuser = JSON.parseObject(resultStr.toString(), FUser.class);
                    RedisObject newobj = new RedisObject();
                    newobj.setExtObject(fuser);
                    redisHelper.set(token, newobj, Constant.EXPIRETIME);
                }
            }

        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // TODO
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO
    }


}
