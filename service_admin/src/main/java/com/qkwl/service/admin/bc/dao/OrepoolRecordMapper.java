package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;

@Mapper
public interface OrepoolRecordMapper {

    List<OrepoolPlan> selectAll();
	
	/**
     * 定期记录分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countRecordListByParam(Map<String, Object> map);
    
    /**
     * 定期记录分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OrepoolRecord> getRecordPageList(Map<String, Object> map);
	
	/**
	 * 查询用户创新区锁仓记录
	 * @param record
	 * @return
	 */
	List<OrepoolRecord> getInnovationRecord(OrepoolRecord record);
	
	/**
	 * 矿池更新记录
	 * @param orepoolRecord
	 * @return
	 */
	int update(OrepoolRecord orepoolRecord);
	
	/**
	 * 根据planId查询未解锁的记录条数
	 * @param planId
	 * @return
	 */
	int countLockRecord(Integer planId);
	
	/**
	 * 根据planId查询未解锁的记录
	 * @param planId
	 * @return
	 */
	List<OrepoolRecord> selectLockRecord(Integer planId);
}
