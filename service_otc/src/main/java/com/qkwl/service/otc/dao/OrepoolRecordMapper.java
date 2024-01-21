package com.qkwl.service.otc.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.orepool.OrepoolRecord;

@Mapper
public interface OrepoolRecordMapper {

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
}
