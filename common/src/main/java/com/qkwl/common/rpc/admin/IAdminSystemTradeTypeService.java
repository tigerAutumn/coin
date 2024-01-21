package com.qkwl.common.rpc.admin;


import java.util.Map;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;

public interface IAdminSystemTradeTypeService {
	/**
	 * 分页查询交易信息
	 * @param page 分页数据
 	 * @param tradeType 交易信息
 	 * @param areaType 交易区类型
	 * @return
	 */
	public Pagination<SystemTradeType> selectSystemTradeTypeList(Pagination<SystemTradeType> page, SystemTradeType tradeType,Integer areaType);
	
	/**
	 * 查询交易信息
	 * @param tradeId 交易ID
	 * @return
	 */
	public SystemTradeType selectSystemTradeType(Integer tradeId);
	
	/**
	 * 新增交易信息
	 * @param tradeType
	 * @return
	 */
	public boolean insertSystemTradeType(SystemTradeType tradeType);
	
	/**
	 * 更新交易信息
	 * @param tradeType
	 * @return
	 */
	public boolean updateSystemTradeType(SystemTradeType tradeType);
	
	/**
	 * 删除交易信息
	 * @param tradeId
	 * @return
	 */
	public boolean deleteSystemTradeType(Integer tradeId);
	
	
	/**
	 * 查询交易信息数量
	 * @param 
	 * @return
	 */
	public Integer selectSystemTradeTypeCount(Map<String,Object> param);

}
