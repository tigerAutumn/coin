package com.qkwl.service.capital.dao.c2c;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.c2c.UserC2CEntrust;

@Mapper
public interface UserC2CEntrustMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserC2CEntrust record);

    int insertSelective(UserC2CEntrust record);

    UserC2CEntrust selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserC2CEntrust record);

    int updateByPrimaryKey(UserC2CEntrust record);
    
    List<UserC2CEntrust> selectList(UserC2CEntrust record);
    
    /**
     * 查询c2c委单条数
     * @param 用户id
     * @param 银行卡id
     * @param 类型
     * @param 状态
     * @return 查询记录数
     */
    Integer countEntrust(@Param("userId") Integer userId,@Param("binkId") Integer bankId,@Param("type") Integer type,@Param("statusList") List<Integer> statusList);
}