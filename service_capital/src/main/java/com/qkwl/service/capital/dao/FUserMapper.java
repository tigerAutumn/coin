package com.qkwl.service.capital.dao;

import com.qkwl.common.dto.user.FUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据接口
 * @author LY
 */
@Mapper
public interface FUserMapper {
	
	/**
	 * ID查询用户
	 */
	FUser selectByPrimaryKey(int fid);

}