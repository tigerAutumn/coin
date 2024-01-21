package com.qkwl.service.admin.bc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.statistic.UserMonthIncrement;
@Mapper
public interface UserMonthIncrementMapper {
    int deleteByPrimaryKey(@Param("id")Integer id,@Param("tableName") String tableName);

    int insert(UserMonthIncrement record);

    int insertSelective(UserMonthIncrement record);

    UserMonthIncrement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserMonthIncrement record);

    int updateByPrimaryKey(UserMonthIncrement record);
    
    /**
     * 通过用户，币种和统计时间查询
     * @param userId
     * @param coinId
     * @param statisticsTime统计时间
     * */
    List<UserMonthIncrement> selectByUserCoinDate(@Param("userId")Integer userId,@Param("coinId") Integer coinId,@Param("statisticsTime") Date statisticsTime,@Param("tableName") String tableName);
    
    /**
     * 通过用户，币种和统计时间查询
     * @param userId
     * @param coinId
     * @param statisticsTime统计时间
     * */
    List<UserMonthIncrement> selectSumByUserAndDate(Map<String,Object> params);
    
    
  //查询最后一次统计时间
    UserMonthIncrement selectLastDateByUser(@Param("userId")Integer userId,@Param("tableName") String tableName);
    
    
    int selectCountByUserAndDate(Map<String,Object> param);
    
  //建表
    int createNewTable(@Param("tableName")String tableName);
    
    //删表
    int dropTable(@Param("tableName")String tableName);
    
    //查询是否存在
    Integer isTableExists(String tableName);
    
    
    
    
    
}