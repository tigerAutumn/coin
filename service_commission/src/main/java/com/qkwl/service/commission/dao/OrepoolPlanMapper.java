package com.qkwl.service.commission.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.orepool.OrepoolPlan;

@Mapper
public interface OrepoolPlanMapper {

	/**
	 * 获取定期矿池计划列表
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	List<OrepoolPlan> getFixedPlanList();
	
	/**
	 * 获取活期矿池计划列表
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	List<OrepoolPlan> getCurrentPlanList();
	
	/**
	 * 获取矿池计划列表
	 * @param currentPage
	 * @param numPerPage
	 * @param type
	 * @return
	 */
	List<OrepoolPlan> getCurrentPlanList(Integer type);
	
	/**
	 * 根据id获取矿池计划
	 * @param id
	 * @return
	 */
	OrepoolPlan getPlanById(Integer id);
	
	/**
	 * 根据type查询所有计划
	 * @param type
	 * @return
	 */
	List<OrepoolPlan> getFixedPlan();
	
	/**
	 * 更新
	 * @param orepoolPlan
	 * @return
	 */
	int update(OrepoolPlan orepoolPlan);
}
