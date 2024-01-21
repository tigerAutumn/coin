package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.UserCoinWalletMapper;

@Service("adminOrepoolServiceImplTX")
public class AdminOrepoolServiceImplTX {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminOrepoolServiceImplTX.class);
	
	@Autowired
    private UserCoinWalletMapper userCoinWalletMapper;

	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result orepoolUnFrozenImpl(Integer uid,Integer coinId, BigDecimal borrow) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("orepoolUnFrozenImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("orepoolUnFrozenImpl coinId is null");

    	}
    	
    	if(borrow.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("orepoolUnFrozenImpl borrow <= 0");
    	}
    	
    	
		UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);
    	
    	if(userCoinWallet.getBorrow().compareTo(borrow) < 0 ) {
    		throw new Exception("矿池解冻余额太多 uid = " +  uid + "coinid = " + coinId + " 解冻金额   = " +  borrow);
		}
		
    	
    	if(userCoinWalletMapper.updateUnBorrowCAS(borrow, Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("矿池解冻更新失败uid = " + uid + "coinid = " + coinId);
    	}
    	
    	return Result.success("矿池解冻成功");
    }
	
	//获取币币账户钱包
    private UserCoinWallet selectWalletByUidAndType(Integer uid ,Integer coinId) {
    	UserCoinWallet userCoinWallet = userCoinWalletMapper.select(uid, coinId);

		//如果账号不存在，检查全部用户账户，创建缺失账户
		if (userCoinWallet == null) {
			logger.info("selectByUidAndTypeAddLock otcCoinWallet is null insert one record = {}", uid);
			createLackWallet(uid, coinId);
			userCoinWallet = userCoinWalletMapper.select(uid, coinId);
		}
		return userCoinWallet;
	}
    
    /**
	 * 缺失user币币账户
	 */
    private void createLackWallet(Integer uid ,Integer coinId) {
		logger.info("createLackWallet uid = {} ",uid);
		 UserCoinWallet userCoinWallet = new UserCoinWallet();
		 userCoinWallet.setTotal(BigDecimal.ZERO);
		 userCoinWallet.setFrozen(BigDecimal.ZERO);
		 userCoinWallet.setBorrow(BigDecimal.ZERO);
		 userCoinWallet.setIco(BigDecimal.ZERO);
		 userCoinWallet.setDepositFrozen(BigDecimal.ZERO);
		 userCoinWallet.setDepositFrozenTotal(BigDecimal.ZERO);
		 userCoinWallet.setCoinId(coinId);
		 userCoinWallet.setUid(uid);
		 userCoinWallet.setGmtCreate(Utils.getTimestamp());
		 userCoinWallet.setGmtModified(Utils.getTimestamp());
         if (userCoinWalletMapper.insert(userCoinWallet) <= 0) {
        	 logger.error("注册用户otc钱包超时！");
         }
	}
}
