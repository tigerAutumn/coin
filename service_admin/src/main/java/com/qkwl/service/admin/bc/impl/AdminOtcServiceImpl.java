package com.qkwl.service.admin.bc.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qkwl.common.dto.Enum.otc.OtcTransferTypeEnum;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.otc.SystemOtcSetting;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminOtcService;
import com.qkwl.service.admin.bc.dao.OtcAdvertMapper;
import com.qkwl.service.admin.bc.dao.OtcMerchantMapper;
import com.qkwl.service.admin.bc.dao.OtcUserTransferMapper;
import com.qkwl.service.admin.bc.dao.SystemOtcSettingMapper;
import com.qkwl.service.admin.bc.dao.UserOtcCoinWalletMapper;

@Service("adminOtcService")
public class AdminOtcServiceImpl implements IAdminOtcService {

	private static final Logger logger = LoggerFactory.getLogger(AdminOtcServiceImpl.class);
	
	@Autowired
	private SystemOtcSettingMapper systemOtcSettingMapper;
	@Autowired
	private UserOtcCoinWalletMapper userOtcCoinWalletMapper;
	@Autowired
	private OtcUserTransferMapper otcUserTransferMapper;
	@Autowired
    private OtcAdvertMapper otcAdvertMapper;
    @Autowired
    private OtcMerchantMapper otcMerchantMapper;
	
	@Override
	public List<SystemOtcSetting> getOtcSetting() {
		try {
			return systemOtcSettingMapper.selectAll();
		} catch (Exception e) {
			logger.error("getOtcSetting 异常",e);
			return null;
		}
	}

	@Override
	public SystemOtcSetting getOtcSettingById(Integer id) {
		try {
			return systemOtcSettingMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("getOtcSettingById 异常",e);
			return null;
		}
	}
	
	@Override
	public int updateOtcSetting(SystemOtcSetting systemOtcSetting, Integer adminId) {
		try {
			 return systemOtcSettingMapper.updateByPrimaryKeySelective(systemOtcSetting);
		} catch (Exception e) {
			logger.error("updateOtcSetting 异常",e);
			return 0;
		}
	}

	@Override
	public UserOtcCoinWallet selectWallet(Integer userId, Integer coinId) {
		return userOtcCoinWalletMapper.select(userId, coinId);
	}

	@Override
	public OtcUserTransfer sumOtcTransfer(Integer userId, Integer type,Integer coinId) {
		return otcUserTransferMapper.sumOtcTransferBalance(userId, type,coinId);
	}
	
	/**
	 * 分页查询otc虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 */
	@Override
	public Pagination<UserOtcCoinWallet> selectUserOtcVirtualWalletListByCoin(Pagination<UserOtcCoinWallet> pageParam, UserOtcCoinWallet filterParam) {
		try {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("orderField", pageParam.getOrderField());
		map.put("orderDirection", pageParam.getOrderDirection());
		map.put("coinId", filterParam.getCoinId());
		// 查询总数
		int count = userOtcCoinWalletMapper.countOtcWalletByParam(map);
		if(count > 0) {
			// 查询数据
			List<UserOtcCoinWallet> list = userOtcCoinWalletMapper.selectOtcWalletList(map);
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
		} catch (Exception e) {
			logger.error("查询钱包失败",e);
			return pageParam;
		}
	}
	
	/**
	 * 分页查询otc虚拟币钱包
	 * @param pageParam 分页参数
	 * @param filterParam 实体参数
	 * @return 分页查询记录列表
	 */
	@Override
	public Pagination<OtcUserTransfer> selectOtcTransferListByCoin(Pagination<OtcUserTransfer> pageParam, OtcUserTransfer filterParam) {
		try {
		// 组装查询条件数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", pageParam.getOffset());
		map.put("limit", pageParam.getPageSize());
		map.put("startTime", pageParam.getBegindate());
		map.put("endTime", pageParam.getEnddate());
		map.put("coinId", filterParam.getCoinId());
		// 查询总数
		int count = otcUserTransferMapper.countOtcTransferByParam(map);
		if(count > 0) {
			// 查询数据
			List<OtcUserTransfer> list = otcUserTransferMapper.selectOtcTransferList(map);
			for (OtcUserTransfer otcUserTransfer : list) {
				if (OtcTransferTypeEnum.transferToOtc.getCode().equals(otcUserTransfer.getType())) {
					otcUserTransfer.setTransferName("转入");
				} else {
					otcUserTransfer.setTransferName("转出");
				}
			}
			// 设置返回数据
			pageParam.setData(list);
		}
		pageParam.setTotalRows(count);
		return pageParam;
		} catch (Exception e) {
			logger.error("查询otc钱包失败",e);
			return pageParam;
		}
	}

	@Override
	public List<OtcUserTransfer> selectTransferOutAmount(String startTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return otcUserTransferMapper.selectTransferOutAmount(map);
	}

	@Override
	public List<OtcUserTransfer> selectTransferInAmount(String startTime, String endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return otcUserTransferMapper.selectTransferInAmount(map);
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
        OtcUserTransfer inObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.transferToOtc.getCode(),coinId);
        in = inObj.getAmount();
        
        //转出
        OtcUserTransfer outObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcTransferTo.getCode(),coinId);
        out = outObj.getAmount();
        
