package com.qkwl.service.capital.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.util.Utils;
import com.qkwl.service.capital.dao.UserCoinWalletMapper;

/**
 * 用户钱包接口
 */
@Service("userWalletService")
public class UserWalletServiceImpl implements IUserWalletService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserWalletServiceImpl.class);
	
    @Autowired
    private UserCoinWalletMapper coinWalletMapper;

    @Override
    public UserCoinWallet getUserCoinWallet(Integer userId, Integer coinId) {
        return coinWalletMapper.selectByUidAndCoin(userId, coinId);
    }

    @Override
    public List<UserCoinWallet> listUserCoinWallet(Integer userId) {
    	try {
    		return coinWalletMapper.selectByUid(userId);
		} catch (Exception e) {
			logger.error("查询用户钱包异常",e);
			return null;
		}
        
    }
    
    public Result total2Frozen(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
    		return total2FrozenImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
     * @throws Exception 
     */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result total2FrozenImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("total2FrozenImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("total2FrozenImpl coinId is null");

    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("total2FrozenImpl amount <= 0");
    	}

    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);
		if(userCoinWallet.getTotal().compareTo(amount) < 0 ) {
    		throw new Exception("余额不够");	
		}

    	if(coinWalletMapper.updateTotal2FrozenCAS(amount, Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otcCoinWallet update err");
    	}
    	
    	return Result.success("冻结成功");
	}
    
    public Result addTotal(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
			return addTotalImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result addTotalImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("addTotalImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("addTotalImpl coinId is null");

    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("addTotalImpl amount <= 0");
    	}
    	
    	//获取收益coinid钱包
    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);
    	if(coinWalletMapper.updateTotalCAS(MathUtils.add(userCoinWallet.getTotal(), amount), Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("addTotalImpl update err");
    	}
    	
    	return Result.success("增加可用成功");
    }
    
    public Result subFrozen(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
			return subFrozenImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result subFrozenImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("subFrozenImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("subFrozenImpl coinId is null");

    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("subFrozenImpl borrow <= 0");
    	}
    	
    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);
		if(userCoinWallet.getFrozen().compareTo(amount) < 0 ) {
    		throw new Exception("冻结不够");	
		}
    	
    	if(coinWalletMapper.updateFrozenCAS(MathUtils.sub(userCoinWallet.getFrozen(), amount), Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("subFrozenImpl update err");
    	}
    	
    	return Result.success("扣除冻结成功");
    }
    
    public Result frozen2Total(Integer uid,Integer coinId, BigDecimal borrow) {
    	try {
    		return frozen2TotalImpl(uid, coinId, borrow);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    /**
     * @throws Exception 
     */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Result frozen2TotalImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("frozen2TotalImpl uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("frozen2TotalImpl coinId is null");

    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("frozen2TotalImpl borrow <= 0");
    	}

    	UserCoinWallet userCoinWallet = selectWalletByUidAndType(uid, coinId);
		if(userCoinWallet.getFrozen().compareTo(amount) < 0 ) {
    		throw new Exception("冻结不够");	
		}

    	if(coinWalletMapper.updateFrozen2TotalCAS(amount, Utils.getTimestamp(), userCoinWallet.getId(), userCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otcCoinWallet update err");
    	}
    	
    	return Result.success("解冻成功");
	}
    
    //获取币币账户钱包
    private UserCoinWallet selectWalletByUidAndType(Integer uid ,Integer coinId) {
    	UserCoinWallet userCoinWallet = coinWalletMapper.selectByUidAndCoin(uid, coinId);

		//如果账号不存在，检查全部用户账户，创建缺失账户
		if (userCoinWallet == null) {
			logger.info("selectByUidAndTypeAddLock otcCoinWallet is null insert one record = {}", uid);
			createLackWallet(uid, coinId);
			userCoinWallet = coinWalletMapper.selectByUidAndCoin(uid, coinId);
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
         if (this.coinWalletMapper.insert(userCoinWallet) <= 0) {
        	 logger.error("注册用户otc钱包超时！");
         }
	}
}
