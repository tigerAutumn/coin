package com.qkwl.admin.layui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.qkwl.common.dto.otc.SmsConfig;
import com.qkwl.common.rpc.admin.IAdminSmsConfigService;
import com.qkwl.common.util.ReturnResult;

@Controller
public class SmsConfigController {

	@Autowired
	private IAdminSmsConfigService adminSmsConfigService;
	
 	/**
	 * 支付方式管理页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/admin/smsConfigList")
	public ModelAndView getSmsConfigList(){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/smsConfigList");
			// 查询
			List<SmsConfig> list = adminSmsConfigService.selectSmsConfigList();
			modelAndView.addObject("smsConfigList", list);
			return modelAndView;
		} catch (Exception e) {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
		
	}
	
	/**
	 * 切换otc短信服务商
	 */
	@RequestMapping("/admin/transferSmsConfig")
	@ResponseBody
	public ReturnResult openSmsConfig(
			@RequestParam(value = "smsClazz", required = true) String smsClazz) {
		SmsConfig smsConfig = new SmsConfig();
		smsConfig = adminSmsConfigService.selectBySmsClazz(smsClazz);
		if (smsConfig == null) {
			return ReturnResult.FAILUER("短信商不存在!");
		}
		if (smsConfig.getIsActivity() == 1) {
			return ReturnResult.FAILUER("短信商已开启，请勿重复开启!");
		}
		smsConfig.setIsActivity(1);
		//开启otc短信服务商
		return adminSmsConfigService.update(smsConfig);
	}
	
}
