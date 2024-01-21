package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.user.UserInfoExtend;

@Mapper
public interface UserInfoExtendMapper {

	UserInfoExtend select(UserInfoExtend userInfoExtend);
	
	void update(UserInfoExtend userInfoExtend);
	
	void insert(UserInfoExtend userInfoExtend);
	
}
