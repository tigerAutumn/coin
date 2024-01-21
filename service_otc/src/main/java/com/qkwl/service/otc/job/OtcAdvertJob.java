package com.qkwl.service.otc.job;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.rpc.otc.OtcOrderService;
import com.qkwl.service.otc.dao.ChatMessageMapper;
import com.qkwl.service.otc.dao.OtcAdvertMapper;
import com.qkwl.service.otc.dao.OtcOrderLogMapper;
import com.qkwl.service.otc.dao.OtcOrderMapper;

@Component
public class OtcAdvertJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(OtcAdvertJob.class);
	
	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private IOtcAdvertService otcAdvertService;
	@Autowired
	private OtcOrderService otcOrderService;
	
	@Scheduled(cron="0 0/1 * * * ? ")
	public void advertOverdueJob() {
		try {
			//更改过期广告的状态
			otcAdvertService.putOffUnableAds();
		} catch (Exception e) {
			logger.error("下架定时出错,ex:{}",e);
		}
	}
	
	@Scheduled(cron="0/5 * * * * ?")
	public void otcOrderJob() {
		try {
			//撤销过期订单
			otcOrderService.putOffUnableOrders();
		} catch (Exception e) {
			logger.error("过期订单撤销定时出错,ex:{}",e);
		}
	}
	
}
