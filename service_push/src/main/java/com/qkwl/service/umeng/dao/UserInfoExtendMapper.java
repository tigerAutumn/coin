package com.qkwl.service.umeng.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.user.UserInfoExtend;

@Mapper
public interface UserInfoExtendMapper {

	List<UserInfoExtend> selectUserDeviceToken(Integer userId);
}
