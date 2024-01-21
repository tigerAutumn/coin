package com.qkwl.common.rpc.admin;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.chat.ChatMessage;
import com.qkwl.common.dto.otc.OtcAppeal;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.util.ReturnResult;

public interface IAdminOtcOrderService {
	PageInfo<OtcOrder> listOrder(Map<String, Object> param,int pageNo,int pageSize);
	
	/**
	 * 根据id查询订单
	 * @param id
	 * @return
	 */
	OtcOrder findById(Long id);
	
	/**
	 * 查询申诉单
	 * @param orderId
	 * @param userId
	 * @return
	 */
	OtcAppeal selectByOrderId(Long orderId);
	
	/**
	 * 处理申诉
	 * @param otcAppeal
	 * @return
	 */
	ReturnResult submitOtcAppeal(OtcAppeal otcAppeal)throws Exception;
	
	/**
	 * 查询聊天记录
	 * @param orderId
	 * @return
	 */
	List<ChatMessage> chatHistory(Integer orderId);
	
	/**
	 * 查询支付方式
	 * @param orderId
	 * @return
	 */
	FUserBankinfoDTO selectUserPaymentByOrder(Integer orderId);
}
