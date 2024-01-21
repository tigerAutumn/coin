package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcOrderLog;

@Mapper
public interface OtcOrderLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OtcOrderLog record);

    int insertSelective(OtcOrderLog record);

    OtcOrderLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OtcOrderLog record);

    int updateByPrimaryKey(OtcOrderLog record);
    
    List<OtcOrderLog> findByOrderId(Long orderId);
}