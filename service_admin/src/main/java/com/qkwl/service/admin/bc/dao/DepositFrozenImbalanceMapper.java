package com.qkwl.service.admin.bc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.capital.DepositFrozenImbalance;

@Mapper
public interface DepositFrozenImbalanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DepositFrozenImbalance record);

    int insertSelective(DepositFrozenImbalance record);

    DepositFrozenImbalance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DepositFrozenImbalance record);

    int updateByPrimaryKey(DepositFrozenImbalance record);
    
    /**
     * 根据币种和用户查询
     * @param userId
     * @param coinId
     * 
     * */
    DepositFrozenImbalance selectByUserAndCoin(@Param("userId")Integer userId,@Param("coinId") Integer coinId);
    
    /**
     * 根据币种和用户删除
     * @param userId
     * @param coinId
     * 
     * */
    int deleteByUserAndCoin(@Param("userId")Integer userId,@Param("coinId") Integer coinId);
    
    
    /**
     * 根据参数查询
     * @param userId
     * @param coinId
     * 
     * */
    List<DepositFrozenImbalance> selectByParam(DepositFrozenImbalance param);
}