        //买入
        OtcUserTransfer buyAmountObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcBuy.getCode(),coinId);
        buyAmount = buyAmountObj.getAmount();
        fee = MathUtils.add(buyAmountObj.getFee(), fee);
        
        //卖出
        OtcUserTransfer sellAmountObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcSell.getCode(),coinId);
        sellAmount = sellAmountObj.getAmount();
        fee = MathUtils.add(sellAmountObj.getFee(), fee);
        
        //otc转入创新区
        OtcUserTransfer outToInnovateAreaObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcTransferToInnovate.getCode(),coinId);
        outToInnovateArea = outToInnovateAreaObj.getAmount();
        out = MathUtils.add(outToInnovateArea, out);
        
        //otc商户押金
        OtcUserTransfer otcMerchantDepositObj = otcUserTransferMapper.sumOtcTransferBalance(userId, OtcTransferTypeEnum.otcMerchantDeposit.getCode(),coinId);
        otcMerchantDeposit = otcMerchantDepositObj.getAmount();
        out = MathUtils.add(otcMerchantDeposit, out);
        
        BigDecimal amountSub = MathUtils.add(MathUtils.add(out,sellAmount),fee);
        BigDecimal total = MathUtils.add(userOtcCoinWallet.getFrozen(),userOtcCoinWallet.getTotal());
        BigDecimal balance = MathUtils.sub(MathUtils.sub(MathUtils.add(amountSub, total),in),buyAmount);
        
//        //上架中的广告金额
//        BigDecimal adFrozen = otcAdvertMapper.getAdvertFrozen(userId, coinId);
//        logger.info("============adFrozen:{}", adFrozen);
//        //订单已消耗
//        BigDecimal orderConsumption = otcAdvertMapper.getOrderConsumption(userId, coinId);
//        logger.info("============orderConsumption:{}", orderConsumption);
//        
//        adFrozen = MathUtils.sub(adFrozen, orderConsumption);
//        logger.info("============adFrozen:{}", adFrozen);
//        //进行中的订单金额
//        BigDecimal orderFrozen = otcAdvertMapper.getOrderFrozen(userId, coinId);
//        logger.info("============orderFrozen:{}", orderFrozen);
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
        } */else {
        	return true;
        }
    }
    
    @Override
    public BigDecimal getAdvertFrozen(Integer userId ,Integer coinId) {
    	return otcAdvertMapper.getAdvertFrozen(userId, coinId);
    }
    
    @Override
    public BigDecimal getOrderConsumption(Integer userId ,Integer coinId) {
    	return otcAdvertMapper.getOrderConsumption(userId, coinId);
    }
}
