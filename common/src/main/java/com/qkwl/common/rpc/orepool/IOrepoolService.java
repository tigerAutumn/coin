package com.qkwl.common.rpc.orepool;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.result.Result;

public interface IOrepoolService {

	/**
	 * 获取定期矿池计划列表
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	PageInfo<OrepoolPlan> getFixedPlanList(Integer currentPage, Integer numPerPage);
	
	/**
	 * 获取活期矿池计划列表
	 * @param currentPage
	 * @param numPerPage
	 * @return
	 */
	PageInfo<OrepoolPlan> getCurrentPlanList(Integer currentPage, Integer numPerPage);
	
	/**
	 * 查询计划参与人数
	 * @param planId
	 * @return
	 */
	Integer getPersonCount(Integer planId);
	
	/**
	 * 根据id获取矿池计划
	 * @param id
	 * @return
	 */
	OrepoolPlan getPlanById(Integer id);
	
	/**
	 * 锁仓
	 * @param id
	 * @param lockVolume
	 * @return
	 */
	Result lockPlan(Integer userId, Integer id, BigDecimal lockVolume) throws BCException;
	
	/**
	 * 解锁
	 * @param id
	 * @return
	 */
	Result unlockPlan(Integer userId, Integer id) throws BCException;
	
	/**
	 * 获取矿池记录列表
	 * @param currentPage
	 * @param numPerPage
	 * @param type
	 * @return
	 */
	PageInfo<OrepoolRecord> getRecordList(Integer currentPage, Integer numPerPage, Map<String, Object> param);
	
	/**
	 * 查询用户余额
	 * @param uid
	 * @param coinId
	 * @return
	 */
	BigDecimal getAccountBalance(Integer uid, Integer coinId);
	
	/**
	 * 定时修改定期计划
	 */
	void changeFixedPlan();
	
	/**
	 * 定时发放活期利息
	 */
	void payCurrentPlan();
	
	/**
	 * 定时发放创新区锁仓利息
	 */
	void payInnovationPlan();
	
	/**
	 * 查询活期昨日收益
	 * @param param
	 * @return
	 */
	BigDecimal getYesterdayProfit(Map<String, Object> param);
	
	/**
	 * 查询活期累计收益
	 * @param param
	 * @return
	 */
	BigDecimal getTotalProfit(Map<String, Object> param);
}
