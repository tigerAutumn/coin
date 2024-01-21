package com.hotcoin.notice.sms.provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.RegExUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.PhoneNumberUtils;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import com.yunpian.sdk.model.Template;

@Service
public class YunpianProvider implements SmsProvider {

	private static final Logger logger = LoggerFactory.getLogger(YunpianProvider.class);
	
	//记录已经导入的模版
	private static Set<String> alreadyImportTpl=new HashSet<>();
	
	private  YunpianClient clnt =null;
	
	@Value("${sms.yunpian.apikey}")
	private String apikey;
	
	@PostConstruct
	private void init(){
		clnt=new YunpianClient(apikey).init();
	}
	
	@PreDestroy
	public void destroy(){
		clnt.close();
	}

	@Override
	public String sendSms(String mobile, String message,MessageTemplateEntity template) {
		if (PhoneNumberUtils.isChinaPhoneNum(mobile)) {
			mobile=PhoneNumberUtils.chinaPhoneDel86(mobile);
		} else {
			mobile=PhoneNumberUtils.internationalPhoneAddPlus(mobile);
		}
		
		Map<String, String> param = clnt.newParam(2);
		param.put(YunpianClient.MOBILE, mobile);
		param.put(YunpianClient.TEXT, String.format("【Hotcoin Global】%s", message));
		Result<SmsSingleSend> r = clnt.sms().single_send(param);
		logger.info("Yunpian result：{}", r.toString());
		
		//找不到模版，导入模版
		if(r.getCode()==5) {
			importTpl(template);
		}
		
		return Optional.ofNullable(r).filter(e->0==e.getCode()).map(e->e.getData()).map(e->String.valueOf(e.getSid())).orElse(null);
	}


	@Override
	public String getName() {
		return "Yunpian";
	}
	
	private void importTpl(MessageTemplateEntity template) {
		if(template==null||alreadyImportTpl.contains(template.getTemplate())) {
			return;
		}
		
		logger.info("云片未找到短信模版,新增模版");
		String tplContent=RegExUtils.replacePattern(template.getTemplate(), "\\$\\{(\\w+)\\}", "#$1#");
		String lang="";
		switch (template.getLang()) {
		case "zh_CN":
			lang="zh_cn";
			break;
		case "en_US":
			lang="en";
			break;
		case "ko_KR":
			lang="ko";
			break;
		default:
			lang="zh_cn";
			break;
		}
		Map<String, String> map=new HashMap<>(4);
		map.put("apikey", apikey);
		map.put("tpl_content", String.format("【Hotcoin Global】%s", tplContent));
		map.put("notify_type", "1");
		map.put("lang",lang);
		Result<Template> result = clnt.tpl().add(map);
		logger.info("云片添加模版返回结果:{}",result.toString());
		if(result.getCode()==0) {
		alreadyImportTpl.add(template.getTemplate());
		}
	}
	
}
