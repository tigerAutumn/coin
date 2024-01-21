package com.qkwl.service.admin.bc.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.whiteList.UserWhiteList;
import com.qkwl.common.rpc.admin.IAdminWhiteListService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.UserWhiteListMapper;
import com.qkwl.service.admin.bc.utils.MQSend;
import com.qkwl.service.admin.bc.utils.WalletUtils;

@Service("adminWhiteListService")
public class AdminWhiteListServiceImpl implements IAdminWhiteListService {

	private static final Logger logger = LoggerFactory.getLogger(AdminWhiteListServiceImpl.class);
	
	
	@Autowired
	private SystemRedisInit systemRedisInit;
	
	@Autowired
	private MQSend mqSend;
	
	@Autowired
	private WalletUtils walletUtils;
	
	@Autowired
	private UserWhiteListMapper userWhiteListMapper;
	
	
	@Override
	public Pagination<UserWhiteList> selectUserWhiteList(Pagination<UserWhiteList> pageParam) {
		try {
			// 组装查询条件数据
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("offset", pageParam.getOffset());
			map.put("limit", pageParam.getPageSize());
			map.put("orderField", pageParam.getOrderField());
			map.put("orderDirection", pageParam.getOrderDirection());
			Map<String, Object> param = pageParam.getParam();
			if(param != null) {
				map.put("userId", param.get("userId"));
				map.put("type", param.get("type"));
			}
			Integer selectByParamCount = userWhiteListMapper.selectByParamCount(map);
			if(selectByParamCount == null || selectByParamCount == 0) {
				pageParam.setTotalRows(0);
				return pageParam;
			}
			pageParam.setTotalRows(selectByParamCount);
			List<UserWhiteList> selectByParam = userWhiteListMapper.selectByParam(map);
			pageParam.setData(selectByParam);
			return pageParam;
		} catch (Exception e) {
			logger.error("查询白名单异常",e);
			return null;
		}
	}


	@Override
	public ReturnResult insert(UserWhiteList userWhiteList) {
		try {
			Map<String,Object> param = new HashMap<>();
			param.put("coinId", userWhiteList.getCoinId());
			param.put("userId", userWhiteList.getUserId());
			param.put("type", userWhiteList.getType());
			Integer selectByParamCount = userWhiteListMapper.selectByParamCount(param);
			if(selectByParamCount != null && selectByParamCount > 0) {
				return ReturnResult.FAILUER("已存在该白名单");
			}
			int insert = userWhiteListMapper.insert(userWhiteList);
			if(insert > 0) {
				return ReturnResult.SUCCESS("添加成功");
			}
			return ReturnResult.FAILUER("添加失败");
		} catch (Exception e) {
			logger.error("添加白名单异常",e);
			return ReturnResult.FAILUER("系统异常");
		}
	}


	@Override
	public ReturnResult delete(Integer id) {
		try {
			int deleteByPrimaryKey = userWhiteListMapper.deleteByPrimaryKey(id);
			if(deleteByPrimaryKey > 0) {
				return ReturnResult.SUCCESS("删除成功");
			}else {
				return ReturnResult.FAILUER("删除失败");
			}
		} catch (Exception e) {
			logger.error("删除白名单异常",e);
			return ReturnResult.FAILUER("系统异常");
		}
	}
	
}
