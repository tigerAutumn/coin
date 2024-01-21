package com.hotcoin.notice.sms.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotcoin.notice.config.ZTSMSConfig;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.HttpClientUtils;
import com.hotcoin.notice.util.MD5Utils;
import com.hotcoin.notice.util.PhoneNumberUtils;

@Service
public class ZTProvider implements SmsProvider {

	private static final Logger logger = LoggerFactory.getLogger(ZTProvider.class);

	@Autowired
	private ZTSMSConfig ztsmsConfig;

	@Override
	public String sendSms(String mobile, String message,MessageTemplateEntity template) {
		String username = ztsmsConfig.getUsername();
		String password = ztsmsConfig.getPassword();
		String productid = ztsmsConfig.getProductid();
		String host = ztsmsConfig.getUrl();

		Map<String, String> param = new HashMap<>();
		try {
			param.put("username", username);
			param.put("password", MD5Utils.md5Password(password));
			param.put("mobile", PhoneNumberUtils.chinaPhoneAdd86(mobile));
			param.put("content", String.format("%s%s", "【Hotcoin Global】",message));
			param.put("productid", productid);
		} catch (Exception e) {
			logger.error("",e);
			return null;
		} 

		logger.info("ZTProvider send param：{}", param);
		String result = HttpClientUtils.get(host, param);
		logger.info("ZTProvider result：{}", result);
		String[] resultMsg = Optional.ofNullable(result).map(e->e.split(";")).orElse(new String[] {});
		String thirdMsgId = null;
		for (String smsRst : resultMsg) {
			if (!"null".equalsIgnoreCase(smsRst)) {
				if (smsRst.startsWith("1,")) {
					thirdMsgId = smsRst.split(",")[1];
				}
			}
		}
		return thirdMsgId;
	}

	@Override
	public String getName() {
		return "ZT";
	}
}
