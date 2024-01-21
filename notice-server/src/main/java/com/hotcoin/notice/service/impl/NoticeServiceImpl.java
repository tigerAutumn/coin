package com.hotcoin.notice.service.impl;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hotcoin.notice.Enum.ErrorCodeEnum;
import com.hotcoin.notice.Enum.SendStatusEnum;
import com.hotcoin.notice.dto.SendEmailReq;
import com.hotcoin.notice.dto.SendSmsReq;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.entity.SmsMessageEntity;
import com.hotcoin.notice.exception.NoticeException;
import com.hotcoin.notice.service.MessageTemplateService;
import com.hotcoin.notice.service.NoticeService;
import com.hotcoin.notice.service.SmsConfigService;
import com.hotcoin.notice.service.SmsMessageService;
import com.hotcoin.notice.sms.provider.SmsProvider;
import com.hotcoin.notice.sms.provider.SmsProviderFactory;
import com.hotcoin.notice.util.Constant;
import com.hotcoin.notice.util.Placeholder;
import com.hotcoin.notice.util.RedisHelper;

@Service
public class NoticeServiceImpl implements NoticeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private MessageTemplateService noticeTemplateService;
	@Resource
	private SmsMessageService smsMessageService;
	@Resource
	private SmsConfigService smsConfigService;
	@Resource(name = "sendSmsExecutor")
	private Executor sendSmsExecutor;

	@Override
	public void sendSms(SendSmsReq req) {
		logger.info("开始发送短信,参数:{}",JSON.toJSONString(req));
		//限制发送频率
		RMapCache<Long, Integer> mapCache = RedisHelper.getMapCache(Constant.REDIS_SMS_LIMIT_DB,req.getPhone());
		if(mapCache.size()>=Constant.SMS_LIMIT_NUM) {
			throw new NoticeException(ErrorCodeEnum.SMS_LIMIT_ERROR);
		}
		mapCache.put(new Date().getTime(), 0, 1,TimeUnit.MINUTES);
		
		MessageTemplateEntity template = noticeTemplateService.findSmsTemplate(req.getPlatform(), req.getBusinessType(), req.getLang());
		if (template == null) {
			throw new NoticeException(ErrorCodeEnum.SMS_TEMPLATE_NOT_FOUND);
		}

		CompletableFuture.runAsync(() -> {
			try {
				String msg = Placeholder.replace(template.getTemplate(), req.getParams());
				SmsProvider smsService = null;
				String thirdMsgId = null;
				// 发送失败重试最多5次
				for (int i = 0; i < 5; i++) {
					smsService = SmsProviderFactory.getSmsProvider();
					thirdMsgId = smsService.sendSms(req.getPhone(), msg,template);
					if (StringUtils.isNotBlank(thirdMsgId)) {
						break;
					}
					logger.error("短信提供商【{}】发送失败,选择其他提供商重试",smsService.getName());
				}
				SmsMessageEntity sms = new SmsMessageEntity();
				sms.setContent(msg);
				sms.setTemplateId(template.getId());
				sms.setPhone(req.getPhone());
				sms.setPlatform(req.getPlatform());
				sms.setThirdMsgId(thirdMsgId);
				sms.setSendChannel(smsService.getName());
				sms.setStatus(StringUtils.isNotBlank(thirdMsgId) ? SendStatusEnum.SEND_SUCCESS.getCode() : SendStatusEnum.SEND_FAILURE.getCode());
				sms.setSendTime(StringUtils.isNotBlank(thirdMsgId) ? new Date() : null);
				smsMessageService.save(sms);
			} finally {
				SmsProviderFactory.remove();
			}
		}, sendSmsExecutor);
	}

	@Override
	public void sendEmail(SendEmailReq req) {
		logger.info("开始发送邮件,参数:{}",JSON.toJSONString(req));
		//限制发送频率
		RMapCache<Long, Integer> mapCache = RedisHelper.getMapCache(Constant.REDIS_EMAIL_LIMIT_DB,req.getEmail());
		if(mapCache.size()>=Constant.EMAIL_LIMIT_NUM) {
			throw new NoticeException(ErrorCodeEnum.SMS_LIMIT_ERROR);
		}
		mapCache.put(new Date().getTime(), 0, 1,TimeUnit.MINUTES);
		
		MessageTemplateEntity template = noticeTemplateService.findEmailTemplate(req.getPlatform(), req.getBusinessType(), req.getLang());
		if (template == null) {
			throw new NoticeException(ErrorCodeEnum.EMAIL_TEMPLATE_NOT_FOUND);
		}
		
		//TODO
	}
}
