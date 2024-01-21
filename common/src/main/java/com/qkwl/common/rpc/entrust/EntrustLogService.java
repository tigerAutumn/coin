package com.qkwl.common.rpc.entrust;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.qkwl.common.dto.OrderMatchResultsReq;
import com.qkwl.common.dto.OrderMatchResultsResp;
import com.qkwl.common.dto.entrust.FEntrustLog;

public interface EntrustLogService {
	
	/**
	 * 根据订单id查询成交明细
	 * @param enrustId
	 * @return
	 */
	List<FEntrustLog> selectByEntrustId(BigInteger entrustId);
	
	/**
	 * 根据参数查询历史成交
	 * @param param
	 * @return
	 */
	List<FEntrustLog> selectByParam(Map<String, Object> param);
	
	/**
	 * 根据对手单id查询成交明细
	 * @param matchId
	 * @return
	 */
	List<FEntrustLog> selectByMatchId(BigInteger matchId);

	/**
	 * @param req
	 * @return
	 */
	List<OrderMatchResultsResp> orderMatchresults(OrderMatchResultsReq req);
}
