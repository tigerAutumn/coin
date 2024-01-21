package com.qkwl.common.rpc.admin;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.statistic.UserPosition;

/**
 * excel导出接口
 * @author huangjinfeng
 * 2019年4月12日
 */
public interface IAdminExcelExportTaskService {
	
	
	public List<ExcelExportTask> listExcelExportTask();
	
	
	public ExcelExportTask getExcelById(long id);


	/**
	 * @param excelExportTask
	 * @return id
	 */
	long insertSelective(ExcelExportTask excelExportTask);


	public int updateByIdSelective(ExcelExportTask updateExcelExportTask);


	public Pagination<ExcelExportTask> selectExcelExportTaskPageList(Pagination<ExcelExportTask> pageParam, String operator);


	public void truncate();
	
	
	
	
}
