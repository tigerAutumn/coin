package com.qkwl.service.admin.bc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.statistic.UserDayIncrement;
import com.qkwl.common.dto.statistic.UserMonthIncrement;
@Mapper
public interface UserDayIncrementMapper {
    int deleteByPrimaryKey(@Param("id")Integer id,@Param("tableName") String tableName);

    int insert(UserDayIncrement record);

    int insertSelective(UserDayIncrement record);

    UserDayIncrement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDayIncrement record);

    int updateByPrimaryKey(UserDayIncrement record);
    
    List<UserDayIncrement> selectSumByUserAndDate(Map<String,Object> param);
    
    int selectCountByUserAndDate(Map<String,Object> param);
    
    //建表
    int createNewTable(@Param("tableName")String tableName);
    
    //删表
    int dropTable(@Param("tableName")String tableName);
    
    //查询是否存在
    Integer isTableExists(String tableName);
    
    //查询最后一次统计时间
    UserDayIncrement selectLastDateByUser(@Param("userId")Integer userId,@Param("tableName")String tableName);
    
    /**
     * 通过用户，币种和统计时间查询
     * @param userId
     * @param coinId
     * @param statisticsTime统计时间
     * */
    List<UserDayIncrement> selectByUserCoinDate(@Param("userId")Integer userId,@Param("coinId") Integer coinId,@Param("statisticsTime") Date statisticsTime,@Param("tableName") String tableName);
    
}