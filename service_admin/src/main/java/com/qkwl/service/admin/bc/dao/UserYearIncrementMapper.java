package com.qkwl.service.admin.bc.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.statistic.UserYearIncrement;
@Mapper
public interface UserYearIncrementMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserYearIncrement record);

    int insertSelective(UserYearIncrement record);

    UserYearIncrement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserYearIncrement record);

    int updateByPrimaryKey(UserYearIncrement record);
    
    //查询最后一次统计时间
    UserYearIncrement selectLastDateByUser(@Param("userId")Integer userId);
    
    
    /**
     * 通过用户，币种和统计时间查询
     * @param userId
     * @param coinId
     * @param statisticsTime统计时间
     * */
    List<UserYearIncrement> selectByUserCoinDate(@Param("userId")Integer userId,@Param("coinId") Integer coinId,@Param("statisticsTime") Date statisticsTime);
    
    
    /**
     * 通过用户，币种和统计时间查询
     * @param userId
     * @param coinId
     * @param statisticsTime统计时间
     * */
    List<UserYearIncrement> selectSumByUserAndDate(Map<String,Object> params);
}