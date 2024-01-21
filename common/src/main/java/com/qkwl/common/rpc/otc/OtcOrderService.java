package com.qkwl.common.rpc.otc;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.otc.OtcAppeal;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.result.Result;

public interface OtcOrderService {
	/**
	 * 创建订单
	 * @param otcOrder
	 * @return
	 */
	Result createOrder(OtcOrder otcOrder) throws BCException;
	
	/**
	 * 订单列表
	 * @param param
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	PageInfo<OtcOrder> listOrder(Map<String, Object> param,int pageNo,int pageSize);
	
	/**
	 * 根据id查询订单
	 * @param id
	 * @return
	 */
	OtcOrder findById(Long id);
	
	/**
	 * 确认打币 
	 * @param otcOrder订单
	 * @param user操作用户
	 * @return
	 */
	Result confirmOrder(OtcOrder otcOrder,FUser user) throws BCException;
	
	/**
	 * 确认支付，售出广告或买入广告都适用
	 * @param otcOrder
	 * @return
	 */
	Result payOrder(OtcOrder otcOrder) throws BCException;
	
	/**
	 * 取消订单
	 * @param otcOrder
	 * @return
	 */
	Result cancelOrder(OtcOrder otcOrder) throws BCException;
	
	/**
	 * 申诉订单
	 * @param otcOrder
	 * @return
	 */
	Result appealOrder(OtcOrder otcOrder,OtcAppeal otcAppeal)  throws BCException;
	
	/**
	 * 取消申诉订单
	 * @param otcOrder
	 * @return
	 */
	Result appealCancel(OtcOrder otcOrder) throws BCException;
	
	/**
	 * 延长订单时间
	 * @param otcOrder
	 * @return
	 */
	Result extendOrder(OtcOrder otcOrder) throws BCException;
	
	/**
	 * 提交对商家的评价
	 * @param otcOrder
	 * @return
	 */
	Result evaluation(Long orderId,Integer userId,Integer type) throws BCException;
	
	
	List<FUserBankinfoDTO> getBankInfoListByUser(Integer userId,Integer typeId);
	
	
	OtcAppeal selectByOrderId(Long orderId,int userId);
	
	int countOrder(Integer userId);
	
	/**
	 * 过期订单撤销
	 */
	void putOffUnableOrders() throws Exception;
	
	/**
	 * 根据
	 * @param advertId
	 * @return
	 */
	Integer countSuccBuyOrder(Integer userId);
	
	/**
	 * 根据
	 * @param advertId
	 * @return
	 */
	Integer countSuccSellOrder(Integer userId);
	
	/**
	 * 查询广告的30内订单完成率
	 * @param advertId
	 * @return
	 */
	String getCompletionRate(Integer userId);
	
	/**
	 * 查询广告的30内订单完成率
	 * @param advertId
	 * @return
	 */
	Integer getCmpOrders(Integer userId);
	
	/**
	 * 查询用户
	 * @param userId
	 * @return
	 */
	FUser getUser(Integer userId);
}