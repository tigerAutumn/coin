package com.qkwl.service.capital.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.wallet.UserWalletBalanceLog;

@Mapper
public interface UserWalletBalanceLogMapper {
	
	List<UserWalletBalanceLog> selectList(Map<String, Object> param);
	
	int insert(UserWalletBalanceLog record);
}