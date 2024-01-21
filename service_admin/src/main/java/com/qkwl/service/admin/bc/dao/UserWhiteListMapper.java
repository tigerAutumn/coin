package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

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
     * 查询白名单详情
     * @param 
     * */
    List<UserWhiteList> selectByParam(Map<String,Object> param);
    
    /**
     * 查询白名单数量
     * @param 
     * */
    Integer selectByParamCount(Map<String,Object> param);
}