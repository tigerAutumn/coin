package com.qkwl.service.admin.bc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.dto.statistic.UserPosition;
import com.qkwl.common.dto.statistic.UserTrade;
import com.qkwl.common.dto.statistic.UserTradePosition;

@Mapper
public interface StatisticMapper {

	/**
     * ebankRank查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
	Integer countEBankRankByParam(Map<String,Object> map);
	
	/**
     * ebankRank分页查询
     * @param map 参数map
     * @return 
     */
	List<EBankRank> selectEBankRankList(Map<String,Object> map);
	
	/**
	 * 查询汇总交易额
	 * @param map
	 * @return
	 */
	BigDecimal selectEBankTotalAmount(Map<String,Object> map);
	
	/**
	 * 查询用户持仓总记录数
	 * @param map
	 * @return
	 */
	Integer countUserPositionByParam(Map<String,Object> map);
	
	/**
	 * 用户持仓分页查询
	 * @param map
	 * @return
	 */
	List<UserPosition> selectUserPositionList(Map<String,Object> map);
	
	/**
	 * 查询汇总持仓数
	 * @param map
	 * @return
	 */
	BigDecimal selectTotalPosition(Map<String,Object> map);
	
	/**
	 * 统计持仓
	 * @param map
	 */
	void countUserPosition(Map<String,Object> map);
	
	/**
	 * 查询用户净交易持仓总记录数
	 * @param map
	 * @return
	 */
	Integer countUserTradePositionByParam(Map<String,Object> map);
	
	/**
	 * 用户净交易持仓分页查询
	 * @param map
	 * @return
	 */
	List<UserTradePosition> selectUserTradePositionPageList(Map<String,Object> map);
	
	/**
	 * 查询用户交易
	 * @param tradeId
	 * @return
	 */
	List<UserTrade> selectTradeByTradeId(Integer tradeId);
	
	/**
	 * 统计净交易持仓
	 * @param map
	 */
	void countUserTradePosition(Map<String,Object> map);
}
