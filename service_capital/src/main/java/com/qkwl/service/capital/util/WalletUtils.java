package com.qkwl.service.capital.util;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.service.capital.dao.UserCoinWalletMapper;

@Component("walletUtils")
public class WalletUtils {
	
	@Autowired
	UserCoinWalletMapper userCoinWalletMapper;

	//可用增加
	public final static int total_add = 0;
	//可用扣除
	public final static int total_sub = 1;
	//冻结增加
	public final static int frozen_add = 2;
	//冻结扣除
	public final static int frozen_sub = 3;
	//可用转冻结
	public final static int total_to_frozen = 4;
	//冻结转可用
	public final static int frozen_to_total = 5;
	
	int TRY_TIMES = 3;
	
	
	@Transactional(value="xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean change(Integer userId,Integer coinId , int businessType ,BigDecimal amount) throws BCException {
		if(userId == null || coinId == null || amount == null || BigDecimal.ZERO.equals(amount)) {
			throw new BCException("参数错误");
		}
		int trytimes = 0;
		while (true ) {
			UserCoinWallet wallet = userCoinWalletMapper.selectByUidAndCoin(userId, coinId);
			if(wallet == null) {
				throw new BizException(ErrorCodeEnum.UNKNOWN_WALLET);
			}
			switch (businessType) {
			case total_add:
				wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
				break;
			case total_sub:
				wallet.setTotal(MathUtils.sub(wallet.getTotal(), amount));
				break;
			case frozen_add:
				wallet.setFrozen(MathUtils.add(wallet.getFrozen(), amount));
				break;
			case frozen_sub:
				wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
				break;
			case total_to_frozen:
				wallet.setTotal(MathUtils.sub(wallet.getTotal(), amount));
				wallet.setFrozen(MathUtils.add(wallet.getFrozen(), amount));
				break;
			case frozen_to_total:
				wallet.setTotal(MathUtils.add(wallet.getTotal(), amount));
				wallet.setFrozen(MathUtils.sub(wallet.getFrozen(), amount));
				break;
			default:
				return false;
			}
			if(BigDecimal.ZERO.compareTo(wallet.getTotal()) > 0 ) {
				throw new BizException(ErrorCodeEnum.TOTAL_NOT_ENOUGH);
			}
			if(BigDecimal.ZERO.compareTo(wallet.getFrozen()) > 0) {
				throw new BizException(ErrorCodeEnum.SUB_FROZEN_FAILED);
			}
			wallet.setGmtModified(new Date());
			if(userCoinWalletMapper.updateByPrimaryKey(wallet) == 0) {
				if (trytimes == TRY_TIMES) {
			        throw new BizException(ErrorCodeEnum.DEFAULT);
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
