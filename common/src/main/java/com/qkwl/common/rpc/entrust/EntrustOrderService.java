package com.qkwl.common.rpc.entrust;

import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;

public interface EntrustOrderService {

	 /**
     * 分页查询当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    委单实体
     * @param stateList  委单状态列表
     * @return 分页实体对象
     */
    Pagination<FEntrust> listEntrust(Pagination<FEntrust> paginParam, FEntrust entrust, List<Integer> stateList);

    /**
     * 分页查询历史当前委单
     *
     * @param paginParam 分页实体对象
     * @param entrust    历史委单实体
     * @param stateList  委单状态列表
     * @return 分页实体对象
     */
    Pagination<FEntrustHistory> listEntrustHistory(Pagination<FEntrustHistory> paginParam, FEntrustHistory entrust, List<Integer> stateList);

}
