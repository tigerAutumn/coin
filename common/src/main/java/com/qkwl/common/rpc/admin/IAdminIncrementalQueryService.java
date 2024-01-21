package com.qkwl.common.rpc.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.qkwl.common.dto.capital.IncrementBean;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.statistic.UserDayIncrement;

public interface IAdminIncrementalQueryService {
	
	
	
	/**
	 * 查询增量数据
	 * @param uid 用户id
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 
	 */
	public Map<Integer, IncrementBean> selectUserIncrement(Integer uid);
	
	
}
