package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.service.admin.bc.dao.ExcelExportTaskMapper;

@Service("adminExcelExportTaskService")
public class AdminExcelExportTaskServiceImpl implements IAdminExcelExportTaskService {

	private static final Logger logger = LoggerFactory.getLogger(AdminExcelExportTaskServiceImpl.class);
	
	
	@Autowired
	private ExcelExportTaskMapper excelExportTaskMapper;

	@Override
	public List<ExcelExportTask> listExcelExportTask() {
		List<ExcelExportTask> list=excelExportTaskMapper.findAll();
		return list;
	}
	
	
	@Override
	public long insertSelective(ExcelExportTask excelExportTask) {
		excelExportTaskMapper.insertSelective(excelExportTask);
		return excelExportTask.getId();
	}
	

	@Override
	public ExcelExportTask getExcelById(long id) {
		return excelExportTaskMapper.findById(id);
	}


	@Override
	public int updateByIdSelective(ExcelExportTask updateExcelExportTask) {
				return excelExportTaskMapper.updateByIdSelective(updateExcelExportTask);
	}


	@Override
	public Pagination<ExcelExportTask> selectExcelExportTaskPageList(Pagination<ExcelExportTask> pageParam,String operator) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("operator", operator);
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总交易排行数
		int count = excelExportTaskMapper.countExcelExportTaskParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<ExcelExportTask> excelExportTaskList = excelExportTaskMapper.selectExcelExportTaskList(map);
			// 设置返回数据
			pageParam.setData(excelExportTaskList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}


	@Override
	public void truncate() {
		excelExportTaskMapper.truncate();
		
	}


}
