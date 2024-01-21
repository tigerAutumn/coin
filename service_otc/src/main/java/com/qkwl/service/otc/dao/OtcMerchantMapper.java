package com.qkwl.service.otc.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcMerchant;

@Mapper
public interface OtcMerchantMapper {

	int insert(OtcMerchant otcMerchant);
	
	int update(OtcMerchant otcMerchant);
	
	OtcMerchant selectByUid(Integer uid);
	
	BigDecimal getDeposit(Integer uid);
}
