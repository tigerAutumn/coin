package com.qkwl.service.commission.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.rpc.orepool.IOrepoolService;

@Component
public class OrepoolJob {
	
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(OrepoolJob.class);
	
	@Autowired
	private IOrepoolService orepoolService;
	
	@Scheduled(cron="0 0/1 * * * ? ")
	public void changeFixedPlan() {
		try {
			orepoolService.changeFixedPlan();
		} catch (Exception e) {
			logger.error("定时修改定期计划失败");
		}
	}
	
	@Scheduled(cron="0 0 10 * * ? ")
	public void payCurrentPlan() {
		try {
			orepoolService.payCurrentPlan();
		} catch (Exception e) {
			logger.error("定时支付活期利息失败");
		}
	}
	
	@Scheduled(cron="0 0 10 * * ? ")
	public void payInnovationPlan() {
		try {
			orepoolService.payInnovationPlan();
		} catch (Exception e) {
			logger.error("定时发放创新区锁仓利息失败");
		}
	}
}
