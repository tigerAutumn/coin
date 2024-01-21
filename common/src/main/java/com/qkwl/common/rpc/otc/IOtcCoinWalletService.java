package com.qkwl.common.rpc.otc;

import java.math.BigDecimal;
import java.util.List;

import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcUserOrder;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.result.Result;

public interface IOtcCoinWalletService {

	/**
	 * 查询otc钱包余额
	 * @param uid 
	 * @param coinId
	 * @return
	 */
	BigDecimal getOtcAccountBalance(Integer uid, Integer coinId);
	/**
	 * 查询账户钱包余额
	 * @param uid 
	 * @param coinId
	 * @return
	 */
	BigDecimal getAccountBalance(Integer uid, Integer coinId);

	/**
	 * 查询otc资产列表
	 * @param uid 
	 * @return
	 */
	List<UserOtcCoinWallet> listUserCoinWallet(Integer userId);
	/**
    用户账户和otc账户划转
	 * @throws Exception 
     */
	Result userAccountOtcWalletTransfer(OtcUserTransfer userOtcTransfer);
    
    /**
   otc账户冻结
     * @throws Exception 
     */
	Result userOtcFrozen(Integer uid,Integer coinId, BigDecimal amount);
    
    /**
    otc账户解冻
     * @throws Exception 
      */
	Result userOtcUnFrozen(Integer uid,Integer coinId, BigDecimal amount);
    
    /**
    otc订单成交
     * @throws Exception 
      */
	Result userOtcOrderDell(OtcUserOrder order) throws Exception;
    
	/**
    otc划转记录
      */
	Pagination<OtcUserTransfer> selectTransferListByUser(Integer fuid, Integer type, Integer coidId, String beginDate,
			String endDate, Pagination<OtcUserTransfer> page, String coinName);
	
	boolean otcBalance(Integer uid,Integer coinId);
	
	UserOtcCoinWallet selectOtcWalletByUidAndType(Integer uid ,Integer coinId);
}
