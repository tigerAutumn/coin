package com.qkwl.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.rpc.user.IUserExtendService;
import com.qkwl.service.user.dao.FUserExtendMapper;

@Service("userExtendService")
public class UserExtendServiceImpl implements IUserExtendService {

	@Autowired
    private FUserExtendMapper userExtendMapper;
	
	@Override
	public FUserExtend selectByUid(Integer uid) {
		return userExtendMapper.selectByUid(uid);
	}

}
