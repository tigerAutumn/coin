package com.qkwl.common.rpc.capital;

import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;

public interface IUserWalletBalanceLogService {
	PageInfo<UserWalletBalanceLog> selectList(Map<String, Object> param,int pageNo,int pageSize);
}
