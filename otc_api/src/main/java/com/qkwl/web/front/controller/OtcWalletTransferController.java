package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.OtcUserTransfer;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserOtcCoinWallet;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.Utils;
import com.qkwl.web.Handler.CheckRepeatSubmit;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.utils.WebConstant;

@Controller
@RequestMapping("/otc/currency")
public class OtcWalletTransferController extends JsonBaseController{
	private static final Logger logger = LoggerFactory.getLogger(OtcUserController.class);

	
	@Autowired
    private RedisHelper redisHelper;
	
	@Autowired
	private IOtcCoinWalletService otcCoinWalletService;
	
	/*
	@ResponseBody
	@CheckRepeatSubmit
    @RequestMapping(value = "/userFonzenOrNotTest", method = {RequestMethod.POST, RequestMethod.GET})
	public ReturnResult userFonzenOrNotTest(
			@RequestParam(required = true) Integer coinId,
			@RequestParam(required = true) BigDecimal amount,
			@RequestParam(required = true) Integer type)throws Exception {
		
		logger.info("userFonzenOrNotTest");
				
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER("请重新登录");
        }	
		
        if(type.equals(Integer.valueOf(1))) {
        	Result result = otcCoinWalletService.userOtcFrozen(user.getFid(), coinId, amount);
        }else if(type.equals(Integer.valueOf(2))) {
        	Result result = otcCoinWalletService.userOtcUnFrozen(user.getFid(), coinId, amount);
        }
       

		return ReturnResult.FAILUER("");
	}
	
	
	@ResponseBody
    @RequestMapping(value = "/userBuyOrSellTest", method = {RequestMethod.POST, RequestMethod.GET})
	public ReturnResult userBuyOrSellTest(
			@RequestParam(required = true) Integer otherUserId,
			@RequestParam(required = true) Integer coinId,
			@RequestParam(required = true) BigDecimal amount,
			@RequestParam(required = true) Integer type,
			@RequestParam(required = true) BigDecimal fee
			)throws Exception {
				
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER("请重新登录");
        }		
    	
		OtcUserOrder userOrder = new OtcUserOrder();
		userOrder.setType(type);
		userOrder.setAmount(amount);
		userOrder.setBuyerId(user.getFid());
		userOrder.setCoinId(coinId);
		userOrder.setSellerId(otherUserId);
		userOrder.setCreateTime(Utils.getTimestamp());
		userOrder.setFee(fee);
		
		Result result = otcCoinWalletService.userOtcOrderDell(userOrder);
		
		if (result.getSuccess()) {
            return ReturnResult.SUCCESS(result.getData());
        } else {
            return ReturnResult.FAILUER(result.getCode(), result.getMsg());
        }
		
	}*/
	
	
	/**
     * 获取用户币币账户余额
     *
     * @return
     */
	@ResponseBody
    @RequestMapping(value = "/userWalletTotal", method = {RequestMethod.POST, RequestMethod.GET})
	public ReturnResult userCoinWalletTotal(
			@RequestParam(required = true) Integer coinId)throws Exception {
				
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.0"));
        }
        
        Integer fid = user.getFid();
		
    			
		BigDecimal total = otcCoinWalletService.getAccountBalance(fid, coinId);
		
		JSONObject result = new JSONObject();
		result.put("total", total);
		
