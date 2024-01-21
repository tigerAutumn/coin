package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.auth.SessionContextUtils;
import com.qkwl.common.controller.BaseController;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.activity.ActivityConfig;
import com.qkwl.common.dto.activity.InnovationAreaActivity;
import com.qkwl.common.dto.activity.NetTradeRankActivity;
import com.qkwl.common.dto.activity.NetTradeRankStatistic;
import com.qkwl.common.dto.activity.RankActivityConfig;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.dict.DictItemAttr;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.statistic.UserPosition;
import com.qkwl.common.dto.wallet.UserWalletBalanceLogDto;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminActivityService;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminStatisticService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class ActivityController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
	
	@Autowired
	private IAdminActivityService adminActivityService;
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
	
 	/**
 	 * 查询活动信息
 	 */
	@RequestMapping("/admin/activityList")
	public ModelAndView getActivityList(
			@RequestParam(value="status",required=false,defaultValue="0") Integer status,
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="start_time") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/activityList");
		// 定义查询条件
		Pagination<ActivityConfig> pageParam = new Pagination<ActivityConfig>(currentPage, numPerPage);
		//参数判断
		if (status > 0) {
			modelAndView.addObject("status", status);
		}
		
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		//查询活动列表
		Pagination<ActivityConfig> pagination = adminActivityService.selectActivityPageList(pageParam, status);

		modelAndView.addObject("activityList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除活动
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteActivity")
	@ResponseBody
	public ReturnResult deleteActivity(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		ActivityConfig activity = new ActivityConfig();
		if (id > 0) {
			activity = adminActivityService.selectActivityById(id);
		}
		if (activity == null) {
			return ReturnResult.FAILUER("活动不存在!");
		}
		//删除活动
		return adminActivityService.deleteActivity(activity);
	}
	
	/**
	 * 添加活动
	 * @return
	 */
	@RequestMapping("/admin/addActivity")
	public ModelAndView addActivity(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/addActivity");
		return modelAndView;
	}
	
	/**
     * 保存新增的币种信息
     */
    @RequestMapping("admin/saveActivity")
    @ResponseBody
    public ReturnResult saveActivity(
            @RequestParam(value = "activityName", required = false) String activityName,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) throws Exception {
    	ActivityConfig activity = new ActivityConfig();
        activity.setActivityName(activityName);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activity.setStartTime(format.parse(startTime));
        activity.setEndTime(format.parse(endTime));
        return adminActivityService.insertActivity(activity);
    }
	
	/**
	 * 读取修改的活动信息
	 * @return
	 */
	@RequestMapping("/admin/changeActivity")
	public ModelAndView changeActivity(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/activity/changeActivity");
		if (id > 0) {
			ActivityConfig activity = adminActivityService.selectActivityById(id);
			modelAndView.addObject("activity", activity);
		}
		return modelAndView;
	}
	
	/**
     * 修改的活动信息
     */
    @RequestMapping("admin/updateActivity")
    @ResponseBody
    public ReturnResult updateActivity(
    		@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
    		@RequestParam(value = "activityName", required = false) String activityName,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) throws Exception {
    	ActivityConfig activity = new ActivityConfig();
		if (id > 0) {
			activity = adminActivityService.selectActivityById(id);
		}
		if (activity == null) {
			return ReturnResult.FAILUER("活动不存在!");
		}
		activity.setActivityName(activityName);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activity.setStartTime(format.parse(startTime));
        activity.setEndTime(format.parse(endTime));
		
        return adminActivityService.updateActivity(activity);
    }
    
    /**
 	 * 查询活动信息
 	 */
	@RequestMapping("/admin/rankActivityList")
	public ModelAndView getRankActivityList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/rankActivityList");
		// 定义查询条件
		Pagination<RankActivityConfig> pageParam = new Pagination<RankActivityConfig>(currentPage, numPerPage);

		//查询活动列表
		Pagination<RankActivityConfig> pagination = adminActivityService.selectRankActivityPageList(pageParam);

		modelAndView.addObject("activityList", pagination);
		return modelAndView;
	}
	
	/**
	 * 添加活动
	 * @return
	 */
	@RequestMapping("/admin/addRankActivity")
	public ModelAndView addRankActivity(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/addRankActivity");
		//查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		modelAndView.addObject("allTradeType", allTradeType);
		return modelAndView;
	}
	
	/**
     * 保存新增
     */
    @RequestMapping("admin/saveRankActivity")
    @ResponseBody
    public ReturnResult saveRankActivity(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "tradeId", required = false) Integer tradeId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "status", required = false) Integer status
            ) throws Exception {
    	RankActivityConfig activity = new RankActivityConfig();
        activity.setName(name);
        activity.setTradeId(tradeId);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activity.setStartTime(format.parse(startTime));
        activity.setEndTime(format.parse(endTime));
        activity.setStatus(status);
        return adminActivityService.insertRankActivity(activity);
    }
	
	/**
	 * 读取修改的活动信息
	 * @return
	 */
	@RequestMapping("/admin/changeRankActivity")
	public ModelAndView changeRankActivity(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/activity/changeRankActivity");
		if (id > 0) {
			RankActivityConfig activity = adminActivityService.selectRankActivityById(id);
			modelAndView.addObject("activity", activity);
		}
		return modelAndView;
	}
	
	/**
     * 修改的活动信息
     */
    @RequestMapping("admin/updateRankActivity")
    @ResponseBody
    public ReturnResult updateRankActivity(
    		@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
    		@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "status", required = false) Integer status
            ) throws Exception {
    	RankActivityConfig activity = new RankActivityConfig();
		if (id > 0) {
			activity = adminActivityService.selectRankActivityById(id);
		}
		if (activity == null) {
			return ReturnResult.FAILUER("活动不存在!");
		}
		activity.setName(name);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activity.setStartTime(format.parse(startTime));
        activity.setEndTime(format.parse(endTime));
        activity.setStatus(status);
		
        return adminActivityService.updateRankActivity(activity);
    }
    
    
    /**
     * 查询创新区奖励活动
     */
    /**
 	 * 查询活动信息
 	 */
	@RequestMapping("/admin/innovationActivityList")
	public ModelAndView innovationActivityList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage)
					throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/innovationActivityList");

		//查询活动列表
		Map<String, Object> params = new HashMap<>();
		PageInfo<InnovationAreaActivity> list = adminActivityService.listInnovationActivity(params, currentPage, numPerPage);
		modelAndView.addObject("activityList", list);
		return modelAndView;
	}
	
	/**
 	 * 添加活动信息
 	 */
	@RequestMapping("/admin/addInnovationActivity")
	public ModelAndView addInnovationActivity()throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/addInnovationActivity");
		//查询所有创新区交易对
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeList();
		
		List<SystemCoinType> newList = new ArrayList<>();
		for(SystemCoinType systemCoinType : allCoinType) {
			if(systemCoinType.getIsInnovateAreaCoin()) {
				newList.add(systemCoinType);
			}
		}
		modelAndView.addObject("allCoinType", newList);
		return modelAndView;
	}
	
	/**
 	 * 保存活动信息
 	 */
	@RequestMapping("/admin/saveInnovationActivity")
	@ResponseBody
    public ReturnResult saveInnovationActivity(
            @RequestParam(value = "coinId", required = true) Integer coinId,
            @RequestParam(value = "entrustType", required = false) Integer entrustType,
            @RequestParam(value = "rate", required = false) BigDecimal rate,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime
			)throws Exception {
		
		InnovationAreaActivity  activity = new InnovationAreaActivity();
        activity.setCoinId(coinId);
        activity.setAmount(amount);
        activity.setRate(rate);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activity.setStartTime(format.parse(startTime));
        activity.setEndTime(format.parse(endTime));
        activity.setStatus((byte)1);
        activity.setBalance(amount);
        adminActivityService.insertInnovationActivity(activity);
		return ReturnResult.SUCCESS();
	}
	
	
	/**
	 * 启用创新区奖励活动
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/enableInnovation")
	@ResponseBody
	public ReturnResult enableInnovation(
			@RequestParam(value = "id", required = false,defaultValue="0") Long id) {
		InnovationAreaActivity innovationAreaActitity = new InnovationAreaActivity();
		if (id > 0) {
			innovationAreaActitity = adminActivityService.selectInnovationById(id);
		}
		if (innovationAreaActitity == null) {
			return ReturnResult.FAILUER("创新区奖励活动不存在!");
		}
		if (innovationAreaActitity.getStatus() == 1) {
			return ReturnResult.FAILUER("创新区奖励活动已处于启用状态!");
		}
		//启用空投活动
		innovationAreaActitity.setStatus((byte)1);
		adminActivityService.updateInnovationActivity(innovationAreaActitity);
		return ReturnResult.SUCCESS("创新区奖励活动启用成功！");
	}
	
	/**
	 * 禁用空投活动
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/disableInnovation")
	@ResponseBody
	public ReturnResult disableInnovation(
			@RequestParam(value = "id", required = false,defaultValue="0") Long id) {
		InnovationAreaActivity innovationAreaActitity = new InnovationAreaActivity();
		if (id > 0) {
			innovationAreaActitity = adminActivityService.selectInnovationById(id);
		}
		if (innovationAreaActitity == null) {
			return ReturnResult.FAILUER("创新区奖励活动不存在!");
		}
		if (innovationAreaActitity.getStatus() == 0) {
			return ReturnResult.FAILUER("创新区奖励活动已处于禁用状态!");
		}
		//启用空投活动
		innovationAreaActitity.setStatus((byte)0);
		adminActivityService.updateInnovationActivity(innovationAreaActitity);
		return ReturnResult.SUCCESS("创新区奖励活动禁用成功！");
	}
	
    /**
 	 * 查询创新区活动记录
 	 */
	@RequestMapping("/admin/innovationActivityLogList")
	public ModelAndView innovationActivityLogList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			 String datetime,Integer coinId)
					throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/innovationActivityLogList");

		//查询活动记录列表
		Map<String, Object> params = new HashMap<>();
		List<Integer> typeList = new ArrayList<>();
        typeList.add(UserWalletBalanceLogTypeEnum.Reward_of_Innovation_Zone.getCode());
		params.put("typeList", typeList);
		params.put("coinId", coinId);
		if(StringUtils.isNotEmpty(datetime)) {
			params.put("start", datetime);
			params.put("end", datetime+" 23:59:59");
		}
		PageInfo<UserWalletBalanceLogDto> list = adminActivityService.listActivityLog(params, currentPage, numPerPage);
		modelAndView.addObject("logList", list);
		
		//查询所有创新区交易对
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeList();
		
		List<SystemCoinType> newList = new ArrayList<>();
		SystemCoinType param = new SystemCoinType();
		param.setId(0);
		param.setShortName("全部");
		newList.add(param);
		for(SystemCoinType systemCoinType : allCoinType) {
			if(systemCoinType.getIsInnovateAreaCoin()) {
				newList.add(systemCoinType);
			}
		}
		modelAndView.addObject("allCoinType", newList);
		modelAndView.addObject("coinId",coinId);
		modelAndView.addObject("datetime", datetime);
		modelAndView.addObject("currentPage", currentPage);
		return modelAndView;
	}

	/**
	 * 查询活动信息
	 */
	@RequestMapping("/admin/netTradeRankActivityList")
	public ModelAndView netTradeRankActivityList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/netTradeRankActivityList");
		// 定义查询条件
		Pagination<NetTradeRankActivity> pageParam = new Pagination<NetTradeRankActivity>(currentPage, numPerPage);
	
		//查询活动列表
		Pagination<NetTradeRankActivity> pagination = adminActivityService.selectNetTradeRankActivityPageList(pageParam);
	
		modelAndView.addObject("activityList", pagination);
		return modelAndView;
	}
	
	/**
	 * 添加活动
	 * @return
	 */
	@RequestMapping("/admin/addNetTradeRankActivity")
	public ModelAndView addNetTradeRankActivity(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/addNetTradeRankActivity");
		//查询所有交易对
		List<SystemTradeType> allTradeType = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
		modelAndView.addObject("allTradeType", allTradeType);
		return modelAndView;
	}
	
	/**
	 * 保存新增
	 */
	@RequestMapping("admin/saveNetTradeRankActivity")
	@ResponseBody
	public ReturnResult saveNetTradeRankActivity(
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "tradeId", required = false) Integer tradeId,
	        @RequestParam(value = "startTime", required = false) String startTime,
	        @RequestParam(value = "endTime", required = false) String endTime,
	        @RequestParam(value = "snapshotTime", required = false) String snapshotTime,
	        @RequestParam(value = "minNetTrade", required = false) BigDecimal minNetTrade,
	        @RequestParam(value = "minPosition", required = false) BigDecimal minPosition
	        ) throws Exception {
		NetTradeRankActivity activity = new NetTradeRankActivity();
	    activity.setName(name);
	    activity.setTradeId(tradeId);
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date startDate = format.parse(startTime);
	    Date endDate = format.parse(endTime);
	    Date snapshotDate = format.parse(snapshotTime);
	    if (!endDate.after(startDate)) {
	    	return ReturnResult.FAILUER("结束时间必须大于开始时间!");
		}
	    if (endDate.after(snapshotDate)) {
	    	return ReturnResult.FAILUER("结束时间不能大于快照时间!");
		}
	    activity.setStartTime(startDate);
	    activity.setEndTime(format.parse(endTime));
	    activity.setSnapshotTime(format.parse(snapshotTime));
	    activity.setMinNetTrade(minNetTrade);
	    activity.setMinPosition(minPosition);
	    return adminActivityService.insertNetTradeRankActivity(activity);
	}
	
	/**
	 * 读取修改的活动信息
	 * @return
	 */
	@RequestMapping("/admin/changeNetTradeRankActivity")
	public ModelAndView changeNetTradeRankActivity(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/activity/changeNetTradeRankActivity");
		if (id > 0) {
			NetTradeRankActivity activity = adminActivityService.selectNetTradeRankActivityById(id);
			modelAndView.addObject("activity", activity);
		}
		return modelAndView;
	}
	
	/**
	 * 修改的活动信息
	 */
	@RequestMapping("admin/updateNetTradeRankActivity")
	@ResponseBody
	public ReturnResult updateNetTradeRankActivity(
			@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "startTime", required = false) String startTime,
	        @RequestParam(value = "endTime", required = false) String endTime,
	        @RequestParam(value = "snapshotTime", required = false) String snapshotTime,
	        @RequestParam(value = "minNetTrade", required = false) BigDecimal minNetTrade,
	        @RequestParam(value = "minPosition", required = false) BigDecimal minPosition
	        ) throws Exception {
		NetTradeRankActivity activity = new NetTradeRankActivity();
		if (id > 0) {
			activity = adminActivityService.selectNetTradeRankActivityById(id);
		}
		if (activity == null) {
			return ReturnResult.FAILUER("活动不存在!");
		}
		Date now = new Date();
		if (activity.getStartTime().before(now)) {
			return ReturnResult.FAILUER("活动已开始，不允许修改!");
		}
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = format.parse(startTime);
	    Date endDate = format.parse(endTime);
	    Date snapshotDate = format.parse(snapshotTime);
	    if (!endDate.after(startDate)) {
	    	return ReturnResult.FAILUER("结束时间必须大于开始时间!");
		}
	    if (endDate.after(snapshotDate)) {
	    	return ReturnResult.FAILUER("结束时间不能大于快照时间!");
		}
		activity.setName(name);
	    activity.setStartTime(startDate);
	    activity.setEndTime(endDate);
	    activity.setSnapshotTime(snapshotDate);
	    activity.setMinNetTrade(minNetTrade);
	    activity.setMinPosition(minPosition);
		
	    return adminActivityService.updateNetTradeRankActivity(activity);
	}
	
	/**
 	 * 查询字典定义
 	 */
	@RequestMapping("/admin/netTradeRankStatistic")
	public ModelAndView netTradeRankStatistic(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="id",required=true) Integer activityId
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("activity/netTradeRankStatistic");
		// 定义查询条件
		Pagination<NetTradeRankStatistic> pageParam = new Pagination<NetTradeRankStatistic>(currentPage, numPerPage);
		
		//查询字典定义列表
		Pagination<NetTradeRankStatistic> pagination = adminActivityService.selectNetTradeRankStatisticList(pageParam, activityId);
		
		NetTradeRankActivity activity = adminActivityService.selectNetTradeRankActivityById(activityId);
		modelAndView.addObject("buyShortName", activity.getBuyShortName());
		modelAndView.addObject("sellShortName", activity.getSellShortName());
		modelAndView.addObject("startTime", activity.getStartTime());
		modelAndView.addObject("endTime", activity.getEndTime());
		modelAndView.addObject("activityId", activityId);
		modelAndView.addObject("rankStatisticList", pagination);
		return modelAndView;
	}
	
	// 导出列名
	private static enum ExportNetTradeRankFiled {
		序号, UID, 手机号码, 邮箱号码, 累计买入数量, 累计卖出数量, 净交易数量, 持仓数量;
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
	@RequestMapping("/admin/importNetTradeRank")
	@ResponseBody
	public ReturnResult importNetTradeRank(
			@RequestParam(required = true) Integer activityId,
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
			for (ExportNetTradeRankFiled filed : ExportNetTradeRankFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<NetTradeRankStatistic> pageParam = new Pagination<NetTradeRankStatistic>(currentPage, 100000);
			// 查询用户持仓
			Pagination<NetTradeRankStatistic> pagination = adminActivityService.selectNetTradeRankStatisticList(pageParam, activityId);
			Collection<NetTradeRankStatistic> rankList = pagination.getData();
			for (NetTradeRankStatistic element : rankList) {
				e.createRow(rowIndex++);
				for (ExportNetTradeRankFiled filed : ExportNetTradeRankFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), element.getId());
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

		// e.exportXls(response);
		return ReturnResult.SUCCESS("请从目录(统计管理-报表导出)查看导出任务");
	}
}