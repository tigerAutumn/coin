package com.qkwl.admin.layui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.statistic.EBankRank;
import com.qkwl.common.dto.statistic.UserPosition;
import com.qkwl.common.dto.statistic.UserTradePosition;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminStatisticService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class StatisticController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticController.class);

	/**
	 * 分页大小
	 */
	private int numPerPage = Constant.adminPageSize;

	private static final Logger LOG = LoggerFactory.getLogger(StatisticController.class);

	@Autowired
	private IAdminStatisticService adminStatisticService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;

	/**
	 * 交易排名统计
	 * 
	 * @param tradeId
	 * @param startTime
	 * @param endTime
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/rankStatistic")
	public ModelAndView getRankStatistic(
			@RequestParam(value = "tradeId", required = false, defaultValue = "0") Integer tradeId,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistic/rankStatistic");
		// 查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);

		if (tradeId == 0) {
			modelAndView.addObject("allTradeType", allTradeType);
			modelAndView.addObject("totalAmount", BigDecimal.ZERO);
			return modelAndView;
		}
		Pagination<EBankRank> pageParam = new Pagination<EBankRank>(currentPage, numPerPage);
		pageParam.setBegindate(startTime);
		pageParam.setEnddate(endTime);
		// 查询活动列表
		Pagination<EBankRank> pagination = adminStatisticService.selectEBankRankPageList(pageParam, tradeId);
		// 查询汇总交易额
		BigDecimal totalAmount = adminStatisticService.selectEBankTotalAmount(startTime, endTime, tradeId);
		modelAndView.addObject("tradeId", tradeId);
		modelAndView.addObject("startTime", startTime);
		modelAndView.addObject("endTime", endTime);
		modelAndView.addObject("ebankRankList", pagination);
		modelAndView.addObject("allTradeType", allTradeType);
		modelAndView.addObject("totalAmount", totalAmount);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportRankFiled {
		序号, UID, 当日成交额, 累计成交额;
	}

	/**
	 * 导出交易排名
	 * 
	 * @param tradeId
	 * @param startTime
	 * @param endTime
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/importRank")
	@ResponseBody
	public ReturnResult importRank(
			@RequestParam(value = "tradeId", required = false, defaultValue = "0") Integer tradeId,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {

		final String tableName = "交易排行榜列表";
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

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;

			// header
			e.createRow(rowIndex++);
			for (ExportRankFiled filed : ExportRankFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<EBankRank> pageParam = new Pagination<EBankRank>(currentPage, 100000);
			pageParam.setBegindate(startTime);
			pageParam.setEnddate(endTime);
			// 查询活动列表
			Pagination<EBankRank> pagination = adminStatisticService.selectEBankRankPageList(pageParam, tradeId);

			Collection<EBankRank> bankList = pagination.getData();
			for (EBankRank eBankRank : bankList) {
				e.createRow(rowIndex++);
				for (ExportRankFiled filed : ExportRankFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), eBankRank.getSort());
						break;
					case UID:
						e.setCell(filed.ordinal(), eBankRank.getUserId());
						break;
					case 当日成交额:
						e.setCell(filed.ordinal(), eBankRank.getTodayAmount().doubleValue());
						break;
					case 累计成交额:
						e.setCell(filed.ordinal(), eBankRank.getTotalAmount().doubleValue());
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
	 * 报表导出
	 * 
	 * @param coinId
	 * @param choosenDate
	 * @param netPosition
	 * @param position
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/excelExport")
	public ModelAndView excelExport(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderField", defaultValue = "id") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) {
		
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistic/excelExport");

		Pagination<ExcelExportTask> pageParam = new Pagination<ExcelExportTask>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 查询excel导出列表，近一个月
		Pagination<ExcelExportTask> pagination = adminExcelExportTaskService.selectExcelExportTaskPageList(pageParam,sessionAdmin.getFname());

		modelAndView.addObject("list", pagination);
		return modelAndView;
	}

	@GetMapping(value = "/admin/downloadExcel")
	@ResponseBody
	public void downloadExcel(@RequestParam String fileName, HttpServletRequest request, HttpServletResponse response) {
		try (InputStream inputStream = new FileInputStream(new File(excelRootPath, fileName));
				OutputStream outputStream = response.getOutputStream()) {
			// 指明为下载
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8")); // 设置文件名

			// 把输入流copy到输出流
			IOUtils.copy(inputStream, outputStream);
			outputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询用户持仓统计
	 * 
	 * @param coinId
	 * @param choosenDate
	 * @param netPosition
	 * @param position
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/positionStatistic")
	public ModelAndView getPositionStatistic(
			@RequestParam(value = "coinId", required = false, defaultValue = "0") Integer coinId,
			@RequestParam(value = "choosenDate", required = false) String choosenDate,
			@RequestParam(value = "netPosition", required = false) BigDecimal netPosition,
			@RequestParam(value = "position", required = false) BigDecimal position,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistic/positionStatistic");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();

		if (coinId == 0) {
			modelAndView.addObject("allCoinType", allCoinType);
			modelAndView.addObject("totalPosition", BigDecimal.ZERO);
			return modelAndView;
		}
		Pagination<UserPosition> pageParam = new Pagination<UserPosition>(currentPage, numPerPage);
		UserPosition userPosition = new UserPosition();
		userPosition.setCoinId(coinId);
		userPosition.setChoosenDate(choosenDate);
		userPosition.setNetPosition(netPosition);
		userPosition.setPosition(position);
		// 查询用户持仓列表
		Pagination<UserPosition> pagination = adminStatisticService.selectUserPositionPageList(pageParam, userPosition);
		// 查询汇总持仓额
		BigDecimal totalPosition = adminStatisticService.selectTotalPosition(userPosition);
		modelAndView.addObject("coinId", coinId);
		modelAndView.addObject("choosenDate", choosenDate);
		modelAndView.addObject("netPosition", netPosition);
		modelAndView.addObject("position", position);
		modelAndView.addObject("userPositionList", pagination);
		modelAndView.addObject("allCoinType", allCoinType);
		modelAndView.addObject("totalPosition", totalPosition);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportPositionFiled {
		序号, UID, 手机号码, 邮箱号码, 充币数量, 持仓数量, 净持仓数量;
	}

	/**
	 * 导出持仓统计
	 * 
	 * @param tradeId
	 * @param startTime
	 * @param endTime
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/importPosition")
	@ResponseBody
	public ReturnResult importPosition(
			@RequestParam(value = "coinId", required = false, defaultValue = "0") Integer coinId,
			@RequestParam(value = "choosenDate", required = false) String choosenDate,
			@RequestParam(value = "netPosition", required = false) BigDecimal netPosition,
			@RequestParam(value = "position", required = false) BigDecimal position,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=capitalOutList.xls");

		final String tableName = "用户持仓统计列表";
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

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;

			// header
			e.createRow(rowIndex++);
			for (ExportPositionFiled filed : ExportPositionFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<UserPosition> pageParam = new Pagination<UserPosition>(currentPage, 100000);
			UserPosition userPosition = new UserPosition();
			userPosition.setCoinId(coinId);
			userPosition.setChoosenDate(choosenDate);
			userPosition.setPosition(position);
			userPosition.setNetPosition(netPosition);
			// 查询用户持仓
			Pagination<UserPosition> pagination = adminStatisticService.selectUserPositionPageList(pageParam,
					userPosition);
			Collection<UserPosition> positionList = pagination.getData();
			for (UserPosition element : positionList) {
				e.createRow(rowIndex++);
				for (ExportPositionFiled filed : ExportPositionFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), element.getSort());
						break;
					case UID:
						e.setCell(filed.ordinal(), element.getUserId());
						break;
					case 手机号码:
						e.setCell(filed.ordinal(), element.getTelephone());
						break;
					case 邮箱号码:
						e.setCell(filed.ordinal(), element.getEmail());
						break;
					case 充币数量:
						e.setCell(filed.ordinal(), element.getChargingNumber().doubleValue());
						break;
					case 持仓数量:
						e.setCell(filed.ordinal(), element.getPosition().doubleValue());
						break;
					case 净持仓数量:
						e.setCell(filed.ordinal(), element.getNetPosition().doubleValue());
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
	 * 查询用户持仓统计
	 * 
	 * @param coinId
	 * @param choosenDate
	 * @param netPosition
	 * @param position
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/tradePositionStatistic")
	public ModelAndView getTradePositionStatistic(
			@RequestParam(value = "coinId", required = false, defaultValue = "0") Integer coinId,
			@RequestParam(value = "tradeId", required = false) Integer tradeId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "netCountStart", required = false) BigDecimal netCountStart,
			@RequestParam(value = "netCountEnd", required = false) BigDecimal netCountEnd,
			@RequestParam(value = "positionStart", required = false) BigDecimal positionStart,
			@RequestParam(value = "positionEnd", required = false) BigDecimal positionEnd,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistic/tradePositionStatistic");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();
		// 查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = sdf.format(today);
		if (coinId == 0) {
			modelAndView.addObject("endDate", endDate);
			modelAndView.addObject("allCoinType", allCoinType);
			modelAndView.addObject("allTradeType", allTradeType);
			return modelAndView;
		}
		Pagination<UserTradePosition> pageParam = new Pagination<UserTradePosition>(currentPage, numPerPage);
		UserTradePosition userTradePosition = new UserTradePosition();
		userTradePosition.setCoinId(coinId);
		userTradePosition.setTradeId(tradeId);
		userTradePosition.setStartDate(startDate);
		userTradePosition.setNetCountStart(netCountStart);
		userTradePosition.setNetCountEnd(netCountEnd);
		userTradePosition.setPositionStart(positionStart);
		userTradePosition.setPositionEnd(positionEnd);
		// 查询用户持仓列表
		Pagination<UserTradePosition> pagination = adminStatisticService.selectUserTradePositionPageList(pageParam, userTradePosition);
		modelAndView.addObject("coinId", coinId);
		modelAndView.addObject("tradeId", tradeId);
		modelAndView.addObject("startDate", startDate);
		modelAndView.addObject("netCountStart", netCountStart);
		modelAndView.addObject("netCountEnd", netCountEnd);
		modelAndView.addObject("positionStart", positionStart);
		modelAndView.addObject("positionEnd", positionEnd);
		modelAndView.addObject("userTradePositionList", pagination);
		modelAndView.addObject("endDate", endDate);
		modelAndView.addObject("allCoinType", allCoinType);
		modelAndView.addObject("allTradeType", allTradeType);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportTradePositionFiled {
		序号, UID, 手机号码, 邮箱号码, 累计买入数量, 累计卖出数量, 净交易数量, 持仓数量;
	}

	/**
	 * 导出净交易持仓统计
	 * 
	 * @param tradeId
	 * @param startTime
	 * @param endTime
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/admin/importTradePosition")
	@ResponseBody
	public ReturnResult importTradePosition(
			@RequestParam(value = "coinId", required = false) Integer coinId,
			@RequestParam(value = "tradeId", required = false) Integer tradeId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "netCountStart", required = false) BigDecimal netCountStart,
			@RequestParam(value = "netCountEnd", required = false) BigDecimal netCountEnd,
			@RequestParam(value = "positionStart", required = false) BigDecimal positionStart,
			@RequestParam(value = "positionEnd", required = false) BigDecimal positionEnd,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=capitalOutList.xls");

		final String tableName = "用户净交易持仓统计列表";
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

		// 异步执行excel导出
		CompletableFuture.supplyAsync(() -> {
			XlsExport e = new XlsExport();
			int rowIndex = 0;

			// header
			e.createRow(rowIndex++);
			for (ExportTradePositionFiled filed : ExportTradePositionFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<UserTradePosition> pageParam = new Pagination<UserTradePosition>(currentPage, 100000);
			UserTradePosition userTradePosition = new UserTradePosition();
			userTradePosition.setCoinId(coinId);
			userTradePosition.setTradeId(tradeId);
			userTradePosition.setStartDate(startDate);
			userTradePosition.setNetCountStart(netCountStart);
			userTradePosition.setNetCountEnd(netCountEnd);
			userTradePosition.setPositionStart(positionStart);
			userTradePosition.setPositionEnd(positionEnd);
			// 查询用户持仓列表
			Pagination<UserTradePosition> pagination = adminStatisticService.selectUserTradePositionPageList(pageParam, userTradePosition);
			
			Collection<UserTradePosition> positionList = pagination.getData();
			for (UserTradePosition element : positionList) {
				e.createRow(rowIndex++);
				for (ExportTradePositionFiled filed : ExportTradePositionFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), element.getSort());
						break;
					case UID:
						e.setCell(filed.ordinal(), element.getUserId());
						break;
					case 手机号码:
						e.setCell(filed.ordinal(), element.getTelephone());
						break;
					case 邮箱号码:
						e.setCell(filed.ordinal(), element.getEmail());
						break;
					case 累计买入数量:
						e.setCell(filed.ordinal(), element.getBuyCount().doubleValue());
						break;
					case 累计卖出数量:
						e.setCell(filed.ordinal(), element.getSellCount().doubleValue());
						break;
					case 净交易数量:
						e.setCell(filed.ordinal(), element.getNetCount().doubleValue());
						break;
					case 持仓数量:
						e.setCell(filed.ordinal(), element.getPosition().doubleValue());
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
}
