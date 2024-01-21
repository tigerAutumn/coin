package com.qkwl.service.otc.impl;

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

import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.Enum.orepool.OrepoolRecordStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcTransferTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.orepool.OrepoolRecord;
import com.qkwl.common.dto.otc.OtcUserOrder;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.otc.dao.FUserMapper;
import com.qkwl.service.otc.dao.OrepoolRecordMapper;
import com.qkwl.service.otc.dao.OtcAdvertMapper;
import com.qkwl.service.otc.dao.OtcMerchantMapper;
import com.qkwl.service.otc.dao.UserCoinWalletMapper;
import com.qkwl.service.otc.dao.UserOtcCoinWalletMapper;
import com.qkwl.service.otc.dao.UserOtcTransferMapper;

@Service("otcCoinWalletServiceImplTX")
public class OtcCoinWalletServiceImplTX {
	
	private static final Logger logger = LoggerFactory.getLogger(OtcCoinWalletServiceImplTX.class);
	
	@Autowired
	private RedisHelper redisHelper;
	
	@Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    
    @Autowired
    private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
    
    @Autowired
    private UserOtcTransferMapper userOtcTransferMapper;
    
    @Autowired
	private OrepoolRecordMapper orepoolRecordMapper;
    
    @Autowired
    private FUserMapper userMapper;
    
    @Autowired
    private OtcAdvertMapper otcAdvertMapper;
    
    @Autowired
    private OtcMerchantMapper otcMerchantMapper;

