package com.qkwl.common.rpc.user;

import com.qkwl.common.dto.user.FUserExtend;

public interface IUserExtendService {

	/**
     * 根据uid查询用户扩展信息
     * @param uid
     * @return
     */
    FUserExtend selectByUid(Integer uid);
}
