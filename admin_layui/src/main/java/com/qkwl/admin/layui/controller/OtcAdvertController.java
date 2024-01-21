package com.qkwl.admin.layui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.rpc.admin.IAdminOtcAdvertService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcAdvertController extends WebBaseController {

	@Autowired
	private IAdminOtcAdvertService adminOtcAdvertService;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
	
	/**
 	 * 查询otc广告信息
 	 */
	@RequestMapping("/admin/otcAdvertList")
	public ModelAndView getOtcAdvertList(
			@RequestParam(value="id",required=false,defaultValue="0") Integer id,
			@RequestParam(value="coinName",required=false) String coinName,
			@RequestParam(value="userId",required=false,defaultValue="0") Integer userId,
			@RequestParam(value="side",required=false) String side,
			@RequestParam(value="status",required=false,defaultValue="0") Integer status,
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="create_time") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="desc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("otc/otcAdvertList");
		// 定义查询条件
		Pagination<OtcAdvert> pageParam = new Pagination<OtcAdvert>(currentPage, numPerPage);
		OtcAdvert otcAdvert = new OtcAdvert();
		//参数判断
		if (id > 0){
			otcAdvert.setId(id);
			modelAndView.addObject("id", id);
		}
		if (!StringUtils.isEmpty(coinName)){
			otcAdvert.setCoinName(coinName.toUpperCase());
			modelAndView.addObject("coinName", coinName.toUpperCase());
		}
		if (userId > 0){
			otcAdvert.setUserId(userId);
			modelAndView.addObject("userId", userId);
		}
		if (!StringUtils.isEmpty(side)){
			otcAdvert.setSide(side);
			modelAndView.addObject("side", side);
		}
		if (status > 0){
			otcAdvert.setStatus(status);
			modelAndView.addObject("status", status);
		}
		
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		if(id == 0 && StringUtils.isEmpty(coinName) && userId == 0 && StringUtils.isEmpty(side) && status == 0){
			return modelAndView;
		}
		//查询佣金列表
		Pagination<OtcAdvert> pagination = adminOtcAdvertService.selectOtcAdvertPageList(pageParam, otcAdvert);

		modelAndView.addObject("otcAdvertList", pagination);
		return modelAndView;
	}
	
	/**
 	 * 查询otc广告信息
 	 */
	@RequestMapping("/admin/advertPaymentList")
	public ModelAndView advertPaymentList(
			@RequestParam(value="id",required=false,defaultValue="0") Integer id
			) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("otc/advertPaymentList");
		List<FUserBankinfoDTO> bankinfoList = adminOtcAdvertService.getBankinfoList(id);
		modelAndView.addObject("bankinfoList", bankinfoList);
		return modelAndView;
	}
	
	@RequestMapping("/admin/frozenOtcAdvert")
	@ResponseBody
	public ReturnResult frozenOtcAdvert(
			@RequestParam(required = true) int id
			) {
		OtcAdvert advert = adminOtcAdvertService.selectAdvertById(id);
		if (advert == null) {
			return ReturnResult.FAILUER("广告不存在");
		}
		if (advert.getIsFrozen() == Constant.OTC_ADVERT_FROZEN) {
			return ReturnResult.FAILUER("广告已处于冻结状态");
		}
		advert.setIsFrozen(Constant.OTC_ADVERT_FROZEN);
		adminOtcAdvertService.updateAdvert(advert);
		return ReturnResult.SUCCESS("成功");
	}
	
	@RequestMapping("/admin/unfrozenOtcAdvert")
	@ResponseBody
	public ReturnResult unfrozenOtcAdvert(
			@RequestParam(required = true) int id
			) {
		OtcAdvert advert = adminOtcAdvertService.selectAdvertById(id);
		if (advert == null) {
			return ReturnResult.FAILUER("广告不存在");
		}
		if (advert.getIsFrozen() == Constant.OTC_ADVERT_UNFROZEN) {
			return ReturnResult.FAILUER("广告已处于解冻状态");
		}
		advert.setIsFrozen(Constant.OTC_ADVERT_UNFROZEN);
		adminOtcAdvertService.updateAdvert(advert);
		return ReturnResult.SUCCESS("成功");
	}
}