    /**
    用户账户和otc账户划转
     * @throws Exception 
     */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result userAccountOtcWalletTransferImpl(OtcUserTransfer userOtcTransfer) throws Exception{
    	if(userOtcTransfer == null) {
    		return Result.param("otcOrder is null");
    	}
    	
    	//前置验证
		Result check = check(userOtcTransfer.getUserId(), userOtcTransfer.getCoinId());
		if(!check.getSuccess()) {
			return check;
		}
    	
		UserOtcCoinWallet otcCoinWallet = selectOtcWalletByUidAndType(userOtcTransfer.getUserId(), userOtcTransfer.getCoinId());
    	logger.info("划转userAccountOtcWalletTransfer");

    	if(userOtcTransfer.getType().equals(OtcTransferTypeEnum.transferToOtc.getCode())) {
    		UserCoinWallet coinWallet = selectWalletByUidAndType(userOtcTransfer.getUserId(), userOtcTransfer.getCoinId());
        			
        	if(coinWallet.getTotal().compareTo(userOtcTransfer.getAmount()) < 0) {
        		return Result.failure("可划转余额不足");
        	}
        	
    		BigDecimal coinWalletTotal = MathUtils.sub(coinWallet.getTotal(), userOtcTransfer.getAmount());
        	BigDecimal otcCoinWalletTotal = MathUtils.add(otcCoinWallet.getTotal(), userOtcTransfer.getAmount());
        	
        	coinWallet.setTotal(coinWalletTotal);
        	coinWallet.setGmtModified(Utils.getTimestamp());
        	if (this.userCoinWalletMapper.updateWalletCAS(coinWallet.getTotal(), coinWallet.getGmtModified(), coinWallet.getId(), coinWallet.getVersion()) <= 0) {
                throw new Exception("用户账户钱包更新出错uid = " + userOtcTransfer.getUserId());
            }
        	
        	otcCoinWallet.setTotal(otcCoinWalletTotal);
        	otcCoinWallet.setGmtModified(Utils.getTimestamp());

        	if (this.userOtcCoinWalletMapper.updateWalletCAS(otcCoinWallet.getTotal(), otcCoinWallet.getFrozen(), otcCoinWallet.getGmtModified(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
                throw new Exception("用户otc钱包更新出错uid = " + userOtcTransfer.getUserId());
            }
    	}else if(userOtcTransfer.getType().equals(OtcTransferTypeEnum.otcTransferTo.getCode())) {
    		UserCoinWallet coinWallet = selectWalletByUidAndType(userOtcTransfer.getUserId(), userOtcTransfer.getCoinId());
    		
        	if(otcCoinWallet.getTotal().compareTo(userOtcTransfer.getAmount()) < 0) {
        		return Result.failure("用户otc钱包余额不够 uid = " + userOtcTransfer.getUserId() + "用户余额  = " + coinWallet.getTotal() + "划转金额   = " + userOtcTransfer.getAmount());
        	}
        	
        	//otc划转到创新区特殊处理，划转到用户创新区冻结
            SystemCoinType coinType = redisHelper.getCoinTypeSystem(userOtcTransfer.getCoinId());
            if(coinType.getIsInnovateAreaCoin()) {
            	BigDecimal depositFrozen = MathUtils.add(coinWallet.getDepositFrozen(), userOtcTransfer.getAmount());
            	BigDecimal depositFrozenTotal = MathUtils.add(coinWallet.getDepositFrozenTotal(), userOtcTransfer.getAmount());
            	BigDecimal otcCoinWalletTotal = MathUtils.sub(otcCoinWallet.getTotal(), userOtcTransfer.getAmount());

            	coinWallet.setDepositFrozen(depositFrozen);
            	coinWallet.setDepositFrozenTotal(depositFrozenTotal);
            	coinWallet.setGmtModified(Utils.getTimestamp());

            	if(userCoinWalletMapper.updateCAS(coinWallet) <= 0) {
                    throw new Exception("用户账户钱包更新出错 创新区 uid = " + userOtcTransfer.getUserId());
            	}
            	
            	//修改用户创新区锁仓记录
				OrepoolRecord record = new OrepoolRecord();
				record.setUserId(coinWallet.getUid());
				record.setLockCoinId(coinWallet.getCoinId());
				List<OrepoolRecord> innovationRecordList = orepoolRecordMapper.getInnovationRecord(record);
				for (OrepoolRecord orepoolRecord : innovationRecordList) {
					orepoolRecord.setStatus(OrepoolRecordStatusEnum.counting.getCode());
					orepoolRecord.setUpdateTime(new Date());
					orepoolRecordMapper.update(record);
				}
            	
            	otcCoinWallet.setTotal(otcCoinWalletTotal);
            	otcCoinWallet.setGmtModified(Utils.getTimestamp());

            	if (this.userOtcCoinWalletMapper.updateWalletCAS(otcCoinWallet.getTotal(), otcCoinWallet.getFrozen(), otcCoinWallet.getGmtModified(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
            		throw new Exception("用户otc钱包更新出错uid = " + userOtcTransfer.getUserId());
    	    	}
            	//创新区划转记录
            	userOtcTransfer.setType(OtcTransferTypeEnum.otcTransferToInnovate.getCode());
            }else {
            	BigDecimal otcCoinWalletTotal = MathUtils.sub(otcCoinWallet.getTotal(), userOtcTransfer.getAmount());
        		BigDecimal coinWalletTotal = MathUtils.add(coinWallet.getTotal(), userOtcTransfer.getAmount());
        		
            	coinWallet.setTotal(coinWalletTotal);
            	coinWallet.setGmtModified(Utils.getTimestamp());

            	if (this.userCoinWalletMapper.updateWalletCAS(coinWallet.getTotal(), coinWallet.getGmtModified(), coinWallet.getId(), coinWallet.getVersion()) <= 0) {
                    throw new Exception("用户账户钱包更新出错uid = " + userOtcTransfer.getUserId());
                }
            	
            	otcCoinWallet.setTotal(otcCoinWalletTotal);
            	otcCoinWallet.setGmtModified(Utils.getTimestamp());

            	if (this.userOtcCoinWalletMapper.updateWalletCAS(otcCoinWallet.getTotal(), otcCoinWallet.getFrozen(), otcCoinWallet.getGmtModified(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
            		throw new Exception("用户otc钱包更新出错uid = " + userOtcTransfer.getUserId());
    	    	}
            }

		}
		
		if(userOtcTransferMapper.insert(userOtcTransfer) <= 0) {
			throw new Exception("otc插入记录出错uid = " + userOtcTransfer.getUserId());
		}
		
		return Result.success("划转成功");
	}
    
  //买入卖出业务检查，
  	public Result check(Integer uid, Integer coinId) 
  	{

  		//判断用户状态 是否被otc禁用
  		FUser user = userMapper.selectByPrimaryKey(uid);
  		if(!user.isOtcAction()) {
  			return Result.failure(I18NUtils.getString("OtcCoinWalletServiceImpl.1"));
  		}
  		
  		//判断用户是否禁用
  		if(user.getFstatus().equals(UserStatusEnum.FORBBIN_VALUE)) {
  			return Result.failure("您的账户已被禁用！");
  		}
  		
  		//判断otc用户是否平衡
  		if(!otcBalance(uid, coinId)) {
  			return Result.failure("您的otc账户不平衡");
  		}
  		
		return Result.success();   
  	}
    
	/**
    otc账户冻结
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result userOtcFrozenImpl(Integer uid,Integer coinId, BigDecimal amount) throws Exception {
    	if(uid == null || uid <= 0) {
    		return Result.param("userOtcFrozen uid is null");
    	}
    	
    	if(coinId == null || coinId <= 0) {
    		return Result.param("userOtcFrozen coinId is null");
    	}
    	
    	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
    		return Result.param("userOtcFrozen amount <= 0");
    	}
    	

		UserOtcCoinWallet otcCoinWallet = selectOtcWalletByUidAndType(uid, coinId);
		if(otcCoinWallet.getTotal().compareTo(amount) < 0 ) {
    		throw new Exception("otc钱包冻结余额不够");
		}

    	if(userOtcCoinWalletMapper.updateFrozenCAS(amount, Utils.getTimestamp(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otcCoinWallet update err");
    	}
        	
    	return Result.success("冻结成功");
    }
    
    /**
    otc账户解冻
     * @throws Exception 
      */
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
    	
		UserOtcCoinWallet otcCoinWallet = selectOtcWalletByUidAndType(uid, coinId);
    	
    	if(otcCoinWallet.getFrozen().compareTo(amount) < 0 ) {
    		throw new Exception("otc钱包解冻余额太多 uid = " +  uid + "coinid = " + coinId + " 解冻金额   = " +  amount);
		}
		
    	if(userOtcCoinWalletMapper.updateUnFrozenCAS(amount, Utils.getTimestamp(), otcCoinWallet.getId(), otcCoinWallet.getVersion()) <= 0) {
    		throw new Exception("otc钱包解冻更新失败uid = " + uid + "coinid = " + coinId);
    	}
    	
    	return Result.success("解冻成功");

    }
    
    /**
    otc卖成交
     * @throws Exception 
      */
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result userOtcOrderDellImpl(OtcUserOrder order) throws Exception {

		UserOtcCoinWallet buyOtcCoinWallet = selectOtcWalletByUidAndType(order.getBuyerId(), order.getCoinId());

    	UserOtcCoinWallet sellOtcCoinWallet = selectOtcWalletByUidAndType(order.getSellerId(), order.getCoinId());

    	if(sellOtcCoinWallet.getFrozen().compareTo(order.getAmount()) < 0) {
    		throw new Exception("卖数额大于冻结 uid =" + sellOtcCoinWallet.getUid());
    	}
    	
    	BigDecimal buyTotal = MathUtils.add(buyOtcCoinWallet.getTotal(), order.getAmount());

    	BigDecimal sellFrozen = MathUtils.sub(sellOtcCoinWallet.getFrozen(), order.getAmount());
    	
    	BigDecimal buyFee = BigDecimal.ZERO;
    	BigDecimal sellFee = BigDecimal.ZERO;
    	
    	//扣手续费，发起方扣手续费
    	if(order.getFee() != null && MathUtils.compareTo(order.getFee(), BigDecimal.ZERO) > 0) {
    		if(order.getType().equals(Integer.valueOf("1"))) {
    			buyTotal = MathUtils.sub(buyTotal, order.getFee());
    			buyFee = order.getFee();
    		}else if(order.getType().equals(Integer.valueOf("2"))){
    			sellFrozen = MathUtils.sub(sellFrozen, order.getFee());
    			sellFee = order.getFee();
    		}
    		
    		if(buyTotal.compareTo(BigDecimal.ZERO) < 0 || sellFrozen.compareTo(BigDecimal.ZERO) < 0) {
        		throw new Exception("手续费计算出错  type = " + order.getType() + "buyid = " + order.getBuyerId() + "buytotal = " + buyTotal + "sellid = " + order.getSellerId() + "sellFrozen = " + sellFrozen + "aoumt = " + order.getAmount());
        	}
    	}
    	

    	if (this.userOtcCoinWalletMapper.updateWalletCAS(buyTotal, buyOtcCoinWallet.getFrozen(), Utils.getTimestamp(), buyOtcCoinWallet.getId(), buyOtcCoinWallet.getVersion()) <= 0) {
    		throw new Exception("buy更新买钱包失败 uid = " + order.getBuyerId() + "coinid = " + order.getCoinId());
        }
    	
    	OtcUserTransfer otcUserTransfer =  new OtcUserTransfer();
    	otcUserTransfer.setAmount(order.getAmount());
    	otcUserTransfer.setCoinId(order.getCoinId());
    	otcUserTransfer.setType(OtcTransferTypeEnum.otcBuy.getCode());
    	otcUserTransfer.setUserId(order.getBuyerId());
    	otcUserTransfer.setCreateTime(Utils.getTimestamp());
    	otcUserTransfer.setOtherUserId(order.getSellerId());
    	otcUserTransfer.setFee(buyFee);
    	if(userOtcTransferMapper.insert(otcUserTransfer) <= 0) {
    		throw new Exception("otc买入记录更新失败uid = " + order.getBuyerId() + "coinid = " + order.getCoinId());
    	}
    	
    	
    	if (this.userOtcCoinWalletMapper.updateWalletCAS(sellOtcCoinWallet.getTotal(), sellFrozen, Utils.getTimestamp(), sellOtcCoinWallet.getId(), sellOtcCoinWallet.getVersion()) <= 0) {
			throw new Exception("sell更新卖钱包失败uid = " + order.getSellerId() + "coinid = " + order.getCoinId());
        }
    	
    	OtcUserTransfer otcOtherUserTransfer =  new OtcUserTransfer();
    	otcOtherUserTransfer.setAmount(order.getAmount());
    	otcOtherUserTransfer.setCoinId(order.getCoinId());
    	otcOtherUserTransfer.setType(OtcTransferTypeEnum.otcSell.getCode());
    	otcOtherUserTransfer.setUserId(order.getSellerId());
    	otcOtherUserTransfer.setCreateTime(Utils.getTimestamp());
    	otcOtherUserTransfer.setOtherUserId(order.getBuyerId());
    	otcOtherUserTransfer.setFee(sellFee);

    	if(userOtcTransferMapper.insert(otcOtherUserTransfer) <= 0) {
			throw new Exception("otc卖出记录更新失败uid = " + order.getSellerId() + "coinid = " + order.getCoinId());
        }
    	
    	
    	return Result.success("钱包更新成功");
    }
    
    
    
    public UserOtcCoinWallet selectOtcWalletByUidAndType(Integer uid ,Integer coinId) {
    	UserOtcCoinWallet otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);

		//如果账号不存在，检查全部用户账户，创建缺失账户
		if (otcCoinWallet == null) {
			logger.info("selectByUidAndTypeAddLock otcCoinWallet is null insert one record = {}", uid);
			createLackOtcWallet(uid, coinId);
			otcCoinWallet = userOtcCoinWalletMapper.select(uid, coinId);
		}
		return otcCoinWallet;
	}
    
    /**
   	 * 缺失otc账户
   	 */
   private void createLackOtcWallet(Integer uid ,Integer coinId) {
	logger.info("createLackOtcWallet uid = {} ",uid);
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
   
   /**
    * otc资产平衡
    */
   public boolean otcBalance(Integer userId ,Integer coinId) {
   	//查询用户OTC钱包
		UserOtcCoinWallet userOtcCoinWallet = userOtcCoinWalletMapper.select(userId, coinId);
		if(userOtcCoinWallet == null) {
			return false;
		}
		
	    BigDecimal in = BigDecimal.ZERO;
       BigDecimal out = BigDecimal.ZERO;
       BigDecimal buyAmount = BigDecimal.ZERO;
       BigDecimal sellAmount = BigDecimal.ZERO;
       BigDecimal fee = BigDecimal.ZERO;
       BigDecimal outToInnovateArea = BigDecimal.ZERO;
       BigDecimal otcMerchantDeposit = BigDecimal.ZERO;
       
       //查询多种类型的总额
       //转入
       OtcUserTransfer inObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.transferToOtc.getCode(),coinId);
       in = inObj.getAmount();
       
       //转出
       OtcUserTransfer outObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcTransferTo.getCode(),coinId);
       out = outObj.getAmount();
       
       //买入
       OtcUserTransfer buyAmountObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcBuy.getCode(),coinId);
       buyAmount = buyAmountObj.getAmount();
       fee = MathUtils.add(buyAmountObj.getFee(), fee);
       
       //卖出
       OtcUserTransfer sellAmountObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcSell.getCode(),coinId);
       sellAmount = sellAmountObj.getAmount();
       fee = MathUtils.add(sellAmountObj.getFee(), fee);
       
       //otc转入创新区
       OtcUserTransfer outToInnovateAreaObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcTransferToInnovate.getCode(),coinId);
       outToInnovateArea = outToInnovateAreaObj.getAmount();
       out = MathUtils.add(outToInnovateArea, out);
       
       //otc商户押金
       OtcUserTransfer otcMerchantDepositObj = userOtcTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcMerchantDeposit.getCode(),coinId);
       otcMerchantDeposit = otcMerchantDepositObj.getAmount();
       out = MathUtils.add(otcMerchantDeposit, out);
       
