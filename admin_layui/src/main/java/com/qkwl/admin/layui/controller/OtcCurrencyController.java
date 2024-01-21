package com.qkwl.admin.layui.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcCurrency;
import com.qkwl.common.rpc.admin.IAdminOtcCurrencyService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcCurrencyController extends WebBaseController {

	@Autowired
	private IAdminOtcCurrencyService adminOtcCurrencyService;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
 	
 	/**
 	 * 查询otc法币信息
 	 */
	@RequestMapping("/admin/otcCurrencyList")
	public ModelAndView getOtcCurrencyList(
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="orderField",required=false,defaultValue="id") String orderField,
			@RequestParam(value="orderDirection",required=false,defaultValue="asc") String orderDirection
			) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("otc/otcCurrencyList");
		// 定义查询条件
		Pagination<OtcCurrency> pageParam = new Pagination<OtcCurrency>(currentPage, numPerPage);
		OtcCurrency otcCurrency = new OtcCurrency();
		
		pageParam.setOrderDirection(orderDirection);
		pageParam.setOrderField(orderField);

		//查询佣金列表
		Pagination<OtcCurrency> pagination = adminOtcCurrencyService.selectOtcCurrencyPageList(pageParam, otcCurrency);

		modelAndView.addObject("otcCurrencyList", pagination);
		return modelAndView;
	}
	
	/**
	 * 删除法币
	 * @param id
	 * @return
	 */
	@RequestMapping("/admin/deleteOtcCurrency")
	@ResponseBody
	public ReturnResult deleteOtcCurrency(
			@RequestParam(value = "id", required = false,defaultValue="0") Integer id) {
		OtcCurrency otcCurrency = new OtcCurrency();
		if (id > 0) {
			otcCurrency = adminOtcCurrencyService.selectOtcCurrencyById(id);
		}
		if (otcCurrency == null) {
			return ReturnResult.FAILUER("法币不存在!");
		}
		//删除法币
		return adminOtcCurrencyService.deleteOtcCurrency(otcCurrency);
	}
	
	/**
	 * 添加法币
	 * @return
	 */
	@RequestMapping("/admin/addOtcCurrency")
	public ModelAndView addOtcCurrency(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("otc/addOtcCurrency");
		return modelAndView;
	}
	
	/**
     * 保存新增的币种信息
     */
    @RequestMapping("admin/saveOtcCurrency")
    @ResponseBody
    public ReturnResult saveOtcCurrency(
            @RequestParam(value = "chineseName", required = false) String chineseName,
            @RequestParam(value = "englishName", required = false) String englishName,
            @RequestParam(value = "currencyCode", required = false) String currencyCode,
            @RequestParam(value = "status", required = false) Integer status
            ) throws Exception {
    	OtcCurrency otcCurrency = new OtcCurrency();
    	otcCurrency.setChineseName(chineseName);
    	otcCurrency.setEnglishName(englishName);
    	otcCurrency.setCurrencyCode(currencyCode);
        otcCurrency.setStatus(status);
        otcCurrency.setCreateTime(new Date());
        return adminOtcCurrencyService.insertOtcCurrency(otcCurrency);
    }
	
	/**
	 * 读取修改的法币信息
	 * @return
	 */
	@RequestMapping("/admin/changeOtcCurrency")
	public ModelAndView changeOtcCurrency(@RequestParam(value="id",required=false) Integer id){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/otc/changeOtcCurrency");
		if (id > 0) {
			OtcCurrency OtcCurrency = adminOtcCurrencyService.selectOtcCurrencyById(id);
			modelAndView.addObject("otcCurrency", OtcCurrency);
		}
		return modelAndView;
	}
	
	/**
     * 修改的币种信息
     */
    @RequestMapping("admin/updateOtcCurrency")
    @ResponseBody
    public ReturnResult updateOtcCurrency(
    		@RequestParam(value = "id", required = true,defaultValue = "0") Integer id,
    		@RequestParam(value = "chineseName", required = false) String chineseName,
            @RequestParam(value = "englishName", required = false) String englishName,
            @RequestParam(value = "currencyCode", required = false) String currencyCode,
            @RequestParam(value = "status", required = false) Integer status
            ) throws Exception {
    	OtcCurrency otcCurrency = new OtcCurrency();
		if (id > 0) {
			otcCurrency = adminOtcCurrencyService.selectOtcCurrencyById(id);
		}
		if (otcCurrency == null) {
			return ReturnResult.FAILUER("法币不存在!");
		}
		otcCurrency.setChineseName(chineseName);
    	otcCurrency.setEnglishName(englishName);
    	otcCurrency.setCurrencyCode(currencyCode);
        otcCurrency.setStatus(status);
		
        return adminOtcCurrencyService.updateOtcCurrency(otcCurrency);
    }
}
