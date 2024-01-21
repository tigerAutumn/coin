package com.qkwl.common.rpc.capital;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.result.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户钱包接口
 */
public interface IUserWalletService {
    /**
     * 获取用户虚拟币钱包
     *
     * @param userId 用户ID
     * @param coinId 虚拟币ID
     * @return 虚拟币钱包实体对象
     */
    UserCoinWallet getUserCoinWallet(Integer userId, Integer coinId);

    /**
     * 获取用户虚拟币钱包列表
     *
     * @param userId 用户ID
     * @return 虚拟币钱包实体对象列表
     */
    List<UserCoinWallet> listUserCoinWallet(Integer userId);
    
    /**
	 * @param uid
	 * @param coinId
	 * @param amount
	 * @return
	 */
	public Result total2Frozen(Integer uid,Integer coinId, BigDecimal amount);
	
	/**
	 * @param uid
	 * @param coinId
	 * @param amount
	 * @return
	 */
	public Result addTotal(Integer uid,Integer coinId, BigDecimal amount);
	
	/**
	 * @param uid
	 * @param coinId
	 * @param amount
	 * @return
	 */
	public Result subFrozen(Integer uid,Integer coinId, BigDecimal amount);
	
	/**
	 * @param uid
	 * @param coinId
	 * @param amount
	 * @return
	 */
	public Result frozen2Total(Integer uid,Integer coinId, BigDecimal amount);
}
