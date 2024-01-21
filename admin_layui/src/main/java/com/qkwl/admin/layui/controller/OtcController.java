package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.utils.FormatUtils;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcTransferTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.otc.SystemOtcSetting;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminOtcService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcController extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(C2CController.class);
	
	@Autowired
	private IAdminOtcService adminOtcService;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private IAdminUserService adminUserService;
    @Autowired
    private IAdminExcelExportTaskService adminExcelExportTaskService;
    @Resource(name="taskExecutor")
    private Executor executor;
    @Value("${excel.path}")
    private String  excelRootPath;
	
	
	
	/**
	 * 查询otc设置参数
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/otcFunctionSetting")
	public ModelAndView otcFunctionSetting(){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/otcFunctionSetting");
			List<SystemOtcSetting> otcSetting = adminOtcService.getOtcSetting();
			modelAndView.addObject("otcSettingList",otcSetting);
			return modelAndView;
		} catch (Exception e) {
			logger.error("c2cFunctionSetting 异常 ",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}
	
	/**
	 * otc设置修改页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/goOtcSettingJSP")
	public ModelAndView goOtcSettingJSP(
	         @RequestParam(required = true) Integer id){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/updateOtcSetting");
			SystemOtcSetting otcSettingById = adminOtcService.getOtcSettingById(id);
			modelAndView.addObject("otcSetting", otcSettingById );
			return modelAndView;
		} catch (Exception e) {
			logger.error("goOtcSettingJSP 执行异常{id:"+id+"}",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}
	
	/**
	 * 修改otc设置参数
	 * @return
	 */
	@RequestMapping("admin/updateOtcFunctionSetting")
	@ResponseBody
	public ReturnResult updateOtcFunctionSetting(
			@RequestParam(required = true) Integer id,
			@RequestParam(required = true) String value
			){
		try {
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			SystemOtcSetting s = new SystemOtcSetting();
			s.setId(id);
			s.setValue(value);
			int updateOtcSetting = adminOtcService.updateOtcSetting(s, admin.getFid());
			if(updateOtcSetting == 0) {
				return ReturnResult.FAILUER("修改失败");
			}
			return ReturnResult.SUCCESS("成功");
		} catch (Exception e) {
			logger.error("updateOtxFunctionSetting 异常 ",e);
			return ReturnResult.FAILUER("修改失败");
		}
	}
	
	/**
	 * OTC对账
	 * @return
	 */
	@RequestMapping("admin/otcAdvancedQuery")
	public ModelAndView otcAdvancedQuery(
			@RequestParam(required = false) Integer userId)
	{
		ModelAndView modelAndView = new ModelAndView();
		try {
			List<SystemCoinType> coinList = redisHelper.getOpenOtcCoinTypeList();
			
	        modelAndView.setViewName("otc/otcBalanceList");
	        if(userId == null || userId == 0) {
	        	return modelAndView;
	        }
	        	
	        FUser user = adminUserService.selectById(userId);
			if(user == null) {
	            return modelAndView;
			}
			modelAndView.addObject("userId", userId);
			JSONArray array = new JSONArray();
			for(SystemCoinType systemCoinType : coinList) {
				//查询用户OTC钱包
				UserOtcCoinWallet userOtcCoinWallet = adminOtcService.selectWallet(userId, systemCoinType.getId());
				if(userOtcCoinWallet == null) {
					continue;
				}
				
				JSONObject jsonObject = new JSONObject();
			    BigDecimal in = BigDecimal.ZERO;
	            BigDecimal out = BigDecimal.ZERO;
	            BigDecimal buyAmount = BigDecimal.ZERO;
	            BigDecimal sellAmount = BigDecimal.ZERO;
	            BigDecimal fee = BigDecimal.ZERO;
	            BigDecimal outToInnovateArea = BigDecimal.ZERO;
	            BigDecimal otcMerchantDeposit = BigDecimal.ZERO;
	            
	            jsonObject.put("coinName", systemCoinType.getName());
	            jsonObject.put("frozen", userOtcCoinWallet.getFrozen());
	            jsonObject.put("total", userOtcCoinWallet.getTotal());
	            
	            //查询多种类型的总额
	            //转入
	            OtcUserTransfer inObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.transferToOtc.getCode(),systemCoinType.getId());
	            in = inObj.getAmount();
	            jsonObject.put("in", inObj.getAmount());
	            
	            //转出
	            OtcUserTransfer outObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.otcTransferTo.getCode(),systemCoinType.getId());
	            out = outObj.getAmount();
	            jsonObject.put("out", outObj.getAmount());
	            
	            //买入
	            OtcUserTransfer buyAmountObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.otcBuy.getCode(),systemCoinType.getId());
	            buyAmount = buyAmountObj.getAmount();
	            jsonObject.put("buyAmount", buyAmountObj.getAmount());
	            fee = MathUtils.add(buyAmountObj.getFee(), fee);
	            
	            //卖出
	            OtcUserTransfer sellAmountObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.otcSell.getCode(),systemCoinType.getId());
	            sellAmount = sellAmountObj.getAmount();
	            jsonObject.put("sellAmount", sellAmountObj.getAmount());
	            fee = MathUtils.add(sellAmountObj.getFee(), fee);
	            
	            //otc转入创新区
	            OtcUserTransfer outToInnovateAreaObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.otcTransferToInnovate.getCode(),systemCoinType.getId());
	            outToInnovateArea = outToInnovateAreaObj.getAmount();
	            jsonObject.put("outToInnovateAreaObj", outToInnovateAreaObj.getAmount());
	            out = MathUtils.add(outToInnovateArea, out);
	            
	            //otc商户押金
	            OtcUserTransfer otcMerchantDepositObj = adminOtcService.sumOtcTransfer(userId, OtcTransferTypeEnum.otcMerchantDeposit.getCode(),systemCoinType.getId());
	            otcMerchantDeposit = otcMerchantDepositObj.getAmount();
	            jsonObject.put("otcMerchantDeposit", otcMerchantDeposit);
	            out = MathUtils.add(otcMerchantDeposit, out);
	            jsonObject.put("fee", fee);
	            
	            //平账 ： 转到场外+买 = 转出+手续费+卖出 
	            //可用+冻结+手续费+卖出+转出-转入-买入=平账
