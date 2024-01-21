package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolPlanTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.orepool.OrepoolPlan;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminOrepoolService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OrepoolController extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(OrepoolController.class);
	/**
	 * 分页大小
	 */
	private int numPerPage = Constant.adminPageSize;

	@Autowired
	private IAdminOrepoolService adminOrepoolService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;

	/**
	 * 锁仓管理
	 */
	@RequestMapping("/admin/orepoolPlanList")
	public ModelAndView orepoolPlanList(
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderField", required = false, defaultValue = "id") String orderField,
			@RequestParam(value = "orderDirection", required = false, defaultValue = "asc") String orderDirection) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("orepool/orepoolPlanList");
		// 定义查询条件
		Pagination<OrepoolPlan> pageParam = new Pagination<OrepoolPlan>(currentPage, numPerPage);
		OrepoolPlan orepoolPlan = new OrepoolPlan();

		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		// 查询佣金列表
		Pagination<OrepoolPlan> pagination = adminOrepoolService.selectPlanPageList(pageParam, orepoolPlan);

		modelAndView.addObject("orepoolPlanList", pagination);
		return modelAndView;
	}

	/**
	 * 删除锁仓计划
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteOrepoolPlan")
	@ResponseBody
	public ReturnResult deleteOrepoolPlan(
			@RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (orepoolPlan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
			return ReturnResult.FAILUER("请选择非定期计划!");
		}
		// 删除锁仓计划
		return adminOrepoolService.deletePlan(orepoolPlan);
	}
	
	/**
	 * 解锁锁仓计划
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/unlockOrepoolPlan")
	@ResponseBody
	public ReturnResult unlockOrepoolPlan(
			@RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (orepoolPlan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
			return ReturnResult.FAILUER("请选择非定期计划!");
		}
		try {
			// 解锁锁仓计划
			return adminOrepoolService.unlockPlan(orepoolPlan);
		} catch (Exception e) {
			return ReturnResult.FAILUER(e.getMessage());
		}
		
	}

	/**
	 * 新增定期
	 * 
	 * @return
	 */
	@RequestMapping("/admin/addFixedPlan")
	public ModelAndView addFixedPlan() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("orepool/addFixedPlan");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();
		modelAndView.addObject("allCoinType", allCoinType);
		return modelAndView;
	}
	
	/**
	 * 新增活期
	 * 
	 * @return
	 */
	@RequestMapping("/admin/addCurrentPlan")
	public ModelAndView addCurrentPlan() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("orepool/addCurrentPlan");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();
		modelAndView.addObject("allCoinType", allCoinType);
		return modelAndView;
	}
	
	/**
	 * 新增创新区存币
	 * 
	 * @return
	 */
	@RequestMapping("/admin/addInnovationPlan")
	public ModelAndView addInnovationPlan() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("orepool/addInnovationPlan");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();
		modelAndView.addObject("allCoinType", allCoinType);
		return modelAndView;
	}

	/**
	 * 保存定期
	 */
	@RequestMapping("/admin/saveFixedPlan")
	@ResponseBody
	public ReturnResult saveFixedPlan(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "maxAmount", required = true) BigDecimal maxAmount,
			@RequestParam(value = "volume", required = true) BigDecimal volume,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "lockPeriod", required = true) Integer lockPeriod,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		orepoolPlan.setName(name);
		orepoolPlan.setType(OrepoolPlanTypeEnum.fixed_plan.getCode());
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setMaxAmount(maxAmount);
		orepoolPlan.setVolume(volume);
		orepoolPlan.setVisibleVolume(volume);
		orepoolPlan.setLockVolume(BigDecimal.ZERO);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setLockPeriod(lockPeriod);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setStatus(OrepoolPlanStatusEnum.not_start.getCode());
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setCreateTime(new Date());
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.insertPlan(orepoolPlan);
	}
	
	/**
	 * 保存活期
	 */
	@RequestMapping("/admin/saveCurrentPlan")
	@ResponseBody
	public ReturnResult saveCurrentPlan(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		orepoolPlan.setName(name);
		orepoolPlan.setType(OrepoolPlanTypeEnum.current_plan.getCode());
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setLockVolume(BigDecimal.ZERO);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setStatus(OrepoolPlanStatusEnum.not_start.getCode());
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setCreateTime(new Date());
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.insertPlan(orepoolPlan);
	}
	
	/**
	 * 保存创新区存币
	 */
	@RequestMapping("/admin/saveInnovationPlan")
	@ResponseBody
	public ReturnResult saveInnovationPlan(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		orepoolPlan.setName(name);
		orepoolPlan.setType(OrepoolPlanTypeEnum.innovation_plan.getCode());
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setLockVolume(BigDecimal.ZERO);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setStatus(OrepoolPlanStatusEnum.not_start.getCode());
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setCreateTime(new Date());
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.insertPlan(orepoolPlan);
	}

	/**
	 * 读取修改的锁仓计划
	 * 
	 * @return
	 */
	@RequestMapping("/admin/changeOrepoolPlan")
	public ModelAndView changeOrepoolPlan(@RequestParam(value = "id", required = false) Integer id) {
		ModelAndView modelAndView = new ModelAndView();
		if (id > 0) {
			OrepoolPlan orepoolPlan = adminOrepoolService.selectPlanById(id);
			if (orepoolPlan.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
				modelAndView.setViewName("/orepool/changeFixedPlan");
			} else if (orepoolPlan.getType() == OrepoolPlanTypeEnum.current_plan.getCode()) {
				modelAndView.setViewName("/orepool/changeCurrentPlan");
			} else {
				modelAndView.setViewName("/orepool/changeInnovationPlan");
			}
			modelAndView.addObject("orepoolPlan", orepoolPlan);
		}
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();
		modelAndView.addObject("allCoinType", allCoinType);
		return modelAndView;
	}
	
	/**
	 * 修改锁仓计划
	 */
	@RequestMapping("admin/updateOrepoolPlan")
	@ResponseBody
	public ReturnResult updateOrepoolPlan(@RequestParam(value = "id", required = true, defaultValue = "0") Integer id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "type", required = true) Integer type,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "maxAmount", required = true) BigDecimal maxAmount,
			@RequestParam(value = "volume", required = true) BigDecimal volume,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "lockPeriod", required = true) Integer lockPeriod,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (MathUtils.compareTo(orepoolPlan.getLockVolume(), BigDecimal.ZERO) == 1) {
			return ReturnResult.FAILUER("已经有人锁仓的计划不能修改!");
		}
		orepoolPlan.setName(name);
		orepoolPlan.setType(type);
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setMaxAmount(maxAmount);
		orepoolPlan.setVolume(volume);
		orepoolPlan.setVisibleVolume(volume);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setLockPeriod(lockPeriod);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.updateOrepoolPlan(orepoolPlan);
	}
	
	/**
	 * 修改定期锁仓计划
	 */
	@RequestMapping("admin/updateFixedPlan")
	@ResponseBody
	public ReturnResult updateFixedPlan(@RequestParam(value = "id", required = true, defaultValue = "0") Integer id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "maxAmount", required = true) BigDecimal maxAmount,
			@RequestParam(value = "volume", required = true) BigDecimal volume,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "lockPeriod", required = true) Integer lockPeriod,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (orepoolPlan.getType() != OrepoolPlanTypeEnum.fixed_plan.getCode()) {
			return ReturnResult.FAILUER("请选择定期计划进行修改!");
		}
		if (MathUtils.compareTo(orepoolPlan.getLockVolume(), BigDecimal.ZERO) == 1) {
			return ReturnResult.FAILUER("已经有人锁仓的计划不能修改!");
		}
		orepoolPlan.setName(name);
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setMaxAmount(maxAmount);
		orepoolPlan.setVolume(volume);
		orepoolPlan.setVisibleVolume(volume);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setLockPeriod(lockPeriod);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.updateOrepoolPlan(orepoolPlan);
	}
	
	/**
	 * 修改活期锁仓计划
	 */
	@RequestMapping("admin/updateCurrentPlan")
	@ResponseBody
	public ReturnResult updateCurrentPlan(@RequestParam(value = "id", required = true, defaultValue = "0") Integer id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (orepoolPlan.getType() != OrepoolPlanTypeEnum.current_plan.getCode()) {
			return ReturnResult.FAILUER("请选择活期计划进行修改!");
		}
		if (MathUtils.compareTo(orepoolPlan.getLockVolume(), BigDecimal.ZERO) == 1) {
			return ReturnResult.FAILUER("已经有人锁仓的计划不能修改!");
		}
		orepoolPlan.setName(name);
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.updateOrepoolPlan(orepoolPlan);
	}
	
	/**
	 * 修改创新区锁仓计划
	 */
	@RequestMapping("admin/updateInnovationPlan")
	@ResponseBody
	public ReturnResult updateInnovationPlan(@RequestParam(value = "id", required = true, defaultValue = "0") Integer id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lockCoinId", required = true) Integer lockCoinId,
			@RequestParam(value = "incomeCoinId", required = true) Integer incomeCoinId,
			@RequestParam(value = "minAmount", required = true) BigDecimal minAmount,
			@RequestParam(value = "incomeRate", required = true) BigDecimal incomeRate,
			@RequestParam(value = "incomePeriod", required = true) Integer incomePeriod,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "sort", required = true) Integer sort) throws Exception {
		OrepoolPlan orepoolPlan = new OrepoolPlan();
		if (id > 0) {
			orepoolPlan = adminOrepoolService.selectPlanById(id);
		}
		if (orepoolPlan == null) {
			return ReturnResult.FAILUER("计划不存在!");
		}
		if (orepoolPlan.getType() != OrepoolPlanTypeEnum.innovation_plan.getCode()) {
			return ReturnResult.FAILUER("请选择创新区计划进行修改!");
		}
		if (MathUtils.compareTo(orepoolPlan.getLockVolume(), BigDecimal.ZERO) == 1) {
			return ReturnResult.FAILUER("已经有人锁仓的计划不能修改!");
		}
		orepoolPlan.setName(name);
		orepoolPlan.setLockCoinId(lockCoinId);
		orepoolPlan.setIncomeCoinId(incomeCoinId);
		orepoolPlan.setMinAmount(minAmount);
		orepoolPlan.setIncomeRate(incomeRate);
		orepoolPlan.setIncomePeriod(incomePeriod);
		orepoolPlan.setSort(sort);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		orepoolPlan.setStartTime(format.parse(startTime));
		orepoolPlan.setEndTime(format.parse(endTime));
		orepoolPlan.setUpdateTime(new Date());
		return adminOrepoolService.updateOrepoolPlan(orepoolPlan);
	}

	@RequestMapping("/admin/openOrepoolPlan")
	@ResponseBody
	public ReturnResult openOrepoolPlan(@RequestParam(value = "id", required = true) Integer id) {
		OrepoolPlan plan = adminOrepoolService.selectPlanById(id);
		if (plan == null) {
			return ReturnResult.FAILUER("矿池计划不存在!");
		}
		if (plan.getStatus() != OrepoolPlanStatusEnum.forbid.getCode()) {
			return ReturnResult.FAILUER("矿池计划不处于禁用状态!");
		}
		// 发放佣金
		return adminOrepoolService.openOrepoolPlan(plan);
	}

	@RequestMapping("/admin/forbidOrepoolPlan")
	@ResponseBody
	public ReturnResult forbidOrepoolPlan(@RequestParam(value = "id", required = true) Integer id) {
		OrepoolPlan plan = adminOrepoolService.selectPlanById(id);
		if (plan == null) {
			return ReturnResult.FAILUER("矿池计划不存在!");
		}
		if (plan.getStatus() == OrepoolPlanStatusEnum.forbid.getCode()) {
			return ReturnResult.FAILUER("矿池计划已处于禁用状态!");
		}
		// 发放佣金
		return adminOrepoolService.forbidOrepoolPlan(plan);
	}

	@RequestMapping("/admin/orepoolRecordList")
	public ModelAndView orepoolRecordList(
			@RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
			@RequestParam(value = "lockCoinId", required = false, defaultValue = "0") Integer lockCoinId,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage,
			@RequestParam(value = "orderField", required = false, defaultValue = "id") String orderField,
			@RequestParam(value = "orderDirection", required = false, defaultValue = "asc") String orderDirection) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("orepool/orepoolRecordList");
		// 查询所有币种
		List<SystemCoinType> allCoinType = redisHelper.getCoinTypeListAll();

		// 定义查询条件
		Pagination<OrepoolRecord> pageParam = new Pagination<OrepoolRecord>(currentPage, numPerPage);
		OrepoolRecord orepoolRecord = new OrepoolRecord();
		if (userId > 0) {
			orepoolRecord.setUserId(userId);
			modelAndView.addObject("userId", userId);
		}
		if (lockCoinId > 0) {
			orepoolRecord.setLockCoinId(lockCoinId);
			modelAndView.addObject("lockCoinId", lockCoinId);
		}
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		if (userId == 0 && lockCoinId == 0) {
			modelAndView.addObject("allCoinType", allCoinType);
			return modelAndView;
		}
		// 查询佣金列表
		Pagination<OrepoolRecord> pagination = adminOrepoolService.selectRecordPageList(pageParam, orepoolRecord);

		modelAndView.addObject("allCoinType", allCoinType);
		modelAndView.addObject("orepoolRecordList", pagination);
		return modelAndView;
	}

	// 导出列名
	private static enum ExportRecordFiled {
		序号, 锁仓项目, 用户UID, 锁仓币种, 奖励币种, 锁仓类型, 锁仓数量, 锁仓时长, 收益周期, 已获得收益数量;
	}

	@RequestMapping("/admin/importOrepoolRecord")
	@ResponseBody
	public ReturnResult importOrepoolRecord(
			@RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
			@RequestParam(value = "lockCoinId", required = false, defaultValue = "0") Integer lockCoinId,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer currentPage) {

		final String tableName = "锁仓记录";
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
			//TODO huanjinfeng
			int rowIndex = 0;

			// header
			e.createRow(rowIndex++);
			for (ExportRecordFiled filed : ExportRecordFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<OrepoolRecord> pageParam = new Pagination<OrepoolRecord>(currentPage, 100000);
			OrepoolRecord orepoolRecord = new OrepoolRecord();
			if (userId > 0) {
				orepoolRecord.setUserId(userId);
			}
			if (lockCoinId > 0) {
				orepoolRecord.setLockCoinId(lockCoinId);
			}

			// 查询用户持仓
			Pagination<OrepoolRecord> pagination = adminOrepoolService.selectRecordPageList(pageParam, orepoolRecord);
			Collection<OrepoolRecord> recordList = pagination.getData();
			for (OrepoolRecord element : recordList) {
				e.createRow(rowIndex++);
				for (ExportRecordFiled filed : ExportRecordFiled.values()) {
					switch (filed) {
					case 序号:
						e.setCell(filed.ordinal(), element.getId());
						break;
					case 锁仓项目:
						e.setCell(filed.ordinal(), element.getName());
						break;
					case 用户UID:
						e.setCell(filed.ordinal(), element.getUserId());
						break;
					case 锁仓币种:
						e.setCell(filed.ordinal(), element.getLockCoinName());
						break;
					case 奖励币种:
						e.setCell(filed.ordinal(), element.getIncomeCoinName());
						break;
					case 锁仓类型:
						if (element.getType() == OrepoolPlanTypeEnum.fixed_plan.getCode()) {
							e.setCell(filed.ordinal(), "定期计划");
						} else {
							e.setCell(filed.ordinal(), "活期计划");
						}
						break;
					case 锁仓数量:
						e.setCell(filed.ordinal(), element.getLockVolume().doubleValue());
						break;
					case 锁仓时长:
						e.setCell(filed.ordinal(), element.getLockPeriod());
						break;
					case 收益周期:
						e.setCell(filed.ordinal(), element.getIncomePeriod());
						break;
					case 已获得收益数量:
						e.setCell(filed.ordinal(), element.getProfit().doubleValue());
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
