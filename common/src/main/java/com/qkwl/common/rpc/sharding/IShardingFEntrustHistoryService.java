package com.qkwl.common.rpc.sharding;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrustHistory;

public interface IShardingFEntrustHistoryService {

	public Pagination<FEntrustHistory> selectFEntrustHistoryList(Pagination<FEntrustHistory> pageParam, FEntrustHistory filterParam);
	
	public List<FEntrustHistory> selectFEntrustHistoryListNoPage(Pagination<FEntrustHistory> pageParam, FEntrustHistory filterParam);
}
