package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.capital.DepositFrozenImbalanceHistory;
@Mapper
public interface DepositFrozenImbalanceHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DepositFrozenImbalanceHistory record);

    int insertSelective(DepositFrozenImbalanceHistory record);

    DepositFrozenImbalanceHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DepositFrozenImbalanceHistory record);

    int updateByPrimaryKey(DepositFrozenImbalanceHistory record);
}