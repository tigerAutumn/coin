package com.qkwl.common.rpc.orepool;

import java.math.BigDecimal;

import com.qkwl.common.dto.orepool.OrepoolIncomeRecord;
import com.qkwl.common.result.Result;

public interface IOrepoolWalletService {

	/**
	 * 查询钱包余额
	 * @param uid
	 * @param coinId
	 * @return
	 */
	public BigDecimal getAccountBalance(Integer uid, Integer coinId);
	
	/**
	 *  矿池冻结
	 * @param uid
	 * @param coinId
	 * @param borrow
	 * @return
	 */
	public Result orepoolFrozen(Integer uid,Integer coinId, BigDecimal borrow);
	
	/**
	 * 矿池解冻
	 * @param uid
	 * @param coinId
	 * @param borrow
	 * @return
	 */
	public Result orepoolUnFrozen(Integer uid,Integer coinId, BigDecimal borrow);
	
	/**
	 * 矿池收益
	 * @param orepoolIncomeRecord
	 * @return
	 */
	public Result orepoolncome(OrepoolIncomeRecord orepoolIncomeRecord);
}
