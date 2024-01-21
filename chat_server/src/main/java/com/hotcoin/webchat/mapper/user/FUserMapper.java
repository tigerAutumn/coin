package com.hotcoin.webchat.mapper.user;

import com.hotcoin.webchat.model.FUser;
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
	FUser selectByPrimaryKey(Long fid);

}