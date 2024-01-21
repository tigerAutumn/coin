package com.qkwl.service.activity.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrepoolPlanMapper {

	/**
	 * 根据id获取创新区存币计划
	 * @param id
	 * @return
	 */
	int getInnovationPlanByCoinId(Integer coinId);
	
}
