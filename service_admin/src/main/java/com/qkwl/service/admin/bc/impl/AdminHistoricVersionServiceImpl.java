package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.system.HistoricVersion;
import com.qkwl.common.rpc.admin.IAdminHistoricVersionService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.HistoricVersionMapper;

@Service("adminHistoricVersionService")
public class AdminHistoricVersionServiceImpl implements IAdminHistoricVersionService{
	
	private static final Logger logger = LoggerFactory.getLogger(AdminHistoricVersionServiceImpl.class);
	
	@Autowired
	private HistoricVersionMapper historicVersionMapper;

	@Override
	public Pagination<HistoricVersion> selectHistoricVersionPageList(Pagination<HistoricVersion> pageParam,
			HistoricVersion historicVersion) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总返佣数
		int count = historicVersionMapper.countHistoricVersionList();
		if(count > 0) {
			// 查询返佣列表
			List<HistoricVersion> HistoricVersionList = historicVersionMapper.getHistoricVersionPageList(map);
			// 设置返回数据
			pageParam.setData(HistoricVersionList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public HistoricVersion selectHistoricVersionById(Integer id) {
		return historicVersionMapper.selectByPrimaryKey(id);
	}

	@Override
	public ReturnResult deleteHistoricVersion(HistoricVersion historicVersion) {
		try {
			//删除法币
			historicVersionMapper.deleteByPrimaryKey(historicVersion.getId());
			return ReturnResult.SUCCESS("删除历史版本成功！");
		} catch (Exception e) {
			logger.error("删除历史版本异常，id："+historicVersion.getId(),e);
			return ReturnResult.FAILUER("删除历史版本失败！");
		}
	}

	@Override
	public ReturnResult insertHistoricVersion(HistoricVersion historicVersion) {
		try {
			//查询历史版本数量
			int count = historicVersionMapper.countHistoricVersionList();
			if (count == 5) {
				return ReturnResult.FAILUER("历史版本最多5个！");
			}
			//新增历史版本
			historicVersionMapper.insert(historicVersion);
			return ReturnResult.SUCCESS("新增历史版本成功！");
		} catch (Exception e) {
			logger.error("新增历史版本异常，id："+historicVersion.getId(),e);
			return ReturnResult.FAILUER("新增历史版本失败！");
		}
	}

	@Override
	public ReturnResult updateHistoricVersion(HistoricVersion historicVersion) {
		try {
			//修改历史版本
			historicVersionMapper.updateByPrimaryKey(historicVersion);
			return ReturnResult.SUCCESS("修改历史版本成功！");
		} catch (Exception e) {
			logger.error("修改历史版本异常，id："+historicVersion.getId(),e);
			return ReturnResult.FAILUER("修改历史版本失败！");
		}
	}

}
