package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.admin.IAdminSystemTradeTypeService;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.SystemTradeTypeMapper;

@Service("adminSystemTradeTypeService")
public class AdminSystemTradeTypeServiceImpl implements IAdminSystemTradeTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminSystemTradeTypeServiceImpl.class);

	@Autowired
	private SystemTradeTypeMapper systemTradeTypeMapper;
	@Autowired
	private SystemRedisInit systemRedisInit;
	@Autowired
	private RedisHelper redisHelper;

	@Override
	public Pagination<SystemTradeType> selectSystemTradeTypeList(Pagination<SystemTradeType> page,
			SystemTradeType tradeType,Integer areaType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", page.getOffset());
		map.put("limit", page.getPageSize());
		map.put("keyword", page.getKeyword());
		map.put("agentid", tradeType.getAgentId());
		map.put("type",areaType);
		int count = systemTradeTypeMapper.selectSystemTradeTypeCount(map);
		if(count > 0) {
			List<SystemTradeType> articleList = systemTradeTypeMapper.selectSystemTradeTypeList(map);
			page.setData(articleList);
		}
		page.setTotalRows(count);
		return page;
	}

	@Override
	public SystemTradeType selectSystemTradeType(Integer tradeId) {
		return systemTradeTypeMapper.selectByPrimaryKey(tradeId);
	}

	@Override
	public boolean insertSystemTradeType(SystemTradeType tradeType) {
		
		//通过redis自增,如果redis没数据则先去数据库拿id最大值
		Long incrByKey = redisHelper.getIncrByKey(RedisConstant.SEQ_SYSTEM_TRADE_TYPE);
		if(incrByKey==1) {
			//从数据库查询最大id，并设置redis
			incrByKey=systemTradeTypeMapper.selectMaxId()+1;
			//设置redis
			redisHelper.setNoExpire(RedisConstant.SEQ_SYSTEM_TRADE_TYPE, String.valueOf(incrByKey));
		}
		tradeType.setId(incrByKey.intValue());
		
		
		if (systemTradeTypeMapper.insert(tradeType) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSystemTradeType(SystemTradeType tradeType) {
		if (systemTradeTypeMapper.updateByPrimaryKey(tradeType) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteSystemTradeType(Integer tradeId) {
		if (systemTradeTypeMapper.deleteByPrimaryKey(tradeId) > 0) {
			systemRedisInit.initSystemTradeType();
			return true;
		}
		return false;
	}

	@Override
	public Integer selectSystemTradeTypeCount(Map<String,Object> param) {
		try {
			int selectSystemTradeTypeCount = systemTradeTypeMapper.selectCountByParam(param);
			return selectSystemTradeTypeCount;
		} catch (Exception e) {
			logger.error("查询交易对数量异常",e);
		}
		return null;
	}

}
