package com.qkwl.service.otc.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.otc.OtcUserExt;

@Mapper
public interface OtcUserExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OtcUserExt record);

    int insertSelective(OtcUserExt record);

    OtcUserExt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OtcUserExt record);

    int updateByPrimaryKey(OtcUserExt record);
    
    /**
     * 通过uid查询
     * @param userId 用户id
     * @return 成功条数
     */
    OtcUserExt selectByUserId(@Param("userId")Integer userId);
}