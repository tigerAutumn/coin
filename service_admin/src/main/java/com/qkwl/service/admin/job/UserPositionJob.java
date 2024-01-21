package com.qkwl.service.admin.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.statistic.UserTrade;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.service.admin.bc.dao.StatisticMapper;

@Component
public class UserPositionJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserPositionJob.class);
	
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private RedisHelper redisHelper;
	
//	@Scheduled(cron="0 0 23 * * ? ")
	public void work() {
		logger.info("======================用户持仓统计定时开始======================");
		
		//查询所有用户的id
		List<FUser> list = adminUserService.selectAll();
		List<SystemCoinType> coinTypeListAll = redisHelper.getCoinTypeListAll();
		Map<String,Object> map = new HashMap<String, Object>();
		for (SystemCoinType systemCoinType : coinTypeListAll) {
			map.put("coinId", systemCoinType.getId());
			for (FUser user : list) {
				map.put("userId", user.getFid());
				statisticMapper.countUserPosition(map);
			}
		}
        
		logger.info("======================用户持仓统计定时结束======================");
	}
	
	@Scheduled(cron="0 0 1 * * ? ")
	public void countUserTradePosition() {
		logger.info("======================用户净交易持仓统计定时开始======================");
		
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(0);
		Map<String,Object> map = new HashMap<String, Object>();
		for (SystemTradeType systemTradeType : allTradeType) {
			map.put("tradeId", systemTradeType.getId());
			//查询今天改交易对的交易
			List<UserTrade> userTradeList = statisticMapper.selectTradeByTradeId(systemTradeType.getId());
			for (UserTrade userTrade : userTradeList) {
				map.put("userId", userTrade.getUserId());
				map.put("buyCount", userTrade.getBuyCount());
				map.put("sellCount", userTrade.getSellCount());
				map.put("netCount", MathUtils.sub(userTrade.getBuyCount(), userTrade.getSellCount()));
				map.put("createTime", new Date());
				statisticMapper.countUserTradePosition(map);
			}
		}
		logger.info("======================用户净交易持仓统计定时结束======================");
	}
}
