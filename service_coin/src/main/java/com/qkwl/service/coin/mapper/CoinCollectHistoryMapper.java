package com.qkwl.service.coin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.coin.CoinCollectHistory;
@Mapper
public interface CoinCollectHistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CoinCollectHistory record);

    int insertSelective(CoinCollectHistory record);

    CoinCollectHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoinCollectHistory record);

    int updateByPrimaryKey(CoinCollectHistory record);
}