package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

import com.alibaba.fastjson.JSON;
import com.hotcoin.coin.utils.CoinRpcUtlis;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.WebConstant;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.coin.CoinDriver;
import com.qkwl.common.coin.CoinDriverFactory;
import com.qkwl.common.dto.Enum.CapitalOperationOutStatus;
import com.qkwl.common.dto.Enum.DataSourceEnum;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationInStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationOutStatusEnum;
import com.qkwl.common.dto.Enum.VirtualCapitalOperationTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.rpc.admin.IAdminUserExtendService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.CoinCommentUtils;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtil;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

@Controller
public class VirtualCapitaloperationController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(VirtualCapitaloperationController.class);
	
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private IAdminUserExtendService adminUserExtendService;
	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;
    @Autowired
    private IAdminExcelExportTaskService adminExcelExportTaskService;
    @Resource(name="taskExecutor")
    private Executor executor;
    @Value("${excel.path}")
    private String  excelRootPath;
    @Autowired
	private ValidateHelper validateHelper;

	/**
	 * 虚拟币充值提现列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitaloperationList")
	public ModelAndView Index(
			@RequestParam(value = "fcoinid", required = false) Integer fcoinid,
			@RequestParam(value = "type", defaultValue = "0") Integer ftype,
			@RequestParam(value = "status", defaultValue =  "-1") Integer status,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection,
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualCapitaloperationList");
		
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> statusList = new ArrayList<>();

		//如果没有关键字，没有coinid，没有开始时间就设置开始时间为前一个月
		if (StringUtils.isEmpty(keyWord) && (fcoinid == null || fcoinid <= 0 ) && StringUtils.isEmpty(logDate)) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.MONTH, -1);
			 logDate = DateUtils.format(calendar.getTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
		}
		
		
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
			logDate = null;
		}
		// 虚拟币ID
		if (fcoinid !=null && fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
			modelAndView.addObject("fcoinid", fcoinid);
		} 
		// 类型提现or充值
		if (ftype > 0) {
			filterParam.setFtype(ftype);
			modelAndView.addObject("type", ftype);
		}
		// 开始时间
		if (!StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}
		if (status >= 0 ){
			statusList.add(status);
			modelAndView.addObject("status", status);
		}

		// 页面参数
		Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
		coinMap.put(0, "全部");
		modelAndView.addObject("coinMap", coinMap);

		// 页面参数
		List<SystemCoinType> type = redisHelper.getCoinTypeList();
		Map<Integer, String> urlMap = new HashMap<Integer, String>();
		
		Map<Integer, String> addressUrlMap = new HashMap<Integer, String>();
		
		for (SystemCoinType coin : type) {
			addressUrlMap.put(coin.getId(), coin.getAddressUrl());
			urlMap.put(coin.getId(), coin.getExplorerUrl());
		}
		modelAndView.addObject("urlMap", urlMap);
		modelAndView.addObject("addressUrlMap", addressUrlMap);
		

		Map<Integer, String> statusMap = new HashMap<>();
		statusMap.put(-1, "全部");
		if(ftype == 1){
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_0, "0/项确认");
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_1, "1/项确认");
			statusMap.put(VirtualCapitalOperationInStatusEnum.WAIT_2, "2/项确认");
			statusMap.put(VirtualCapitalOperationInStatusEnum.SUCCESS, "充值成功");
		}else{
			statusMap.put(VirtualCapitalOperationOutStatusEnum.WaitForOperation, "等待提现");
			statusMap.put(VirtualCapitalOperationOutStatusEnum.OperationLock, "正在处理");
			statusMap.put(VirtualCapitalOperationOutStatusEnum.OperationSuccess, "提现成功");
			statusMap.put(VirtualCapitalOperationOutStatusEnum.Cancel, "用户撤销");
			statusMap.put(VirtualCapitalOperationOutStatusEnum.LockOrder, "锁定");
		}
		modelAndView.addObject("statusMap", statusMap);

		// 查询
		Pagination<FVirtualCapitalOperationDTO> pagination = adminUserCapitalService.selectVirtualCapitalOperationList(
				pageParam, filterParam, statusList, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("virtualCapitaloperationList", pagination);
		return modelAndView;
	}

	/**
	 * 待审核虚拟币提现列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutList")
	public ModelAndView virtualCapitalOutList(
			@RequestParam(value = "fcoinid", defaultValue = "0") Integer fcoinid,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "logDate", required=false) String logDate,
			@RequestParam(value = "endDate", required=false) String endDate,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualCapitalOutList");
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, numPerPage);
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);
		// 开始时间
		if (!StringUtils.isEmpty(logDate)) {
			modelAndView.addObject("logDate", logDate);
			pageParam.setBegindate(logDate);
		}
		// 结束时间
		if (!StringUtils.isEmpty(endDate)) {
			modelAndView.addObject("endDate", endDate);
			pageParam.setEnddate(endDate);
		}
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		filterParam.setFtype(VirtualCapitalOperationTypeEnum.COIN_OUT.getCode());
		status.add(VirtualCapitalOperationOutStatusEnum.WaitForOperation);
		status.add(VirtualCapitalOperationOutStatusEnum.OperationLock);
		status.add(VirtualCapitalOperationOutStatusEnum.LockOrder);

		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 虚拟币ID
		if (fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
		} 
		modelAndView.addObject("fcoinid", fcoinid);

		// 页面参数
		Map<Integer, String> coinMap = redisHelper.getCoinTypeNameMap();
		coinMap.put(0, "全部");
		modelAndView.addObject("coinMap", coinMap);
		
		List<SystemCoinType> coinTypeListAll = redisHelper.getCoinTypeListAll();
		Map<Integer, String> addressUrlMap = coinTypeListAll.parallelStream().collect(Collectors.toMap(SystemCoinType::getId,a -> StringUtils.isEmpty(a.getAddressUrl()) ? "" : a.getAddressUrl()));
		modelAndView.addObject("addressUrlMap", addressUrlMap);
		
		// 查询
		Pagination<FVirtualCapitalOperationDTO> pagination = adminUserCapitalService.selectVirtualCapitalOperationList(
				pageParam, filterParam, status, isvip6.equals("on"));
		modelAndView.addObject("isvip6", isvip6.equals("on"));
		modelAndView.addObject("virtualCapitaloperationList", pagination);
		return modelAndView;
	}

	/**
	 * 审核虚拟币提现-弹出确定框
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/goVirtualCapitaloperationJSP")
	public ModelAndView goVirtualCapitaloperationJSP() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		String url = request.getParameter("url");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);
		if (request.getParameter("uid") != null) {
			int fid = Integer.parseInt(request.getParameter("uid"));
			FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
			modelAndView.addObject("virtualCapitaloperation", virtualCapitaloperation);
		}

		List<SystemCoinType> type = redisHelper.getCoinTypeCoinList();
		Map<Integer, String> coinMap = new LinkedHashMap<>();
		for (SystemCoinType coin : type) {
			if(coin.getIsInnovateAreaCoin()) {
				coinMap.put(coin.getId(), coin.getName() + "(创新区)");
			}else {
				coinMap.put(coin.getId(), coin.getName());
			}
			
		}
		modelAndView.addObject("coinMap", coinMap);
		return modelAndView;
	}
	
	/**
	 * 审核虚拟币提现订单-最后确定
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutAudit")
	@ResponseBody
	public ReturnResult virtualCapitalOutAudit() {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int fid = Integer.parseInt(request.getParameter("uid"));
		FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		// 检测状态
		int status = virtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationLock);
			return ReturnResult.FAILUER("审核失败,只有状态为:" + status_s + "的提现记录才允许审核!");
		}
		//钱包密码
		String walletPass = request.getParameter("fpassword");
		if (StringUtils.isEmpty(walletPass) ) {
			return ReturnResult.FAILUER("请输入钱包密码！");
		}
		if(walletPass.startsWith("\r") || walletPass.startsWith("\n") || walletPass.endsWith("\r") || walletPass.endsWith("\n")) {
			return ReturnResult.FAILUER("密码携带换行符");
		}
		// 检测用户钱包余额
		int userId = virtualCapitaloperation.getFuid();
		int coinTypeId = virtualCapitaloperation.getFcoinid();
		UserCoinWallet virtualWalletInfo = adminUserCapitalService.selectUserVirtualWallet(userId, virtualCapitaloperation.getWalletCoinId());
		BigDecimal amount = MathUtils.add(MathUtils.add(virtualCapitaloperation.getFamount(),
				virtualCapitaloperation.getFfees()),virtualCapitaloperation.getFbtcfees());
		BigDecimal frozenRmb = virtualWalletInfo.getFrozen();
		if (MathUtils.sub(frozenRmb, amount).compareTo(BigDecimal.ZERO) < 0) {
			return ReturnResult.FAILUER("审核失败,冻结数量:" + frozenRmb + "小于提现数量:" + amount + "，操作异常!");
		}
		SystemCoinType virtualCoinType = redisHelper.getCoinTypeSystem(coinTypeId);
		if(virtualCoinType == null){
			return ReturnResult.FAILUER(String.format("审核失败,SystemCoinType未不到,id=%s",coinTypeId));
		}
		if(!virtualCoinType.getIsWithdraw()){
			return ReturnResult.FAILUER("审核失败," + virtualCoinType.getName() + "已禁止提现!");
		}
		//地址检测
		if(!CoinCommentUtils.isLegitimateAddress(virtualCoinType.getCoinType(),virtualCapitaloperation.getFwithdrawaddress())) {
			return ReturnResult.FAILUER("提现地址错误，请驳回");
		}
		
		
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		
		//是否平台互转
		int addressNum = 0;
		if(!virtualCoinType.getCoinType().equals(SystemCoinSortEnum.EOS.getCode()) 
				&& !virtualCoinType.getCoinType().equals(SystemCoinSortEnum.BTS.getCode()) 
				&& !virtualCoinType.getCoinType().equals(SystemCoinSortEnum.XRP.getCode())){
			addressNum = adminUserCapitalService.selectAddressNum(virtualCapitaloperation.getFwithdrawaddress());
		}else {
			if(virtualCoinType.getCoinType().equals(SystemCoinSortEnum.XRP.getCode()) && !StringUtils.isNumeric(virtualCapitaloperation.getMemo())) {
				return ReturnResult.FAILUER("提现地址标签只能是数字，请驳回");
			}
			//如果转到我们普通的提现地址
			if(virtualCapitaloperation.getFwithdrawaddress().equals(virtualCoinType.getEthAccount())) {
				addressNum = 1;
			}
		}
		if(addressNum > 0) {
			//平台互转
			return capitalPlatformExchange(admin, virtualCoinType, virtualCapitaloperation);
		}else if(virtualCoinType.getUseNewWay()) {
			//委托钱包机处理
			return capitalNewWay(admin, virtualCoinType, virtualCapitaloperation);
		}else {
			//直连钱包节点处理
			return capitalOldWay(admin, virtualCoinType, virtualCapitaloperation, walletPass);
		}
			
		
		
	}
	//处理提现，平台互转
	private ReturnResult capitalPlatformExchange(FAdmin admin,SystemCoinType virtualCoinType,FVirtualCapitalOperationDTO virtualCapitaloperation) {
		boolean flag = false;
		try {
			//先把订单状态改成锁定
			virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
			virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
			virtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("审核失败,订单锁定失败,请稍后再试!");
			}
			//执行转账
			flag = adminUserCapitalService.platformExchange(virtualCapitaloperation.getFid(), admin.getFid(), virtualCoinType);
		} catch (Exception e) {
			logger.info("虚拟币提现异常,recordid:"+virtualCapitaloperation.getFid(),e);
			/*//转账失败也没关系，解锁订单
			virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
			virtualCapitaloperation.setFadminid(admin.getFid());
			try {
				flag = adminUserCapitalService.updateVirtualCapital(admin, virtualCapitaloperation);
			} catch (Exception e2) {
				logger.error("更改提现订单状态失败",e);
			}*/
			return ReturnResult.FAILUER(e.getMessage());
		}
		if (!flag) {
			return ReturnResult.FAILUER("更新审核失败");
		}
		return ReturnResult.SUCCESS("审核成功");
	}
	
	//处理提现，旧方式处理，需要直连钱包节点查余额
	private ReturnResult capitalOldWay(FAdmin admin,SystemCoinType virtualCoinType,FVirtualCapitalOperationDTO virtualCapitaloperation,String walletPass) {
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(virtualCoinType.getCoinType(), virtualCoinType.getWalletLink(), virtualCoinType.getChainLink())
				.accessKey(virtualCoinType.getAccessKey())
				.secretKey(virtualCoinType.getSecrtKey())
				.pass(walletPass)
				.assetId(virtualCoinType.getAssetId())
				.sendAccount(virtualCoinType.getEthAccount())
				.contractAccount(virtualCoinType.getContractAccount())
				.contractWei(virtualCoinType.getContractWei())
				.contractAccount(virtualCoinType.getContractAccount())
				.shortName(virtualCoinType.getShortName())
				.walletAccount(virtualCoinType.getWalletAccount())
				.builder()
				.getDriver();
		try {
			BigDecimal balance = coinDriver.getBalance();
			if(balance == null){
				return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
			}
			if (balance.compareTo(virtualCapitaloperation.getFamount()) < 0) {
				return ReturnResult.FAILUER("查询余额失败，钱包余额：" + balance + "小于" + virtualCapitaloperation.getFamount());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
		}
		
		boolean flag = false;
		try {
			//先把订单状态改成锁定
			virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
			virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
			virtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("审核失败,订单锁定失败,请稍后再试!");
			}
			//转账
			flag = adminUserCapitalService.virtualCapitalWithdraw(virtualCapitaloperation.getFid(), admin.getFid(), coinDriver);
		} catch (Exception e) {
			//如果是因为密码错误或者nonce too low的问题导致审核失败的就回滚状态
			if(e instanceof BCException) {
				Integer code = ((BCException) e).getCode();
				if(code != null && (code.equals(Integer.valueOf(403)) || code.equals(Integer.valueOf(409)))) {
					virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
					virtualCapitaloperation.setFadminid(admin.getFid());
					virtualCapitaloperation.setVersion(virtualCapitaloperation.getVersion() + 1);
					try {
						flag = adminUserCapitalService.updateVirtualCapital(admin, virtualCapitaloperation);
					} catch (Exception e2) {
						logger.error("更改提现订单状态失败",e);
					}
				}
			}else {
				logger.error("虚拟币提现异常,recordid:"+virtualCapitaloperation.getFid(),e);
			}
			return ReturnResult.FAILUER(e.getMessage());
		}
		if (!flag) {
			return ReturnResult.FAILUER("更新审核失败");
		}
		return ReturnResult.SUCCESS("审核成功");
	}
	
	//处理提现，新方式，需要去钱包机查余额
	private ReturnResult capitalNewWay(FAdmin admin,SystemCoinType virtualCoinType,FVirtualCapitalOperationDTO virtualCapitaloperation) {
		try {
			BigDecimal balance = CoinRpcUtlis.getBalance(virtualCoinType);
			if(balance == null){
				return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
			}
			if (balance.compareTo(virtualCapitaloperation.getFamount()) < 0) {
				return ReturnResult.FAILUER("查询余额失败，钱包余额：" + balance + "小于" + virtualCapitaloperation.getFamount());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
		}
		boolean flag = false;
		try {
			//先把订单状态改成锁定
			virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
			virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
			virtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("审核失败,订单锁定失败,请稍后再试!");
			}
			//转账
			flag = adminUserCapitalService.virtualCapitalWithdraw(virtualCapitaloperation.getFid(), admin.getFid(), virtualCoinType);
		} catch (Exception e) {
			logger.info("虚拟币提现异常,recordid:"+virtualCapitaloperation.getFid(),e);
			return ReturnResult.FAILUER("虚拟币提现异常," + e.getMessage());
		}
		if (!flag) {
			return ReturnResult.FAILUER("更新审核失败");
		}
		return ReturnResult.SUCCESS("审核成功");
	}
	
	
	/**
	 * 审核虚拟币提现列表-提现加速
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutAuditAccelerate")
	@ResponseBody
	public ReturnResult virtualCapitalOutAuditAccelerate() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int fid = Integer.parseInt(request.getParameter("uid"));
		FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		// 检测状态
		int status = virtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder) {
			return ReturnResult.FAILUER("加速失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		if (status != VirtualCapitalOperationOutStatusEnum.OperationSuccess) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationSuccess);
			return ReturnResult.FAILUER("加速失败,只有状态为:" + status_s + "的提现记录才允许加速!");
		}
		
		int coinTypeId = virtualCapitaloperation.getFcoinid();
		SystemCoinType virtualCoinType = redisHelper.getCoinTypeSystem(coinTypeId);
		if(virtualCoinType == null || virtualCoinType.getCoinType() != SystemCoinSortEnum.ETH.getCode()) {
			return ReturnResult.FAILUER("当前仅支持以太类加速");
		}
		
		//钱包密码
		String walletPass = request.getParameter("fpassword");
		if (walletPass == null || walletPass.equals("") || walletPass.length() <= 0) {
			return ReturnResult.FAILUER("请输入钱包密码！");
		}
		
		String gasPrice = null;
		boolean  isUseNewNonce = false;
		String maxGasPrice = redisHelper.getSystemArgs(ArgsConstant.ETH_MAX_GASPRICE);
		BigDecimal maxGasPriceLimit = new BigDecimal("100000000000");//默认100gwei
		if(!StringUtils.isEmpty(maxGasPrice)) {
			maxGasPriceLimit = new BigDecimal(maxGasPrice);
		}
		try {
			String parameter = request.getParameter("gasPrice");
			BigDecimal b = new BigDecimal(parameter).setScale(2);
			BigDecimal multiply = b.multiply(new BigDecimal("1000000000"));
			if(multiply.compareTo(maxGasPriceLimit) > 0) {
				return ReturnResult.FAILUER("超过风控阈值，操作失败");
			}
			
			gasPrice = "0x" + Long.toHexString(multiply.longValue());
			String nonceConfirm = request.getParameter("nonceConfirm");
			if(!StringUtils.isEmpty(nonceConfirm)) {
				isUseNewNonce = true;
			}
		} catch (Exception e) {
			return ReturnResult.FAILUER("gwei只能是数字");
		}
		
		//是否平台互转
		int addressNum = adminUserCapitalService.selectAddressNum(virtualCapitaloperation.getFwithdrawaddress());
		if(addressNum > 0) {
			return ReturnResult.FAILUER("平台互转不做加速");
		}

		BigDecimal amount = MathUtils.add(MathUtils.add(virtualCapitaloperation.getFamount(),
				virtualCapitaloperation.getFfees()),virtualCapitaloperation.getFbtcfees());
		// get CoinDriver
		CoinDriver coinDriver = new CoinDriverFactory.Builder(virtualCoinType.getCoinType(), virtualCoinType.getWalletLink(), virtualCoinType.getChainLink())
				.accessKey(virtualCoinType.getAccessKey())
				.secretKey(virtualCoinType.getSecrtKey())
				.pass(walletPass)
				.assetId(virtualCoinType.getAssetId())
				.sendAccount(virtualCoinType.getEthAccount())
				.contractAccount(virtualCoinType.getContractAccount())
				.contractWei(virtualCoinType.getContractWei())
				.shortName(virtualCoinType.getShortName())
				.walletAccount(virtualCoinType.getWalletAccount())
				.builder()
				.getDriver();
		String transactionByHash = coinDriver.getTransactionByHash(virtualCapitaloperation.getFuniquenumber());
		if(!StringUtils.isEmpty(transactionByHash) && !transactionByHash.equals("null")) {
			return ReturnResult.FAILUER("该交易已被记录，无需加速");
		}
		try {
			BigDecimal balance = coinDriver.getBalance();
			if(balance == null){	
				return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
			}
			if (balance.compareTo(amount) < 0) {
				return ReturnResult.FAILUER("查询余额失败，钱包余额：" + balance + "小于" + amount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("查询余额失败，钱包连接失败");
		}

		// 设置订单状态和时间
		virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
		virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
		// 管理员ID
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		virtualCapitaloperation.setFadminid(admin.getFid());
		boolean flag = false;
		try {
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("加速失败,订单锁定失败,请稍后再试!");
			}
			flag = adminUserCapitalService.updateVirtualCapitalAccelerate(virtualCapitaloperation.getFid(), admin.getFid(), amount,gasPrice,isUseNewNonce, coinDriver);
		} catch (Exception e) {
			logger.error("加速失败 {admin:"+admin+",recordId:"+virtualCapitaloperation.getFid()+",amount:"+amount+",addressNum:"+addressNum+"}",e);
			return ReturnResult.FAILUER("加速失败");
		}
		if (!flag) {
			return ReturnResult.FAILUER("加速失败");
		}
		return ReturnResult.SUCCESS("加速成功");
	}
	
	/**
	 * 审核虚拟币提现列表-手动完成，用于手动完成转账操作
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/virtualCapitalOutAuditSuccess")
	@ResponseBody
	public ReturnResult virtualCapitalOutAuditSuccess(
			@RequestParam(value = "uid", required = true) Integer fid,
			@RequestParam(value = "txhash", required = true) String txhash
			) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		FVirtualCapitalOperationDTO virtualCapitaloperation = adminUserCapitalService.selectVirtualById(fid);
		// 检测状态
		if(virtualCapitaloperation == null) {
			return ReturnResult.FAILUER("操作失败,委单不存在");
		}
		int status = virtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.OperationSuccess || status == VirtualCapitalOperationOutStatusEnum.Cancel) {
			String status_s = VirtualCapitalOperationOutStatusEnum.getEnumString(
					VirtualCapitalOperationOutStatusEnum.OperationSuccess);
			return ReturnResult.FAILUER("操作失败，该笔提现状态已结束");
		}
		//是否平台互转
		int addressNum = adminUserCapitalService.selectAddressNum(virtualCapitaloperation.getFwithdrawaddress());
		if(addressNum > 0) {
			return ReturnResult.FAILUER("平台互转不允许操作");
		}
		
		
		// 设置订单状态和时间
		virtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.LockOrder);
		virtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
		// 管理员ID
		FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
		virtualCapitaloperation.setFadminid(admin.getFid());
		
		BigDecimal amount = MathUtils.add(MathUtils.add(virtualCapitaloperation.getFamount(),
				virtualCapitaloperation.getFfees()),virtualCapitaloperation.getFbtcfees());
		
		
		boolean flag = false;
		try {
			flag = adminUserCapitalService.updateVirtualCapital(admin,virtualCapitaloperation);
			if(!flag){
				return ReturnResult.FAILUER("加速失败,订单锁定失败,请稍后再试!");
			}
			flag = adminUserCapitalService.updateVirtualCapitalSuccessful(virtualCapitaloperation.getFid(), admin.getFid(), amount, txhash);
			
			FUser user = adminUserService.selectById(virtualCapitaloperation.getFuid());
			FUserExtend userExtend = adminUserExtendService.selectByUid(virtualCapitaloperation.getFuid());
			Integer langCode = 1;
			if (userExtend != null) {
				langCode = LocaleEnum.getCodeByName(userExtend.getLanguage());
			}
			// 发送短信
			if (user.getFistelephonebind()) {
				validateHelper.smsCoinToAccount(user.getFareacode(), user.getFtelephone(), PlatformEnum.BC.getCode(),
						BusinessTypeEnum.SMS_WITHDRAW_TO_ACCOUNT.getCode(), virtualCapitaloperation.getFamount(), virtualCapitaloperation.getFcoinname(), langCode);
			}
		} catch (Exception e) {
			logger.error("手动完成失败 virtualCapitaloperation.fid:{},e:{}",virtualCapitaloperation.getFid(),e);
			if(e instanceof BCException) {
				return ReturnResult.FAILUER("手动完成失败"+e.getMessage());
			}
			return ReturnResult.FAILUER("手动完成失败");
		}
		if (!flag) {
			return ReturnResult.FAILUER("手动完成失败");
		}
		return ReturnResult.SUCCESS("手动完成成功");
	}
	
	/**
	 * 修改订单状态
	 */
	@RequestMapping("admin/goVirtualCapitaloperationChangeStatus")
	@ResponseBody
	public ReturnResult goVirtualCapitaloperationChangeStatus() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		int type = Integer.valueOf(request.getParameter("type"));
		int uid = Integer.valueOf(request.getParameter("uid"));
		FVirtualCapitalOperationDTO fvirtualCapitaloperation = adminUserCapitalService.selectVirtualById(uid);
		fvirtualCapitaloperation.setFupdatetime(Utils.getTimestamp());
		int status = fvirtualCapitaloperation.getFstatus();
		if (status == VirtualCapitalOperationOutStatusEnum.LockOrder && type != 4) {
			return ReturnResult.FAILUER("审核失败,订单锁定中,请联系技术人员确定订单状态!");
		}
		String tips = "";
		switch (type) {
			case 1:
				tips = "锁定";
				if (status != VirtualCapitalOperationOutStatusEnum.WaitForOperation) {
					String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.WaitForOperation);
					return ReturnResult.FAILUER("锁定失败,只有状态为:‘" + status_s + "’的充值记录才允许锁定!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
				break;
			case 2:
				tips = "取消锁定";
				if (status != VirtualCapitalOperationOutStatusEnum.OperationLock) {
					String status_s = CapitalOperationOutStatus.getEnumString(CapitalOperationOutStatus.OperationLock);
					return ReturnResult.FAILUER("取消锁定失败,只有状态为:‘" + status_s + "’的充值记录才允许取消锁定!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.WaitForOperation);
				break;
			case 3:
				tips = "取消提现";
				if (status == VirtualCapitalOperationOutStatusEnum.Cancel) {
					return ReturnResult.FAILUER("取消提现失败,该记录已处于取消状态!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.Cancel);
				break;
			case 4:
				tips = "恢复提现";
				if (status != VirtualCapitalOperationOutStatusEnum.LockOrder) {
					return ReturnResult.FAILUER("恢复提现失败,只有状态为:锁定的充值记录才允许恢复提现!");
				}
				fvirtualCapitaloperation.setFstatus(VirtualCapitalOperationOutStatusEnum.OperationLock);
				break;
		}

		boolean flag = false;
		try {	
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			fvirtualCapitaloperation.setFadminid(admin.getFid());
			flag = adminUserCapitalService.updateVirtualCapital(admin, fvirtualCapitaloperation);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("未知错误，请刷新列表后重试！");
		}
		if (flag) {
			return ReturnResult.SUCCESS(tips + "成功！");
		} else {
			return ReturnResult.FAILUER("未知错误，请刷新列表后重试！");
		}
	}

	/**
	 * 虚拟币手工充值订单
	 */
	@RequestMapping("admin/virtualCoinRechargeOrderByWork")
	@ResponseBody
	public ReturnResult virtualCoinRechargeOrderByWork(
			@RequestParam(value = "fuid", required = true) Integer fuid,
			@RequestParam(value = "fcoinid", required = true) Integer fcoinid,
			@RequestParam(value = "famount", required = true) BigDecimal famount,
			@RequestParam(value = "frechargeaddress", required = true) String frechargeaddress,
			@RequestParam(value = "funiquenumber", required = true) String funiquenumber,
			@RequestParam(value = "fblocknumber", required = true) BigInteger fblocknumber
	){
		FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
		FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();

		operation.setFadminid(admin.getFid());
		operation.setFadminname(admin.getFname());

		FUser user = adminUserService.selectById(fuid);
		if(user == null){
			return ReturnResult.FAILUER("未找到此用户！");
		}
		
		SystemCoinType coinType = redisHelper.getCoinType(fcoinid);
		if(coinType == null) {
			return ReturnResult.FAILUER("币种不存在");
		}
		
		Integer walletCoinId = coinType.getId();
		if(coinType.getIsSubCoin()) {
			try {
				walletCoinId = Integer.valueOf(coinType.getLinkCoin());
			} catch (Exception e) {
				logger.error("创建充值订单异常",e);
				return ReturnResult.FAILUER("币种配置错误");
			}
		}
		
		operation.setIsFrozen(coinType.getIsInnovateAreaCoin());
		operation.setFuid(fuid);
		operation.setFnickname(user.getFnickname());
		operation.setFrealname(user.getFrealname());
		operation.setFcoinid(fcoinid);
		operation.setFamount(famount);
		operation.setFrechargeaddress(frechargeaddress);
		operation.setFuniquenumber(funiquenumber);
		operation.setFblocknumber(fblocknumber);
		operation.setFconfirmations(0);
		operation.setFstatus(VirtualCapitalOperationInStatusEnum.WAIT_0);
		operation.setFtype(VirtualCapitalOperationTypeEnum.COIN_IN.getCode());
		operation.setFsource(DataSourceEnum.WEB.getCode());
		operation.setFbtcfees(BigDecimal.ZERO);
		operation.setFfees(BigDecimal.ZERO);
		operation.setFagentid(WebConstant.BCAgentId);
		operation.setFnonce(0);
		operation.setFhasowner(true);
		operation.setFplatform(PlatformEnum.BC.getCode());
		operation.setFcreatetime(new Date());
		operation.setFupdatetime(new Date());
		operation.setVersion(0);
		operation.setWalletCoinId(walletCoinId);

		try{
			Result result = adminUserCapitalService.insertRecharge(operation);
			if(result.getSuccess()){
				return ReturnResult.SUCCESS("增加虚拟币充值订单成功！");
			}
			return ReturnResult.FAILUER(result.getMsg());
		}catch (Exception e){
			logger.error("新增虚拟币手工充值订单异常："+ JSON.toJSONString(operation), e);
		}
		return ReturnResult.FAILUER("新增虚拟币手工充值订单失败！");
	}


	/**
	 * 审核虚拟币手工充值订单
	 */
	@RequestMapping("admin/checkVirtualCoinRechargeOrder")
	@ResponseBody
	public ReturnResult checkVirtualCoinRechargeOrder(
			@RequestParam(value = "id", required = true) Integer id,
			@RequestParam(value = "confirmations", required = true) Integer confirmations){

		if(id == null){
			return ReturnResult.FAILUER("参数异常");
		}

		if(confirmations <= 0){
			return ReturnResult.FAILUER("确认数不足无法到账");
		}

		FAdmin admin = (FAdmin) sessionContextUtils.getContextRequest().getSession().getAttribute("login_admin");
		FVirtualCapitalOperationDTO operation = new FVirtualCapitalOperationDTO();

		operation.setFadminid(admin.getFid());
		operation.setFadminname(admin.getFname());
		operation.setFid(id);
		operation.setFconfirmations(confirmations);

		try{
			Result result = adminUserCapitalService.recheckVirtualRecharge(operation);
			if(result.getSuccess()){
				return ReturnResult.SUCCESS("审核虚拟币充值订单成功！");
			}
			return ReturnResult.FAILUER(result.getMsg());
		}catch (Exception e){
			logger.error("审核虚拟币充值订单失败: id-"+id + " confirmations-"+confirmations, e);
			if(e instanceof BCException) {
				return ReturnResult.FAILUER(e.getMessage());
			}
		}
		return ReturnResult.FAILUER("审核虚拟币充值订单失败！");
	}
	
	// 导出列名
	private static enum ExportFiled {
		UID, 会员登录名, 会员昵称, 会员真实姓名, 虚拟币类型, 状态, 交易类型, 交易数量, 网络手续费, 手续费, 提现地址, 充值地址, 交易ID, 交易时间, 交易成功时间;
	}
	/**
	 * 虚拟币充值提现列表导出数据
	 */
	@RequestMapping("admin/virtualCapitaloperationExport")
	@ResponseBody
	public ReturnResult virtualCapitaloperationExport(
			@RequestParam(value = "fcoinid", required = false) Integer fcoinid,
			@RequestParam(value = "type", defaultValue = "0") Integer ftype,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "logDate", required = false) String logDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "isvip6", required = false, defaultValue = "off") String isvip6,
			@RequestParam(value = "orderField", defaultValue = "fupdatetime") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		
		final String tableName="虚拟币充值列表";
	 	   HttpServletRequest request = sessionContextUtils.getContextRequest();
	 		//存储excel_export_task记录
	 		 FAdmin sessionAdmin = (FAdmin) request.getSession().getAttribute("login_admin");
	 		ExcelExportTask excelExportTask=new ExcelExportTask();
	 		excelExportTask.setCreateTime(new Date());
	 		excelExportTask.setExcelFileName(null);
	 		excelExportTask.setOperator(sessionAdmin.getFname());
	 		excelExportTask.setStatus(ExcelExportStatusEnum.EXPORTING.getCode().byteValue());
	 		excelExportTask.setTableName(tableName);
			long excelExportTaskId = adminExcelExportTaskService.insertSelective(excelExportTask);
			excelExportTask.setId(excelExportTaskId);
		
			//异步执行excel导出
			CompletableFuture.supplyAsync(()->{
		XlsExport e = new XlsExport();
		int rowIndex = 0;
		// header
		e.createRow(rowIndex++);
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		
		Pagination<FVirtualCapitalOperationDTO> pageParam = new Pagination<FVirtualCapitalOperationDTO>(currentPage, 100000);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		// 日期
		if (!StringUtils.isEmpty(logDate)) {
			pageParam.setBegindate(logDate);
		}else{
			pageParam.setBegindate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 00:00:00");
		}

		// 日期
		if (!StringUtils.isEmpty(endDate)) {
			pageParam.setEnddate(endDate);
		}else{
			pageParam.setEnddate(DateUtils.format(new Date(), DateUtils.YYYY_MM_DD) + " 23:59:59");
		}
		
		FVirtualCapitalOperationDTO filterParam = new FVirtualCapitalOperationDTO();
		List<Integer> status = new ArrayList<>();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
		}
		// 虚拟币ID
		if (fcoinid !=null && fcoinid > 0) {
			filterParam.setFcoinid(fcoinid);
		} 
		
		// 类型提现or充值
		if (ftype > 0) {
			filterParam.setFtype(ftype);
		}
		// 查询
		try {
			pageParam = adminUserCapitalService.selectVirtualCapitalOperationList(pageParam, filterParam, status, isvip6.equals("on"));
		} catch (Exception e1) {
			logger.error("查询充值记录异常");
		}
		Collection<FVirtualCapitalOperationDTO> virtualCapitaloperationList = pageParam.getData();
		for (FVirtualCapitalOperationDTO capitalOperation : virtualCapitaloperationList) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case UID:
					e.setCell(filed.ordinal(), capitalOperation.getFuid());
					break;
				case 会员登录名:
						e.setCell(filed.ordinal(), capitalOperation.getFloginname());
					break;
				case 会员昵称:
						e.setCell(filed.ordinal(), capitalOperation.getFnickname());
					break;
				case 会员真实姓名:
						e.setCell(filed.ordinal(), capitalOperation.getFrealname());
					break;
				case 虚拟币类型:
					e.setCell(filed.ordinal(), capitalOperation.getFcoinname());
					break;
				case 状态:
					e.setCell(filed.ordinal(), capitalOperation.getFstatus_s());
					break;
				case 交易类型:
					e.setCell(filed.ordinal(), capitalOperation.getFtype_s());
					break;
				case 交易数量:
					e.setCell(filed.ordinal(), Utils.number4String(capitalOperation.getFamount(),4));
					break;
				case 网络手续费:
					String btcfees = capitalOperation.getFbtcfees() == null ? "0.0000" : Utils.number4String(capitalOperation.getFbtcfees(),4);
					e.setCell(filed.ordinal(), btcfees);
					break;
				case 手续费:
					e.setCell(filed.ordinal(), Utils.number4String(capitalOperation.getFfees(),4));
					break;
				case 提现地址:
					e.setCell(filed.ordinal(), capitalOperation.getFwithdrawaddress());
					break;
				case 充值地址:
					e.setCell(filed.ordinal(), capitalOperation.getFrechargeaddress());
					break;
				case 交易ID:
					e.setCell(filed.ordinal(), capitalOperation.getFuniquenumber());
					break;
				case 交易时间:
					e.setCell(filed.ordinal(), capitalOperation.getFcreatetime());
					break;
				case 交易成功时间:
					e.setCell(filed.ordinal(), capitalOperation.getFupdatetime());
					break;
				default:
					break;
				}
			}
		}
		//写入到文件
 		String fileName=tableName.concat(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).concat(".xls");
 		
 		e.exportXls(excelRootPath.concat(fileName));
 		return fileName;
		}, executor).whenComplete((r,e)->{
			//导出成功
			if(r!=null) {
				//更新状态和文件名
				ExcelExportTask updateExcelExportTask=new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FINISHED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			}else {
				//更新状态和文件名
				ExcelExportTask updateExcelExportTask=new ExcelExportTask();
				updateExcelExportTask.setId(excelExportTask.getId());
				updateExcelExportTask.setExcelFileName(r);
				updateExcelExportTask.setStatus(ExcelExportStatusEnum.FAILED.getCode().byteValue());
				updateExcelExportTask.setUpdateTime(new Date());
				adminExcelExportTaskService.updateByIdSelective(updateExcelExportTask);
			}
			
		});
		
 		//e.exportXls(response);
 		return ReturnResult.SUCCESS("请从目录(统计管理-报表导出)查看导出任务");
 	}

	
	/**
	 * 删除nonce页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/deleteNoncePage")
	public ModelAndView deleteNoncePage() throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/deleteNoncePage");
		Map<Integer, String> typeMap = new HashMap<Integer, String>();
		
		for(SystemCoinSortEnum systemCoinSortEnum:SystemCoinSortEnum.values()) {
			typeMap.put(systemCoinSortEnum.getCode(), systemCoinSortEnum.getValue().toString());
		}
		modelAndView.addObject("typeMap",typeMap);
		return modelAndView;
	}
	
	
	/**
	 * 删除nonce
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/deleteNonce")
	@ResponseBody
	public ReturnResult deleteNonce(
			@RequestParam(value = "type", required = true) Integer type
			) throws Exception {
		if(type == SystemCoinSortEnum.ETH.getCode()) {
			redisHelper.delete(RedisConstant.ETH_CURRENT_NONCE);
			return ReturnResult.SUCCESS("删除成功");
		}
		if(type == SystemCoinSortEnum.FOD.getCode()) {
			redisHelper.delete(RedisConstant.FOD_CURRENT_NONCE);
			return ReturnResult.SUCCESS("删除成功");
		}
		if(type == SystemCoinSortEnum.MOAC.getCode()) {
			redisHelper.delete(RedisConstant.MOAC_CURRENT_NONCE);
			return ReturnResult.SUCCESS("删除成功");
		}
		if(type == SystemCoinSortEnum.VCC.getCode()) {
			redisHelper.delete(RedisConstant.VCC_CURRENT_NONCE);
			return ReturnResult.SUCCESS("删除成功");
		}
		if(type == SystemCoinSortEnum.ETC.getCode()) {
			redisHelper.delete(RedisConstant.ETC_CURRENT_NONCE);
			return ReturnResult.SUCCESS("删除成功");
		}
		return ReturnResult.FAILUER("不存在该类型");
	}
	
	

}
