package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.excel.ExcelExportTask;


@Mapper
public interface ExcelExportTaskMapper {

    int insert(ExcelExportTask record);

    int insertSelective(ExcelExportTask record);

	List<ExcelExportTask> findAll();

	ExcelExportTask findById(long id);

	int updateByIdSelective(ExcelExportTask record);

	int countExcelExportTaskParam(Map<String, Object> map);

	List<ExcelExportTask> selectExcelExportTaskList(Map<String, Object> map);

	void truncate();

}