package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.activity.NetTradeRankActivity;
import com.qkwl.common.dto.activity.NetTradeRankStatistic;

@Mapper
public interface NetTradeRankStatisticMapper {

	/**
     * 活动分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countByParam(Map<String, Object> map);
	
	/**
     * 活动分页查询
     * @param map 参数map
     * @return 
     */
	List<NetTradeRankStatistic> getPageList(Map<String, Object> map);
	
	List<NetTradeRankStatistic> countRank(NetTradeRankActivity activity);
	
	void snapshot(Map<String,Object> map);
}
