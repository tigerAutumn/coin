package com.qkwl.web.interceptor;

import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.exception.APISecurityPermissionException;
import com.qkwl.web.permission.Enum.URLTypeEnum;
import com.qkwl.web.permission.PermissionCheckUtil;
import com.qkwl.web.permission.annotation.APIPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
public class APISecurityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(APISecurityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 验证权限
        if (this.hasPermission(request,response,handler)) {
            return true;
        }
        //  null == request.getHeader("x-requested-with") TODO 暂时用这个来判断是否为ajax请求
        // 如果没有权限 则抛403异常 springboot会处理，跳转到 /error/403 页面
        throw new APISecurityPermissionException(502,I18NUtils.getString("APISecurityInterceptor.0")); 
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
            APIPermission apiPermission = handlerMethod.getMethod().getAnnotation(APIPermission.class);
            // 如果方法上的注解为空 则获取类的注解
            if (null == apiPermission) {
                apiPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(APIPermission.class);
            }
            // 如果标记了注解，则判断权限
            if (null != apiPermission && null !=apiPermission.urlType()) {
                URLTypeEnum urlTypeEnum = apiPermission.urlType();
                if(urlTypeEnum.urlName.equals(URLTypeEnum.API_URL.urlName)){
                    if (!PermissionCheckUtil.checkSign(request,false)) {
                        ReturnResult returnResult = ReturnResult.FAILUER(I18NUtils.getString("APISecurityInterceptor.1")); 
                        //response.getWriter().write(JSON.toJSONString(returnResult));
                        //System.out.println("签名错误");
                        return false;
                    }
                    return true;
                }else{

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
