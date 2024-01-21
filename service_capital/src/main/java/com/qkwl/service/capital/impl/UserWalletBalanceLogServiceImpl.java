package com.qkwl.service.capital.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.rpc.capital.IUserWalletBalanceLogService;
import com.qkwl.service.capital.dao.UserWalletBalanceLogMapper;

@Service("userWalletBalanceLogService")
public class UserWalletBalanceLogServiceImpl implements IUserWalletBalanceLogService{

	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	
	@Override
	public PageInfo<UserWalletBalanceLog> selectList(Map<String, Object> param, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		List<UserWalletBalanceLog> list = userWalletBalanceLogMapper.selectList(param);
		if (list == null) {
			list = new ArrayList<>();
		}
		PageInfo<UserWalletBalanceLog> pageInfo = new PageInfo<UserWalletBalanceLog>(list);
		return pageInfo;
	}
}
