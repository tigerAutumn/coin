package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.matchGroup.MatchGroup;
import com.qkwl.common.rpc.admin.IAdminMatchGroupService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.MatchGroupMapper;

@Service("adminMatchGroupService")
public class AdminMatchGroupServiceImpl implements IAdminMatchGroupService {

	private static final Logger logger = LoggerFactory.getLogger(AdminMatchGroupServiceImpl.class);
	
	@Autowired
	private MatchGroupMapper matchGroupMapper;
	
	@Override
	public List<MatchGroup> selectAll() {
		return matchGroupMapper.selectAll();
	}
	
	@Override
	public List<MatchGroup> selectOthers(Integer id) {
		return matchGroupMapper.selectOthers(id);
	}
	
	@Override
	public Pagination<MatchGroup> selectMatchGroupPageList(Pagination<MatchGroup> pageParam,
			MatchGroup matchGroup) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总交易对组
		int count = matchGroupMapper.countMatchGroupListByParam(map);
		if(count > 0) {
			// 查询交易对组列表
			List<MatchGroup> matchGroupList = matchGroupMapper.getMatchGroupPageList(map);
			// 设置返回数据
			pageParam.setData(matchGroupList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public MatchGroup selectMatchGroupById(Integer id) {
		return matchGroupMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public MatchGroup selectMatchGroupByGroupName(String groupName) {
		return matchGroupMapper.selectByGroupName(groupName);
	}
	
	@Override
	public ReturnResult deleteMatchGroup(MatchGroup matchGroup) {
		try {
			//删除交易对组
			matchGroupMapper.deleteByPrimaryKey(matchGroup.getId());
			return ReturnResult.SUCCESS("删除交易对组成功，请重置redis后重启match");
		} catch (Exception e) {
			logger.error("删除交易对组异常，id："+matchGroup.getId(),e);
			return ReturnResult.FAILUER("删除交易对组失败！");
		}
	}
	
	@Override
	public ReturnResult insertMatchGroup(MatchGroup matchGroup) {
		try {
			//新增交易对组
			matchGroupMapper.insert(matchGroup);
			return ReturnResult.SUCCESS("新增交易对组成功，请重置redis后重启match");
		} catch (Exception e) {
			logger.error("新增交易对组异常，id："+matchGroup.getId(),e);
			return ReturnResult.FAILUER("新增交易对组失败！");
		}
	}
	
	@Override
	public ReturnResult updateMatchGroup(MatchGroup matchGroup) {
		try {
			//修改交易对组
			matchGroupMapper.updateByPrimaryKey(matchGroup);
			return ReturnResult.SUCCESS("修改交易对组成功，请重置redis后重启match");
		} catch (Exception e) {
			logger.error("删除交易对组异常，id："+matchGroup.getId(),e);
			return ReturnResult.FAILUER("修改交易对组失败！");
		}
	}

}
