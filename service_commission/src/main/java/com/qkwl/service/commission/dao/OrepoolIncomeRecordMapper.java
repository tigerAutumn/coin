package com.qkwl.service.commission.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.orepool.OrepoolIncomeRecord;

@Mapper
public interface OrepoolIncomeRecordMapper {

	int insert(OrepoolIncomeRecord record);
}
