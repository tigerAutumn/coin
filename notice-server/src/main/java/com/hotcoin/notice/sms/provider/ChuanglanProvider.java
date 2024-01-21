package com.hotcoin.notice.sms.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hotcoin.notice.config.ChuanglanSMSConfig;
import com.hotcoin.notice.dto.ChuanglanSmsSendResponse;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.HttpClientUtils;
import com.hotcoin.notice.util.PhoneNumberUtils;

@Service
public class ChuanglanProvider implements SmsProvider {

	private static final Logger logger = LoggerFactory.getLogger(ChuanglanProvider.class);

	@Autowired
	private ChuanglanSMSConfig chuanglanSMSConfig;


	private String send253NormalSMS(String mobile, String sendMessage) {
		String thirdMsgId = null;
		logger.info("send253NormalSMS send mobile:{}", mobile);
		Map<String, Object> map = new HashMap<>();
		map.put("account", chuanglanSMSConfig.getAccessKey());// API账号
		map.put("password", chuanglanSMSConfig.getSecretKey());// API密码
		map.put("msg", sendMessage.trim());// 短信内容
		map.put("phone", mobile);// 手机号
		map.put("report", "true");// 是否需要状态报告
		String requestJson = JSON.toJSONString(map);
		logger.info("send253NormalSMS before chuanglan send sms, mobile:{},msg:{}", mobile, sendMessage.trim());
		String response = HttpClientUtils.postJSON(chuanglanSMSConfig.getUrl(), requestJson);
		logger.info("send253NormalSMS after chuanglan send253NormalSMS send sms, mobile:{},response:{}", mobile, response);

		if (StringUtils.isNotBlank(response)) {
			ChuanglanSmsSendResponse resp = JSON.parseObject(response, ChuanglanSmsSendResponse.class);
			thirdMsgId = Optional.ofNullable(resp).filter(e -> "0".equals(e.getCode())).map(e -> e.getMsgId()).orElse(null);
		}
		return thirdMsgId;
	}

	private String send253InternationalSms(String mobile, String sendMessage) {
		String thirdMsgId = null;
		Map<String, Object> map = new HashMap<>();
		map.put("account", chuanglanSMSConfig.getInternationalAccessKey());// API账号
		map.put("password", chuanglanSMSConfig.getInternationalSecretKey());// API密码
		map.put("msg", sendMessage.trim());// 短信内容
		map.put("mobile", mobile);// 手机号
		map.put("report", "true");// 是否需要状态报告
		String requestJson = JSON.toJSONString(map);
		logger.info("send253InternationalSms before chuanglan send sms, mobile:{},sendMessage:{}", mobile, sendMessage.trim());
		String response = HttpClientUtils.postJSON(chuanglanSMSConfig.getInternationalUrl(), requestJson);
		logger.info("send253InternationalSms after chuanglan send sms, mobile:{},response:{}", mobile, response);

		if (StringUtils.isNotBlank(response)) {
			ChuanglanSmsSendResponse resp = JSON.parseObject(response, ChuanglanSmsSendResponse.class);
			thirdMsgId = Optional.ofNullable(resp).filter(e -> "0".equals(e.getCode())).map(e -> e.getMsgId()).orElse(null);
		}
		return thirdMsgId;
	}

	@Override
	public String sendSms(String mobile, String message,MessageTemplateEntity template) {
		if (PhoneNumberUtils.isChinaPhoneNum(mobile)) {
			return this.send253NormalSMS(PhoneNumberUtils.chinaPhoneDel86(mobile), message);
		} else {
			return this.send253InternationalSms(mobile, message);
		}
	}

	@Override
	public String getName() {
		return "Chuanglan";
	}
}
