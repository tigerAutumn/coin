package com.qkwl.admin.layui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.qkwl.common.dto.Enum.otc.OtcMerchantStatusEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.rpc.admin.IAdminOtcMerchantService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcMerchantController {

	@Autowired
	private IAdminOtcMerchantService adminOtcMerchantService;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	/**
 	 * 查询otc商户信息
 	 */
	@RequestMapping("/admin/otcMerchantList")
	public ModelAndView getOtcMerchantList(
			@RequestParam(required=false,defaultValue="0") Integer uid,
			@RequestParam(required=false,defaultValue="0") Integer status,
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="apply_time") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("otc/otcMerchantList");
		
		// 定义查询条件
		OtcMerchant otcMerchant = new OtcMerchant();
		if (uid > 0) {
			otcMerchant.setUid(uid);
			modelAndView.addObject("uid", uid);
		}
		if (status > 0) {
			otcMerchant.setStatus(status);
			modelAndView.addObject("status", status);
		}
		
		Pagination<OtcMerchant> pageParam = new Pagination<OtcMerchant>(currentPage, numPerPage);
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		//查询商户列表
		Pagination<OtcMerchant> pagination = adminOtcMerchantService.selectOtcMerchantPageList(pageParam, otcMerchant);
		modelAndView.addObject("otcMerchantList", pagination);
		return modelAndView;
	}
	
	/**
	 * 通过
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/passOtcMerchant")
	@ResponseBody
	public ReturnResult passOtcMerchant(
			@RequestParam(required = true) Integer id) {
		OtcMerchant otcMerchant = adminOtcMerchantService.selectOtcMerchantById(id);
		if (otcMerchant == null) {
			return ReturnResult.FAILUER("商户不存在!");
		}
		if (otcMerchant.getStatus() != OtcMerchantStatusEnum.Inited.getCode()) {
			return ReturnResult.FAILUER("商户应为未审核状态!");
		}
		return adminOtcMerchantService.passOtcMerchant(otcMerchant);
	}
	
	/**
	 * 拒绝
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/refuseOtcMerchant")
	@ResponseBody
	public ReturnResult refuseOtcMerchant(
			@RequestParam(required = true) Integer id) {
		OtcMerchant otcMerchant = adminOtcMerchantService.selectOtcMerchantById(id);
		if (otcMerchant == null) {
			return ReturnResult.FAILUER("商户不存在!");
		}
		if (otcMerchant.getStatus() != OtcMerchantStatusEnum.Inited.getCode()) {
			return ReturnResult.FAILUER("商户应为未审核状态!");
		}
		return adminOtcMerchantService.refuseOtcMerchant(otcMerchant);
	}
	
	/**
	 * 解除正常商户权限
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/removeOtcMerchant")
	@ResponseBody
	public ReturnResult removeOtcMerchant(
			@RequestParam(required = true) Integer id) {
		OtcMerchant otcMerchant = adminOtcMerchantService.selectOtcMerchantById(id);
		if (otcMerchant == null) {
			return ReturnResult.FAILUER("商户不存在!");
		}
		if (otcMerchant.getStatus() != OtcMerchantStatusEnum.Passed.getCode()) {
			return ReturnResult.FAILUER("商户应为已通过状态!");
		}
		try {
			return adminOtcMerchantService.removeOtcMerchant(otcMerchant);
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER("解除正常商户权限失败！");
		}
	}
	
	/**
	 * 解除恶意商户权限
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/prohibitOtcMerchant")
	@ResponseBody
	public ReturnResult prohibitOtcMerchant(
			@RequestParam(required = true) Integer id) {
		OtcMerchant otcMerchant = adminOtcMerchantService.selectOtcMerchantById(id);
		if (otcMerchant == null) {
			return ReturnResult.FAILUER("商户不存在!");
		}
		if (otcMerchant.getStatus() != OtcMerchantStatusEnum.Passed.getCode()) {
			return ReturnResult.FAILUER("商户应为已通过状态!");
		}
		return adminOtcMerchantService.prohibitOtcMerchant(otcMerchant);
	}
	
	/**
	 * 恢复
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/resumeOtcMerchant")
	@ResponseBody
	public ReturnResult resumeOtcMerchant(
			@RequestParam(required = true) Integer id) {
		OtcMerchant otcMerchant = adminOtcMerchantService.selectOtcMerchantById(id);
		if (otcMerchant == null) {
			return ReturnResult.FAILUER("商户不存在!");
		}
		if (otcMerchant.getStatus() != OtcMerchantStatusEnum.Prohibited.getCode()) {
			return ReturnResult.FAILUER("商户应为已禁用状态!");
		}
		return adminOtcMerchantService.resumeOtcMerchant(otcMerchant);
	}
}
