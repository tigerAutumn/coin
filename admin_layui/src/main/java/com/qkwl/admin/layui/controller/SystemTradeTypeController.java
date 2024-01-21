package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminSystemCoinTypeService;
import com.qkwl.common.rpc.admin.IAdminSystemTradeTypeService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class SystemTradeTypeController extends WebBaseController {
	private static final Logger logger = LoggerFactory.getLogger(SystemTradeTypeController.class);
	
	@Autowired
	private IAdminSystemTradeTypeService adminSystemTradeTypeService;
	@Autowired
    private IAdminSystemCoinTypeService adminSystemCoinTypeService;

	@Autowired
	private RedisHelper redisHelper;

	/**
	 * 加载交易信息列表
	 */
	@RequestMapping("/admin/tradeTypeList")
	public ModelAndView tradeTypeList(
			@RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
			@RequestParam(value = "keywords",required=false) String keywords,
			@RequestParam(value = "tradeType",defaultValue="0",required=false) Integer tradeType
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("trade/tradeTypeList");
		
		Pagination<SystemTradeType> page=new Pagination<SystemTradeType>(currentPage,Constant.adminPageSize);
		page.setKeyword(keywords);
		modelAndView.addObject("tradeType",tradeType);
		if(tradeType == 0) {
			tradeType = null;
		}
		Map<Integer,String> systemTradeTypes = new HashMap<>();
		systemTradeTypes.put(0, "全部");
		for (SystemTradeTypeEnum systemTradeTypeEnum : SystemTradeTypeEnum.values()) {
			systemTradeTypes.put(systemTradeTypeEnum.getCode(), (String) systemTradeTypeEnum.getValue());
		}
		modelAndView.addObject("systemTradeType",systemTradeTypes);
		SystemTradeType systemTradeType = new SystemTradeType();
		systemTradeType.setAgentId(WebConstant.BCAgentId);
		
		page=adminSystemTradeTypeService.selectSystemTradeTypeList(page, systemTradeType,tradeType);
		
		if(keywords != null && !"".equals(keywords)){
			modelAndView.addObject("keywords", keywords);
		}

		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);

		Map<Integer, Object> statusMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeStatusEnum statusEnum : SystemTradeStatusEnum.values()){
			statusMap.put(statusEnum.getCode(), statusEnum.getValue());
		}
		modelAndView.addObject("statusMap", statusMap);
		
		modelAndView.addObject("page", page);
		return modelAndView;
	}
	
	/**
	 * 页面跳转
	 */
	@RequestMapping("admin/goTradeTypeJSP")
	public ModelAndView goVirtualCoinTypeJSP(
			@RequestParam(value = "url", required=false) String url,
			@RequestParam(value = "tradeId", defaultValue="0") Integer tradeId
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (tradeId > 0) {
			SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(tradeId);
			modelAndView.addObject("tradeType", tradeType);
		}

		Map<Integer, String> coinTypeMap = redisHelper.getCoinTypeNameSymbolMap();
		modelAndView.addObject("coinTypeMap", coinTypeMap);

		Map<Integer, Object> typeMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeTypeNewEnum typeEnum : SystemTradeTypeNewEnum.values()){
			if(typeEnum.getCode().equals(SystemTradeTypeNewEnum.FAVORITE.getCode())) {
				continue;
			}
			typeMap.put(typeEnum.getCode(), typeEnum.getValue());
		}
		modelAndView.addObject("typeMap", typeMap);

		Map<Integer, Object> statusMap = new LinkedHashMap<Integer, Object>();
		for(SystemTradeStatusEnum statusEnum : SystemTradeStatusEnum.values()){
			statusMap.put(statusEnum.getCode(), statusEnum.getValue());
		}
		modelAndView.addObject("statusMap", statusMap);

		return modelAndView;
	}
	
	/**
	 * 新增交易信息
	 */
	@RequestMapping("admin/insertTradeType")
	@ResponseBody
	public ReturnResult insertTradeType(
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "sortId", required = false) Integer sortId,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "buyCoinId", required = false) Integer buyCoinId,
			@RequestParam(value = "sellCoinId", required = false) Integer sellCoinId,
			@RequestParam(value = "isShare", required = false) String isShare,
			@RequestParam(value = "isStop", required = false) String isStop,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "stopTime", required = false) String stopTime,
			@RequestParam(value = "buyFee", required = false) BigDecimal buyFee,
			@RequestParam(value = "sellFee", required = false) BigDecimal sellFee,
			@RequestParam(value = "remoteId", required = false) Integer remoteId,
			@RequestParam(value = "priceWave", required = false) BigDecimal priceWave,
			@RequestParam(value = "priceRange", required = false) BigDecimal priceRange,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "maxCount", required = false) BigDecimal maxCount,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
			@RequestParam(value = "amountOffset", required = false) String amountOffset,
			@RequestParam(value = "priceOffset", required = false) String priceOffset,
			@RequestParam(value = "digit", required = false) String digit,
			@RequestParam(value = "openPrice", required = false) BigDecimal openPrice,
			@RequestParam(value = "isOpenIoe", required = false) String isOpenIoe,
			@RequestParam(value = "ioeAmountLimit", required = false) BigDecimal ioeAmountLimit,
			@RequestParam(value = "ioeWhitelist", required = false) String ioeWhitelist,
			@RequestParam(value = "listedDateTime", required = false) String listedDateTime
			) {
		try {
		//20190104,创新区，判断交易对是否重复
		SystemCoinType coinType = adminSystemCoinTypeService.selectVirtualCoinById(sellCoinId);
		if(coinType == null) {
			return ReturnResult.FAILUER("卖方币种不存在");
		}
		if(BigDecimal.ZERO.compareTo(openPrice) >= 0) {
			return ReturnResult.FAILUER("开盘价不得小于0");
		}
		if(SystemTradeStatusEnum.NORMAL.getCode().equals(status)) {
			Map<String, Object> param = new HashMap<>();
			param.put("buyCoinId", buyCoinId);
			param.put("sellCoinId", sellCoinId);
			param.put("status", status);
			Integer selectSystemTradeTypeCount = adminSystemTradeTypeService.selectSystemTradeTypeCount(param);
			if(selectSystemTradeTypeCount != null && selectSystemTradeTypeCount > 0) {
				return ReturnResult.FAILUER("已存在相同交易对，请先禁用该交易对再新增，或新增禁用的交易对");
			}
			if(SystemTradeTypeEnum.INNOVATION_AREA.getCode().equals(type)) {
				if(!coinType.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("非上线创新区的币不允许上线创新区，请到币种管理更改币种设置");
				}
			}else {
				if(coinType.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("创新区币种不允许上其他交易区，请到币种管理更改币种设置");
				}
			}
		}
		
		SystemTradeType tradeType = new SystemTradeType();
		tradeType.setType(type);
		tradeType.setSortId(sortId);
		tradeType.setStatus(status);
		tradeType.setBuyCoinId(buyCoinId);
		tradeType.setSellCoinId(sellCoinId);
		if (isShare != null && !isShare.isEmpty()) {
			tradeType.setIsShare(true);
		} else {
			tradeType.setIsShare(false);
		}
		if (isStop != null && !isStop.isEmpty()) {
			tradeType.setIsStop(true);
		} else {
			tradeType.setIsStop(false);
		}
		if (openTime != null && !openTime.isEmpty()  && !openTime.equals("00:00:00")) {
			tradeType.setOpenTime(Time.valueOf(openTime));
		} else {
			tradeType.setOpenTime(null);
		}
		if (stopTime != null && !stopTime.isEmpty()  && !stopTime.equals("00:00:00")) {
			tradeType.setStopTime(Time.valueOf(stopTime));
		} else {
			tradeType.setStopTime(null);
		}
		//20190402 交易对添加上市时间
		if(listedDateTime.isEmpty()) {
			return ReturnResult.FAILUER("新增失败！上市时间为空");
		}
		Date parseDate = DateUtils.parseDate(listedDateTime, new String[] {"yyyy-MM-dd HH:mm"});
		if(parseDate.getTime() < new Date().getTime()) {
		 return ReturnResult.FAILUER("新增失败！上市时间不得小于当前时间");
		}
		tradeType.setListedDateTime(parseDate);
		tradeType.setBuyFee(buyFee);
		tradeType.setSellFee(sellFee);
		if(remoteId != null){
			tradeType.setRemoteId(remoteId);
		}
		tradeType.setPriceWave(priceWave);
		tradeType.setPriceRange(priceRange);
		tradeType.setMinCount(minCount);
		tradeType.setMaxCount(maxCount);
		tradeType.setMinPrice(minPrice);
		tradeType.setMaxPrice(maxPrice);
		tradeType.setAmountOffset(amountOffset);
		tradeType.setPriceOffset(priceOffset);
		tradeType.setDigit(digit);
		tradeType.setOpenPrice(openPrice);
		tradeType.setGmtCreate(new Date());
		tradeType.setGmtModified(new Date());
		tradeType.setVersion(0);
		tradeType.setAgentId(WebConstant.BCAgentId);
		//2019-03-12,ioe功能 
		if (isOpenIoe != null && !isOpenIoe.isEmpty()) {
			tradeType.setIsOpenIoe(true);
		} else {
			tradeType.setIsOpenIoe(false);
		}
		tradeType.setIoeAmountLimit(ioeAmountLimit);
		tradeType.setIoeWhitelist(ioeWhitelist);
		
		if(adminSystemTradeTypeService.insertSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("新增成功！");
		}
		} catch (Exception e) {
			logger.error("新增交易对异常",e);
		}
		return ReturnResult.FAILUER("新增失败！");
	}
	
	/**
	 * 修改交易信息
	 */
	@RequestMapping("admin/updateTradeType")
	@ResponseBody
	public ReturnResult updateTradeType(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "type", required = false) Integer type,
			@RequestParam(value = "sortId", required = false) Integer sortId,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "buyCoinId", required = false) Integer buyCoinId,
			@RequestParam(value = "sellCoinId", required = false) Integer sellCoinId,
			@RequestParam(value = "isShare", required = false) String isShare,
			@RequestParam(value = "isStop", required = false) String isStop,
			@RequestParam(value = "openTime", required = false) String openTime,
			@RequestParam(value = "stopTime", required = false) String stopTime,
			@RequestParam(value = "buyFee", required = false) BigDecimal buyFee,
			@RequestParam(value = "sellFee", required = false) BigDecimal sellFee,
			@RequestParam(value = "remoteId", required = false) Integer remoteId,
			@RequestParam(value = "priceWave", required = false) BigDecimal priceWave,
			@RequestParam(value = "priceRange", required = false) BigDecimal priceRange,
			@RequestParam(value = "minCount", required = false) BigDecimal minCount,
			@RequestParam(value = "maxCount", required = false) BigDecimal maxCount,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
			@RequestParam(value = "amountOffset", required = false) String amountOffset,
			@RequestParam(value = "priceOffset", required = false) String priceOffset,
			@RequestParam(value = "digit", required = false) String digit,
			@RequestParam(value = "openPrice", required = false) BigDecimal openPrice,
			@RequestParam(value = "isOpenIoe", required = false) String isOpenIoe,
			@RequestParam(value = "ioeAmountLimit", required = false) BigDecimal ioeAmountLimit,
			@RequestParam(value = "ioeWhitelist", required = false) String ioeWhitelist,
			@RequestParam(value = "listedDateTime", required = false) String listedDateTime) {
		try {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(id);
		if(tradeType == null){
			return ReturnResult.FAILUER("交易信息错误！");
		}
		if(BigDecimal.ZERO.compareTo(openPrice) >= 0) {
			return ReturnResult.FAILUER("开盘价不得小于0");
		}
		//20190104,创新区，判断交易对是否重复
		if(SystemTradeStatusEnum.NORMAL.getCode().equals(status)) {
			Map<String, Object> param = new HashMap<>();
			param.put("buyCoinId", buyCoinId);
			param.put("sellCoinId", sellCoinId);
			param.put("status", status);
			param.put("notInId", id);
			Integer selectSystemTradeTypeCount = adminSystemTradeTypeService.selectSystemTradeTypeCount(param);
			if(selectSystemTradeTypeCount != null && selectSystemTradeTypeCount > 0) {
				return ReturnResult.FAILUER("已存在相同交易对");
			}
			
			SystemCoinType coinType = adminSystemCoinTypeService.selectVirtualCoinById(sellCoinId);
			if(coinType == null) {
				return ReturnResult.FAILUER("卖方币种不存");
			}
			if(SystemTradeTypeEnum.INNOVATION_AREA.getCode().equals(type)) {
				if(!coinType.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("非上线创新区的币不允许上线创新区，请到币种管理更改币种设置");
				}
			}else {
				if(coinType.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("创新区币种不允许上其他交易区，请到币种管理更改币种设置");
				}
			}
		}
		
		
		tradeType.setType(type);
		tradeType.setSortId(sortId);
		tradeType.setStatus(status);
		tradeType.setBuyCoinId(buyCoinId);
		tradeType.setSellCoinId(sellCoinId);
		if (isShare != null && !isShare.isEmpty()) {
			tradeType.setIsShare(true);
		} else {
			tradeType.setIsShare(false);
		}
		if (isStop != null && !isStop.isEmpty()) {
			tradeType.setIsStop(true);
		} else {
			tradeType.setIsStop(false);
		}
		if (openTime != null && !openTime.isEmpty()  && !openTime.equals("00:00:00")) {
			tradeType.setOpenTime(Time.valueOf(openTime));
		} else {
			tradeType.setOpenTime(null);
		}
		if (stopTime != null && !stopTime.isEmpty()  && !stopTime.equals("00:00:00")) {
			tradeType.setStopTime(Time.valueOf(stopTime));
		} else {
			tradeType.setStopTime(null);
		}
		//20190402 交易对添加上市时间
		if(listedDateTime.isEmpty()) {
			return ReturnResult.FAILUER("修改失败！上市时间为空");
		}
		Date parseDate = DateUtils.parseDate(listedDateTime, new String[] {"yyyy-MM-dd HH:mm"});
		tradeType.setListedDateTime(parseDate);
		tradeType.setBuyFee(buyFee);
		tradeType.setSellFee(sellFee);
		if(remoteId != null){
			tradeType.setRemoteId(remoteId);
		}
		tradeType.setPriceWave(priceWave);
		tradeType.setPriceRange(priceRange);
		tradeType.setMinCount(minCount);
		tradeType.setMaxCount(maxCount);
		tradeType.setMinPrice(minPrice);
		tradeType.setMaxPrice(maxPrice);
		tradeType.setAmountOffset(amountOffset);
		tradeType.setPriceOffset(priceOffset);
		tradeType.setDigit(digit);
		tradeType.setOpenPrice(openPrice);
		tradeType.setGmtModified(new Date());
		//2019-03-12,ioe功能 
		if (isOpenIoe != null && !isOpenIoe.isEmpty()) {
			tradeType.setIsOpenIoe(true);
		} else {
			tradeType.setIsOpenIoe(false);
		}
		tradeType.setIoeAmountLimit(ioeAmountLimit);
		tradeType.setIoeWhitelist(ioeWhitelist);
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("修改成功！");
		}
		} catch (Exception e) {
			logger.error("修改交易对异常",e);
		}
		return ReturnResult.FAILUER("修改失败！");
	}
		
	/**
	 * 状态操作
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/updateTradeStatus")
	@ResponseBody
	public ReturnResult updateTradeStatus(
			@RequestParam(required = false) int tradeId, @RequestParam(required = false) int status) throws Exception {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(tradeId);
		if(tradeType == null){
			return ReturnResult.FAILUER("ID错误，操作失败！");
		}
		// 禁用
		if(status == 1){
			tradeType.setStatus(SystemCoinStatusEnum.ABNORMAL.getCode());
		}
		// 停盘
		if(status == 2){
			tradeType.setIsStop(true);
		}
		// 开盘
		if(status == 3){
			tradeType.setIsStop(false);
		}
		tradeType.setGmtModified(new Date());
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("操作成功！");
		}else{
			return ReturnResult.FAILUER("操作失败！");
		}
	}
	
	/**
	 * 启用交易
	 */
	@RequestMapping("admin/updateTradeStop")
	@ResponseBody
	public ReturnResult updateTradeStop(@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "status", required = false) Integer status) {
		SystemTradeType tradeType = adminSystemTradeTypeService.selectSystemTradeType(id);
		if(tradeType == null){
			return ReturnResult.FAILUER("交易信息错误！");
		}
		
		//20190104,创新区，判断交易对是否重复
		if(SystemTradeStatusEnum.NORMAL.getCode().equals(status)) {
			Map<String, Object> param = new HashMap<>();
			param.put("buyCoinId", tradeType.getBuyCoinId());
			param.put("sellCoinId", tradeType.getSellCoinId());
			param.put("status", status);
			param.put("notInId", id);
			Integer selectSystemTradeTypeCount = adminSystemTradeTypeService.selectSystemTradeTypeCount(param);
			if(selectSystemTradeTypeCount != null && selectSystemTradeTypeCount > 0) {
				return ReturnResult.FAILUER("已存在相同交易对");
			}
			//如果是启用创新区交易对
			SystemCoinType selectVirtualCoinById = adminSystemCoinTypeService.selectVirtualCoinById(tradeType.getSellCoinId());
			if(SystemTradeTypeEnum.INNOVATION_AREA.getCode() == tradeType.getType()) {
				if(!selectVirtualCoinById.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("启用失败，请到币种列表将卖方币种改为创新区币种");
				}
			}else {
				if(selectVirtualCoinById.getIsInnovateAreaCoin()) {
					return ReturnResult.FAILUER("启用失败，请到币种列表将卖方币种改为非创新区币种");
				}
			}
			
		}
		
		tradeType.setStatus(status);
		tradeType.setGmtModified(new Date());
		if(adminSystemTradeTypeService.updateSystemTradeType(tradeType)){
			return ReturnResult.SUCCESS("启用成功！");
		}
		return ReturnResult.FAILUER("启用失败!");
	}
}
