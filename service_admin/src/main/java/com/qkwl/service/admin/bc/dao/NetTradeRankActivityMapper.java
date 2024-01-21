package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.activity.NetTradeRankActivity;

@Mapper
public interface NetTradeRankActivityMapper {

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
	List<NetTradeRankActivity> getPageList(Map<String, Object> map);
	
	/**
     * 根据id查询活动
     * @param id
     * @return
     */
	NetTradeRankActivity getById(Integer id);
	
	/**
     * 新增活动
     * @param RankActivity
     * @return
     */
	void insertNetTradeRankActivity(NetTradeRankActivity activity);
	
	/**
     * 修改活动
     * @param id
     * @return 
     */
	void updateNetTradeRankActivity(NetTradeRankActivity activity);
	
	/**
     * 活动分页查询
     * @return 
     */
	List<NetTradeRankActivity> selectNotSnapshot();
	
	/**
	 * 更新用户快照
	 * @param id
	 */
	void updateSnapshot(Integer id);
}
