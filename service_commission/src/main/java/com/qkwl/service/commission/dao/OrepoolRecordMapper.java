package com.qkwl.service.commission.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.orepool.OrepoolRecord;

@Mapper
public interface OrepoolRecordMapper {

	/**
	 * 获取矿池记录列表
	 * @param currentPage
	 * @param numPerPage
	 * @param type
	 * @return
	 */
	List<OrepoolRecord> getRecordList(Map<String, Object> param);
	
	/**
	 * 查询计划参与人数
	 * @param planId
	 * @return
	 */
	Integer getPersonCount(Integer planId);
	
	/**
	 * 根据计划id和用户id查询记录
	 * @param planId
	 * @param userId
	 * @return
	 */
	OrepoolRecord getRecord(Map<String, Object> param);
	
	/**
	 * 根据id查询记录
	 * @param id
	 * @return
	 */
	OrepoolRecord getRecordById(Integer id);
	
	/**
	 * 根据planId查询记录
	 * @param planId
	 * @return
	 */
	List<OrepoolRecord> getRecordByPlanId(Integer planId);
	
	/**
	 * 查询所有活期计划
	 * @return
	 */
	List<OrepoolRecord> getCurrentRecord();
	
	/**
	 * 查询所有创新区锁仓计划
	 * @return
	 */
	List<OrepoolRecord> getInnovationRecord();
	
	/**
	 * 矿池生成记录
	 * @param orepoolRecord
	 * @return
	 */
	int insert(OrepoolRecord orepoolRecord);
	
	/**
	 * 矿池更新记录
	 * @param orepoolRecord
	 * @return
	 */
	int update(OrepoolRecord orepoolRecord);
}
