package com.hotcoin.notice.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotcoin.notice.dto.AddMessageTemplateReq;
import com.hotcoin.notice.dto.AddSmsConfigReq;
import com.hotcoin.notice.dto.DelMessageTemplateReq;
import com.hotcoin.notice.dto.DelSmsConfigReq;
import com.hotcoin.notice.dto.ListMessageTemplateReq;
import com.hotcoin.notice.dto.ListSmsConfigReq;
import com.hotcoin.notice.dto.MessageTemplateResp;
import com.hotcoin.notice.dto.RespData;
import com.hotcoin.notice.dto.SendEmailReq;
import com.hotcoin.notice.dto.SendSmsReq;
import com.hotcoin.notice.dto.SmsConfigResp;
import com.hotcoin.notice.dto.UpdateMessageTemplateReq;
import com.hotcoin.notice.dto.UpdateSmsConfigReq;
import com.hotcoin.notice.service.MessageTemplateService;
import com.hotcoin.notice.service.NoticeService;
import com.hotcoin.notice.service.SmsConfigService;
import com.hotcoin.notice.util.PageData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("")
@Api("NoticeController相关api")
public class NoticeController {
	private  static final Logger logger = LoggerFactory.getLogger(NoticeController.class);
	
	@Resource
	private NoticeService noticeService;
	@Resource
	private SmsConfigService smsConfigService;
	@Resource
	private MessageTemplateService messageTemplateService;
	@Resource
	private CacheManager cacheManager;
	

	@ApiOperation(value = "发送短信")
	@PostMapping("/sms/send")
	public RespData<Void> sendSms(@Valid @RequestBody SendSmsReq req) {
		noticeService.sendSms(req);
		return RespData.ok();
	}
	
	@ApiOperation(value = "发送邮件")
	@PostMapping("/email/send")
	public RespData<Void> sendEmail(@Valid @RequestBody SendEmailReq req) {
		noticeService.sendEmail(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "添加sms配置")
	@PostMapping("/sms_config/add")
	public RespData<Void> addSmsConfig(@Valid @RequestBody AddSmsConfigReq req) {
		smsConfigService.addSmsConfig(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "列出sms配置")
	@GetMapping("/sms_config/list")
	public RespData<List<SmsConfigResp>> listSmsConfig(@Valid  ListSmsConfigReq req) {
		PageData<SmsConfigResp> pageData=smsConfigService.listSmsConfig(req);
		return RespData.ok(pageData);
	}
	
	@ApiOperation(value = "列出信息模版")
	@GetMapping("/message_template/list")
	public RespData<List<MessageTemplateResp>> listMessageTemplate(@Valid  ListMessageTemplateReq req) {
		PageData<MessageTemplateResp> pageData=messageTemplateService.listMessageTemplate(req);
		return RespData.ok(pageData);
	}
	
	
	@ApiOperation(value = "修改sms配置")
	@PutMapping("/sms_config/update")
	public RespData<Void> updateSmsConfig(@Valid @RequestBody UpdateSmsConfigReq req) {
		smsConfigService.updateSmsConfig(req);
		return RespData.ok();
	}
	
	@ApiOperation(value = "删除sms配置")
	@DeleteMapping("/sms_config/del")
	public RespData<Void> delSmsConfig(@Valid @RequestBody DelSmsConfigReq req) {
		smsConfigService.delSmsConfig(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "添加信息模版")
	@PostMapping("/message_template/add")
	public RespData<Void> addMessageTemplate(@Valid @RequestBody AddMessageTemplateReq req) {
		messageTemplateService.addMessageTemplate(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "修改信息模版")
	@PutMapping("/message_template/update")
	public RespData<Void> updateMessageTemplateReq(@Valid @RequestBody UpdateMessageTemplateReq req) {
		messageTemplateService.updateMessageTemplateReq(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "删除信息模版")
	@DeleteMapping("/message_template/del")
	public RespData<Void> delMessageTemplateReq(@Valid @RequestBody DelMessageTemplateReq req) {
		messageTemplateService.delMessageTemplateReq(req);
		return RespData.ok();
	}
	
	
	@ApiOperation(value = "查询缓存")
	@GetMapping("/cache/query")
	public RespData<Object> queryCache(@RequestParam String cacheName,@RequestParam String key) {
		return RespData.ok(Optional.ofNullable(cacheManager.getCache(cacheName)).map(e->e.get(key)).map(e->e.get()).orElse(null));
	}
	
	
	@ApiOperation(value = "清理所有缓存")
	@DeleteMapping("/cache/removeAll")
	public RespData<Void> removeAllCache() {
		Collection<String> cacheNames = cacheManager.getCacheNames();
		if(!CollectionUtils.isEmpty(cacheNames)) {
			cacheNames.stream().map(e->cacheManager.getCache(e)).forEach(e->{
				e.clear();
			});
		}
		return RespData.ok();
	}
	

}
