package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.coin.SystemTradeTypeRule;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.IAdminTradeTypeRulesService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.TradeTypeRulesMapper;

@Service("adminTradeTypeRulesService")
public class AdminTradeTypeRulesServiceImpl implements IAdminTradeTypeRulesService {

	private static final Logger logger = LoggerFactory.getLogger(AdminTradeTypeRulesServiceImpl.class);
	
	@Autowired
	TradeTypeRulesMapper tradeTypeRulesMapper;
	
	@Override
	public Pagination<SystemTradeTypeRule> selectTradeTypeRulesPageList(Pagination<SystemTradeTypeRule> pageParam,
			SystemTradeTypeRule tradeTypeRules) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		
		// 查询总返佣数
		int count = tradeTypeRulesMapper.countTradeTypeRulesListByParam(map);
		if(count > 0) {
			// 查询返佣列表
			List<SystemTradeTypeRule> tradeTypeRuleList = tradeTypeRulesMapper.getTradeTypeRulesPageList(map);
			// 设置返回数据
			pageParam.setData(tradeTypeRuleList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public SystemTradeTypeRule selectTradeTypeRulesById(Integer id) {
		return tradeTypeRulesMapper.selectByPrimaryKey(id);
	}

	@Override
	public ReturnResult insertTradeTypeRules(SystemTradeTypeRule tradeTypeRules) {
		try {
			//删除规则
			tradeTypeRulesMapper.insert(tradeTypeRules);
			return ReturnResult.SUCCESS("新增规则成功！");
		} catch (Exception e) {
			logger.error("新增规则异常，id："+tradeTypeRules.getId(),e);
			return ReturnResult.FAILUER("新增规则失败！");
		}
	}

	@Override
	public ReturnResult deleteTradeTypeRules(SystemTradeTypeRule tradeTypeRules) {
		try {
			//删除规则
			tradeTypeRulesMapper.deleteByPrimaryKey(tradeTypeRules.getId());
			return ReturnResult.SUCCESS("删除规则成功！");
		} catch (Exception e) {
			logger.error("删除规则异常，id："+tradeTypeRules.getId(),e);
			return ReturnResult.FAILUER("删除规则失败！");
		}
	}

	@Override
	public ReturnResult updateTradeTypeRules(SystemTradeTypeRule tradeTypeRules) {
		try {
			//删除规则
			tradeTypeRulesMapper.updateByPrimaryKey(tradeTypeRules);
			return ReturnResult.SUCCESS("修改规则成功！");
		} catch (Exception e) {
			logger.error("删除规则异常，id："+tradeTypeRules.getId(),e);
			return ReturnResult.FAILUER("修改规则失败！");
		}
	}

}
