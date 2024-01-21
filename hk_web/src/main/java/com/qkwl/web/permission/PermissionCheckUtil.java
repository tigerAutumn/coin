package com.qkwl.web.permission;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.api.FApiAuth;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

public class PermissionCheckUtil {

   public static final Logger logger = LoggerFactory.getLogger(PermissionCheckUtil.class);

    public static String getToken(HttpServletRequest request,RedisHelper redisHelper) {
        String token = request.getParameter("token"); 
        if (!StringUtils.isEmpty(token)) {
            String json = redisHelper.get(token);
            if (StringUtils.isEmpty(json)) {
            	throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
            }else{
                try {
                    RedisObject obj = JSON.parseObject(json, RedisObject.class);
                    if (Utils.getTimestamp().getTime() / 1000 - obj.getLastActiveDateTime() > Constant.EXPIRETIME) {
                        redisHelper.delete(token);
                        logger.info("当前从redis获取token不为空且已超过失效时间----" + token);
                        throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
                    }
                    Object resultStr = obj.getExtObject();
                    if (resultStr == null) {
                        logger.info("当前从redis获取token不为空且ExtObject为空----" + token);
                        throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
                    }
                }catch (Exception ex){
                	throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
                }
            }
        }
        return token;

    }

    /**
     * api的签名验证
     *
     * @param httpServletRequest
     * @return
     */
    public static boolean checkSign(HttpServletRequest httpServletRequest, boolean isApp) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        Map<String, String> realParams = new HashMap<>();
        //这四个参数是必须的
        if (!parameterMap.containsKey("AccessKeyId") || !parameterMap.containsKey("SignatureVersion")  //$NON-NLS-2$
                || !parameterMap.containsKey("SignatureMethod") 
                || !parameterMap.containsKey("Timestamp") 
                || !parameterMap.containsKey("Signature")) { 
            return false;
        }

        //签名方法不正确
        if (!httpServletRequest.getParameter("SignatureMethod").equals("HmacSHA256")) {  //$NON-NLS-2$
            return false;
        }

        String Signature = httpServletRequest.getParameter("Signature"); 
        String AccessKeyId = httpServletRequest.getParameter("AccessKeyId"); 
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(httpServletRequest.getServletContext());
        RedisHelper redisHelper = (RedisHelper) factory.getBean("redisHelper"); 


        //根据 AccessKeyId 获取对应的appSecret
        String appSecret = null;
        if(isApp){
            appSecret = findAppApiSecret(redisHelper,httpServletRequest.getParameter("token")); 
        }else{
            appSecret = findWebApiSecret(redisHelper,AccessKeyId);
        }

        if (null == appSecret || "".equals(appSecret)) { 
            return false;
        }

        //获取请求的参数
        while (parameterNames.hasMoreElements()) {
            String s = parameterNames.nextElement();
            //Signature 和 token 不需要参与签名的计算
            if (!"Signature".equals(s) && !"token".equals(s)) {  //$NON-NLS-2$
                realParams.put(s, httpServletRequest.getParameter(s));
            }
        }
        //根据亚马逊的签名规范
        String method = httpServletRequest.getMethod();
        String requestURI = httpServletRequest.getRequestURI();
        boolean bool = check(method, requestURI, realParams, appSecret, Signature, "testtest.hotcoin.top"); 
        if(bool) {
            return true;
        }
        return check(method, requestURI, realParams, appSecret, Signature, "hotcoin.top"); 
    }


    public static boolean check(String method,String requestURI,Map<String, String> realParams,String appSecret,String Signature,String host) {
        //String host = "hotcoin.top";
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(requestURI).append('\n'); // /path
        SortedMap<String, String> map = new TreeMap<>(realParams);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }

        sb.deleteCharAt(sb.length() - 1);
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256"); 
            SecretKeySpec secKey =
                    new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"); 
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (InvalidKeyException e) {
            return false;
        }

        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        //需要对签名进行base64的编码
        String actualSign = Base64.getEncoder().encodeToString(hash);
        Signature = Signature.replace("\n","");  //$NON-NLS-2$
        actualSign = actualSign.replace("\n","");  //$NON-NLS-2$
        if (!Signature.equals(actualSign)) {
            return false;
        }
        return true;
    }






    public static String findWebApiSecret(RedisHelper redisHelper,String appKey) {
        FApiAuth apiAuth = redisHelper.getApiAuthByKey(RedisConstant.IS_AUTH_API_KEY + appKey);
        if (apiAuth == null || !apiAuth.isValid()) {
            return null;
        }
        return apiAuth.getFsecretkey();
    }

    public static String findAppApiSecret(RedisHelper redisHelper,String token){
        FUser user = redisHelper.getCurrentUserInfoByToken(token);
        String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
        String md5Account = MD5Util.md5(MD5Util.md5(token) + md5AccountId);
        String accountKeyInfo = RedisConstant.ACCOUNT_SIGN__KEY + md5AccountId + "_"; 
        String keyname = accountKeyInfo + md5Account;
        RedisObject obj = redisHelper.getRedisObject(keyname);
        if(obj != null) {
            return obj.getExtObject().toString();
        }else {
            return null;
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");  //$NON-NLS-2$ //$NON-NLS-3$
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!"); 
        }
    }

    public static void returnJson(HttpServletResponse response, ReturnResult returnResult){
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8"); 
        response.setContentType("text/html; charset=utf-8"); 
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSONString(returnResult));

        } catch (IOException e) {
            logger.error("response error",e); 
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
