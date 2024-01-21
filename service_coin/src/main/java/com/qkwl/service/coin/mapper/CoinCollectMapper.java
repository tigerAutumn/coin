package com.qkwl.service.coin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.coin.CoinCollect;

@Mapper
public interface CoinCollectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CoinCollect record);

    int insertSelective(CoinCollect record);

    CoinCollect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoinCollect record);

    int updateByPrimaryKey(CoinCollect record);
    
    /**
     * 根据币种id和状态查询
     * @param coinId 币种id
     * @param status 状态
     * @return	
     */
    List<CoinCollect> selectByCoinidAndStatus(@Param("coinId") Integer coinId,@Param("status") Integer status);
    
    int deleteByAddress(@Param("address")String address,@Param("coinId")Integer coinId);
    
}