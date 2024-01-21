package com.qkwl.service.otc.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.qkwl.common.dto.Enum.otc.OtcTransferTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcUserOrder;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.Utils;
import com.qkwl.service.otc.dao.OtcAdvertMapper;
import com.qkwl.service.otc.dao.OtcMerchantMapper;
import com.qkwl.service.otc.dao.OtcOrderMapper;
import com.qkwl.service.otc.dao.UserCoinWalletMapper;
import com.qkwl.service.otc.dao.UserOtcCoinWalletMapper;
import com.qkwl.service.otc.dao.UserOtcTransferMapper;


@Service("otcCoinWalletService")
public class OtcCoinWalletServiceImpl implements IOtcCoinWalletService{
	private static final Logger logger = LoggerFactory.getLogger(OtcCoinWalletServiceImpl.class);

	@Autowired
	private RedisHelper redisHelper;
	
    @Autowired
    private UserCoinWalletMapper userCoinWalletMapper;
    
    @Autowired
    private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
    
    @Autowired
    private UserOtcTransferMapper userOtcTransferMapper;
    
    @Autowired
    private OtcCoinWalletServiceImplTX otcCoinWalletServiceImplTX;
    
    @Autowired
    private OtcAdvertMapper otcAdvertMapper;
    
    @Autowired
    private OtcMerchantMapper otcMerchantMapper;
    
    /**
    查询钱包余额
     */
    @Override
    public BigDecimal getAccountBalance(Integer uid, Integer coinId) {
    	try {
    		UserCoinWallet coinWallet = userCoinWalletMapper.select(uid, coinId);
        	return coinWallet.getTotal();
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
    }
    
    /**
    查询otc钱包余额
     */
    @Override
    public BigDecimal getOtcAccountBalance(Integer uid, Integer coinId) {
    	try {
    		UserOtcCoinWallet otcCoinWallet = selectOtcWalletByUidAndType(uid, coinId);
        	
        	return otcCoinWallet.getTotal();
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
    }
    
    /**
    otc资产
     * @throws Exception 
     */
    @Override
    public List<UserOtcCoinWallet>  listUserCoinWallet(Integer userId) {
    	try {
    		List<SystemCoinType> coinTypes= redisHelper.getOpenOtcCoinTypeList();
    		
    		if(coinTypes != null) {
				List<Integer> coinidsList = new ArrayList<Integer>();
		
				for(SystemCoinType coinType : coinTypes) {
					selectOtcWalletByUidAndType(userId, coinType.getId());
					coinidsList.add(coinType.getId());
				}
    			
				return userOtcCoinWalletMapper.selectByUidAndCoinids(userId, coinidsList);
    		}else {
    			return null;
    		}   		
		} catch (Exception e) {
			logger.error("查询otc用户钱包异常",e);
			return null;
		}
        
    }
    

    @Override
    public Result userAccountOtcWalletTransfer(OtcUserTransfer userOtcTransfer) {
    	try {
			return otcCoinWalletServiceImplTX.userAccountOtcWalletTransferImpl(userOtcTransfer);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    }
    
    
    
    @Override
    public Result userOtcFrozen(Integer uid,Integer coinId, BigDecimal amount){
    	try {
    		return otcCoinWalletServiceImplTX.userOtcFrozenImpl(uid, coinId, amount);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}
    	    	
    }
    
    @Override
    public Result userOtcUnFrozen(Integer uid,Integer coinId, BigDecimal amount){
    	try {
    		return otcCoinWalletServiceImplTX.userOtcUnFrozenImpl(uid, coinId, amount);
		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}    	
    }

    @Override
    public Result userOtcOrderDell(OtcUserOrder order){
    	try {
    		return otcCoinWalletServiceImplTX.userOtcOrderDellImpl(order);

		} catch (Exception e) {
			e.printStackTrace();
			return Result.failure(e.getMessage());
		}  	
    }
    
    
    /**
     * 查询otc划转记录
     *
     * @param fuid 用户ID
     * @param page 分页实体对象
     * @return 分页实体对象
     * @see com.qkwl.common.rpc.user.IUserService#selectScoreListByUser(int,
     * java.lang.String, java.lang.String, com.qkwl.common.dto.common.Pagination)
     */
    @Override
    @Transactional(value="flexibleTransMgr" ,isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagination<OtcUserTransfer> selectTransferListByUser(Integer user_id, Integer type, Integer coinId, String beginDate, String endDate, 
    		Pagination<OtcUserTransfer> page, String coinName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offset", page.getOffset());
        map.put("limit", page.getPageSize());
        if(beginDate != null && beginDate.length() > 0) {
            map.put("beginDate", beginDate);
        }
        
        if(endDate != null && endDate.length() > 0) {
            map.put("endDate", endDate);
        }
        
        map.put("user_id", user_id);
        map.put("type", type);
        map.put("coin_id", coinId);
        map.put("coinName", coinName);

        int count = userOtcTransferMapper.countListByUser(map);
        if (count > 0) {
            List<OtcUserTransfer> scoreList = userOtcTransferMapper.selectByType(map);
            page.setData(scoreList);
        }
        page.setTotalRows(count);
        page.generate();
        return page;
    }
    
    //获取otc钱包
    @Override
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
    
    /**
     * otc资产平衡
     */
    @Override
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
    	
//        //上架中的广告金额
//        BigDecimal adFrozen = otcAdvertMapper.getAdvertFrozen(userId, coinId);
//        //订单已消耗
//        BigDecimal orderConsumption = otcAdvertMapper.getOrderConsumption(userId, coinId);
//        
//        adFrozen = MathUtils.sub(adFrozen, orderConsumption);
//        //进行中的订单金额
//        BigDecimal orderFrozen = otcAdvertMapper.getOrderFrozen(userId, coinId);
//        
//        //商户押金冻结
//        BigDecimal deposit = BigDecimal.ZERO;
//        if (coinId == 9) {
//        	deposit = otcMerchantMapper.getDeposit(userId);
//        	if (deposit == null) {
//        		deposit = BigDecimal.ZERO;
//        	}
//		}
//        		
//        BigDecimal frozen = userOtcCoinWallet.getFrozen();
//        BigDecimal frozenBalance = MathUtils.sub(MathUtils.add(MathUtils.add(adFrozen, orderFrozen), deposit),frozen);
        
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
