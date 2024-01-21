package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.service.user.dao.base.ApiKeyBaseMapper;

@Mapper
public interface ApiKeyMapper extends  ApiKeyBaseMapper{

	/**
	 * 
	 */
	void updateExpireKeyStatus();
   
}