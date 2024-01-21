package com.qkwl.service.coin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.user.FUserExtend;

@Mapper
public interface FUserExtendMapper {

	/**
     * 根据uid查询用户扩展信息
     * @param uid
     * @return
     */
    FUserExtend selectByUid(Integer uid);
    
}
