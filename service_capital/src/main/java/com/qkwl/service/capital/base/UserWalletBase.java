package com.qkwl.service.capital.base;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.service.capital.dao.UserCoinWalletMapper;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包操作
 * Created by wangchen on 2017-04-19.
 */
public class UserWalletBase {

    @Autowired
    private UserCoinWalletMapper coinWalletMapper;


    /**
     * 更新虚拟币钱包可用操作
     *
     * @param uid    用户
     * @param coinId 币种ID
     * @param total  可用虚拟币金额（正数是加，负数是减）
     * @return true-成功，false-失败
     * @throws BCException 
     */
    protected boolean updateUserCoinWalletTotal(Integer uid, Integer coinId, BigDecimal total) throws BCException {
    	return updateUserCoinWallet(uid, coinId, total, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * 更新虚拟币钱包冻结操作
     *
     * @param uid         用户
     * @param coinId      币种ID
     * @param freezeTotal 冻结虚拟币金额（正数是加，负数是减）
     * @return true-成功，false-失败
     * @throws BCException 
     */
    protected boolean updateUserCoinWalletFrozen(Integer uid, Integer coinId, BigDecimal freezeTotal) throws BCException {
		return updateUserCoinWallet(uid, coinId, BigDecimal.ZERO, freezeTotal, BigDecimal.ZERO);
    }

    /**
     * 更新虚拟币钱包理财操作
     *
     * @param uid         用户
     * @param coinId      币种ID
     * @param borrowTotal 理财虚拟币金额（正数是加，负数是减）
     * @return true-成功，false-失败
     * @throws BCException 
     */
    protected boolean updateUserCoinWalletBorrow(Integer uid, Integer coinId, BigDecimal borrowTotal) throws BCException {
		return updateUserCoinWallet(uid, coinId, BigDecimal.ZERO, BigDecimal.ZERO, borrowTotal);
    }

    /**
     * 更新虚拟币钱包操作（不含借贷）
     *
     * @param uid         用户
     * @param coinId      币种ID
     * @param total       可用虚拟币金额（正数是加，负数是减）
     * @param freezeTotal 冻结虚拟币金额（正数是加，负数是减）
     * @return true-成功，false-失败
     * @throws BCException 
     */
    protected boolean updateUserCoinWallet(Integer uid, Integer coinId, BigDecimal total, BigDecimal freezeTotal) throws BCException {
		return updateUserCoinWallet(uid, coinId, total, freezeTotal, BigDecimal.ZERO);
    }

    /**
     * 更新虚拟币钱包操作
     *
     * @param uid         用户
     * @param coinId      币种ID
     * @param total       可用虚拟币金额（正数是加，负数是减）
     * @param freezeTotal 冻结虚拟币金额（正数是加，负数是减）
     * @param borrowTotal 理财金额（正数是加，负数是减）
     * @return true-成功，false-失败
     * @throws BCException 
     */
    protected boolean updateUserCoinWallet(Integer uid, Integer coinId, BigDecimal total, BigDecimal freezeTotal, BigDecimal borrowTotal) throws BCException {
        int trytimes = 0;
		while (true ) {
			UserCoinWallet coinWallet = coinWalletMapper.selectByUidAndCoin(uid, coinId);
	        if (coinWallet == null) {
	            return false;
	        }
	        coinWallet.setTotal(MathUtils.add(coinWallet.getTotal(), total));
	        coinWallet.setFrozen(MathUtils.add(coinWallet.getFrozen(), freezeTotal));
	        coinWallet.setBorrow(MathUtils.add(coinWallet.getBorrow(), borrowTotal));
	        coinWallet.setGmtModified(new Date());
	        // 可用金额判断
	        if (coinWallet.getTotal().compareTo(BigDecimal.ZERO) == -1) {
	            return false;
	        }
	        // 冻结金额判断
	        if (coinWallet.getFrozen().compareTo(BigDecimal.ZERO) == -1) {
	            return false;
	        }
	        // 借贷金额
	        if (coinWallet.getBorrow().compareTo(BigDecimal.ZERO) == -1) {
	            return false;
	        }
			if(coinWalletMapper.updateByPrimaryKey(coinWallet) == 0) {
				if (trytimes == 3) {
			        throw new BCException("更改失败");
		        }
		        trytimes = trytimes +1;
		        try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				return true;
			}
		}
    }
}
