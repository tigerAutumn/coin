package com.qkwl.admin.layui.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;

//@Component
//不使用
public class ExcelExportCleanJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExcelExportCleanJob.class);
	
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Value("${excel.path}")
	private String excelRootPath;
	
	/**
	 * 每天0点定时清空excel_export_task表
	 */
	@Scheduled(cron="0 0 0 * * ? ")
	public void releaseCommission() {
		logger.info("======================清空excel_export_task表开始======================");
		try {
			adminExcelExportTaskService.truncate();
		} catch (Exception e) {
			logger.error("=============清空excel_export_task表失败==============");
		}
	}
}
