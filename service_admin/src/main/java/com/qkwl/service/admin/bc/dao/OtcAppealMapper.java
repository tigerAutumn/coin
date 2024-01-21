package com.qkwl.service.admin.bc.dao;

import java.util.List;

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
    
    List<OtcAppeal> selectByOrderId(@Param("orderId")Long orderId,@Param("userId")Integer userId,@Param("statusList") List<Integer> statusList);
}