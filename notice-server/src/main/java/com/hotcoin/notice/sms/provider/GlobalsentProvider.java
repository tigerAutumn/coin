package com.hotcoin.notice.sms.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.notice.config.GlobalsentSMSConfig;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.HttpClientUtils;
import com.hotcoin.notice.util.PhoneNumberUtils;

@Service
public class GlobalsentProvider implements SmsProvider {

	private static final Logger logger = LoggerFactory.getLogger(GlobalsentProvider.class);

	@Autowired
	private GlobalsentSMSConfig globalsentSMSConfig;

	@Override
	public String sendSms(String mobile, String message,MessageTemplateEntity template) {
		String host = globalsentSMSConfig.getUrl();
		Map<String, String> parmas = new HashMap<>();
		parmas.put("access_key", globalsentSMSConfig.getAccessKey());
		parmas.put("mobile", PhoneNumberUtils.chinaPhoneAdd86(mobile));
		parmas.put("content", message);
		String result = HttpClientUtils.get(host,parmas);
		logger.info("GlobalsentService result：{}", result);
		JSONObject jsonObj = (JSONObject) JSON.parse(result);
		
		return Optional.ofNullable(jsonObj)
				.filter(e->"0000".equals(e.getString("code")))
				.map(e->e.getString("data"))
				.map(e->(JSONObject) JSON.parse(e))
				.map(e->e.getString("send_id"))
				.orElse(null);
	}

	@Override
	public String getName() {
		return "Globalsent";
	}
}