//	            BigDecimal amountAdd = MathUtils.add(in,buyAmount);
//	            BigDecimal amountSub = MathUtils.add(MathUtils.add(out,sellAmount),fee);
//	            BigDecimal total = MathUtils.add(userOtcCoinWallet.getFrozen(),userOtcCoinWallet.getTotal());
//	            BigDecimal balance = MathUtils.sub(total,(MathUtils.sub(amountAdd, amountSub)));
	            BigDecimal amountSub = MathUtils.add(MathUtils.add(out,sellAmount),fee);
	            BigDecimal total = MathUtils.add(userOtcCoinWallet.getFrozen(),userOtcCoinWallet.getTotal());
	            BigDecimal balance = MathUtils.sub(MathUtils.sub(MathUtils.add(amountSub, total),in),buyAmount);
	            jsonObject.put("balance", balance);
	            array.add(jsonObject);
			}
			modelAndView.addObject("list", array);
			return modelAndView;
		} catch (Exception e) {
			logger.error("otcBalanceList 异常 ",e);
		}
		return modelAndView;
	}
	
	/**
     * otc虚拟币钱包
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("admin/otcCapitalStorage")
    public ModelAndView otcCapitalStorage(
            @RequestParam(value = "ftype",defaultValue="-1") Integer type,
            @RequestParam(value = "pageNum",defaultValue="1") Integer currentPage,
            @RequestParam(value = "orderField",defaultValue="total") String orderField,
            @RequestParam(value = "orderDirection",defaultValue="desc") String orderDirection) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("otc/otcCapitalStorage");
        // 搜索关键字
        Pagination<UserOtcCoinWallet> pageParam = new Pagination<UserOtcCoinWallet>(currentPage, Constant.adminPageSize);
        // 排序条件
        pageParam.setOrderField(orderField);
        pageParam.setOrderDirection(orderDirection);

        // 页面参数
        Map<Integer, String> typeMap = redisHelper.getOtcCoinTypeNameMap();
        typeMap.put(-1, "全部");
        modelAndView.addObject("typeMap", typeMap);

        UserOtcCoinWallet filterParam = new UserOtcCoinWallet();
        // 虚拟币类型
        if(type < 0){
            return modelAndView;
        } else {
            filterParam.setCoinId(type);
            modelAndView.addObject("ftype", type);
        }

        // 查询
        Pagination<UserOtcCoinWallet> pagination = adminOtcService.selectUserOtcVirtualWalletListByCoin(
                pageParam, filterParam);

        modelAndView.addObject("virtualwalletList", pagination);
        return modelAndView;
    }
    
    // 导出列名
 	private static enum ExportRecordFiled {
 		UID, 登录名, 会员昵称, 会员真实姓名, 币种类型, 可用数量, 冻结数量, 最后修改时间;
 	}
 	
 	@RequestMapping("/admin/importOtcCapitalStorage")
 	@ResponseBody
 	public ReturnResult importOtcCapitalStorage(
 			@RequestParam(value="ftype",required=false,defaultValue="0") Integer coinId,
 			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
 			) {
 		
 		
 		final String tableName="otc用户币存量";
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
 		//TODO huanjinfeng
 		int rowIndex = 0;
 		
 		// header
 		e.createRow(rowIndex++);
 		for (ExportRecordFiled filed : ExportRecordFiled.values()) {
 			e.setCell(filed.ordinal(), filed.toString());
 		}
 		
 		Pagination<UserOtcCoinWallet> pageParam = new Pagination<UserOtcCoinWallet>(currentPage, 100000);
 		UserOtcCoinWallet userCoinWallet = new UserOtcCoinWallet();
 		if (coinId > 0) {
 			userCoinWallet.setCoinId(coinId);
 		}
 		
 		//查询otc用户持仓
 		// 查询
        Pagination<UserOtcCoinWallet> pagination = adminOtcService.selectUserOtcVirtualWalletListByCoin(
                pageParam, userCoinWallet);
 		Collection<UserOtcCoinWallet> walletList = pagination.getData();
 		for (UserOtcCoinWallet element : walletList) {
 			e.createRow(rowIndex++);
 			for (ExportRecordFiled filed : ExportRecordFiled.values()) {
 				switch (filed) {
 				case UID:
 					e.setCell(filed.ordinal(), element.getUid());
 					break;
 				case 登录名:
 					e.setCell(filed.ordinal(), element.getLoginName());
 					break;
 				case 会员昵称:
 					e.setCell(filed.ordinal(), element.getNickName());
 					break;
 				case 会员真实姓名:
 					e.setCell(filed.ordinal(), element.getRealName());
 					break;
 				case 币种类型:
 					e.setCell(filed.ordinal(), element.getCoinName());
 					break;
 				case 可用数量:
 					e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getTotal()));
 					break;
 				case 冻结数量:
 					e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getFrozen()));
 					break;
 				case 最后修改时间:
 					e.setCell(filed.ordinal(), element.getGmtModified());
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
     * otc用户划转记录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("admin/otcTransferRecord")
    public ModelAndView otcTransferRecord(
            @RequestParam(value = "ftype",defaultValue="-1") Integer type,
            @RequestParam(value = "startTime",required=false) String startTime,
            @RequestParam(value = "endTime",required=false) String endTime,
            @RequestParam(value = "pageNum",defaultValue="1") Integer currentPage) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("otc/otcTransferRecord");
        // 搜索关键字
        Pagination<OtcUserTransfer> pageParam = new Pagination<OtcUserTransfer>(currentPage, Constant.adminPageSize);
        // 排序条件
        pageParam.setBegindate(startTime);
		pageParam.setEnddate(endTime);

        // 页面参数
        Map<Integer, String> typeMap = redisHelper.getOtcCoinTypeNameMap();
        typeMap.put(-1, "全部");
        modelAndView.addObject("typeMap", typeMap);

        OtcUserTransfer filterParam = new OtcUserTransfer();
        // 虚拟币类型
        if(type < 0){
            return modelAndView;
        } else {
            filterParam.setCoinId(type);
            modelAndView.addObject("ftype", type);
            modelAndView.addObject("startTime", startTime);
    		modelAndView.addObject("endTime", endTime);
        }

        // 查询
        Pagination<OtcUserTransfer> pagination = adminOtcService.selectOtcTransferListByCoin(
                pageParam, filterParam);

        //查询汇总转出数量
        List<OtcUserTransfer> transferInAmountList = adminOtcService.selectTransferInAmount(startTime, endTime);
        //查询汇总转入数量
        List<OtcUserTransfer> transferOutAmountList = adminOtcService.selectTransferOutAmount(startTime, endTime);
        modelAndView.addObject("otcTransferList", pagination);
        modelAndView.addObject("transferInAmountList", transferInAmountList);
        modelAndView.addObject("transferOutAmountList", transferOutAmountList);
        return modelAndView;
    }
    
    // 导出列名
  	private static enum ExportTransferFiled {
  		UID, 币种类型, 划转方向, 划转数量, 划转时间;
  	}
  	
  	@RequestMapping("/admin/importOtcTransferRecord")
  	@ResponseBody
  	public ReturnResult importOtcTransferRecord(
  			@RequestParam(value="ftype",required=false,defaultValue="0") Integer coinId,
  			@RequestParam(value = "startTime",required=false) String startTime,
            @RequestParam(value = "endTime",required=false) String endTime,
  			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage
  			) {
  		
  		
  		final String tableName="otc划转记录";
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
  	//TODO huanjinfeng
  		int rowIndex = 0;
  		
  		// header
  		e.createRow(rowIndex++);
  		for (ExportTransferFiled filed : ExportTransferFiled.values()) {
  			e.setCell(filed.ordinal(), filed.toString());
  		}
  		
  		Pagination<OtcUserTransfer> pageParam = new Pagination<OtcUserTransfer>(currentPage, 100000);
  		pageParam.setBegindate(startTime);
		pageParam.setEnddate(endTime);
  		OtcUserTransfer otcUserTransfer = new OtcUserTransfer();
  		if (coinId > 0) {
  			otcUserTransfer.setCoinId(coinId);
  		}
  		
  		//查询otc划转记录
  		// 查询
         Pagination<OtcUserTransfer> pagination = adminOtcService.selectOtcTransferListByCoin(
                 pageParam, otcUserTransfer);
  		Collection<OtcUserTransfer> walletList = pagination.getData();
  		for (OtcUserTransfer element : walletList) {
  			e.createRow(rowIndex++);
  			for (ExportTransferFiled filed : ExportTransferFiled.values()) {
  				switch (filed) {
  				case UID:
  					e.setCell(filed.ordinal(), element.getUserId());
  					break;
  				case 币种类型:
  					e.setCell(filed.ordinal(), element.getCoinName());
  					break;
  				case 划转方向:
  					e.setCell(filed.ordinal(), element.getTransferName());
  					break;
  				case 划转数量:
  					e.setCell(filed.ordinal(), FormatUtils.toString10AndstripTrailingZeros(element.getAmount()));
  					break;
  				case 划转时间:
  					e.setCell(filed.ordinal(), element.getCreateTime());
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
}
