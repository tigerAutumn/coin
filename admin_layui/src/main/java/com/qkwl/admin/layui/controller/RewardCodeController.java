package com.qkwl.admin.layui.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminRewardCodeService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;

/**
 * 兑换码
 * @author ZKF
 */
@Controller
public class RewardCodeController extends WebBaseController {

	private static final Logger logger = LoggerFactory.getLogger(RewardCodeController.class);
	
	@Autowired
	private IAdminRewardCodeService adminRewardCodeService;
	@Autowired
	private RedisHelper redisHelper;
    @Autowired
    private IAdminExcelExportTaskService adminExcelExportTaskService;
    @Resource(name="taskExecutor")
    private Executor executor;
    @Value("${excel.path}")
    private String  excelRootPath;
	

	@RequestMapping("/admin/rewardcodeList")
	public ModelAndView rewardcodeList(
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage, 
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField, 
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection, 
			@RequestParam(value = "keywords", required = false) String keywords, 
			@RequestParam(value = "ftype", required = false, defaultValue = "0") String ftype,
			@RequestParam(value = "startCreateDate", required = false) String startCreateDate, 
			@RequestParam(value = "endCreateDate", required = false) String endCreateDate, 
			@RequestParam(value = "startUseDate", required = false) String startUseDate, 
			@RequestParam(value = "endUseDate", required = false) String endUseDate, 
			@RequestParam(value = "fstate", required = false) String fstate, 
			@RequestParam(value = "fbatch", required = false) String fbatch) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("rewardcode/rewardcodeList");

		Pagination<FRewardCodeDTO> page = new Pagination<FRewardCodeDTO>(currentPage, Constant.adminPageSize);
		FRewardCodeDTO rc = new FRewardCodeDTO();
		// 排序字段
		if (!StringUtils.isEmpty(orderField)) {
			page.setOrderField(orderField);
		}
		// 正序倒序
		if (!StringUtils.isEmpty(orderDirection)) {
			page.setOrderDirection(orderDirection);
		}
		// 查询关键字
		if (!StringUtils.isEmpty(keywords)) {
			page.setKeyword(keywords);
			rc.setFloginname(keywords);
		}

		if (!StringUtils.isEmpty(ftype)) {
			if(!ftype.equals("-1")){
				rc.setFtype(Integer.valueOf(ftype));
			}
			modelAndView.addObject("ftype", ftype);
		}
		if (!StringUtils.isEmpty(fstate)) {
			if (fstate.equals("0")) {
				rc.setFstate(false);
			}
			if (fstate.equals("1")) {
				rc.setFstate(true);
			}
			modelAndView.addObject("fstate", fstate);
		}
		if (!StringUtils.isEmpty(fbatch)) {
			rc.setFbatch(Integer.valueOf(fbatch));
			modelAndView.addObject("fbatch", fbatch);
		}
		Map<String, Object> param = new HashMap<>();
		if (!StringUtils.isEmpty(startCreateDate)) {
			param.put("startCreateDate", startCreateDate);
			modelAndView.addObject("startCreateDate", startCreateDate);
		}
		if (!StringUtils.isEmpty(endCreateDate)) {
			param.put("endCreateDate", endCreateDate);
			modelAndView.addObject("endCreateDate", endCreateDate);
		}
		if (!StringUtils.isEmpty(startUseDate)) {
			param.put("startUseDate", startUseDate);
			modelAndView.addObject("startUseDate", startUseDate);
		}
		if (!StringUtils.isEmpty(endUseDate)) {
			param.put("endUseDate", endUseDate);
			modelAndView.addObject("endUseDate", endUseDate);
		}
		page.setParam(param);
		page = adminRewardCodeService.selectRewardCodePageList(page, rc);

		modelAndView.addObject("keywords", keywords);

		Map<Integer, String> codetype = redisHelper.getCoinTypeNameMap();
		codetype.put(-1, "全部");

		Map<Integer, String> stateMap = new LinkedHashMap<Integer, String>();
		stateMap.put(-1, "全部");
		stateMap.put(0, "未使用");
		stateMap.put(1, "已使用");

		modelAndView.addObject("stateMap", stateMap);
		modelAndView.addObject("codetype", codetype);

