package com.qkwl.service.admin.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.activity.NetTradeRankActivity;
import com.qkwl.common.dto.activity.NetTradeRankStatistic;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.service.admin.bc.dao.FUserMapper;
import com.qkwl.service.admin.bc.dao.NetTradeRankActivityMapper;
import com.qkwl.service.admin.bc.dao.NetTradeRankStatisticMapper;

@Component
public class NetTradeRankJob {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserPositionJob.class);
	
	@Autowired
	private NetTradeRankActivityMapper netTradeRankActivityMapper;
	@Autowired
	private NetTradeRankStatisticMapper netTradeRankStatisticMapper;
	@Autowired
	private FUserMapper userMapper;
	
	@Scheduled(cron="0 0/1 * * * ? ")
	public void work() {
		//查询所有未快照的活动
		List<NetTradeRankActivity> list = netTradeRankActivityMapper.selectNotSnapshot();
		Map<String,Object> map = new HashMap<String, Object>();
		for (NetTradeRankActivity activity : list) {
			Integer activityId = activity.getId();
			logger.info("===============快照净交易排行榜，id:{}" ,activityId);
			map.put("activityId", activityId);
			//查询满足条件的记录
			List<NetTradeRankStatistic> rankList = netTradeRankStatisticMapper.countRank(activity);
			for (NetTradeRankStatistic rank : rankList) {
				map.put("userId", rank.getUserId());
				map.put("buyCount", rank.getBuyCount());
				map.put("sellCount", rank.getSellCount());
				map.put("netCount", rank.getNetCount());
				map.put("position", rank.getPosition());
				//查询用户
				FUser user = userMapper.selectByPrimaryKey(rank.getUserId());
				if (user != null) {
					map.put("telephone", user.getFtelephone());
					map.put("email", user.getFemail());
				}
				netTradeRankStatisticMapper.snapshot(map);
			}
			//更新状态为已快照
			netTradeRankActivityMapper.updateSnapshot(activity.getId());
		}
	}
		
}
