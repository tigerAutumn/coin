package com.qkwl.service.activity.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.user.FUserIdentity;

/**
 * 用户实名信息
 * @author ZKF
 */
@Mapper
public interface FUserIdentityMapper {
    int updateByPrimaryKey(FUserIdentity record);
 
    //查询所有需要更新照片的实名认证用户
    List<FUserIdentity> selectAllNewValidate();
}