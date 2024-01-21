package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcCurrency;
import com.qkwl.common.rpc.admin.IAdminOtcCurrencyService;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.service.admin.bc.dao.OtcCurrencyMapper;

@Service("adminOtcCurrencyService")
public class AdminOtcCurrencyServiceImpl implements IAdminOtcCurrencyService {

	private static final Logger logger = LoggerFactory.getLogger(AdminOtcCurrencyServiceImpl.class);
	
	@Autowired
	private OtcCurrencyMapper otcCurrencyMapper;
	
	@Override
	public Pagination<OtcCurrency> selectOtcCurrencyPageList(Pagination<OtcCurrency> pageParam,
			OtcCurrency otcCurrency) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		// 查询总返佣数
		int count = otcCurrencyMapper.countOtcCurrencyListByParam(map);
		if(count > 0) {
			// 查询返佣列表
			List<OtcCurrency> otcCurrencyList = otcCurrencyMapper.getOtcCurrencyPageList(map);
			// 设置返回数据
			pageParam.setData(otcCurrencyList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public OtcCurrency selectOtcCurrencyById(Integer id) {
		return otcCurrencyMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public ReturnResult deleteOtcCurrency(OtcCurrency OtcCurrency) {
		try {
			//删除法币
			otcCurrencyMapper.deleteByPrimaryKey(OtcCurrency.getId());
			return ReturnResult.SUCCESS("删除法币成功！");
		} catch (Exception e) {
			logger.error("删除法币异常，id："+OtcCurrency.getId(),e);
			return ReturnResult.FAILUER("删除法币失败！");
		}
	}
	
	@Override
	public ReturnResult insertOtcCurrency(OtcCurrency OtcCurrency) {
		try {
			//删除法币
			otcCurrencyMapper.insert(OtcCurrency);
			return ReturnResult.SUCCESS("新增法币成功！");
		} catch (Exception e) {
			logger.error("新增法币异常，id："+OtcCurrency.getId(),e);
			return ReturnResult.FAILUER("新增法币失败！");
		}
	}
	
	@Override
	public ReturnResult updateOtcCurrency(OtcCurrency OtcCurrency) {
		try {
			//删除法币
			otcCurrencyMapper.updateByPrimaryKey(OtcCurrency);
			return ReturnResult.SUCCESS("修改法币成功！");
		} catch (Exception e) {
			logger.error("删除法币异常，id："+OtcCurrency.getId(),e);
			return ReturnResult.FAILUER("修改法币失败！");
		}
	}
}
