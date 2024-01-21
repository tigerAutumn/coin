package com.hotcoin.notice.sms.provider;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.notice.config.MeilianSMSConfig;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.HttpClientUtils;
import com.hotcoin.notice.util.MD5Utils;
import com.hotcoin.notice.util.PhoneNumberUtils;

@Service
public class MeilianProvider implements SmsProvider {
	private static final Logger logger = LoggerFactory.getLogger(MeilianProvider.class);

	@Autowired
	MeilianSMSConfig meilianSMSConfig;

	@Override
	public String sendSms(String mobile, String message,MessageTemplateEntity template) {

		String encode = "UTF-8";
		String username = meilianSMSConfig.getUserName(); // 用户名
		String password = meilianSMSConfig.getPassword(); // 密码
		String apikey = meilianSMSConfig.getApikey(); // apikey秘钥（请登录 http://m.5c.com.cn 短信平台-->账号管理-->我的信息 中复制apikey）

		Map<String, String> data;
		try {
			data = new HashMap<>();
			data.put("type", "send");
			data.put("apikey", apikey);
			data.put("username", username);
			data.put("password_md5", MD5Utils.md5Password(password));
			data.put("encode", encode);
			data.put("mobile", PhoneNumberUtils.chinaPhoneAdd86(mobile));
			data.put("content", URLEncoder.encode(message, encode));
		} catch (Exception e) {
			logger.info(null,e);
			return null;
		}

		// 把发送链接存入buffer中，如连接超时，可能是您服务器不支持域名解析，请将下面连接中的：【m.5c.com.cn】修改为IP：【115.28.23.78】
		Map<String, String> header = new HashMap<>(1);
		// 使用长链接方式
		header.put("Connection", "Keep-Alive");
		String result = HttpClientUtils.postJSON(meilianSMSConfig.getUrl() + "&data=" + JSON.toJSONString(data), null, header);
		logger.info("MeilianService result：{}", result);
		JSONObject jsonObj = (JSONObject) JSON.parse(result);

		return Optional.ofNullable(jsonObj).filter(e -> "success".equalsIgnoreCase(e.getString("status"))).map(e -> e.getString("msg")).orElse(null);
	}

	@Override
	public String getName() {
		return "Meilian";
	}
}
