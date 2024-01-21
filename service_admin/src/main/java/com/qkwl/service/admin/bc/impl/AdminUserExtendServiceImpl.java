package com.qkwl.service.admin.bc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.rpc.admin.IAdminUserExtendService;
import com.qkwl.service.admin.bc.dao.FUserExtendMapper;

@Service("adminUserExtendService")
public class AdminUserExtendServiceImpl implements IAdminUserExtendService {

	@Autowired
	private FUserExtendMapper userExtendMapper;
	
	@Override
	public FUserExtend selectByUid(Integer uid) {
		return userExtendMapper.selectByUid(uid);
	}

}