		return ReturnResult.SUCCESS(result);
	}
	
	
	/**
     * 获取用户otc资产
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/balance", method = {RequestMethod.POST, RequestMethod.GET})
    public ReturnResult balance() throws Exception {
    	
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.2"));
        }
        
        Integer fid = user.getFid();
		
    	    	                
        JSONObject result = new JSONObject();
        try {
        	List<UserOtcCoinWallet> userCoinWallets = otcCoinWalletService.listUserCoinWallet(fid);
            Iterator<UserOtcCoinWallet> iterator = userCoinWallets.iterator();
            while (iterator.hasNext()) {
            	UserOtcCoinWallet wallet = (UserOtcCoinWallet) iterator.next();
                if (!redisHelper.hasCoinId(wallet.getCoinId())) {
                    iterator.remove();
                }
            }
            BigDecimal totalAssets = getOtcTotalAssets(userCoinWallets);
            BigDecimal btcPrice = redisHelper.getLastPrice(8);
            BigDecimal btcAssets = MathUtils.div(totalAssets, btcPrice);
            
            result.put("netAssets", getOtcNetAssets(userCoinWallets));
            result.put("totalAssets", totalAssets);
            result.put("btcAssets", btcAssets);
            result.put("userWalletList", userCoinWallets);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.7"));
        }
        return ReturnResult.SUCCESS(result);
    }
	
    /**
     * 用户币币账户余额与otc账户划转
     *
     * @return
     */
	@RequestMapping(value = "/transfer", method = RequestMethod.POST)
	@CheckRepeatSubmit
    @ResponseBody
    public ReturnResult otcWalletTransfer(
			@RequestParam(required = true) BigDecimal amount,
			@RequestParam(required = true) Integer coinType,
			@RequestParam(required = true) String type) throws Exception {
    	try {
    		logger.info("amount = " + amount + "coinType = " + coinType + "type = " + type);
    		
        	if(amount.compareTo(BigDecimal.ZERO) <= 0) {
        		return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.11"));
        	}
        	
        	SystemCoinType systemCoinType = redisHelper.getCoinTypeSystem(coinType);
        	if(systemCoinType == null) {
        		return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.12"));
        	}
        	        	
    		FUser user = getUser();
            if (user == null) {
                return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.13"));
            }
            
            Integer fid = user.getFid();
    		        	        	
        	OtcUserTransfer userOtcTransfer = new OtcUserTransfer();
        	userOtcTransfer.setAmount(amount);
        	userOtcTransfer.setCoinId(coinType);
        	userOtcTransfer.setType(Integer.valueOf(type));
        	userOtcTransfer.setUserId(fid);
        	userOtcTransfer.setCreateTime(Utils.getTimestamp());
        	
        	Result result = otcCoinWalletService.userAccountOtcWalletTransfer(userOtcTransfer);
        	
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
    	return ReturnResult.FAILUER("");

	}
	
	/**
     * 用户otc账户划转记录
     *
     * @return
     */
	@RequestMapping(value = "/transferList", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
	public ReturnResult otcWalletTransferList(
			@RequestParam(required = false, defaultValue = "") Integer type,
			@RequestParam(required = false, defaultValue = "") Integer coinId,
			@RequestParam(required = false, defaultValue = "") String begindate,
            @RequestParam(required = false, defaultValue = "") String enddate,
            @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String coinName
			) {
		
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("OtcWalletTransferController.15"));
        }
        
        Integer fid = user.getFid();
		    	
		Pagination<OtcUserTransfer> page = new Pagination<OtcUserTransfer>(currentPage, pageSize);
		
		JSONObject jsonObject = new JSONObject();
		
		Pagination<OtcUserTransfer> transferlist = otcCoinWalletService.selectTransferListByUser(fid, type, coinId, begindate,
				enddate, page, coinName);
		
        jsonObject.put("currentPage", currentPage);
        jsonObject.put("list", transferlist);

		return ReturnResult.SUCCESS(jsonObject);
		
		

	}
	
	 /**
     * 总资产
     */
    public BigDecimal getOtcTotalAssets(List<UserOtcCoinWallet> coinWallets) {
    	BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets = BigDecimal.ZERO;
        BigDecimal tmp = BigDecimal.ZERO;
        List<SystemTradeType> GAVCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.GAVC.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> BTCTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.BTC.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> ETHTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.ETH.getCode(), WebConstant.BCAgentId);
        List<SystemTradeType> USDTTypeList = redisHelper.getTradeTypeSortAndInnovation(
        		SystemTradeTypeEnum.USDT.getCode(), WebConstant.BCAgentId);
    	for (UserOtcCoinWallet coinWallet : coinWallets) {
    		assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
            assets = MathUtils.add(assets, coinWallet.getBorrow());
    		if (MathUtils.compareTo(assets, BigDecimal.ZERO) == 0) {
    			continue;
			}
    		//币种为GAVC时
            if (coinWallet.getCoinId() == 9) {
                totalAssets = MathUtils.add(totalAssets, assets);
                continue;
            }
            
    		//GAVC交易区
    		BigDecimal GAVCAssets = getAssets(coinWallet, GAVCTypeList, 9);
    		if (MathUtils.compareTo(GAVCAssets, BigDecimal.ZERO) != 0) {
    			tmp = GAVCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//BTC交易区
    		BigDecimal BTCAssets = getAssets(coinWallet, BTCTypeList, 1);
    		if (MathUtils.compareTo(BTCAssets, BigDecimal.ZERO) != 0) {
    			tmp = BTCAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
    		//ETH交易区
    		BigDecimal ETHAssets = getAssets(coinWallet, ETHTypeList, 4);
    		if (MathUtils.compareTo(ETHAssets, BigDecimal.ZERO) != 0) {
    			tmp = ETHAssets;
    			totalAssets = MathUtils.add(totalAssets, tmp);
    			continue;
    		}
            //USDT交易区
            BigDecimal USDTAssets = getAssets(coinWallet, USDTTypeList, 52);
            if (MathUtils.compareTo(USDTAssets, BigDecimal.ZERO) != 0) {
            	tmp = USDTAssets;
            	totalAssets = MathUtils.add(totalAssets, tmp);
            	continue;
            }
    	}
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }
    
    /**
     * 获取币种对应交易区的资产
     * @param coinWallet
     * @param coinId
     * @return
     */
    private BigDecimal getAssets(UserOtcCoinWallet coinWallet, List<SystemTradeType> tradeTypeList, int coinId) {
    	BigDecimal price = BigDecimal.ZERO;
    	BigDecimal assets = BigDecimal.ZERO;
    	Integer tradeId;
    	for (SystemTradeType systemTradeType : tradeTypeList) {
			if (systemTradeType.getSellCoinId() == coinWallet.getCoinId() && systemTradeType.getBuyCoinId() == coinId) {
                price = redisHelper.getLastPrice(systemTradeType.getId());
                //买方币种不为GAVC时
                if (systemTradeType.getBuyCoinId() != 9) {
                	Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(WebConstant.BCAgentId);
                	//其他区币种转化为gavc卖方币种
        			tradeId = trades.get(systemTradeType.getBuyCoinId());
        			if (tradeId == null) {
        				continue;
					}
        			BigDecimal lastPrice = redisHelper.getLastPrice(tradeId);
        			price = MathUtils.mul(lastPrice, price);
				}
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                assets = MathUtils.add(assets, coinWallet.getBorrow());
                assets = MathUtils.mul(assets, price);
                break;
			}
		}
    	return assets;
    }
    
    /**
     * 净资产
     */
    public BigDecimal getOtcNetAssets(List<UserOtcCoinWallet> coinWallets) {
        Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(WebConstant.BCAgentId);
        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal assets, price;
        Integer tradeId;
        for (UserOtcCoinWallet coinWallet : coinWallets) {
            // 人民币
            if (coinWallet.getCoinId().equals(9)) {
                assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
                coinWallet.setTotalCny(assets);
                totalAssets = MathUtils.add(totalAssets, assets);
                continue;
            }
            // 虚拟币
            tradeId = trades.get(coinWallet.getCoinId());
            if (tradeId == null) {
                continue;
            }
            price = redisHelper.getLastPrice(tradeId);
            assets = MathUtils.add(coinWallet.getTotal(), coinWallet.getFrozen());
            assets = MathUtils.mul(assets, price);
            coinWallet.setTotalCny(assets);
            totalAssets = MathUtils.add(totalAssets, assets);
        }
        return MathUtils.toScaleNum(totalAssets, MathUtils.DEF_CNY_SCALE);
    }
	
    /**
     * 检查用户押金是否足够
     */
	@ResponseBody
    @RequestMapping(value = "/cheakDeposit", method = {RequestMethod.POST, RequestMethod.GET})
	public ReturnResult cheakDeposit()throws Exception {
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER("请重新登录");
        }
        
        Integer fid = user.getFid();
		BigDecimal total = otcCoinWalletService.getOtcAccountBalance(fid, 9);
		String depositStr = redisHelper.getSystemArgs(ArgsConstant.OTC_MERCHANT_DEPOSIT);
		BigDecimal deposit = new BigDecimal("500");
		if (depositStr != null) {
			deposit = new BigDecimal(depositStr);
		}
		JSONObject result = new JSONObject();
		if (total.compareTo(deposit) >= 0) {
			result.put("enough", "yes");
		} else {
			result.put("enough", "no");
		}
		return ReturnResult.SUCCESS(result);
	}
	
	/**
     * 获取商户押金
     */
	@ResponseBody
    @RequestMapping(value = "/getDeposit", method = {RequestMethod.POST, RequestMethod.GET})
	public ReturnResult getDeposit()throws Exception {
		String depositStr = redisHelper.getSystemArgs(ArgsConstant.OTC_MERCHANT_DEPOSIT);
		BigDecimal deposit = new BigDecimal("500");
		if (depositStr != null) {
			deposit = new BigDecimal(depositStr);
		}
		JSONObject result = new JSONObject();
		result.put("deposit", deposit);
		return ReturnResult.SUCCESS(result);
	}
	
}
