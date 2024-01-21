package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qkwl.admin.layui.utils.PageData;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.admin.layui.base.WebBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.util.Constant;
import com.qkwl.common.dto.Enum.EntrustStateEnum;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.MatchTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.rpc.admin.IAdminEntrustServer;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.sharding.IShardingFEntrustHistoryService;
import com.qkwl.common.framework.redis.RedisHelper;

@Controller
public class EntrustController extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(EntrustController.class);

	@Autowired
	private IAdminEntrustServer adminEntrustServer;
	
	@Autowired
	private IShardingFEntrustHistoryService shardingFEntrustHistoryService;
	
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;

	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;

	/**
	 * 委单当前记录
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("entrust/entrustList")
	public ModelAndView entrustList(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "tradeId", defaultValue = "0") Integer tradeId,
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
			@RequestParam(value = "status", defaultValue = "0") Integer fstatus,
			@RequestParam(value = "entype", defaultValue = "-1") Integer fentype,
			@RequestParam(value = "matchType", defaultValue = "-1") Integer matchType,
			@RequestParam(value = "price", required = false) BigDecimal fprice) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("entrust/entrustList");
		// 查询条件定义
		Pagination<FEntrust> pageParam = new Pagination<FEntrust>(currentPage, numPerPage);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		FEntrust filterParam = new FEntrust();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 委单虚拟币
		if (tradeId > 0) {
			filterParam.setFtradeid(tradeId);
		}
		modelAndView.addObject("tradeId", tradeId);
		// 委单状态
		if (fstatus > 0) {
			filterParam.setFstatus(fstatus);
		}
		modelAndView.addObject("status", fstatus);
		// 委单类型
		if (fentype > -1) {
			filterParam.setFtype(fentype);
		}
		modelAndView.addObject("entype", fentype);
		
		if (matchType >-1) {
			filterParam.setFmatchtype(matchType);
		}
		modelAndView.addObject("matchType", matchType);
		
		// 订单价格
		if (fprice != null) {
			filterParam.setFprize(fprice);
			modelAndView.addObject("price", fprice);
		}

		// 页面参数-交易类型
		List<SystemTradeType> tradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		Map<Integer, String> tradeMap = new HashMap<Integer, String>();
		tradeMap.put(0, "全部");
		for (SystemTradeType trade : tradeType) {
			tradeMap.put(trade.getId(), trade.getSellName() + " " + trade.getSellShortName() + " / " + trade.getBuyShortName());
		}
		modelAndView.addObject("tradeMap", tradeMap);
		Map<Integer, String> typeShortNameMap = redisHelper.getCoinTypeShortNameMap();
		modelAndView.addObject("typeShortNameMap", typeShortNameMap);

		// 页面参数-虚拟币类型
		Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
		typeMap.put(0, "全部");
		modelAndView.addObject("typeMap", typeMap);

		// 页面参数-委单状态
		Map<Integer, String> statusMap = new HashMap<Integer, String>();
		statusMap.put(EntrustStateEnum.Going.getCode(),
				EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.Going.getCode()));
		statusMap.put(EntrustStateEnum.PartDeal.getCode(),
				EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.PartDeal.getCode()));
		statusMap.put(EntrustStateEnum.WAITCancel.getCode(),
				EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.WAITCancel.getCode()));
		statusMap.put(0, "全部");
		modelAndView.addObject("statusMap", statusMap);
		// 页面参数-委单类型
		Map<Integer, String> entypeMap = new HashMap<Integer, String>();
		entypeMap.put(-1, "全部");
		entypeMap.put(EntrustTypeEnum.BUY.getCode(),
				EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.BUY.getCode()));
		entypeMap.put(EntrustTypeEnum.SELL.getCode(),
				EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.SELL.getCode()));
		modelAndView.addObject("entypeMap", entypeMap);
		
		
		MatchTypeEnum[] matchTypeEnums = MatchTypeEnum.values();
		Map<Integer, String> matchTypeMap=new LinkedHashMap<Integer, String>(matchTypeEnums.length);
		matchTypeMap.put(-1, "全部");
		for(MatchTypeEnum m:matchTypeEnums) {
			matchTypeMap.put(m.getCode(), m.getValue());
		}
		modelAndView.addObject("matchTypeMap", matchTypeMap);
		
		
		// 查询
		Pagination<FEntrust> pagination = adminEntrustServer.selectFEntrustList(pageParam, filterParam);
		modelAndView.addObject("entrustList", pagination);
		return modelAndView;
	}

	/**
	 * 委单历史记录
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("entrust/entrustHistoryList")
	public ModelAndView entrustHistoryList(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "tradeId", defaultValue = "0") Integer tradeId,
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
			@RequestParam(value = "status", defaultValue = "0") Integer fstatus,
			@RequestParam(value = "entype", defaultValue = "-1") Integer fentype,
			@RequestParam(value = "price", required = false) BigDecimal fprice,
			@RequestParam(value = "beginDate", required = false) String beginDate,
			@RequestParam(value = "matchType", defaultValue = "-1") Integer matchType,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "filterApi", required = false) String filterApi) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("entrust/entrustHistoryList");
		// 查询条件定义
		Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, numPerPage);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		Map<String, Object> param = new HashMap<String, Object>();

		FEntrustHistory filterParam = new FEntrustHistory();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 委单虚拟币
		if (tradeId > 0) {
			filterParam.setFtradeid(tradeId);
		}
		modelAndView.addObject("tradeId", tradeId);
		// 委单状态
		if (fstatus > 0) {
			filterParam.setFstatus(fstatus);
		}
		modelAndView.addObject("status", fstatus);
		// 委单类型
		if (fentype > -1) {
			filterParam.setFtype(fentype);
		}
		modelAndView.addObject("entype", fentype);
		
		
		
		if (matchType > -1) {
			filterParam.setFmatchtype(matchType);
		}
		modelAndView.addObject("matchType", matchType);
		// 订单价格
		if (fprice != null) {
			filterParam.setFprize(fprice);
			modelAndView.addObject("price", fprice);
		}
		// 过滤api
		if (filterApi != null && filterApi.equals("on")) {
			param.put("filterApi", true);
			modelAndView.addObject("filterApi", true);
		} else {
			param.put("filterApi", false);
			modelAndView.addObject("filterApi", false);
		}

		if (StringUtils.isNotEmpty(beginDate)) {
			pageParam.setBegindate(beginDate);
			modelAndView.addObject("beginDate", beginDate);
		}

		if (StringUtils.isNotEmpty(endDate)) {
			pageParam.setEnddate(endDate);
			modelAndView.addObject("endDate", endDate);
		}

		// 页面参数-交易类型
		List<SystemTradeType> tradeType = redisHelper.getTradeTypeList(WebConstant.BCAgentId);
		Map<Integer, String> tradeMap = new HashMap<Integer, String>();
		tradeMap.put(0, "全部");
		for (SystemTradeType trade : tradeType) {
			tradeMap.put(trade.getId(), trade.getSellName() +" "+ trade.getSellShortName() + " / " + trade.getBuyShortName());
		}
		modelAndView.addObject("tradeMap", tradeMap);

		// 页面参数-虚拟币类型
		Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
		typeMap.put(0, "全部");
		modelAndView.addObject("typeMap", typeMap);

		Map<Integer, String> typeShortNameMap = redisHelper.getCoinTypeShortNameMap();

		modelAndView.addObject("typeShortNameMap", typeShortNameMap);
		// 页面参数-委单状态
		Map<Integer, String> statusMap = new HashMap<Integer, String>();
		statusMap.put(EntrustStateEnum.AllDeal.getCode(),
				EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.AllDeal.getCode()));
		statusMap.put(EntrustStateEnum.Cancel.getCode(),
				EntrustStateEnum.getEntrustStateValueByCode(EntrustStateEnum.Cancel.getCode()));
		statusMap.put(0, "全部");
		modelAndView.addObject("statusMap", statusMap);
		// 页面参数-委单类型
		Map<Integer, String> entypeMap = new HashMap<Integer, String>();
		entypeMap.put(-1, "全部");
		entypeMap.put(EntrustTypeEnum.BUY.getCode(),
				EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.BUY.getCode()));
		entypeMap.put(EntrustTypeEnum.SELL.getCode(),
				EntrustTypeEnum.getEntrustTypeValueByCode(EntrustTypeEnum.SELL.getCode()));
		modelAndView.addObject("entypeMap", entypeMap);
		
		MatchTypeEnum[] matchTypeEnums = MatchTypeEnum.values();
		Map<Integer, String> matchTypeMap=new LinkedHashMap<Integer, String>(matchTypeEnums.length);
		matchTypeMap.put(-1, "全部");
		for(MatchTypeEnum m:matchTypeEnums) {
			matchTypeMap.put(m.getCode(), m.getValue());
		}
		modelAndView.addObject("matchTypeMap", matchTypeMap);

		if (StringUtils.isEmpty(keyWord) && tradeId == 0 && fprice == null && fentype == -1 && fstatus == 0) {
			modelAndView.addObject("filterApi", true);
			return modelAndView;
		}

		// 查询
		pageParam.setParam(param);
		Pagination<FEntrustHistory> pagination = shardingFEntrustHistoryService.selectFEntrustHistoryList(pageParam, filterParam);
		modelAndView.addObject("entrustList", pagination);

		// 查询日期最小的历史委单
		Pagination<FEntrustHistory> minDateParam = new Pagination<FEntrustHistory>(1, 1);
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			minDateParam.setKeyword(keyWord);
		}
		if (StringUtils.isNotEmpty(beginDate)) {
			minDateParam.setBegindate(beginDate);
		}
		if (StringUtils.isNotEmpty(endDate)) {
			minDateParam.setEnddate(endDate);
		}
		minDateParam.setOrderField("fcreatetime");
		minDateParam.setOrderDirection("asc");
		minDateParam.setParam(param);
		Pagination<FEntrustHistory> minDate = shardingFEntrustHistoryService.selectFEntrustHistoryList(minDateParam, filterParam);
		Collection<FEntrustHistory> minDateList = minDate.getData();
		if (minDateList.size() > 0) {
			Iterator<FEntrustHistory> minDateIterator = minDateList.iterator();
			Date date = null;
			while (minDateIterator.hasNext()) {
				FEntrustHistory obj = minDateIterator.next();
				date = obj.getFcreatetime();
			}
			if (date != null) {
				modelAndView.addObject("minDate", date);
			}
		}
		return modelAndView;
	}

	/**
	 * 撤单
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("entrust/cancelEntrust")
	@ResponseBody
	public ReturnResult cancelEntrust(@RequestParam(value = "uid", required = true) BigInteger fid) throws Exception {
		FEntrust fentrust = adminEntrustServer.selectFEntrust(fid);
		if (fentrust != null && (fentrust.getFstatus().equals(EntrustStateEnum.Going.getCode())
				|| fentrust.getFstatus().equals(EntrustStateEnum.PartDeal.getCode()))) {
			boolean flag = false;
			try {
				// flag = adminEntrustServer.updateCancelEntrust(fentrust.getFuid(),
				// fentrust.getFid());
				flag = adminEntrustServer.updateCancelEntrust(fentrust.getFid());
			} catch (Exception e) {
				e.printStackTrace();
				return ReturnResult.FAILUER("委单撤销失败");
			}
			if (flag) {
				return ReturnResult.SUCCESS("委单撤销成功");
			} else {
				return ReturnResult.FAILUER("委单错误，委单数据不存在或委单是杠杆单或委单状态不是未成交、部分成交");
			}
		} else {
			return ReturnResult.FAILUER("委单错误，委单数据不存在或委单是杠杆单或委单状态不是未成交、部分成交");
		}
	}

	/**
	 * 批量撤单
	 */
	@RequestMapping("entrust/batchCancelEntrust")
	@ResponseBody
	public ReturnResult batchCancelEntrust(@RequestParam(value = "ids", required = true) String ids) {
		String[] idString = ids.split(",");
		Integer errCount = 0;
		for (String id : idString) {
			FEntrust fentrust = adminEntrustServer.selectFEntrust(new BigInteger(id));
			if (fentrust != null && (fentrust.getFstatus().equals(EntrustStateEnum.Going.getCode())
					|| fentrust.getFstatus().equals(EntrustStateEnum.PartDeal.getCode()))) {
				try {
					if (!adminEntrustServer.updateCancelEntrust(fentrust.getFid()))
						errCount++;
				} catch (Exception e) {
					logger.error("批量撤销当前委单异常，委单id:" + id, e);
					errCount++;
				}
			} else {
				errCount++;
			}
		}
		return ReturnResult.SUCCESS((errCount > 0 ? ("部分撤销成功，失败" + errCount + "条!") : "批量撤销成功!"));
	}

	/**
	 * 导出委单当前记录
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("entrust/EntrustExport")
	@ResponseBody
	public ReturnResult EntrustExport() throws Exception {

		final String tableName = "当前委单";
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		// 存储excel_export_task记录
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		ExcelExportTask excelExportTask = new ExcelExportTask();
		excelExportTask.setCreateTime(new Date());
		excelExportTask.setExcelFileName(null);
		excelExportTask.setOperator(sessionAdmin.getFname());
		excelExportTask.setStatus(ExcelExportStatusEnum.EXPORTING.getCode().byteValue());
		excelExportTask.setTableName(tableName);
		long excelExportTaskId = adminExcelExportTaskService.insertSelective(excelExportTask);
		excelExportTask.setId(excelExportTaskId);
		
		PageData pageData=new PageData(request);

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;
			// header
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}
			
			Collection<FEntrust> fentrustList = GetFentrustList(pageData);
			
			for (FEntrust fentrust : fentrustList) {
				e.createRow(rowIndex++);
				for (ExportFiled filed : ExportFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), rowIndex - 1);
						break;
					case 登录名:
						e.setCell(filed.ordinal(), fentrust.getFloginname());
						break;
					case 会员昵称:
						e.setCell(filed.ordinal(), fentrust.getFnickname());
						break;
					case 会员真实姓名:
						e.setCell(filed.ordinal(), fentrust.getFrealname());
						break;
					case 虚拟币类型:
						e.setCell(filed.ordinal(), fentrust.getFcoinname());
						break;
					case 交易类型:
						e.setCell(filed.ordinal(), fentrust.getFtype_s());
						break;
					case 状态:
						e.setCell(filed.ordinal(), fentrust.getFstatus_s());
						break;
					case 单价:
						e.setCell(filed.ordinal(), fentrust.getFprize().doubleValue());
						break;
					case 数量:
						e.setCell(filed.ordinal(), fentrust.getFcount().doubleValue());
						break;
					case 未成交数量:
						e.setCell(filed.ordinal(), fentrust.getFleftcount().doubleValue());
						break;
					case 已成交数量:
						e.setCell(filed.ordinal(),
								MathUtils.sub(fentrust.getFcount(), fentrust.getFleftcount()).doubleValue());
						break;
					case 总金额:
						e.setCell(filed.ordinal(), fentrust.getFamount().doubleValue());
						break;
					case 成交总金额:
						e.setCell(filed.ordinal(), fentrust.getFsuccessamount().doubleValue());
						break;
					case 修改时间:
						e.setCell(filed.ordinal(), fentrust.getFlastupdattime());
						break;
					case 创建时间:
						e.setCell(filed.ordinal(), fentrust.getFcreatetime());
						break;
					default:
						break;
					}
				}
			}
			// 写入到文件
			String fileName = tableName.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xls");

			e.exportXls(excelRootPath.concat(fileName));
			return fileName;
		}, executor).whenComplete((r, e) -> {
			// 导出成功
			if (r != null) {
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FINISHED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			} else {
				logger.info("导出失败",e);
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FAILED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			}

		});

		// e.exportXls(response);
		return ReturnResult.SUCCESS("请从目录(统计管理-报表导出)查看导出任务");
	}

	/**
	 * 导出委单当前记录列表数据查询
	 *
	 * @return
	 */
	private Collection<FEntrust> GetFentrustList(PageData pageData) {
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = pageData.getString("keywords");
		String orderField = pageData.getString("orderField");
		String orderDirection = pageData.getString("orderDirection");
		String fstatus = pageData.getString("status");
		String tradeId = pageData.getString("tradeId");
		String fentype = pageData.getString("entype");
		String fprice = pageData.getString("price");
		String logDate = pageData.getString("logDate");
		String endDate = pageData.getString("endDate");
		if (StringUtils.isNotBlank(pageData.getString("pageNum"))) {
			currentPage = Integer.parseInt(pageData.getString("pageNum"));
		}
		// 查询条件定义
		Pagination<FEntrust> pageParam = new Pagination<FEntrust>(currentPage, numPerPage);
		FEntrust filterParam = new FEntrust();
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
		}
		// 委单虚拟币
		if (!StringUtils.isEmpty(tradeId)) {
			int type = Integer.parseInt(tradeId);
			if (type != 0) {
				filterParam.setFtradeid(type);
			}
		}
		// 委单状态
		if (fstatus != null && fstatus.trim().length() > 0) {
			int status = Integer.parseInt(fstatus);
			if (status != 0) {
				filterParam.setFstatus(status);
			}
		}
		// 委单类型
		if (fentype != null && fentype.trim().length() > 0) {
			int entype = Integer.parseInt(fentype);
			if (entype != -1) {
				filterParam.setFtype(entype);
			}
		}
		// 订单价格
		if (fprice != null && fprice.trim().length() > 0) {
			try {
				BigDecimal price = new BigDecimal(fprice);
				filterParam.setFprize(price);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 创建时间
		if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
			pageParam.setBegindate(logDate);
		}
		if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
			pageParam.setEnddate(endDate);
		}
		// 排序条件
		if (orderField != null && orderField.trim().length() > 0) {
			pageParam.setOrderField(orderField);
		} else {
			pageParam.setOrderField("fcreatetime");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			pageParam.setOrderDirection(orderDirection);
		} else {
			pageParam.setOrderDirection("desc");
		}
		// 查询
		Pagination<FEntrust> pagination = adminEntrustServer.selectFEntrustList(pageParam, filterParam);
		return pagination.getData();
	}

	/**
	 * 导出委单历史记录
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("entrust/EntrustHistoryExport")
	@ResponseBody
	public ReturnResult EntrustHistoryExport(@RequestParam(value = "type", required = true) int type) throws Exception {

		final String tableName = "历史委单";
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		// 存储excel_export_task记录
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		ExcelExportTask excelExportTask = new ExcelExportTask();
		excelExportTask.setCreateTime(new Date());
		excelExportTask.setExcelFileName(null);
		excelExportTask.setOperator(sessionAdmin.getFname());
		excelExportTask.setStatus(ExcelExportStatusEnum.EXPORTING.getCode().byteValue());
		excelExportTask.setTableName(tableName);
		long excelExportTaskId = adminExcelExportTaskService.insertSelective(excelExportTask);
		excelExportTask.setId(excelExportTaskId);
		
		PageData pageData=new PageData(request);

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}
			Collection<FEntrustHistory> fentrustList = null;
			if (type == 0) {
				fentrustList = GetFEntrustHistoryList(pageData);
			} else {
				fentrustList = GetFEntrustHistoryListNoPage(pageData);
			}
			
			logger.info("导出历史委单记录数:{}",fentrustList.size());
			
			for (FEntrustHistory fentrust : fentrustList) {
				e.createRow(rowIndex++);
				for (ExportFiled filed : ExportFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), rowIndex - 1);
						break;
					case 登录名:
						e.setCell(filed.ordinal(), fentrust.getFloginname());
						break;
					case 会员昵称:
						e.setCell(filed.ordinal(), fentrust.getFnickname());
						break;
					case 会员真实姓名:
						e.setCell(filed.ordinal(), fentrust.getFrealname());
						break;
					case 虚拟币类型:
						e.setCell(filed.ordinal(), fentrust.getFcoinname());
						break;
					case 交易类型:
						e.setCell(filed.ordinal(), fentrust.getFtype_s());
						break;
					case 状态:
						e.setCell(filed.ordinal(), fentrust.getFstatus_s());
						break;
					case 单价:
						e.setCell(filed.ordinal(), fentrust.getFprize().doubleValue());
						break;
					case 数量:
						e.setCell(filed.ordinal(), fentrust.getFcount().doubleValue());
						break;
					case 未成交数量:
						e.setCell(filed.ordinal(), fentrust.getFleftcount().doubleValue());
						break;
					case 已成交数量:
						e.setCell(filed.ordinal(),
								MathUtils.sub(fentrust.getFcount(), fentrust.getFleftcount()).doubleValue());
						break;
					case 总金额:
						e.setCell(filed.ordinal(), fentrust.getFamount().doubleValue());
						break;
					case 成交总金额:
						e.setCell(filed.ordinal(), fentrust.getFsuccessamount().doubleValue());
						break;
					case 修改时间:
						e.setCell(filed.ordinal(), fentrust.getFlastupdattime());
						break;
					case 创建时间:
						e.setCell(filed.ordinal(), fentrust.getFcreatetime());
						break;
					default:
						break;
					}
				}
			}
			// 写入到文件
			String fileName = tableName.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xls");

			e.exportXls(excelRootPath.concat(fileName));
			return fileName;
		}, executor).whenComplete((r, e) -> {
			// 导出成功
			if (r != null) {
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FINISHED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			} else {
				logger.info("导出失败",e);
				// 更新状态和文件名
				ExcelExportTask updateExcelExportTask = new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FAILED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			}

		});

		return ReturnResult.SUCCESS("请从目录(统计管理-报表导出)查看导出任务");
	}


	/**
	 * 导出委单当前记录列表数据查询
	 *
	 * @return
	 */
	private Collection<FEntrustHistory> GetFEntrustHistoryList(PageData pageData) {
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = pageData.getString("keywords");
		String orderField = pageData.getString("orderField");
		String orderDirection = pageData.getString("orderDirection");
		String fstatus = pageData.getString("status");
		String tradeId = pageData.getString("tradeId");
		String fentype = pageData.getString("entype");
		String fprice = pageData.getString("price");
		String logDate = pageData.getString("logDate");
		String endDate = pageData.getString("endDate");
		if (StringUtils.isNotBlank(pageData.getString("pageNum") )) {
			currentPage = Integer.parseInt(pageData.getString("pageNum"));
		}
		// 查询条件定义
		Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, 100000);
		FEntrustHistory filterParam = new FEntrustHistory();
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
		}
		// 委单虚拟币
		if (!StringUtils.isEmpty(tradeId)) {
			int type = Integer.parseInt(tradeId);
			if (type != 0) {
				filterParam.setFtradeid(type);
			}
		}
		// 委单状态
		if (fstatus != null && fstatus.trim().length() > 0) {
			int status = Integer.parseInt(fstatus);
			if (status != 0) {
				filterParam.setFstatus(status);
			}
		}
		// 委单类型
		if (fentype != null && fentype.trim().length() > 0) {
			int entype = Integer.parseInt(fentype);
			if (entype != -1) {
				filterParam.setFtype(entype);
			}
		}
		// 订单价格
		if (fprice != null && fprice.trim().length() > 0) {
			try {
				BigDecimal price = new BigDecimal(fprice);
				filterParam.setFprize(price);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 起始时间
		if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
			pageParam.setBegindate(logDate);
		}
		// 起始时间
		if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
			pageParam.setEnddate(endDate);
		}
		// 排序条件
		if (orderField != null && orderField.trim().length() > 0) {
			pageParam.setOrderField(orderField);
		} else {
			pageParam.setOrderField("fcreatetime");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			pageParam.setOrderDirection(orderDirection);
		} else {
			pageParam.setOrderDirection("desc");
		}
		// 查询
		Pagination<FEntrustHistory> pagination = adminEntrustServer.selectFEntrustHistoryList(pageParam, filterParam);
		return pagination.getData();
	}

	/**
	 * 导出委单当前记录列表数据查询
	 *
	 * @return
	 */
	private Collection<FEntrustHistory> GetFEntrustHistoryListNoPage(PageData pageData) {
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = pageData.getString("keywords");
		String orderField = pageData.getString("orderField");
		String orderDirection = pageData.getString("orderDirection");
		String fstatus = pageData.getString("status");
		String tradeId = pageData.getString("tradeId");
		String fentype = pageData.getString("entype");
		String fprice = pageData.getString("price");
		String logDate = pageData.getString("logDate");
		String endDate = pageData.getString("endDate");
		String filterApi=pageData.getString("filterApi");
		
		// 查询条件定义
		Pagination<FEntrustHistory> pageParam = new Pagination<FEntrustHistory>(currentPage, 100000);
		FEntrustHistory filterParam = new FEntrustHistory();
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
		}
		// 委单虚拟币
		if (!StringUtils.isEmpty(tradeId)) {
			int type = Integer.parseInt(tradeId);
			if (type != 0) {
				filterParam.setFtradeid(type);
			}
		}
		// 委单状态
		if (fstatus != null && fstatus.trim().length() > 0) {
			int status = Integer.parseInt(fstatus);
			if (status != 0) {
				filterParam.setFstatus(status);
			}
		}
		// 委单类型
		if (fentype != null && fentype.trim().length() > 0) {
			int entype = Integer.parseInt(fentype);
			if (entype != -1) {
				filterParam.setFtype(entype);
			}
		}
		// 订单价格
		if (fprice != null && fprice.trim().length() > 0) {
			try {
				BigDecimal price = new BigDecimal(fprice);
				filterParam.setFprize(price);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 起始时间
		if (!StringUtils.isEmpty(logDate) && logDate.trim().length() > 0) {
			pageParam.setBegindate(logDate);
		}
		if (!StringUtils.isEmpty(endDate) && endDate.trim().length() > 0) {
			pageParam.setEnddate(endDate);
		}
		// 排序条件
		if (orderField != null && orderField.trim().length() > 0) {
			pageParam.setOrderField(orderField);
		} else {
			pageParam.setOrderField("fcreatetime");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			pageParam.setOrderDirection(orderDirection);
		} else {
			pageParam.setOrderDirection("desc");
		}
		
		
		// 过滤api
		if (filterApi != null && filterApi.equals("on")) {
			filterParam.setFilterApi(true);
		} else {
			filterParam.setFilterApi(false);
		}
		
		// 查询
		List<FEntrustHistory> pagination = adminEntrustServer.selectFEntrustHistoryListNoPage(pageParam, filterParam);
		return pagination;
	}

	// 导出列名
	private static enum ExportFiled {
		序号, 登录名, 会员昵称, 会员真实姓名, 虚拟币类型, 交易类型, 状态, 单价, 数量, 未成交数量, 已成交数量, 总金额, 成交总金额, 修改时间, 创建时间;
	}
}
