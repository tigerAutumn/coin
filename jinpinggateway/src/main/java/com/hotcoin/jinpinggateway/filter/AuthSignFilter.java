package com.hotcoin.jinpinggateway.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.qkwl.common.dto.Enum.ApiKeyStatusEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.entity.OpenApiEntity;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.rpc.user.IApiKeyService;
import com.qkwl.common.rpc.user.IOpenApiService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.RespData;


/**
 * 验证签名
 * 
 * @author huangjinfeng
 */
@Component
public class AuthSignFilter extends ZuulFilter {
	
	private Logger logger =LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IApiKeyService apiKeyService;
	@Autowired
	private IOpenApiService openApiService;

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		try {
			apiCheckSign(request);
		} catch (Exception e) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.addZuulResponseHeader("content-type", "text/html;charset=utf-8");
			if (e instanceof BizException) {
				BizException bizException = (BizException) e;
				ctx.setResponseBody(JSON.toJSONString(RespData.error(bizException.getErrorCodeEnum(), bizException.getArgs())));
			} else {
				ctx.setResponseBody(JSON.toJSONString(RespData.error(ErrorCodeEnum.DEFAULT)));
			}
		}

		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	private void apiCheckSign(HttpServletRequest httpServletRequest) throws Exception {
		Map<String, String[]> queryParams = httpServletRequest.getParameterMap();
		
		String requestURI = httpServletRequest.getRequestURI();
		String signature = httpServletRequest.getParameter("Signature");
		String accessKey = httpServletRequest.getParameter("AccessKeyId");
		
		// 查询api是否需要签名校验，支持正则
		OpenApiEntity openApiEntity = openApiService.findByUrlPattern(requestURI);
		if (openApiEntity == null) {
			throw new BizException(ErrorCodeEnum.API_NO_OPEN);
		}

		// 不需要签名
		if (!openApiEntity.getIfSignVerify()) {
			return;
		}

		// 这四个参数是必须的
		if (!queryParams.containsKey("AccessKeyId") || !queryParams.containsKey("SignatureVersion") || !queryParams.containsKey("SignatureMethod") || !queryParams.containsKey("Timestamp") || !queryParams.containsKey("Signature")) {
			throw new BizException(ErrorCodeEnum.PARAM_ERROR);
		}


		// 签名方法不正确
		if (!httpServletRequest.getParameter("SignatureMethod").equals("HmacSHA256")) {
			throw new BizException(ErrorCodeEnum.SIGNATURE_METHOD_ERROR);
		}

		ApiKeyEntity apiKeyEntity = apiKeyService.selectByAccessKey(accessKey);

		if (apiKeyEntity == null || !apiKeyEntity.getStatus().equals(ApiKeyStatusEnum.NORMAL.getCode())||StringUtils.isBlank(apiKeyEntity.getSecretKey())) {
			throw new BizException(ErrorCodeEnum.API_KEY_INVALID);
		}
		
		
		//判断ip是否允许访问
		if(StringUtils.isNotBlank(apiKeyEntity.getIp())&&!apiKeyEntity.getIp().contains(HttpRequestUtils.getIPAddress(httpServletRequest))) {
			throw new BizException(ErrorCodeEnum.IP_LIMIT);
		}
		


		// 判断当前accessKey是否有权访问当前接口
		// 1.当前key能访问的权限
		List<String> types = Arrays.asList(Optional.ofNullable(apiKeyEntity.getTypes()).orElse("").split(","));
		// 2.当前接口需要哪些权限
		List<String> types2 = Arrays.asList(Optional.ofNullable(openApiEntity.getTypes()).orElse("").split(","));
		boolean containsAll = types.containsAll(types2);
		if (!containsAll) {
			throw new BizException(ErrorCodeEnum.API_KEY_NO_PERM_TO_URL);
		}

		Map<String, String> realParams = new HashMap<>();
		queryParams.forEach((k, v) -> {
			// Signature 和 token 不需要参与签名的计算
			if (!"Signature".equals(k) && !"token".equals(k)) {
				realParams.put(k, v[0]);
			}
		});

		// 根据亚马逊的签名规范
		String method = httpServletRequest.getMethod();
		boolean bool = check(method, requestURI, realParams, apiKeyEntity.getSecretKey(), signature, "api.hotcoin.top");
		if (bool) {
			return;
		}

		boolean check = check(method, requestURI, realParams, apiKeyEntity.getSecretKey(), signature, "hkapi.hotcoin.top");
		if (!check) {
			throw new BizException(ErrorCodeEnum.VERIFY_SIGNATURE_FAILED);
		}
	}

	public static boolean check(String method, String requestURI, Map<String, String> realParams, String appSecret, String Signature, String host) {
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
			SecretKeySpec secKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			hmacSha256.init(secKey);
		} catch (NoSuchAlgorithmException e) {
			return false;
		} catch (InvalidKeyException e) {
			return false;
		}

		String payload = sb.toString();
		byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		// 需要对签名进行base64的编码
		String actualSign = Base64.getEncoder().encodeToString(hash);
		Signature = Signature.replace("\n", "");
		actualSign = actualSign.replace("\n", "");
		if (!Signature.equals(actualSign)) {
			return false;
		}
		return true;
	}

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("UTF-8 encoding not supported!");
		}
	}
}
