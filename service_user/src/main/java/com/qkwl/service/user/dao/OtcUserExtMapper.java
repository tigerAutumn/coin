package com.qkwl.service.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcUserExt;

@Mapper
public interface OtcUserExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OtcUserExt record);

    int insertSelective(OtcUserExt record);

    OtcUserExt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OtcUserExt record);

    int updateByPrimaryKey(OtcUserExt record);
}