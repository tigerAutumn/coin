package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.otc.SmsConfig;
import com.qkwl.common.util.ReturnResult;

public interface IAdminSmsConfigService {

	/**
	 * 查询所有otc短信商
	 * @param 
	 * @return
	 */
	List<SmsConfig> selectSmsConfigList();
	
	/**
	 * 根据主键ID查询短信商
	 * @param id 主键ID
	 * @return
	 */
	SmsConfig selectBySmsClazz(String smsClazz);
	
	/**
	 * 更新otc短信商
	 * @param smsConfig
	 * @return
	 */
	ReturnResult update(SmsConfig smsConfig);
}
