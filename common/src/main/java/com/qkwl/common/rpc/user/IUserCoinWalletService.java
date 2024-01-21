/**
 * 
 */
package com.qkwl.common.rpc.user;

import java.math.BigDecimal;

import com.qkwl.common.entity.UserCoinWalletEntity;

public interface IUserCoinWalletService {

	/**
	 * @param uid
	 * @param walletId
	 * @param frozen
	 */
	void updateFronzenCAS(Integer uid, Integer walletId, BigDecimal frozen);

	/**
	 * @param userId
	 * @param coinId
	 * @return
	 */
	UserCoinWalletEntity getUserCoinWallet(Integer userId, Integer coinId);
				 

}
