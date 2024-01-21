package com.qkwl.service.admin.bc.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.otc.SmsConfig;
import com.qkwl.common.rpc.admin.IAdminSmsConfigService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.SmsConfigMapper;

@Service("adminSmsConfigService")
public class AdminSmsConfigServiceImpl implements IAdminSmsConfigService {

	@Autowired
	private SmsConfigMapper smsConfigMapper;
	
	@Override
	public List<SmsConfig> selectSmsConfigList() {
		return smsConfigMapper.selectAll();
	}

	@Override
	public SmsConfig selectBySmsClazz(String smsClazz) {
		return smsConfigMapper.selectBySmsClazz(smsClazz);
	}
	
	@Override
	public ReturnResult update(SmsConfig smsConfig) {
		try {
			//关闭已开启的短信商
			smsConfigMapper.close();
			//开启新短信商
			smsConfigMapper.update(smsConfig);
			return ReturnResult.SUCCESS("切换短信商成功！");
		} catch (Exception e) {
			return ReturnResult.FAILUER("切换短信商失败！");
		}
	}

}
