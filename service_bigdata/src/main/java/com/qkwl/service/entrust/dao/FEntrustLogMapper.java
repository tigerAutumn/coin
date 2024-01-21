package com.qkwl.service.entrust.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.OrderMatchResultsReq;
import com.qkwl.common.dto.entrust.EntrustLogApi;
import com.qkwl.common.dto.entrust.FEntrustLog;

@Mapper
public interface FEntrustLogMapper {
	/**
	 * 根据订单id查询成交明细
	 * @param matchId
	 * @return
	 */
	List<FEntrustLog> selectByEntrustId(BigInteger entrustId);

	/**
	 * 根据对手单id查询成交明细
	 * @param matchId
	 * @return
	 */
	List<FEntrustLog> selectByMatchId(BigInteger matchId);

	/**
	 * @param map
	 * @return
	 */
	List<FEntrustLog> orderMatchresults(Map<String, Object> map);
}
