package com.qkwl.common.rpc.admin;

import java.math.BigDecimal;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.dto.statistic.UserPosition;
import com.qkwl.common.dto.statistic.UserTradePosition;

public interface IAdminStatisticService {

	/**
	 * 查询交易排行榜
	 * @param pageParam
	 * @return
	 */
	Pagination<EBankRank> selectEBankRankPageList(Pagination<EBankRank> pageParam, Integer tradeId);
	
	/**
	 * 查询汇总交易额
	 * @param pageParam
	 * @param tradeId
	 * @return
	 */
	BigDecimal selectEBankTotalAmount(String startTime, String endTime, Integer tradeId);
	
	/**
	 * 查询用户持仓统计
	 * @param pageParam
	 * @param userPosition
	 * @return
	 */
	Pagination<UserPosition> selectUserPositionPageList(Pagination<UserPosition> pageParam, UserPosition userPosition);
	
	/**
	 * 查询汇总持仓额
	 * @param userPosition
	 * @return
	 */
	BigDecimal selectTotalPosition(UserPosition userPosition);
	
	/**
	 * 查询用户持仓统计
	 * @param pageParam
	 * @param userPosition
	 * @return
	 */
	Pagination<UserTradePosition> selectUserTradePositionPageList(Pagination<UserTradePosition> pageParam, UserTradePosition userTradePosition);
	
}
