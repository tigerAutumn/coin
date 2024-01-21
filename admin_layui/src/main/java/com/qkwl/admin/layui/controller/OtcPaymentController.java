package com.qkwl.admin.layui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcPaymentTypeEnum;
import com.qkwl.common.dto.admin.FAdmin;
import com.qkwl.common.dto.otc.GoPaymentJspResp;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminC2CService;
import com.qkwl.common.rpc.admin.IAdminOtcPaymentService;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcPaymentController extends WebBaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(OtcPaymentController.class);
	
	@Autowired
	private IAdminC2CService adminC2CService;
	
	@Autowired
	private IAdminOtcPaymentService adminOTCService;

	@Autowired
	private RedisHelper redisHelper;


	
	//**************************************  支付方式 ****************************************************** 
	/**
	 * 支付方式管理页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/otc/list_payment")
	public ModelAndView listPayment(){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/otcPaymentPage");
			// 查询
			List<OtcPayment> list = adminOTCService.listPayment();
			modelAndView.addObject("OtcPaymentList", list);
			return modelAndView;
		} catch (Exception e) {
			logger.error("访问admin/otc/list_payment异常",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
		
	}

	/**
	 * 支付方式新增修改页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/otc/goPaymentJSP")
	public ModelAndView goPaymentJSP(
			 @RequestParam(value = "url", required = false) String url,
	         @RequestParam(value = "id", defaultValue = "0") Integer id){
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName(url);
			Map<Integer,String>  paymentType = new HashMap<>();
			for (OtcPaymentTypeEnum OtcPaymentTypeEnum : OtcPaymentTypeEnum.values()) {
				paymentType.put(OtcPaymentTypeEnum.getCode(), OtcPaymentTypeEnum.getValue());
			}
			modelAndView.addObject("paymentType", paymentType);
			// 查询
			if(id != null && id != 0) {
				//OtcPayment selectPaymentByPrimaryKey = adminOTCService.selectPaymentByPrimaryKey(id);
				OtcPayment resp=adminOTCService.goPaymentJSP(id);
				
				if(resp != null) {
					modelAndView.addObject("payment", resp);
				}
			}
			return modelAndView;
		} catch (Exception e) {
			logger.error("goPaymentJSP 执行异常{url:"+url +",id:"+id+"}",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}
	
	
	/**
	 * 添加支付方式
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/otc/addPayment")
	@ResponseBody
	public ReturnResult addPayment(OtcPayment otcPayment){
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			int saveC2CBusiness = adminOTCService.savePayment(otcPayment);
			if(saveC2CBusiness == 0) {
				return ReturnResult.FAILUER("添加支付方式失败");
			}
			return ReturnResult.SUCCESS("成功");
	}
	
	/**
	 * 修改支付方式
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/otc/updatePayment")
	@ResponseBody
	public ReturnResult updatePayment(OtcPayment otcPayment){
			HttpServletRequest request = sessionContextUtils.getContextRequest();
			FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
			int saveC2CBusiness = adminOTCService.updatePaymentWithMultiLang(otcPayment);
			if(saveC2CBusiness == 0) {
				return ReturnResult.FAILUER("修改支付方式失败");
			}
			return ReturnResult.SUCCESS("成功");
	}
	/**
	 * 支付方式更改状态
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("admin/c2c/updatePaymentStatus")
	@ResponseBody
	public ReturnResult updateMerchantStatus(
			@RequestParam(required = true) int status,
			@RequestParam(required = true) int id){
		try {
			OtcPayment selectPaymentByPrimaryKey = adminOTCService.selectPaymentByPrimaryKey(id);
			if(selectPaymentByPrimaryKey == null) {
				return ReturnResult.FAILUER("支付方式不存在");
			}
			if(OtcPaymentStatusEnum.Normal.getCode() != status && OtcPaymentStatusEnum.Prohibit.getCode() != status) {
				return ReturnResult.FAILUER("状态错误");
			}
			if(selectPaymentByPrimaryKey.getStatus() == status) {
				return ReturnResult.SUCCESS("成功");
			}else {
				selectPaymentByPrimaryKey.setStatus(status);
				HttpServletRequest request = sessionContextUtils.getContextRequest();
				FAdmin admin = (FAdmin) request.getSession().getAttribute("login_admin");
				int updateC2CBusinessById = adminOTCService.updatePayment(selectPaymentByPrimaryKey);
				if(updateC2CBusinessById == 0) {
					return ReturnResult.FAILUER("更新失败");
				}
			}
			return ReturnResult.SUCCESS("成功");
		} catch (Exception e) {
			logger.error("OTCController.updateMerchantStatus 异常:{id:"+id+",status："+status +"}",e);
			return ReturnResult.FAILUER("更新失败");
		}
	}
	
}
