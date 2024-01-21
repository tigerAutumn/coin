package com.qkwl.admin.layui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.common.dto.Enum.otc.OtcOrderStatusEnum;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.chat.ChatMessage;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.otc.OtcAppeal;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.IAdminOtcOrderService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ReturnResult;

@Controller
public class OtcOrderController extends WebBaseController {
	private static final Logger logger = LoggerFactory.getLogger(OtcOrderController.class);

	@Autowired
	private IAdminOtcOrderService adminOtcOrderService;
	@Autowired
	private RedisHelper redisHelper;
	
	/**
	 * 分页大小
	 */	
 	private int numPerPage = Constant.adminPageSize;
	
	/**
 	 * 查询otc订单信息
 	 */
	@RequestMapping("admin/otc/otcOrderList")
	public ModelAndView getOtcOrderList(
			@RequestParam(value="coinId",required=false) Integer coinId,
			@RequestParam(value="userId",required=false) Integer userId,
			@RequestParam(value="side",required=false ,defaultValue="0") String side,
			@RequestParam(value="status",required=false,defaultValue="0") Integer status,
			@RequestParam(value="pageNum",required=false,defaultValue="1") Integer currentPage,
			@RequestParam(value="keywords",required=false) String keywords
			) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/otcOrderList");
			modelAndView.addObject("status", status);
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("keywords", keywords);
			modelAndView.addObject("coinId", coinId);
			modelAndView.addObject("side", side);
			//币种
			List<SystemCoinType> coinTypeList = redisHelper.getOpenOtcCoinTypeList();
			Map<Integer,String> coinTypeMap = new HashMap<>();
			coinTypeMap.put(0, "全部");
			for (SystemCoinType systemCoinType : coinTypeList) {
					coinTypeMap.put(systemCoinType.getId(), systemCoinType.getName());
			}
			modelAndView.addObject("coinTypeMap", coinTypeMap);
			//状态
			Map<Integer,String>  otcStatusMap = new HashMap<>();
			otcStatusMap.put(0, "全部");
			for (OtcOrderStatusEnum o:OtcOrderStatusEnum.values()) {
				otcStatusMap.put(o.getCode(), o.getValue());
			}
			modelAndView.addObject("otcStatusMap", otcStatusMap);
			//类型
			Map<String,String>  sideMap = new HashMap<>();
			sideMap.put("0", "全部");
			sideMap.put("BUY", "买入");
			sideMap.put("SELL", "卖出");
			modelAndView.addObject("sideMap", sideMap);
			
			//请求数据
			if (userId == null && "0".equals(side) && coinId == null && status == 0 && StringUtils.isEmpty(keywords)) {
				return modelAndView;
			}
			Map<String,Object> param = new HashMap<>();
			if(userId != null) {
				param.put("userId", userId);
			}
			if(!"0".equals(side)) {
				param.put("side", side);
			}
			if(coinId != null) {
				param.put("coinId", coinId);
			}
			if(status != 0) {
				param.put("status", status);
			}
			if(!StringUtils.isEmpty(keywords)) {
				param.put("keywords", keywords);
			}
			PageInfo<OtcOrder> listOrder = adminOtcOrderService.listOrder(param, currentPage, numPerPage);
			modelAndView.addObject("orderList",listOrder);
			return modelAndView;
		} catch (Exception e) {
			logger.error("查询otc订单异常",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}
	
	
	/**
 	 * 查询otc申诉
 	 */
	@RequestMapping("admin/otc/otcAppeal")
	public ModelAndView otcAppeal(
			@RequestParam(value="id",required=true) Long id
			) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/otcAppeal");
			OtcAppeal selectByOrderId = adminOtcOrderService.selectByOrderId(id);
			modelAndView.addObject("otcAppeal",selectByOrderId);
			return modelAndView;
		} catch (Exception e) {
			logger.error("查询otc申诉异常",e);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("comm/404");
			return modelAndView;
		}
	}
	
	/**
 	 * 提交otc申诉结果
 	 */
	@ResponseBody
	@RequestMapping("admin/otc/submitOtcAppeal")
	public ReturnResult submitOtcAppeal(
			@RequestParam(required=true) Integer id,
			@RequestParam(required=true) Byte result,
			@RequestParam(required=true) Integer winSide,
			@RequestParam(required=true) String remark,
			@RequestParam(required=true) String imageUrl
			) {
		try {
			OtcAppeal otcAppeal = new OtcAppeal();
			otcAppeal.setId(id.longValue());
			otcAppeal.setResult(result);
			otcAppeal.setWinSide(winSide);
			otcAppeal.setImageUrl(imageUrl);
			otcAppeal.setRemark(remark);
			ReturnResult submitOtcAppeal = adminOtcOrderService.submitOtcAppeal(otcAppeal);
			return submitOtcAppeal;
		} catch (Exception e) {
			logger.error("提交otc申诉结果异常，appealid："+id, e);
			return ReturnResult.FAILUER(e.getMessage());
		}
	}
	
	/**
 	 * 查询聊天记录
 	 */
	@RequestMapping("admin/otc/otcChatHistory")
	public ModelAndView otcChatHistory(
			@RequestParam(required=true) Integer id
			) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/chatHistory");
			OtcOrder findById = adminOtcOrderService.findById(id.longValue());
			if(findById == null) {
				return returnErrorModel("订单不存在");
			}
			modelAndView.addObject("order",findById);
			List<ChatMessage> chatHistory = adminOtcOrderService.chatHistory(id);
			modelAndView.addObject("chatHistory",chatHistory);
			return modelAndView;
		} catch (Exception e) {
			logger.error("查询otc订单异常",e);
			return returnErrorModel("系统异常");
		}
	}
	
	/**
 	 * 查询支付方式
 	 */
	@RequestMapping("admin/otc/userPayment")
	public ModelAndView userPayment(
			@RequestParam(required=true) Integer id
			) {
		try {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("otc/userPayment");
			FUserBankinfoDTO selectUserPaymentByOrder = adminOtcOrderService.selectUserPaymentByOrder(id);
			if(selectUserPaymentByOrder == null) {
				return returnErrorModel("支付方式未找到");
			}
			modelAndView.addObject("bankinfo",selectUserPaymentByOrder);
			return modelAndView;
		} catch (Exception e) {
			logger.error("查询otc订单异常",e);
			return returnErrorModel("系统异常");
		}
	}

}
