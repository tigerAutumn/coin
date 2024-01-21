package com.qkwl.service.entrust.impl;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.OrderMatchResultsReq;
import com.qkwl.common.dto.OrderMatchResultsResp;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.entrust.FEntrustLog;
import com.qkwl.common.entity.ApiKeyEntity;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.rpc.entrust.EntrustLogService;
import com.qkwl.common.rpc.user.IApiKeyService;
import com.qkwl.common.util.DateUtil;
import com.qkwl.service.entrust.config.MyCacheManager;
import com.qkwl.service.entrust.dao.FEntrustLogMapper;

@Service("entrustLogService")
public class EntrustLogServiceImpl implements EntrustLogService{
	
	@Autowired
	private FEntrustLogMapper entrustLogMapper;
	@Autowired
	private MyCacheManager myCacheManager;
	@Autowired
	private IApiKeyService apiKeyService;
	
	private static final Logger logger = LoggerFactory.getLogger(EntrustLogServiceImpl.class);

	@Override
	public List<FEntrustLog> selectByEntrustId(BigInteger entrustId) {
		return entrustLogMapper.selectByEntrustId(entrustId);
	}

	@Override
	public List<FEntrustLog> selectByParam(Map<String, Object> param) {
		logger.info("==============selectByParam方法未实现");
		return null;
	}
	
	

	@Override
	public List<FEntrustLog> selectByMatchId(BigInteger matchId) {
		return entrustLogMapper.selectByMatchId(matchId);
	}

	@Override
	public List<OrderMatchResultsResp> orderMatchresults(OrderMatchResultsReq req) {
		
		Map<String, Object> map=new HashMap<>();
		
		Integer tradeId = myCacheManager.getTradeId(req.getSymbol());
		if(tradeId==null) {
			throw new BizException(ErrorCodeEnum.SYMBOL_ERROR);
		}
		
		 ApiKeyEntity apiKeyEntity = apiKeyService.selectByAccessKey(req.getAccessKeyId());
		 
		 Date fcreatetimeEnd=null;
		 Date fcreatetimeStart=null;
		 try {
			if(StringUtils.isBlank(req.getStartDate())&&StringUtils.isBlank(req.getEndDate())) {
				 fcreatetimeEnd=Date.from(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				 fcreatetimeStart=DateUtils.addDays(fcreatetimeEnd, -2);
			 }else if(StringUtils.isBlank(req.getStartDate())&&StringUtils.isNotBlank(req.getEndDate())) {
				 fcreatetimeEnd=DateUtils.addDays(DateUtils.parseDate(req.getEndDate(), "yyyy-MM-dd"),1);
				 fcreatetimeStart=DateUtils.addDays(fcreatetimeEnd, -2);
			 }else if(StringUtils.isNotBlank(req.getStartDate())&&StringUtils.isBlank(req.getEndDate())) {
				 fcreatetimeStart=DateUtils.parseDate(req.getStartDate(), "yyyy-MM-dd");
				 fcreatetimeEnd=DateUtils.addDays(fcreatetimeStart, 2);
			 }else {
				 fcreatetimeStart=DateUtils.parseDate(req.getStartDate(), "yyyy-MM-dd");
				 fcreatetimeEnd=DateUtils.addDays(DateUtils.parseDate(req.getEndDate(), "yyyy-MM-dd"),1);
				 if(DateUtils.addDays(fcreatetimeStart, 2).getTime()<fcreatetimeEnd.getTime()) {
					 throw new BizException(ErrorCodeEnum.DATE_INTERVAL_GREATER_THAN_2);
				 }
			 }
		} catch (ParseException e) {
			throw new BizException(ErrorCodeEnum.DATE_FORMAT_ERROR);
		}
		 
		
		map.put("ftradeId", tradeId);
		map.put("fuid", apiKeyEntity.getUid());
		map.put("fentrusttype", Integer.valueOf(req.getTypes()));
		map.put("fcreatetimeStart", fcreatetimeStart);
		map.put("fcreatetimeEnd", fcreatetimeEnd);
		map.put("offSet",req.getFrom()==null?null:Integer.valueOf(req.getFrom()));
		map.put("direct",req.getDirect());
		map.put("size", Integer.valueOf(req.getSize()));
		logger.info("获取当前记录和成交记录参数：{}",JSON.toJSONString(map));
		List<FEntrustLog> list= entrustLogMapper.orderMatchresults(map);
		
		if(list==null) {
			return Collections.emptyList();
		}
		return list.parallelStream().map(e->fentrustLog2OrderMatchResultsResp(e)).collect(Collectors.toList());
	}
	
	
	private OrderMatchResultsResp fentrustLog2OrderMatchResultsResp(FEntrustLog entrustLog) {
		OrderMatchResultsResp resp=new OrderMatchResultsResp();
		resp.setCreatedAt(entrustLog.getFcreatetime().getTime());
		resp.setFilledAmount(String.valueOf(entrustLog.getFcount()));
		resp.setFilledFees(String.valueOf(entrustLog.getFfee()));
		resp.setId(entrustLog.getFid().longValue());
		resp.setMatchId(entrustLog.getFmatchid().longValue());
		resp.setOrderId(entrustLog.getFentrustid().longValue());
		resp.setPrice(String.valueOf(entrustLog.getFprize()));
		resp.setRole(entrustLog.getFisactive()?"taker":"maker");
		resp.setType(String.valueOf(entrustLog.getFentrusttype()));
		return resp;
	}

	
}
