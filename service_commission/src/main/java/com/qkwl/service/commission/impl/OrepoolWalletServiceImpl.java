package com.qkwl.service.commission.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.orepool.OrepoolIncomeRecord;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.orepool.IOrepoolService;
import com.qkwl.common.rpc.orepool.IOrepoolWalletService;
import com.qkwl.common.util.Utils;
import com.qkwl.service.commission.dao.OrepoolIncomeRecordMapper;
import com.qkwl.service.commission.dao.UserCoinWalletMapper;


@Service("orepoolWalletService")
public class OrepoolWalletServiceImpl implements IOrepoolWalletService {

	private static final Logger logger = LoggerFactory.getLogger(OrepoolWalletServiceImpl.class);

	@Autowired
	private RedisHelper redisHelper;
	
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    
    @Autowired
    private OrepoolIncomeRecordMapper orepoolIncomeRecordMapper;
    
    /**
    查询钱包余额
     */
    public BigDecimal getAccountBalance(Integer uid, Integer coinId) {
    	try {
    		UserCoinWallet coinWallet = userCoinWalletMapper.select(uid, coinId);
        	
        	return coinWallet.getTotal();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return BigDecimal.ZERO;
    }
    
    
    public Result orepoolFrozen(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
    		return orepoolFrozenImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
   矿池冻结
     * @throws Exception 
     */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result orepoolFrozenImpl(Integer uid,Integer coinId, BigDecimal borrow) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("OrepoolFrozenImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("OrepoolFrozenImpl coinId is null");

    	}
    	
    	if(borrow.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("OrepoolFrozenImpl borrow <= 0");
    	}
    	

    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);

		if(userCoinWallet.getTotal().compareTo(borrow) < 0 ) {
    		throw new Exception("矿池冻结余额不够");	
		}


    	if(userCoinWalletMapper.updateBorrowCAS(borrow, Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otcCoinWallet update err");
    	}
    
    	
    	return Result.success("矿池冻结成功");
	}
    
    public Result orepoolUnFrozen(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
        	return orepoolUnFrozenImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
    矿池解冻
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result orepoolUnFrozenImpl(Integer uid,Integer coinId, BigDecimal borrow) throws Exception {
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
    
    
    public Result orepoolncome(OrepoolIncomeRecord orepoolIncomeRecord) {
    	try {
			return orepoolncomeImpl(orepoolIncomeRecord);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    
    /**
    矿池收益
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result orepoolncomeImpl(OrepoolIncomeRecord orepoolIncomeRecord) throws Exception {
    	if(orepoolIncomeRecord.getUserId() == null || orepoolIncomeRecord.getUserId() <= 0) {
    		return Result.param("矿池 orepoolncomeImpl  uid is null");
    	}
    	
    	if(orepoolIncomeRecord.getIncomeCoinId() == null || orepoolIncomeRecord.getIncomeCoinId() <= 0) {
    		return Result.param("矿池orepoolncomeImpl  coinId is null");

    	}
    	
    	if(orepoolIncomeRecord.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("矿池orepoolncomeImpl  income <= 0");
    	}
    	//获取收益coinid钱包
    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(orepoolIncomeRecord.getUserId(), orepoolIncomeRecord.getIncomeCoinId());
    	
    	if(userCoinWalletMapper.updateWalletCAS(MathUtils.add(userCoinWallet.getTotal(), orepoolIncomeRecord.getIncome()), Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("矿池orepoolncomeImpl更新钱包 = " + orepoolIncomeRecord.getUserId() + "coinid = " + orepoolIncomeRecord.getIncomeCoinId());
    	}
    	
    	if(orepoolIncomeRecordMapper.insert(orepoolIncomeRecord) <= 0) {
    		throw new Exception("矿池orepoolncomeImpl插入记录 = " + orepoolIncomeRecord.getUserId() + "coinid = " + orepoolIncomeRecord.getIncomeCoinId());
    	}
    	
    	
    	return Result.success("矿池更新收益成功");
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
         if (this.userCoinWalletMapper.insert(userCoinWallet) <= 0) {
        	 logger.error("注册用户otc钱包超时！");
         }
	}
    
}
