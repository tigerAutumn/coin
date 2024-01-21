package com.qkwl.admin.layui.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.Excel.XlsExport;
import com.qkwl.common.dto.Enum.ExcelExportStatusEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.excel.ExcelExportTask;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminExcelExportTaskService;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class WalletController extends WebBaseController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
	
	@Autowired
	private IAdminUserCapitalService adminUserCapitalService;
	@Autowired
	private RedisHelper redisHelper;
	// 每页显示多少条数据
	private int numPerPage = Constant.adminPageSize;
	@Autowired
	private IAdminExcelExportTaskService adminExcelExportTaskService;
	@Resource(name = "taskExecutor")
	private Executor executor;
	@Value("${excel.path}")
	private String excelRootPath;

	/**
	 * 人民币钱包
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/walletList")
	public ModelAndView walletList() throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/walletList");
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String fuids = request.getParameter("fuids");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, numPerPage);
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		List<Integer> fuidsList = null;
		if (fuids != null && !fuids.isEmpty()) {
			try {
				String fuid[] = fuids.split("#");
				fuidsList = new ArrayList<>();
				for (String string : fuid) {
					fuidsList.add(Integer.valueOf(string));
				}
				modelAndView.addObject("fuids", fuids);
			} catch (Exception e) {
			}
		}
		// 排序条件
		if (orderField != null && orderField.trim().length() > 0) {
			pageParam.setOrderField(orderField);
		} else {
			pageParam.setOrderField("fupdatetime");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			pageParam.setOrderDirection(orderDirection);
		} else {
			pageParam.setOrderDirection("desc");
		}

		if (StringUtils.isEmpty(keyWord) && StringUtils.isEmpty(fuids)) {
			return modelAndView;
		}
		// 查询
		Pagination<UserCoinWallet> pagination = adminUserCapitalService.selectUserWalletList(pageParam, fuidsList);
		modelAndView.addObject("wallet", pagination);
		return modelAndView;
	}

	/**
	 * 虚拟币钱包
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/virtualwalletList")
	public ModelAndView virtualwalletList(@RequestParam(value = "fuids", required = false) String fuids,
			@RequestParam(value = "type", defaultValue = "-1") Integer type,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "orderField", defaultValue = "gmt_modified") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("capital/virtualwalletList");
		// 搜索关键字
		Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, numPerPage);
		// 排序条件
		pageParam.setOrderField(orderField);
		pageParam.setOrderDirection(orderDirection);

		UserCoinWallet filterParam = new UserCoinWallet();
		// 关键字
		if (!StringUtils.isEmpty(keyWord)) {
			pageParam.setKeyword(keyWord);
			modelAndView.addObject("keywords", keyWord);
		}
		// 虚拟币类型
		if (type >= 0) {
			filterParam.setCoinId(type);
			modelAndView.addObject("type", type);
		}

		List<Integer> fuidsList = null;
		if (fuids != null && !fuids.isEmpty()) {
			try {
				String fuid[] = fuids.split("#");
				fuidsList = new ArrayList<>();
				for (String string : fuid) {
					fuidsList.add(Integer.valueOf(string));
				}
				modelAndView.addObject("fuids", fuids);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 页面参数
		Map<Integer, String> typeMap = redisHelper.getCoinTypeNameMap();
		typeMap.put(-1, "全部");
		modelAndView.addObject("typeMap", typeMap);

		if (StringUtils.isEmpty(keyWord) && type < 0 && StringUtils.isEmpty(fuids)) {
			return modelAndView;
		}

		// 查询
		Pagination<UserCoinWallet> pagination = adminUserCapitalService.selectUserVirtualWalletList(pageParam,
				filterParam, fuidsList);
		modelAndView.addObject("virtualwalletList", pagination);
		return modelAndView;
	}

	@RequestMapping(value = "/admin/virtualwalletExport")
	@ResponseBody
	public ReturnResult virtualwalletExport(@RequestParam(value = "fuids", required = false) String fuids,
			@RequestParam(value = "type", defaultValue = "0") Integer type,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer currentPage,
			@RequestParam(value = "keywords", required = false) String keyWord,
			@RequestParam(value = "orderField", defaultValue = "gmt_modified") String orderField,
			@RequestParam(value = "orderDirection", defaultValue = "desc") String orderDirection) throws Exception {
		HttpServletResponse response = sessionContextUtils.getContextResponse();
		response.setContentType("Application/excel");
		response.addHeader("Content-Disposition", "attachment;filename=virtualwallet.xls");

		final String tableName = "会员虚拟币列表";
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
			for (ExportFiled filed : ExportFiled.values()) {
				e.setCell(filed.ordinal(), filed.toString());
			}

			Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, 100000);
			// 排序条件
			pageParam.setOrderField(orderField);
			pageParam.setOrderDirection(orderDirection);

			UserCoinWallet filterParam = new UserCoinWallet();
			// 关键字
			if (!StringUtils.isEmpty(keyWord)) {
				pageParam.setKeyword(keyWord);
			}
			// 虚拟币类型
			if (type > 0) {
				filterParam.setCoinId(type);
			}

			List<Integer> fuidsList = null;
			if (fuids != null && !fuids.isEmpty()) {
				String fuid[] = fuids.split("#");
				fuidsList = new ArrayList<>();
				for (String string : fuid) {
					fuidsList.add(Integer.valueOf(string));
				}
			}

			// 查询
			pageParam = adminUserCapitalService.selectUserVirtualWalletList(pageParam, filterParam, fuidsList);
			Collection<UserCoinWallet> virtualwalletList = pageParam.getData();
			for (UserCoinWallet virtualwallet : virtualwalletList) {
				e.createRow(rowIndex++);
				for (ExportFiled filed : ExportFiled.values()) {
					switch (filed) {
					case 登录名:
						e.setCell(filed.ordinal(), virtualwallet.getLoginName());
						break;
					case 会员昵称:
						e.setCell(filed.ordinal(), virtualwallet.getNickName());
						break;
					case 会员真实姓名:
						e.setCell(filed.ordinal(), virtualwallet.getRealName());
						break;
					case 币种类型:
						e.setCell(filed.ordinal(), virtualwallet.getCoinName());
						break;
					case 总数量:
						e.setCell(filed.ordinal(), virtualwallet.getTotal().doubleValue());
						break;
					case 冻结数量:
						e.setCell(filed.ordinal(), virtualwallet.getFrozen().doubleValue());
						break;
					case 当前理财数量:
						e.setCell(filed.ordinal(), virtualwallet.getBorrow().doubleValue());
						break;
					case 创新区充值冻结:
						e.setCell(filed.ordinal(), virtualwallet.getDepositFrozen().doubleValue());
						break;
					case 最后修改时间:
						e.setCell(filed.ordinal(), virtualwallet.getGmtModified());
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
	 * 虚拟币导出查询数据
	 * 
	 * @return
	 */
	private Collection<UserCoinWallet> getVirtualWalletList() {
		HttpServletRequest request = sessionContextUtils.getContextRequest();
		// 当前页
		int currentPage = 1;
		// 搜索关键字
		String keyWord = request.getParameter("keywords");
		String fuids = request.getParameter("fuids");
		String orderField = request.getParameter("orderField");
		String orderDirection = request.getParameter("orderDirection");
		if (request.getParameter("pageNum") != null) {
			currentPage = Integer.parseInt(request.getParameter("pageNum"));
		}
		Pagination<UserCoinWallet> pageParam = new Pagination<UserCoinWallet>(currentPage, numPerPage);
		UserCoinWallet filterParam = new UserCoinWallet();
		// 关键字
		if (keyWord != null && keyWord.trim().length() > 0) {
			pageParam.setKeyword(keyWord);
		}
		// 虚拟币类型
		String strFtype = request.getParameter("ftypeDefault");
		if (strFtype == null) {
			strFtype = request.getParameter("ftype");
		}
		if (strFtype != null) {
			int type = Integer.parseInt(strFtype);
			if (type != 0) {
				filterParam.setCoinId(type);
			}
		}
		List<Integer> fuidsList = null;
		if (fuids != null && !fuids.isEmpty()) {
			try {
				String fuid[] = fuids.split("#");
				fuidsList = new ArrayList<>();
				for (String string : fuid) {
					fuidsList.add(Integer.valueOf(string));
				}
			} catch (Exception e) {
			}
		}
		// 排序条件
		if (orderField != null && orderField.trim().length() > 0) {
			pageParam.setOrderField(orderField);
		} else {
			pageParam.setOrderField("fupdatetime");
		}
		if (orderDirection != null && orderDirection.trim().length() > 0) {
			pageParam.setOrderDirection(orderDirection);
		} else {
			pageParam.setOrderDirection("desc");
		}
		// 查询
		Pagination<UserCoinWallet> pagination = adminUserCapitalService.selectUserVirtualWalletList(pageParam,
				filterParam, fuidsList);
		return pagination.getData();
	}

	// 虚拟币导出列名
	private static enum ExportFiled {
		登录名, 会员昵称, 会员真实姓名, 币种类型, 总数量, 冻结数量, 当前理财数量, 创新区充值冻结,最后修改时间;
	}
}
