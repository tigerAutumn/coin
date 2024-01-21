package com.qkwl.service.capital.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.whiteList.UserWhiteList;
@Mapper
public interface UserWhiteListMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWhiteList record);

    int insertSelective(UserWhiteList record);

    UserWhiteList selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWhiteList record);

    int updateByPrimaryKey(UserWhiteList record);
    
    /**
     * 通过uid，coinid，type 查询白名单
     * @author hwj
     * @param userId
     * @param coinId
     * @param type
     * */
    List<UserWhiteList> selectByUserAndCoinAndType(@Param("userId")Integer userId ,@Param("coinId")Integer coinId,@Param("type")Integer type);
}