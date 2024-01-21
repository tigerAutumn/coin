package com.qkwl.service.commission.job;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.RankActivityConfig;
import com.qkwl.common.dto.commission.CommissionRankList;
import com.qkwl.common.dto.commission.Invitee;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.util.AccountUtil;
import com.qkwl.common.util.Constant;
import com.qkwl.service.commission.dao.ActivityConfigMapper;
import com.qkwl.service.commission.dao.CommissionMapper;

@Component
public class CommissionJob {

	@Autowired
	ActivityConfigMapper activityConfigMapper;
	@Autowired
	CommissionMapper commissionMapper;
	@Autowired
    RedisHelper redisHelper;
	@Autowired
    private MemCache memCache;
	
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommissionJob.class);
	
	@Scheduled(cron="0 0 0 1/1 * ? ")
	public void work() {
		logger.info("======================返佣统计定时开始======================");
		//获取活动开始结束时间
		ActivityConfig activity = activityConfigMapper.selectActivityById(Constant.COMMISSION_ACTIVITY);
		if (activity != null) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date startTime = activity.getStartTime();
			Date endTime = activity.getEndTime();
			//统计榜单
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("startTime", format.format(startTime));
			map.put("endTime", format.format(endTime));
			//统计总条数
			int rankCount = 0;
			List<CommissionRankList> rankCountList = commissionMapper.selectRankCount(map);
			if (rankCountList != null) {
				rankCount = rankCountList.size();
			}
			if (rankCount > 50) {
				rankCount = 50;
			}
			memCache.set(RedisConstant.COMMISSION + "rankCount", rankCount+"", 24*60*60);
			List<CommissionRankList> indexRankList = commissionMapper.selectIndexRankList(map);
			for (CommissionRankList commissionRankList : indexRankList) {
				commissionRankList.setInviterLoginname(AccountUtil.blurAccount(commissionRankList.getInviterLoginname()));
			}
			//统计详情页榜单，每页25条，共2页
			List<CommissionRankList> firstRankList = getRankList(map, 0);
			List<CommissionRankList> secondRankList = getRankList(map, 25);
			//入Redis 设置失效时间为1天
			RedisObject newobj = new RedisObject();
			newobj.setExtObject(indexRankList);
			redisHelper.set(RedisConstant.COMMISSION + "indexRankList", newobj, 24*60*60);
			newobj.setExtObject(firstRankList);
			redisHelper.set(RedisConstant.COMMISSION + "firstRankList", newobj, 24*60*60);
			newobj.setExtObject(secondRankList);
			redisHelper.set(RedisConstant.COMMISSION + "secondRankList", newobj, 24*60*60);
		} else {
			logger.info("======================返佣活动不存在======================");
		}
	}
	
	//分页
	private List<CommissionRankList> getRankList(Map<String,Object> map, int offset) {
		map.put("offset", offset);
		List<CommissionRankList> rankList = commissionMapper.selectRankList(map);
		List<CommissionRankList> orderedRankList = new ArrayList<CommissionRankList>();
		for (int i = 0; i < rankList.size(); i++) {
			CommissionRankList commissionRankList = rankList.get(i);
			commissionRankList.setOrderNumber(i+offset+1);
			commissionRankList.setInviterLoginname(AccountUtil.blurAccount(commissionRankList.getInviterLoginname()));
			orderedRankList.add(commissionRankList);
		}
		return orderedRankList;
	}
	
	@Scheduled(cron="0 0/10 * * * ? ")
	public void getEBankRankPageList() {
		//查询ebank交易赛活动
    	RankActivityConfig activity = activityConfigMapper.selectRankActivityById(Constant.EBANK_RANK_ACTIVITY);
    	Date now = new Date();
    	//判断活动存在，已开启且已到开始时间
    	if (activity != null && activity.getStatus() == Constant.EBANK_RANK_ACTIVITY_OPEN 
    			&& activity.getStartTime().before(now)) {
    		Map<String,Object> map = new HashMap<String, Object>();
    		map.put("startTime", activity.getStartTime());
    		if (activity.getEndTime().after(now)) {
    			map.put("endTime", now);
			} else {
				map.put("endTime", activity.getEndTime());
			}
    		map.put("tradeId", activity.getTradeId());
    		map.put("now", now);
    		//入Redis 设置失效时间为10分钟
    		RedisObject newobj = new RedisObject();
    		//查询50条
    		List<EBankRank> firstPage = commissionMapper.selectEBankRankList(map);
    		newobj.setExtObject(firstPage);
    		for (EBankRank eBankRank : firstPage) {
				eBankRank.setLoginName(AccountUtil.blurAccount(eBankRank.getLoginName()));
			}
    		redisHelper.set(RedisConstant.EBANK_RANK + "list", newobj, 10*60);
    		redisHelper.set(RedisConstant.EBANK_RANK + "updateTime", now.getTime()+"", 10*60);
    	}
	}
}
