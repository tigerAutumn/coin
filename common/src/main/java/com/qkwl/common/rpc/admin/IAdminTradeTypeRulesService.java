package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.coin.SystemTradeTypeRule;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.util.ReturnResult;

public interface IAdminTradeTypeRulesService {

	/**
	 * 分页查询交易对规则
	 */
	public Pagination<SystemTradeTypeRule> selectTradeTypeRulesPageList(Pagination<SystemTradeTypeRule> pageParam, SystemTradeTypeRule tradeTypeRules);
	
	/**
	 * 根据id查询交易对规则
	 */
	public SystemTradeTypeRule selectTradeTypeRulesById(Integer id);
	
	/**
     * 新增规则
     */
	public ReturnResult insertTradeTypeRules(SystemTradeTypeRule tradeTypeRules);
	
	/**
	 * 删除规则
	 */
	public ReturnResult deleteTradeTypeRules(SystemTradeTypeRule tradeTypeRules);
	
	/**
	 * 修改规则
	 */
	public ReturnResult updateTradeTypeRules(SystemTradeTypeRule tradeTypeRules);
}
