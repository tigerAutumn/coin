package com.hotcoin.notice.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hotcoin.notice.dao.SmsMessageMapper;
import com.hotcoin.notice.entity.SmsMessageEntity;
import com.hotcoin.notice.service.SmsMessageService;

@Service
public class SmsMessageServiceImpl implements SmsMessageService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private SmsMessageMapper smsMessageMapper;

	@Override
	public int save(SmsMessageEntity sms) {
		return smsMessageMapper.insertSelective(sms);
	}


}
