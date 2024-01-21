package com.qkwl.service.activity.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeStatusEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.RedEnvelopeMapper;
import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import com.qkwl.service.activity.dao.UserWalletBalanceLogMapper;

@Service("returnRedEnvelopeService")
public class ReturnRedEnvelopeService {
	
	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReturnRedEnvelopeService.class);
	
	@Autowired
	private RedEnvelopeMapper redEnvelopeMapper;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	@Autowired
    private UserCoinWalletMapper coinWalletMapper;

	public void work() {
		//查询过期未发完的红包
		List<RedEnvelope> list = redEnvelopeMapper.selectOverdueEnvelope();
		for (RedEnvelope redEnvelope : list) {
			try {
				returnRedEnvelope(redEnvelope);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("退还红包异常，红包id:{}", redEnvelope.getId());
			}
		}
	}
	
	@Transactional(value="flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
	private void returnRedEnvelope(RedEnvelope redEnvelope) {
		BigDecimal amount = MathUtils.sub(redEnvelope.getAmount(), redEnvelope.getReceiveAmount());
		
		Result frozen2TotalResult = frozen2Total(redEnvelope.getUid(), redEnvelope.getCoinId(), amount);
		if (!frozen2TotalResult.getSuccess()) {
			logger.error("冻结转可用失败,红包id:{}", redEnvelope.getId());
			throw new BizException(ErrorCodeEnum.UNFROZEN_FAILED);
		}
		
		Date now = new Date();
		// 领取红包流水
		UserWalletBalanceLog addTotalLog = new UserWalletBalanceLog();
		addTotalLog.setUid(redEnvelope.getUid());
		addTotalLog.setCoinId(redEnvelope.getCoinId());
		addTotalLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
		addTotalLog.setChange(amount);
		addTotalLog.setSrcId(redEnvelope.getId());
		addTotalLog.setSrcType(UserWalletBalanceLogTypeEnum.RETURN_RED_ENVELOPE.getCode());
		addTotalLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
		addTotalLog.setCreatetime(now);
		addTotalLog.setCreatedatestamp(now.getTime());
		userWalletBalanceLogMapper.insert(addTotalLog);
		
		redEnvelope.setStatus(RedEnvelopeStatusEnum.RETURN.getCode());
		redEnvelopeMapper.updateStatus(redEnvelope);
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
