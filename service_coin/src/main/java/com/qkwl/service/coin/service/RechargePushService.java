package com.qkwl.service.coin.service;

import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.exceptions.BCException;

public interface RechargePushService {
	
	/**
	 * 处理充值
	 * @param record 充值记录
	 * @return true 入库成功，false 入库失败 
	 * @throws BCException 
	 * */
	public boolean handlingRechargePush(SystemCoinType coinType,FVirtualCapitalOperationDTO record) throws BCException;
	
	/**
	 * 处理充值确认
	 * @param record 充值记录
	 * @return true 入库成功，false 入库失败 
	 * @throws BCException 
	 * */
	public boolean handlingConfirmRecharge(SystemCoinType coinType,FVirtualCapitalOperationDTO record) throws BCException;
	
	
	/**
	 * 更新充值确认数
	 * @param coinType 币种
	 * @return true 有更新数据，false 没有更新数据 
	 * */
	public boolean updateConfirm(SystemCoinType coinType);
	
	
	/**
	 * 查询确认数到了的记录，入账
	 * @param coinType 币种
	 * @return true 有更新数据，false 没有更新数据 
	 * */
	public boolean rechargeArrivals(SystemCoinType coinType);

}
