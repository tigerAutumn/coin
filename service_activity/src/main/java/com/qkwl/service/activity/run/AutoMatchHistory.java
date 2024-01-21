package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.service.activity.impl.AutoEntrustHistoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component("autoMatchHistory")
public class AutoMatchHistory {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoMatchHistory.class);
	
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private AutoEntrustHistoryService entrustHistoryService;

	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("AutoMatchHistory");
		thread.start();
	}

	class Work implements Runnable {
		public void run() {
			while (true) {
				try {
					// 获取币种列表
					List<SystemTradeType> systemTradeTypes = redisHelper.getAllTradeTypeList(0);
					if (systemTradeTypes == null) {
						continue;
					}
					long start = System.currentTimeMillis();
					// 遍历虚拟币列表
					for (SystemTradeType systemTradeType : systemTradeTypes) {
						if (systemTradeType == null) {
							continue;
						}
						// 火币撮合跳过
						if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.HUOBI.getCode())) {
							continue;
						}
						// 币种ID
						int tradeId = systemTradeType.getId();

						// 强事务锁单更新
						List<FEntrust> fEntrusts = entrustHistoryService.getHistoryEntrust(tradeId);
						
						//如果是创新区
						if(fEntrusts != null && fEntrusts.size() > 0) {
							//单独处理
							entrustHistoryService.innovationAreaDividend(fEntrusts);
						    //entrustHistoryService.updateMatchHistoryBatch(fEntrusts);
						}
					}
					long end = System.currentTimeMillis();
					//logger.info("====================costTime："+(end-start)+"ms===============");
				} catch (Exception e) {
					logger.error("报错",e);
					continue;
				}
			}
		}
	}
}
