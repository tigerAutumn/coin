package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.CommissionRecord;
import com.qkwl.common.exceptions.BCException;

/**
 * 佣金记录
 */
public interface IAdminCommissionRecordService {


    Pagination<CommissionRecord> selectCommissionRecordPageList(Pagination<CommissionRecord> pageParam, CommissionRecord record);

    boolean update(Integer id,Integer status) throws BCException;


}
