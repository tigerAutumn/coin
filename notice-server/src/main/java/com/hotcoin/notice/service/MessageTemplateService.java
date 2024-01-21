/**
 * 
 */
package com.hotcoin.notice.service;

import javax.validation.Valid;

import com.hotcoin.notice.Enum.TemplateTypeEnum;
import com.hotcoin.notice.dto.AddMessageTemplateReq;
import com.hotcoin.notice.dto.DelMessageTemplateReq;
import com.hotcoin.notice.dto.ListMessageTemplateReq;
import com.hotcoin.notice.dto.MessageTemplateResp;
import com.hotcoin.notice.dto.UpdateMessageTemplateReq;
import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.util.PageData;

/**
 * @author huangjinfeng
 */
public interface MessageTemplateService {
	
	
	MessageTemplateEntity findTemplate(TemplateTypeEnum templateType,String platform,String businessType,String lang);

	MessageTemplateEntity findSmsTemplate(String platform, String businessType, String lang);

	MessageTemplateEntity findEmailTemplate(String platform, String businessType, String lang);

	void addMessageTemplate(@Valid AddMessageTemplateReq req);

	void updateMessageTemplateReq(@Valid UpdateMessageTemplateReq req);

	void delMessageTemplateReq(@Valid DelMessageTemplateReq req);

	PageData<MessageTemplateResp> listMessageTemplate(@Valid ListMessageTemplateReq req);

}
