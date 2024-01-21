package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.orepool.OrepoolPlan;

@Mapper
public interface OrepoolPlanMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(OrepoolPlan plan);

    OrepoolPlan selectByPrimaryKey(Integer id);

    int update(OrepoolPlan plan);
    
    List<OrepoolPlan> selectAll();
	
	/**
     * 定期计划分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countPlanListByParam(Map<String, Object> map);
    
    /**
     * 定期计划分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OrepoolPlan> getPlanPageList(Map<String, Object> map);
}
