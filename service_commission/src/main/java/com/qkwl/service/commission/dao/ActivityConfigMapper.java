package com.qkwl.service.commission.dao;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.RankActivityConfig;

@Mapper
public interface ActivityConfigMapper {

	/**
     * 根据活动id查询活动记录
     * @param id
     * @return 
     */
	ActivityConfig selectActivityById(Integer id);
	
	/**
     * 根据活动id查询排行榜活动记录
     * @param id
     * @return 
     */
	RankActivityConfig selectRankActivityById(Integer id);
}
