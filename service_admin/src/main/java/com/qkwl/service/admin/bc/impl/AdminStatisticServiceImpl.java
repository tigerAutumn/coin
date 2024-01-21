package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.dto.statistic.UserPosition;
import com.qkwl.common.dto.statistic.UserTradePosition;
import com.qkwl.common.rpc.admin.IAdminStatisticService;
import com.qkwl.common.util.DateUtil;
import com.qkwl.common.util.DateUtils;
import com.qkwl.service.admin.bc.dao.StatisticMapper;

@Service("adminStatisticService")
public class AdminStatisticServiceImpl implements IAdminStatisticService {

	private static final Logger logger = LoggerFactory.getLogger(AdminStatisticServiceImpl.class);
	
	@Autowired
	private StatisticMapper statisticMapper;
	
	@Override
	public Pagination<EBankRank> selectEBankRankPageList(Pagination<EBankRank> pageParam, Integer tradeId) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("tradeId", tradeId);
		map.put("startTime", pageParam.getBegindate());
		map.put("endTime", pageParam.getEnddate());
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询总交易排行数
		int count = statisticMapper.countEBankRankByParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<EBankRank> activityList = statisticMapper.selectEBankRankList(map);
			for (int i = 0; i < activityList.size(); i++) {
				activityList.get(i).setSort(i+1);
			}
			// 设置返回数据
			pageParam.setData(activityList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public BigDecimal selectEBankTotalAmount(String startTime, String endTime, Integer tradeId) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("tradeId", tradeId);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		BigDecimal totalAmount = statisticMapper.selectEBankTotalAmount(map);
		if (totalAmount == null) {
			totalAmount = BigDecimal.ZERO;
		}
		return totalAmount;
	}
	
	@Override
	public Pagination<UserPosition> selectUserPositionPageList(Pagination<UserPosition> pageParam, UserPosition userPosition) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("coinId", userPosition.getCoinId());
		map.put("choosenDate", userPosition.getChoosenDate());
		map.put("netPosition", userPosition.getNetPosition());
		map.put("position", userPosition.getPosition());
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询总交易排行数
		int count = statisticMapper.countUserPositionByParam(map);
		if(count > 0) {
			// 查询交易排行列表
			List<UserPosition> userPositionList = statisticMapper.selectUserPositionList(map);
			for (int i = 0; i < userPositionList.size(); i++) {
				userPositionList.get(i).setSort(i+1);
			}
			// 设置返回数据
			pageParam.setData(userPositionList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
	@Override
	public BigDecimal selectTotalPosition(UserPosition userPosition) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("coinId", userPosition.getCoinId());
		map.put("choosenDate", userPosition.getChoosenDate());
		map.put("netPosition", userPosition.getNetPosition());
		map.put("position", userPosition.getPosition());
		BigDecimal totalAmount = statisticMapper.selectTotalPosition(map);
		if (totalAmount == null) {
			totalAmount = BigDecimal.ZERO;
		}
		return totalAmount;
	}

	@Override
	public Pagination<UserTradePosition> selectUserTradePositionPageList(Pagination<UserTradePosition> pageParam,
			UserTradePosition userTradePosition) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("coinId", userTradePosition.getCoinId());
		map.put("tradeId", userTradePosition.getTradeId());
		map.put("startDate", DateUtil.strToDate(userTradePosition.getStartDate(),DateUtil.FORMATE_DATE));
		map.put("netCountStart", userTradePosition.getNetCountStart());
		map.put("netCountEnd", userTradePosition.getNetCountEnd());
		map.put("positionStart", userTradePosition.getPositionStart());
		map.put("positionEnd", userTradePosition.getPositionEnd());
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		// 查询净交易持仓统计
		int count = statisticMapper.countUserTradePositionByParam(map);
		if(count > 0) {
			List<UserTradePosition> userTradePositionList = statisticMapper.selectUserTradePositionPageList(map);
			for (int i = 0; i < userTradePositionList.size(); i++) {
				userTradePositionList.get(i).setSort(i+1);
			}
			// 设置返回数据
			pageParam.setData(userTradePositionList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}
	
}
