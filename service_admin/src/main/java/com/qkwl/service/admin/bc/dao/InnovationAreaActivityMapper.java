package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.activity.InnovationAreaActivity;

@Mapper
public interface InnovationAreaActivityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InnovationAreaActivity record);

    int insertSelective(InnovationAreaActivity record);

    InnovationAreaActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InnovationAreaActivity record);

    int updateByPrimaryKey(InnovationAreaActivity record);
    
    List<InnovationAreaActivity> listInnovationActivity(Map<String, Object> params);
}