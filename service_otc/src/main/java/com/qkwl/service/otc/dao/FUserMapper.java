package com.qkwl.service.otc.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.user.FUser;

@Mapper
public interface FUserMapper {
    int deleteByPrimaryKey(Integer fid);

    int insert(FUser record);

    int insertSelective(FUser record);

    FUser selectByPrimaryKey(Integer fid);

    int updateByPrimaryKeySelective(FUser record);

    int updateByPrimaryKey(FUser record);
}