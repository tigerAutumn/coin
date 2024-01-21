package com.qkwl.common.rpc.admin;

import com.qkwl.common.dto.user.FUserExtend;

public interface IAdminUserExtendService {

	/**
     * 根据uid查询用户扩展信息
     * @param uid
     * @return
     */
    FUserExtend selectByUid(Integer uid);
}
