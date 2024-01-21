package com.hotcoin.notice.service;

import com.hotcoin.notice.dto.SendEmailReq;
import com.hotcoin.notice.dto.SendSmsReq;

public interface NoticeService {

	/**
	 * @param req
	 */
	void sendSms(SendSmsReq req);

	/**
	 * @param req
	 */
	void sendEmail(SendEmailReq req);



}
