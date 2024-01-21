package com.qkwl.service.admin.bc.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.rpc.admin.IAdminOtcAdvertService;
import com.qkwl.common.rpc.admin.IMultiLangService;
import com.qkwl.service.admin.bc.dao.OtcAdvertMapper;

@Service("adminOtcAdvertService")
public class AdminOtcAdvertServiceImpl implements IAdminOtcAdvertService {

	@Autowired
	private OtcAdvertMapper otcAdvertMapper;
	@Autowired
	private IMultiLangService multiLangService;
	
	@Override
	public Pagination<OtcAdvert> selectOtcAdvertPageList(Pagination<OtcAdvert> pageParam, OtcAdvert otcAdvert) {
		// 组装查询条件数据
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		
		map.put("id", otcAdvert.getId());
		map.put("coinName", otcAdvert.getCoinName());
		map.put("userId", otcAdvert.getUserId());
		map.put("side", otcAdvert.getSide());
		map.put("status", otcAdvert.getStatus());
		
		// 查询总广告数
		int count = otcAdvertMapper.countOtcAdvertListByParam(map);
		if(count > 0) {
			// 查询广告列表
			List<OtcAdvert> otcAdvertList = otcAdvertMapper.getOtcAdvertPageList(map);
			// 设置返回数据
			pageParam.setData(otcAdvertList);
		}
		pageParam.setTotalRows(count);
		return pageParam;
	}

	@Override
	public List<FUserBankinfoDTO> getBankinfoList(Integer id) {
	  List<FUserBankinfoDTO> bankinfoList = otcAdvertMapper.getBankinfoList(id);
	  
	  bankinfoList.forEach(e->{
	    e.setPaymentName(multiLangService.getMsg(e.getPaymentNameCode()));
	  });
	  
		return otcAdvertMapper.getBankinfoList(id);
	}
	
	@Override
	public OtcAdvert selectAdvertById(Integer id) {
		return otcAdvertMapper.selectAdvertById(id);
	}
	
	@Override
	public void updateAdvert(OtcAdvert advert) {
		otcAdvertMapper.updateAdvert(advert);
	}
}
