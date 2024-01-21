package com.qkwl.service.admin.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.commission.Commission;
import com.qkwl.common.rpc.admin.IAdminCommissionService;
import com.qkwl.common.util.ReturnResult;

@Component
public class CommissionJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommissionJob.class);
	
	@Autowired
	private IAdminCommissionService adminCommissionService;
	
	/**
	 * 每天中午12点定时发放佣金
	 */
	@Scheduled(cron="0 0 12 1/1 * ? ")
	public void releaseCommission() {
		logger.info("======================发放佣金定时开始======================");
		try {
			List<Commission> commissionList = adminCommissionService.selectAllCommission();
			for (Commission commission : commissionList) {
				//发放佣金
				if (adminCommissionService.grantCommission(commission).getCode() != ReturnResult.SUCCESS) {
					logger.info("============佣金发放失败，id为" + commission.getId() + "================");
				}
			}
		} catch (Exception e) {
			logger.error("=============发放佣金定时失败==============");
		}
	}
}