		modelAndView.addObject("codelist", page);
		return modelAndView;
	}

	@RequestMapping("admin/goRewardcodeJSP")
	public ModelAndView goRewardcodeJSP(@RequestParam(value = "url", required = true) String url) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(url);

		Map<Integer, String> codetype = redisHelper.getCoinTypeNameMap();
		modelAndView.addObject("codetype", codetype);

		return modelAndView;
	}

	@RequestMapping("admin/createRewardcode")
	@ResponseBody
	public ReturnResult createRewardcode(
			@RequestParam(value = "ftype", required = true) Integer ftype,
			@RequestParam(value = "famount", required = true) BigDecimal famount,
			@RequestParam(value = "fusedate", required = true) String fusedate,
			@RequestParam(value = "userLookup.id", required = true) Integer fuid) {
		try {

			FRewardCodeDTO code = new FRewardCodeDTO();

			String fcode = "R";
			Map<Integer, String> shortNameMap = redisHelper.getCoinTypeShortNameMap();
			String shortName = shortNameMap.get(ftype);
			if (shortName != null) {
				fcode = shortName.substring(0, 1);
			}
			fcode += Utils.randomString(15);
			code.setFcode(fcode);
			code.setFtype(ftype);
			code.setFamount(famount);
			code.setFuid(fuid);
			code.setFcreatetime(new Date());
			code.setFislimituser(true);
			code.setFstate(false);
			if (!StringUtils.isEmpty(fusedate)) {
				code.setFusedate(DateUtils.parse(fusedate, DateUtils.YYYY_MM_DD_HH_MM_SS));
			} else {
				code.setFusedate(null);
			}
			code.setVersion(0);

			adminRewardCodeService.insertRewardCode(code);
			return ReturnResult.SUCCESS("新增成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ReturnResult.FAILUER("新增失败！");
	}

	@RequestMapping("admin/createRewardcodebatch")
	@ResponseBody
	public ReturnResult createRewardcodebatch(
			@RequestParam(value = "ftype", required = true) Integer ftype, 
			@RequestParam(value = "fbatch", required = true) Integer fbatch,
			@RequestParam(value = "fusenum", required = true) Integer fusenum, 
			@RequestParam(value = "famount", required = true) BigDecimal famount,
			@RequestParam(value = "fusedate", required = false) String fusedate,
			@RequestParam(value = "fislimituse", required = true) String fislimituse, 
			@RequestParam(value = "fgenerate", required = true) Integer fgenerate) {
		try {

			List<FRewardCodeDTO> list = new ArrayList<FRewardCodeDTO>();
			Map<Integer, String> shortNameMap = redisHelper.getCoinTypeShortNameMap();
			for (int i = 0; i < fgenerate; i++) {
				FRewardCodeDTO code = new FRewardCodeDTO();

				String fcode = "R";
				String shortName = shortNameMap.get(ftype);
				if (shortName != null) {
					fcode = shortName.substring(0, 1);
				}
				fcode += Utils.randomString(15);
				code.setFcode(fcode);
				code.setFtype(ftype);
				code.setFamount(famount);
				code.setFcreatetime(new Date());
				code.setFislimituser(true);
				code.setFstate(false);
				code.setFbatch(fbatch);
				if (fusedate != null) {
					code.setFusedate(DateUtils.parse(fusedate, DateUtils.YYYY_MM_DD_HH_MM_SS));
				} else {
					code.setFusedate(null);
				}
				if (fislimituse != null && fislimituse.length() > 0) {
					code.setFislimituse(true);
					code.setFusenum(fusenum);
				} else {
					code.setFislimituse(false);
					code.setFusenum(0);
				}
				code.setVersion(0);
				list.add(code);
			}

			adminRewardCodeService.insertRewardCodeList(list);


			return ReturnResult.SUCCESS("新增成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ReturnResult.FAILUER("新增失败！");
	}

	@RequestMapping("admin/deleteRewardcode")
	@ResponseBody
	public ReturnResult deleteRewardcode(@RequestParam(value = "codeid", required = true) Integer codeid) {

		boolean i = adminRewardCodeService.deleteRewardCode(codeid);

		if (i) {
			return ReturnResult.SUCCESS("删除成功！");
		} else {
			return ReturnResult.SUCCESS("删除失败！");
		}
	}
	
	/**
	 * 导出激活码
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/RewardcodeExport")
	@ResponseBody
	public ReturnResult userExport(
			@RequestParam(value = "pageNum", defaultValue = "1") int currentPage, 
			@RequestParam(value = "orderField", defaultValue = "fcreatetime") String orderField, 
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection, 
			@RequestParam(value = "keywords", required = false) String keywords, 
			@RequestParam(value = "ftype", required = false) String ftype, 
			@RequestParam(value = "startCreateDate", required = false) String startCreateDate, 
			@RequestParam(value = "endCreateDate", required = false) String endCreateDate, 
			@RequestParam(value = "startUseDate", required = false) String startUseDate, 
			@RequestParam(value = "endUseDate", required = false) String endUseDate, 
			@RequestParam(value = "fstate", required = false) String fstate, 
			@RequestParam(value = "fbatch", required = false) String fbatch) throws Exception {
		
		
		final String tableName="兑换码列表";
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
		for (ExportFiled filed : ExportFiled.values()) {
			e.setCell(filed.ordinal(), filed.toString());
		}
		
		//导出用户获取用户列表
		Pagination<FRewardCodeDTO> page = new Pagination<FRewardCodeDTO>(currentPage, 100000);
		FRewardCodeDTO rcode = new FRewardCodeDTO();
		// 排序字段
		if (!StringUtils.isEmpty(orderField)) {
			page.setOrderField(orderField);
		}
		// 正序倒序
		if (!StringUtils.isEmpty(orderDirection)) {
			page.setOrderDirection(orderDirection);
		}
		// 查询关键字
		if (!StringUtils.isEmpty(keywords)) {
			page.setKeyword(keywords);
			rcode.setFloginname(keywords);
		}

		if (!StringUtils.isEmpty(ftype)) {
			if(!ftype.equals("-1")){
				rcode.setFtype(Integer.valueOf(ftype));
			}
		}
		if (!StringUtils.isEmpty(fstate)) {
			if (fstate.equals("0")) {
				rcode.setFstate(false);
			}
			if (fstate.equals("1")) {
				rcode.setFstate(true);
			}
		}
		if (!StringUtils.isEmpty(fbatch)) {
			rcode.setFbatch(Integer.valueOf(fbatch));
		}
		Map<String, Object> param = new HashMap<>();
		if (!StringUtils.isEmpty(startCreateDate)) {
			param.put("startCreateDate", startCreateDate);
		}
		if (!StringUtils.isEmpty(endCreateDate)) {
			param.put("endCreateDate", endCreateDate);
		}
		if (!StringUtils.isEmpty(startUseDate)) {
			param.put("startUseDate", startUseDate);
		}
		if (!StringUtils.isEmpty(endUseDate)) {
			param.put("endUseDate", endUseDate);
		}
		
		page.setParam(param);
		page = adminRewardCodeService.selectRewardCodePageList(page, rcode);

		for (FRewardCodeDTO rc : page.getData()) {
			e.createRow(rowIndex++);
			for (ExportFiled filed : ExportFiled.values()) {
				switch (filed) {
				case 登录名:
					e.setCell(filed.ordinal(), rc.getFloginname());
					break;
				case 类型:
					e.setCell(filed.ordinal(), rc.getFtype_s());
					break;
				case 激活码:
					e.setCell(filed.ordinal(), rc.getFcode());
					break;
				case 数量或金额:
					e.setCell(filed.ordinal(), rc.getFamount().toString());
					break;
				case 批次:
					e.setCell(filed.ordinal(), rc.getFbatch() == null ? 0:rc.getFbatch());
					break;
				case 是否限制使用:
					e.setCell(filed.ordinal(), rc.getFislimituse() == null ? "未限制" : rc.getFislimituse() ? "限制" : "未限制");
					break;
				case 使用次数:
					e.setCell(filed.ordinal(), rc.getFusenum() == null ? 0 : rc.getFusenum());
					break;
				case 有效期至:
					e.setCell(filed.ordinal(), rc.getFusedate());
					break;
				case 状态:
					e.setCell(filed.ordinal(), rc.getFstate() ? "已使用" : "未使用" );
					break;
				case 创建时间:
					e.setCell(filed.ordinal(), rc.getFcreatetime());
					break;
				case 使用时间:
					e.setCell(filed.ordinal(), rc.getFupdatetime());
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
	 * 导出激活码
	 */
	private static enum ExportFiled {
		登录名,
		类型,
		激活码,
		数量或金额,
		批次,
		是否限制使用,
		使用次数,
		有效期至,
		状态,
		创建时间,
		使用时间,
	}

	
	
}
