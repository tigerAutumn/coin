package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.otc.OtcUserOrder;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.Utils;
import com.qkwl.service.admin.bc.dao.OtcUserTransferMapper;
import com.qkwl.service.admin.bc.dao.UserOtcCoinWalletMapper;

@Service("adminOtcOrderServiceImplTX")
public class AdminOtcOrderServiceImplTX {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminOtcOrderServiceImplTX.class);
	
	@Autowired
	private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
	@Autowired
	private OtcUserTransferMapper userOtcTransferMapper;

	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Result userOtcOrderDellImpl(OtcUserOrder order) throws Exception {
		UserOtcCoinWallet buyOtcCoinWallet = selectByUidAndType(order.getBuyerId(), order.getCoinId());

		UserOtcCoinWallet sellOtcCoinWallet = selectByUidAndType(order.getSellerId(), order.getCoinId());

		if (sellOtcCoinWallet.getFrozen().compareTo(order.getAmount()) < 0) {
			throw new Exception("卖数额大于冻结 uid =" + sellOtcCoinWallet.getUid());
		}

		BigDecimal buyTotal = MathUtils.add(buyOtcCoinWallet.getTotal(), order.getAmount());

		BigDecimal sellFrozen = MathUtils.sub(sellOtcCoinWallet.getFrozen(), order.getAmount());

		// 扣手续费，发起方扣手续费
		if (order.getFee() != null && MathUtils.compareTo(order.getFee(), BigDecimal.ZERO) > 0) {
			if (order.getType().equals(Integer.valueOf("1"))) {
				buyTotal = MathUtils.sub(buyTotal, order.getFee());
			} else if (order.getType().equals(Integer.valueOf("2"))) {
				sellFrozen = MathUtils.sub(sellFrozen, order.getFee());
			}

			if (buyTotal.compareTo(BigDecimal.ZERO) < 0 || sellFrozen.compareTo(BigDecimal.ZERO) < 0) {
				throw new Exception("手续费计算出错  type = " + order.getType() + "buyid = " + order.getBuyerId()
						+ "buytotal = " + buyTotal + "sellid = " + order.getSellerId() + "sellFrozen = "
						+ sellFrozen + "aoumt = " + order.getAmount());
			}
		}

		buyOtcCoinWallet.setTotal(buyTotal);
		buyOtcCoinWallet.setGmtModified(Utils.getTimestamp());
		if (this.userOtcCoinWalletMapper.updateCAS(buyOtcCoinWallet) <= 0) {
			throw new Exception("buy更新买钱包失败 uid = " + order.getBuyerId() + "coinid = " + order.getCoinId());
		}

		OtcUserTransfer otcUserTransfer = new OtcUserTransfer();
		otcUserTransfer.setAmount(order.getAmount());
		otcUserTransfer.setCoinId(order.getCoinId());
		otcUserTransfer.setType(Integer.valueOf("3"));
		otcUserTransfer.setUserId(order.getBuyerId());
		otcUserTransfer.setCreateTime(Utils.getTimestamp());
		otcUserTransfer.setOtherUserId(order.getSellerId());
		otcUserTransfer.setFee(order.getFee());
		if (userOtcTransferMapper.insert(otcUserTransfer) <= 0) {
			throw new Exception("otc买入记录更新失败uid = " + order.getBuyerId() + "coinid = " + order.getCoinId());
		}

		sellOtcCoinWallet.setFrozen(sellFrozen);
		sellOtcCoinWallet.setGmtModified(Utils.getTimestamp());
		if (this.userOtcCoinWalletMapper.updateCAS(sellOtcCoinWallet) <= 0) {
			throw new Exception("sell更新卖钱包失败uid = " + order.getSellerId() + "coinid = " + order.getCoinId());
		}

		OtcUserTransfer otcOtherUserTransfer = new OtcUserTransfer();
		otcOtherUserTransfer.setAmount(order.getAmount());
		otcOtherUserTransfer.setCoinId(order.getCoinId());
		otcOtherUserTransfer.setType(Integer.valueOf("4"));
		otcOtherUserTransfer.setUserId(order.getSellerId());
		otcOtherUserTransfer.setCreateTime(Utils.getTimestamp());
		otcOtherUserTransfer.setOtherUserId(order.getBuyerId());
		otcUserTransfer.setFee(order.getFee());
		if (userOtcTransferMapper.insert(otcOtherUserTransfer) <= 0) {
			throw new Exception("otc卖出记录更新失败uid = " + order.getSellerId() + "coinid = " + order.getCoinId());

		}
		return Result.success("卖钱包更新成功");
	}
	
	@Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result userOtcUnFrozenImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("userOtcFrozen uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("userOtcFrozen coinId is null");

    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("userOtcFrozen amount <= 0");
    	}
    	
		UserOtcCoinWallet otcCoinWallet = selectByUidAndType(uid, coinId);
    	
    	if(otcCoinWallet.getFrozen().compareTo(amount) < 0 ) {
    		throw new Exception("otc钱包解冻余额太多 uid = " +  uid + "coinid = " + coinId + " 解冻金额   = " +  amount);
		}
		
    	if(userOtcCoinWalletMapper.updateUnFrozenCAS(amount, Utils.getTimestamp(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otc钱包解冻更新失败uid = " + uid + "coinid = " + coinId);
    	}
		
    	return Result.success("解冻成功");
    }
	
	private UserOtcCoinWallet selectByUidAndType(Integer uid, Integer coinId) {

		UserOtcCoinWallet otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);

		// 如果账号不存在，检查全部用户账户，创建缺失账户
		if (otcCoinWallet == null) {
			logger.info("selectByUidAndTypeAddLock otcCoinWallet is null insert one record = {}", uid);
			createLackWallet(uid, coinId);
			otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);
		}
		return otcCoinWallet;
	}
	
	/**
	 * 缺失otc账户
	 */
	private void createLackWallet(Integer uid, Integer coinId) {
		logger.info("createLackWallet uid = {} ", uid);
		UserOtcCoinWallet otcCoinWallet = new UserOtcCoinWallet();
		otcCoinWallet.setTotal(BigDecimal.ZERO);
		otcCoinWallet.setFrozen(BigDecimal.ZERO);
		otcCoinWallet.setBorrow(BigDecimal.ZERO);
		otcCoinWallet.setIco(BigDecimal.ZERO);
		otcCoinWallet.setCoinId(coinId);
		otcCoinWallet.setUid(uid);
		otcCoinWallet.setGmtCreate(Utils.getTimestamp());
		otcCoinWallet.setGmtModified(Utils.getTimestamp());
		if (this.userOtcCoinWalletMapper.insert(otcCoinWallet) <= 0) {
			logger.error("注册用户otc钱包超时！");
		}
	}
}
