package com.qkwl.service.admin.bc.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.capital.AssetImbalanceHistory;

@Mapper
public interface AssetImbalanceHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AssetImbalanceHistory record);

    int insertSelective(AssetImbalanceHistory record);

    AssetImbalanceHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AssetImbalanceHistory record);

    int updateByPrimaryKey(AssetImbalanceHistory record);
}