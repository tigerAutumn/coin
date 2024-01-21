/**
 * 
 */
package com.hotcoin.notice.service;

import java.util.List;

import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import com.hotcoin.notice.Enum.SendTypeEnum;
import com.hotcoin.notice.dto.AddSmsConfigReq;
import com.hotcoin.notice.dto.DelSmsConfigReq;
import com.hotcoin.notice.dto.ListSmsConfigReq;
import com.hotcoin.notice.dto.SmsConfigResp;
import com.hotcoin.notice.dto.UpdateSmsConfigReq;
import com.hotcoin.notice.entity.SmsConfigEntity;
import com.hotcoin.notice.util.PageData;

/**
 * @author huangjinfeng
 */
public interface SmsConfigService {

	/**
	 * @param smsText
	 * @return
	 */
	List<SmsConfigEntity> findByAction(SendTypeEnum smsText);

	/**
	 * @param req
	 */
	void addSmsConfig(@Valid AddSmsConfigReq req);

	/**
	 * @param req
	 */
	void updateSmsConfig(@Valid UpdateSmsConfigReq req);

	/**
	 * @param req
	 */
	void delSmsConfig(@Valid DelSmsConfigReq req);

	/**
	 * @param req
	 * @return
	 */
	PageData<SmsConfigResp> listSmsConfig(@Valid ListSmsConfigReq req);


}
