package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.SmsConfig;

@Mapper
public interface SmsConfigMapper {

	List<SmsConfig> selectAll();
	
	SmsConfig selectBySmsClazz(String smsClazz);
	
	void close();
	
	void update(SmsConfig smsConfig);
}