       BigDecimal amountSub = MathUtils.add(MathUtils.add(out,sellAmount),fee);
       BigDecimal total = MathUtils.add(userOtcCoinWallet.getFrozen(),userOtcCoinWallet.getTotal());
       BigDecimal balance = MathUtils.sub(MathUtils.sub(MathUtils.add(amountSub, total),in),buyAmount);
   	
//       //上架中的广告金额
//       BigDecimal adFrozen = otcAdvertMapper.getAdvertFrozen(userId, coinId);
//       //订单已消耗
//       BigDecimal orderConsumption = otcAdvertMapper.getOrderConsumption(userId, coinId);
//       
//       adFrozen = MathUtils.sub(adFrozen, orderConsumption);
//       //进行中的订单金额
//       BigDecimal orderFrozen = otcAdvertMapper.getOrderFrozen(userId, coinId);
//       
//       //商户押金冻结
//       BigDecimal deposit = BigDecimal.ZERO;
//       if (coinId == 9) {
//       	deposit = otcMerchantMapper.getDeposit(userId);
//       	if (deposit == null) {
//       		deposit = BigDecimal.ZERO;
//       	}
//		}
//       		
//       BigDecimal frozen = userOtcCoinWallet.getFrozen();
//       BigDecimal frozenBalance = MathUtils.sub(MathUtils.add(MathUtils.add(adFrozen, orderFrozen), deposit),frozen);
       
       if(balance.compareTo(BigDecimal.ZERO)!=0) {
       	logger.info("========资产不平衡==userId:"+userId+"==coinId:"+coinId+"==balance:"+balance+"========");
       	return false;
       } /*else if(frozenBalance.compareTo(BigDecimal.ZERO)!=0) {
       	logger.info("========冻结数不对==userId:"+userId+"==coinId:"+coinId+"==frozenBalance:"+frozenBalance+"========");
       	return false;
       }*/ else {
       	return true;
       }
   }
}
