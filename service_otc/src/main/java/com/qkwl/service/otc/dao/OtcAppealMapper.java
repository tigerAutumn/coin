package com.qkwl.service.otc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.otc.OtcAppeal;

@Mapper
public interface OtcAppealMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OtcAppeal record);

    int insertSelective(OtcAppeal record);

    OtcAppeal selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OtcAppeal record);

    int updateByPrimaryKey(OtcAppeal record);
    
    OtcAppeal selectByOrderId(@Param("orderId")Long orderId,@Param("userId")int userId);
}