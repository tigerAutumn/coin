package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.otc.OtcMerchantStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.otc.BestOtcAdvert;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAdvertPrice;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcCoinWalletService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.rpc.otc.OtcOrderService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.Handler.CheckRepeatSubmit;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

@Controller
@RequestMapping("/otc/advert")
public class OtcAdvertController extends JsonBaseController{
	private static final Logger logger = LoggerFactory.getLogger(OtcAdvertController.class);
	
	@Autowired
	private IOtcAdvertService otcAdvertService;
	@Autowired
	private IOtcCoinWalletService otcCoinWalletService;
	@Autowired
	private IOtcUserService otcUserService;
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private OtcOrderService otcOrderService;
	
	/*
	 * 交易大厅查询广告列表
	 */
	@PassToken
	@ResponseBody
    @RequestMapping("/tradeIndex")
    public ReturnResult tradeIndex(
    		@RequestParam(required = true) String side,
    		@RequestParam(required = false, defaultValue = "1") Integer currencyId,
    		@RequestParam(required = false) Integer bankinfoType,
    		@RequestParam(required = false) BigDecimal amount,
    		@RequestParam(required = true) Integer coinId,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage
    		) {
		logger.info("tradeIndex side:{},currencyId:{},bankinfoType:{},amount:{},coinId:{},currentPage:{},numPerPage:{}",
				side,currencyId,bankinfoType,amount,coinId,currentPage,numPerPage);
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		
		//查询广告列表 
		Pagination<OtcAdvert> pageParam = new Pagination<OtcAdvert>(currentPage, numPerPage);
		
		//填充数据
		OtcAdvert otcAdvert = new OtcAdvert();
		otcAdvert.setSide(side);
		if (currencyId != null) {
			otcAdvert.setCurrencyId(currencyId);
		}
		if (bankinfoType != null) {
			otcAdvert.setBankinfoType(bankinfoType);
		}
		if (amount != null) {
			otcAdvert.setAmount(amount);
		}
		otcAdvert.setCoinId(coinId);
		
		//获取币对名称
		String coinName = otcAdvertService.getCoinName(coinId);
		//获取法币名称
		String currencyName = otcAdvertService.getCurrencyName(currencyId);
		//市场平均价
		BigDecimal averagePrice = otcAdvertService.getAveragePrice(coinName, currencyName);
		//热币价
		BigDecimal hotcoinPrice = otcAdvertService.getHotcoinPrice(coinId);

		logger.info("tradeIndex otcAdvert:{}", JSON.toJSONString(otcAdvert));
		Pagination<OtcAdvert> pagination = otcAdvertService.selectAdvertPageList(pageParam, otcAdvert
				, averagePrice, hotcoinPrice);
		
		for (OtcAdvert advert : pagination.getData()) {
			//30天交易次数
			Integer cmpOrders = otcOrderService.getCmpOrders(advert.getUserId());
			advert.setCmpOrders(new BigDecimal(cmpOrders));
			//成交数过少
			advert.setIsLimited(0);
			if (advert.getSuccessCount() > cmpOrders) {
				advert.setIsLimited(1);
			}
			//正在处理的订单过多
			if (advert.getMaxProcessing() != 0) {
				Integer processingOrders = otcAdvertService.getProcessingOrders(advert.getId());
				if (processingOrders >= advert.getMaxProcessing()) {
					if (advert.getIsLimited() == 1) {
						advert.setIsLimited(3);
					} else {
						advert.setIsLimited(2);
					}
				}
			}
			//30天订单完成率
			String completionRate = otcOrderService.getCompletionRate(advert.getUserId());
			advert.setCompletionRate(completionRate);
		}
		
		
		//设置价格和图标
		setPriceAndPayIcons(pagination.getData());
		
		jsonObject.put("total", pagination.getTotalRows());
        jsonObject.put("page", currentPage);
        jsonObject.put("list", pagination.getData());
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
    }
	
	/*
	 * 商户查询广告列表
	 */
	@ResponseBody
    @RequestMapping("/merchantIndex")
    public ReturnResult merchantIndex(
    		@RequestParam(required = false) Integer userId,
    		@RequestParam(required = false, defaultValue = Constant.OTC_BUY) String side
    		) {
		JSONObject jsonObject = new JSONObject();
		OtcAdvert otcAdvert = new OtcAdvert();
		otcAdvert.setSide(side);
		List<OtcAdvert> advertList = new ArrayList<>();
		//判断查询自己和其他商户
		if (userId == null) {
			FUser userInfo = getCurrentUserInfoByToken();
			otcAdvert.setUserId(userInfo.getFid());
			advertList = otcAdvertService.selectMerchantAdvert(otcAdvert);
		} else {
			otcAdvert.setUserId(userId);
			advertList = otcAdvertService.selectOtherMerchantAdvert(otcAdvert);
		}
		//设置价格和图标
		setPriceAndPayIcons(advertList);
		
        jsonObject.put("list", advertList);
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
    }
	
	/*
	 * 广告管理页面
	 */
	@ResponseBody
    @RequestMapping("/manageIndex")
    public ReturnResult manageIndex(
    		@RequestParam(required = false) String side,
    		@RequestParam(required = false) Integer currencyId,
    		@RequestParam(required = false) Integer coinId,
    		@RequestParam(required = false) Integer status,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "10") Integer numPerPage
    		) {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		if (userInfo == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.4"));
		}
		Pagination<OtcAdvert> pageParam = new Pagination<OtcAdvert>(currentPage, numPerPage);
		
		//填充数据
		OtcAdvert otcAdvert = new OtcAdvert();
		otcAdvert.setUserId(userInfo.getFid());
		if (!StringUtils.isEmpty(side)) {
			otcAdvert.setSide(side);
		}
		if (currencyId != null) {
			otcAdvert.setCurrencyId(currencyId);
		}
		if (coinId != null) {
			otcAdvert.setCoinId(coinId);
		}
		if (status != null) {
			otcAdvert.setStatus(status);
		}
		
		Pagination<OtcAdvert> pagination = otcAdvertService.selectMyAdvert(pageParam, otcAdvert);
		//设置价格和图标
		setPriceAndPayIcons(pagination.getData());
		
		jsonObject.put("total", pagination.getTotalRows());
        jsonObject.put("page", currentPage);
        jsonObject.put("list", pagination.getData());
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
    }
	
	/*
	 * 查看广告
	 */
	@ResponseBody
    @RequestMapping("/checkAdvert")
	public ReturnResult checkAdvert(
			@RequestParam(required = true) Integer id
			) {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		if (userInfo == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.8"));
		}
		//查询用户
		OtcAdvert advert = otcAdvertService.selectAdvertById(id);
		if (advert == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.9"));
        }
		//查询相应币种账户余额
		BigDecimal otcAccountBalance = otcCoinWalletService.getOtcAccountBalance(userInfo.getFid(), advert.getCoinId());
		
		//设置价格
		setPrice(advert);
		//设置数量精度
		setVolume(advert);
		
		//转换支付图标
		List<OtcPayment> paymentList = redisHelper.getOpenOtcPaymentList();
		List<OtcPayment> payList = new ArrayList<>();
		for (OtcPayment element : paymentList) {
			if (element.getId() == advert.getBankinfoFirstId()) {
				payList.add(getOtcPayment(element));
			} else if (element.getId() == advert.getBankinfoSecondId()) {
				payList.add(getOtcPayment(element));
			} else if (element.getId() == advert.getBankinfoThirdId()) {
				payList.add(getOtcPayment(element));
			}
		}
		advert.setPay(payList);
		Integer countDigit = Constant.OTC_COUNT_DIGIT;
		Integer amountDigit = Constant.OTC_AMOUNT_DIGIT;
		
		jsonObject.put("countDigit", countDigit);
		jsonObject.put("amountDigit", amountDigit);
		jsonObject.put("otcAccountBalance", otcAccountBalance);
		jsonObject.put("advert", advert);
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
	}
	
	
	
	/*
	 * 发布广告页面
	 */
	@PassToken
	@ResponseBody
    @RequestMapping("/releaseIndex")
    public ReturnResult releaseIndex(
    		@RequestParam(required = true) Integer coinId,
    		@RequestParam(required = true) Integer currencyId
    		) {
		Map<String, String> param = otcAdvertService.getParam();
		// 场外OTC手续费
		String feeRate = Double.parseDouble(param.get("feeRate"))*100 + "%"; //$NON-NLS-2$
		
		// 广告有效期设置
		Integer existenceTime = Integer.parseInt(param.get("existenceTime"));
		// 发布出售广告的资金限制
		BigDecimal minTradeAmount = new BigDecimal(param.get("minTradeAmount"));
		
		Integer countDigit = Constant.OTC_COUNT_DIGIT;
		Integer priceDigit = Constant.OTC_AMOUNT_DIGIT;
		if (coinId.equals(9)) {
			priceDigit = Constant.OTC_PRICE_DIGIT;
		}
		//获取币对名称
		String coinName = otcAdvertService.getCoinName(coinId);
		//获取法币名称
		String currencyName = otcAdvertService.getCurrencyName(currencyId);
		List<BigDecimal> marketPrice = new ArrayList<>();
		//市场平均价
		BigDecimal averagePrice = otcAdvertService.getAveragePrice(coinName, currencyName);
		marketPrice.add(MathUtils.toScaleNum(averagePrice, priceDigit));
		//热币价
		BigDecimal hotcoinPrice = otcAdvertService.getHotcoinPrice(coinId);
		marketPrice.add(MathUtils.toScaleNum(hotcoinPrice, priceDigit));
		
		//查询市场最低或最高价
		BigDecimal minSellPrice = BigDecimal.ZERO;
		BigDecimal maxBuyPrice = BigDecimal.ZERO;
		
		OtcAdvertPrice otcAdvertPrice = otcAdvertService.selectAdvertPrice(coinId, currencyId);
		if (otcAdvertPrice != null) {
			BigDecimal minFixedSellPrice = otcAdvertPrice.getMinFixedSellPrice();
			BigDecimal maxFixedBuyPrice = otcAdvertPrice.getMaxFixedBuyPrice();
			
			if (minFixedSellPrice.compareTo(BigDecimal.ZERO) != 0) {
				minSellPrice = minFixedSellPrice;
			}
			if (maxFixedBuyPrice.compareTo(BigDecimal.ZERO) != 0) {
				maxBuyPrice = maxFixedBuyPrice;
			}
			//最低出售价
			if (otcAdvertPrice.getMinAverageSellRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal minAverageSellPrice = MathUtils.mul(otcAdvertPrice.getMinAverageSellRate(), averagePrice);
				//当最低价为0 或者（最低价大于0同时小于平均卖价）时修改最低价
				if (minSellPrice.compareTo(BigDecimal.ZERO) == 0 ||
						(minSellPrice.compareTo(BigDecimal.ZERO) > 0 && minSellPrice.compareTo(minAverageSellPrice) > 0)) {
					minSellPrice = minAverageSellPrice;
				}
			}
			if (otcAdvertPrice.getMinHotcoinSellRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal minHotcoinSellPrice = MathUtils.mul(otcAdvertPrice.getMinHotcoinSellRate(), hotcoinPrice);
				//当最低价为0 或者（最低价大于0同时小于币安卖价）时修改最低价
				if (minSellPrice.compareTo(BigDecimal.ZERO) == 0 ||
						(minSellPrice.compareTo(BigDecimal.ZERO) > 0 && minSellPrice.compareTo(minHotcoinSellPrice) > 0)) {
					minSellPrice = minHotcoinSellPrice;
				}
			}
			//最高购买价
			if (otcAdvertPrice.getMaxAverageBuyRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal maxAverageBuyPrice = MathUtils.mul(otcAdvertPrice.getMaxAverageBuyRate(), averagePrice);
				if (maxBuyPrice.compareTo(maxAverageBuyPrice) <= 0) {
					maxBuyPrice = maxAverageBuyPrice;
				}
			}
			if (otcAdvertPrice.getMaxHotcoinBuyRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal maxHotcoinBuyPrice = MathUtils.mul(otcAdvertPrice.getMaxHotcoinBuyRate(), hotcoinPrice);
				if (maxBuyPrice.compareTo(maxHotcoinBuyPrice) <= 0) {
					maxBuyPrice = maxHotcoinBuyPrice;
				}
			}
		}
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		//查询相应币种账户余额
		if (userInfo != null) {
			BigDecimal otcAccountBalance = otcCoinWalletService.getOtcAccountBalance(userInfo.getFid(), coinId);
			BigDecimal visiableBalance = MathUtils.mul(otcAccountBalance, MathUtils.sub(BigDecimal.ONE, new BigDecimal(param.get("feeRate"))));
			jsonObject.put("balance", MathUtils.toScaleNum(otcAccountBalance, countDigit));
			jsonObject.put("accountBalance", MathUtils.toScaleNum(visiableBalance, countDigit));
		}
		jsonObject.put("marketPrice", marketPrice);
		jsonObject.put("minSellPrice", MathUtils.toScaleNum(minSellPrice, priceDigit));
		jsonObject.put("maxBuyPrice", MathUtils.toScaleNum(maxBuyPrice, priceDigit));
		jsonObject.put("feeRate", feeRate);
		jsonObject.put("existenceTime", existenceTime);
		jsonObject.put("minTradeAmount", minTradeAmount);
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
    }
	
	/*
	 * 修改广告页面
	 */
	@ResponseBody
    @RequestMapping("/updateAdvert")
    public ReturnResult updateAdvert(
    		@RequestParam(required = true) Integer id
    		) {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		if (userInfo == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.108"));
		}
		OtcAdvert advert = otcAdvertService.selectAdvertById(id);
		if (!userInfo.getFid().equals(advert.getUserId())) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.111"));
		}
		//查询相应币种账户余额
		BigDecimal otcAccountBalance = otcCoinWalletService.getOtcAccountBalance(userInfo.getFid(), advert.getCoinId());
		
		Integer countDigit = Constant.OTC_COUNT_DIGIT;
		Integer priceDigit = Constant.OTC_AMOUNT_DIGIT;
		if (advert.getCoinId().equals(9)) {
			priceDigit = Constant.OTC_PRICE_DIGIT;
		}
		List<BigDecimal> marketPrice = new ArrayList<>();
		//市场平均价
		BigDecimal averagePrice = otcAdvertService.getAveragePrice(advert.getCoinName(), advert.getCurrencyName());
		marketPrice.add(MathUtils.toScaleNum(averagePrice, priceDigit));
		//热币价
		BigDecimal hotcoinPrice = otcAdvertService.getHotcoinPrice(advert.getCoinId());
		marketPrice.add(MathUtils.toScaleNum(hotcoinPrice, priceDigit));
		
		//查询市场最低或最高价
		BigDecimal minSellPrice = BigDecimal.ZERO;
		BigDecimal maxBuyPrice = BigDecimal.ZERO;
		
		OtcAdvertPrice otcAdvertPrice = otcAdvertService.selectAdvertPrice(advert.getCoinId(), advert.getCurrencyId());
		if (otcAdvertPrice != null) {
			BigDecimal minFixedSellPrice = otcAdvertPrice.getMinFixedSellPrice();
			BigDecimal maxFixedBuyPrice = otcAdvertPrice.getMaxFixedBuyPrice();
			
			if (minFixedSellPrice.compareTo(BigDecimal.ZERO) != 0) {
				minSellPrice = minFixedSellPrice;
			}
			if (maxFixedBuyPrice.compareTo(BigDecimal.ZERO) != 0) {
				maxBuyPrice = maxFixedBuyPrice;
			}
			//最低出售价
			if (otcAdvertPrice.getMinAverageSellRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal minAverageSellPrice = MathUtils.mul(otcAdvertPrice.getMinAverageSellRate(), averagePrice);
				//当最低价为0 或者（最低价大于0同时小于平均卖价）时修改最低价
				if (minSellPrice.compareTo(BigDecimal.ZERO) == 0 ||
						(minSellPrice.compareTo(BigDecimal.ZERO) > 0 && minSellPrice.compareTo(minAverageSellPrice) > 0)) {
					minSellPrice = minAverageSellPrice;
				}
			}
			if (otcAdvertPrice.getMinHotcoinSellRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal minHotcoinSellPrice = MathUtils.mul(otcAdvertPrice.getMinHotcoinSellRate(), hotcoinPrice);
				//当最低价为0 或者（最低价大于0同时小于币安卖价）时修改最低价
				if (minSellPrice.compareTo(BigDecimal.ZERO) == 0 ||
						(minSellPrice.compareTo(BigDecimal.ZERO) > 0 && minSellPrice.compareTo(minHotcoinSellPrice) > 0)) {
					minSellPrice = minHotcoinSellPrice;
				}
			}
			//最高购买价
			if (otcAdvertPrice.getMaxAverageBuyRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal maxAverageBuyPrice = MathUtils.mul(otcAdvertPrice.getMaxAverageBuyRate(), averagePrice);
				if (maxBuyPrice.compareTo(maxAverageBuyPrice) <= 0) {
					maxBuyPrice = maxAverageBuyPrice;
				}
			}
			if (otcAdvertPrice.getMaxHotcoinBuyRate().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal maxHotcoinBuyPrice = MathUtils.mul(otcAdvertPrice.getMaxHotcoinBuyRate(), hotcoinPrice);
				if (maxBuyPrice.compareTo(maxHotcoinBuyPrice) <= 0) {
					maxBuyPrice = maxHotcoinBuyPrice;
				}
			}
		}
		
		//转换支付图标
		List<Integer> payIds = new ArrayList<>();
		if (advert.getBankinfoFirstId() != null) {
			payIds.add(advert.getBankinfoFirstId());
		}
		if (advert.getBankinfoSecondId() != null) {
			payIds.add(advert.getBankinfoSecondId());
		}
		if (advert.getBankinfoThirdId() != null) {
			payIds.add(advert.getBankinfoThirdId());
		}
		advert.setPayIds(payIds);
		//设置数量为0
        advert.setVolume(BigDecimal.ZERO);
		//修改溢价比例
		advert.setPremiumRate(MathUtils.mul(advert.getPremiumRate(), new BigDecimal("100")));
		
		BigDecimal visiableBalance = MathUtils.mul(otcAccountBalance, MathUtils.sub(BigDecimal.ONE, advert.getFeeRate()));
		jsonObject.put("accountBalance", MathUtils.toScaleNum(visiableBalance, countDigit));
		jsonObject.put("marketPrice", marketPrice);
		jsonObject.put("minSellPrice", MathUtils.toScaleNum(minSellPrice, priceDigit));
		jsonObject.put("maxBuyPrice", MathUtils.toScaleNum(maxBuyPrice, priceDigit));
		jsonObject.put("advert", advert);
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
	}
	
	/*
	 * 查询用户支付方式
	 */
	@ResponseBody
    @RequestMapping("/userPaymentList")
    public ReturnResult userPaymentList() {
		JSONObject jsonObject = new JSONObject();
		FUser userInfo = getCurrentUserInfoByToken();
		List<OtcPayment> paymentList = otcAdvertService.getAllPaymentList(userInfo.getFid());
		jsonObject.put("list", paymentList);
		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
		return result;
	}
	
	/*
	 * 发布广告
	 */
	@ResponseBody
	@CheckRepeatSubmit
    @RequestMapping("/releaseAdvert")
    public ReturnResult releaseAdvert(
    		@RequestParam(required = false) Integer id,
    		@RequestParam(required = true) String side,
    		@RequestParam(required = true) Integer coinId,
    		@RequestParam(required = true) Integer currencyId,
    		@RequestParam(required = true) Integer priceType,
    		@RequestParam(required = false) Integer floatMarket,
    		@RequestParam(required = false) BigDecimal premiumRate,
    		@RequestParam(required = false, defaultValue = "0") BigDecimal acceptablePrice,
    		@RequestParam(required = false) BigDecimal fixedPrice,
    		@RequestParam(required = true) BigDecimal volume,
    		@RequestParam(required = true) String bankinfoIds,
    		@RequestParam(required = true) BigDecimal minAmount,
    		@RequestParam(required = true) BigDecimal maxAmount,
    		@RequestParam(required = false) String description,
    		@RequestParam(required = false) String note,
    		@RequestParam(required = false) String greetings,
    		@RequestParam(required = false) String tag,
    		@RequestParam(required = false, defaultValue = "0") Integer maxProcessing,
    		@RequestParam(required = false, defaultValue = "0") Integer successCount
    		) {
		if (MathUtils.compareTo(BigDecimal.ZERO, volume) >= 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.30"));
		}
		if (maxProcessing < 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.31"));
		}
		if (successCount < 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.32"));
		}
		if (MathUtils.compareTo(BigDecimal.ZERO, minAmount) >= 0 
				|| MathUtils.compareTo(BigDecimal.ZERO, maxAmount) >= 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.34"));
		}
		if (MathUtils.compareTo(minAmount, maxAmount) > 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.33"));
		}
		//校验币对是否开放otc
		if (!checkCoin(coinId)) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.35"));
		}
		//校验用户是否启用otc
		FUser userInfo = getCurrentUserInfoByToken();
		if (userInfo == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.36"));
		}
		if (!otcAdvertService.checkUser(userInfo.getFid())) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.37"));
		}
		//校验用户是否是认证商户
		OtcMerchant merchant = otcUserService.getMerchantByUid(userInfo.getFid());
		if (merchant == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10009"));
		}
		if (merchant.getStatus().equals(OtcMerchantStatusEnum.Removed.getCode())) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10010"));
		}
		if (merchant.getStatus().equals(OtcMerchantStatusEnum.Prohibited.getCode())) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10011"));
		}
		
		JSONObject jsonObject = new JSONObject();
		
		//查询相应币种账户余额
		BigDecimal otcAccountBalance = otcCoinWalletService.getOtcAccountBalance(userInfo.getFid(), coinId);
		Map<String, String> param = otcAdvertService.getParam();
		// 广告发布条数限制
		String countStr = param.get("adMaxCount");
		if (StringUtils.isNotEmpty(countStr)) {
			Integer adMaxCount =  Integer.parseInt(countStr);
			if (adMaxCount != 0) {
				OtcAdvert processingAdvert = new OtcAdvert();
				processingAdvert.setUserId(userInfo.getFid());
				processingAdvert.setCoinId(coinId);
				processingAdvert.setSide(side);
				Integer countProcessingAdvert = otcAdvertService.countProcessingAdvert(processingAdvert);
				if (countProcessingAdvert >= adMaxCount) {
					return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.74", adMaxCount));
				}
			}
		}
		// 场外OTC手续费
		String feeRate = param.get("feeRate");
		// 买入订单超时期限
		String maxPaytime = param.get("maxPaytime");
		// 广告有效期设置
		Integer existenceTime = Integer.parseInt(param.get("existenceTime"));
		// 发布出售广告的资金限制
		BigDecimal minTradeAmount = new BigDecimal(param.get("minTradeAmount"));
		
		if (Constant.OTC_SELL.equals(side)) {
			//查询GAVC所有交易对，将sellCoinId与tradeId对应存入map
			Map<Integer, Integer> trades = redisHelper.getCoinIdToTradeId(WebConstant.BCAgentId);
			BigDecimal lastPrice = BigDecimal.ONE;
			Integer tradeId;
			SystemCoinType coinType = redisHelper.getCoinTypeSystem(coinId);
            if(coinType.getIsInnovateAreaCoin()) {
            	//查询创新区的交易对
            	SystemTradeType tradeType = redisHelper.getTradeTypeFromInnvationCoinId(coinId, WebConstant.BCAgentId);
        		if (tradeType != null) {
        			//买方GAVC直接换算
        			lastPrice = redisHelper.getLastPrice(tradeType.getId());
        			if(!tradeType.getBuyShortName().equals("GAVC")) {
        				//买方不为GAVC则先查询该交易区对应GAVC的价格
        				tradeId = trades.get(tradeType.getBuyCoinId());
        				BigDecimal price = BigDecimal.ONE;
        				if (tradeId != null) {
        					price = redisHelper.getLastPrice(tradeId);
        				}
        				//最终价为两次价格的乘积
        				lastPrice = MathUtils.mul(lastPrice, price);
        			}
        		}
            }else {
            	 // 虚拟币
                tradeId = trades.get(coinId);
                if (tradeId != null) {
                	lastPrice = redisHelper.getLastPrice(tradeId);
                }
            }
            
            logger.info("余额:{}, 价格:{}", otcAccountBalance, lastPrice);
			//校验用户余额是否大于资金限制
			if (minTradeAmount.compareTo(MathUtils.mul(otcAccountBalance, lastPrice)) > 0) {
				return ReturnResult.FAILUER("账户余额小于"+minTradeAmount+"GAVC!");
			}
			//校验出售的数量是否超出钱包数量
			if (volume.compareTo(otcAccountBalance) > 0) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.45"));
			}
		}
		
		OtcAdvert otcAdvert = new OtcAdvert();
		//校验参数是否传输正确
		BigDecimal priceRate = BigDecimal.ONE;
		if (Constant.OTC_FIXED_PRICE == priceType) {
			if (fixedPrice == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.46"));
			}
			if (MathUtils.compareTo(BigDecimal.ZERO, fixedPrice) > 0) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.49"));
			}
			otcAdvert.setFixedPrice(fixedPrice);
		} else if (Constant.OTC_FLOAT_PRICE == priceType) {
			if (floatMarket == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.47"));
			}
			if (premiumRate == null) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.48"));
			}
			priceRate = MathUtils.add(MathUtils.div(premiumRate, new BigDecimal("100")), BigDecimal.ONE);
			if (priceRate.compareTo(new BigDecimal("1.2")) > 0
					|| priceRate.compareTo(new BigDecimal("0.8")) < 0) {
				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.52"));
			}
			otcAdvert.setFloatMarket(floatMarket);
			otcAdvert.setPriceRate(priceRate);
			otcAdvert.setAcceptablePrice(acceptablePrice);
		}
		
		//判断发布的金额是否小于最小限额
		BigDecimal amount = BigDecimal.ZERO;
		if (priceType == Constant.OTC_FIXED_PRICE) {
			//固定价格总金额
			amount = MathUtils.mul(fixedPrice, volume);
		} else if (priceType == Constant.OTC_FLOAT_PRICE) {
			//获取币对名称
			String coinName = otcAdvertService.getCoinName(coinId);
			//获取法币名称
			String currencyName = otcAdvertService.getCurrencyName(currencyId);
			BigDecimal floatPrice = BigDecimal.ZERO;
			if (floatMarket == Constant.OTC_MARKET_AVERAGE) {
				floatPrice = otcAdvertService.getAveragePrice(coinName, currencyName);
			} else if (floatMarket == Constant.OTC_MARKET_HOTCOIN) {
				floatPrice = otcAdvertService.getHotcoinPrice(coinId);
			}
			//浮动价格总金额
			amount = MathUtils.mul(MathUtils.mul(floatPrice, priceRate), volume);
		}
		if (minAmount.compareTo(amount) > 0) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.53"));
		}
		
		otcAdvert.setUserId(userInfo.getFid());
		otcAdvert.setPriceType(priceType);
		otcAdvert.setStatus(Constant.OTC_ADVERT_ON);
		otcAdvert.setVolume(volume);
		otcAdvert.setVisiableVolume(volume);
		otcAdvert.setTradingVolume(BigDecimal.ZERO);
		otcAdvert.setFrozenVolume(BigDecimal.ZERO);
		otcAdvert.setFeeRate(new BigDecimal(feeRate));
		otcAdvert.setMaxPaytime(Integer.parseInt(maxPaytime));
		
		otcAdvert.setBankinfoThirdId(null);
		otcAdvert.setBankinfoSecondId(null);
		otcAdvert.setBankinfoFirstId(null);
		String[] split = bankinfoIds.split(",");
		switch (split.length) {
		case 3:
			otcAdvert.setBankinfoThirdId(Integer.parseInt(split[2]));
		case 2:
			otcAdvert.setBankinfoSecondId(Integer.parseInt(split[1]));
		case 1:
			otcAdvert.setBankinfoFirstId(Integer.parseInt(split[0]));
			break;
		default:
			break;
		}
		
		otcAdvert.setMinAmount(minAmount);
		otcAdvert.setMaxAmount(maxAmount);
		otcAdvert.setDescription(description);
		otcAdvert.setNote(note);
		otcAdvert.setGreetings(greetings);
		otcAdvert.setTag(tag);
		otcAdvert.setMaxProcessing(maxProcessing);
		otcAdvert.setSuccessCount(successCount);
		
		Date date = new Date();
		otcAdvert.setUpdateTime(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, existenceTime);
		otcAdvert.setOverdueTime(calendar.getTime());
		otcAdvert.setSide(side);
		otcAdvert.setCoinId(coinId);
		otcAdvert.setCurrencyId(currencyId);
		
		Result result = new Result();
		try {
			if (id != null) {
				OtcAdvert advert = otcAdvertService.selectAdvertById(id);
				if (!userInfo.getFid().equals(advert.getUserId())) {
					return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.111"));
				}
				//广告id存在则更新广告
				otcAdvert.setId(id);
				result = otcAdvertService.updateAdvert(otcAdvert);
			} else {
				//广告id不存在则新增广告
				otcAdvert.setCreateTime(new Date());
				Integer advertId = otcAdvertService.releaseAdvert(otcAdvert);
				if (advertId == null) {
					return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.55"));
				} else {
					result = Result.success();
				}
				jsonObject.put("advertId", advertId);
			}
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.57"));
		}
		if (result.getSuccess()) {
            return ReturnResult.SUCCESS(jsonObject);
        } else if (result.getCode().equals(Result.FAILURE)) {
            logger.error("release advert fail, {}", result.getMsg());
            return ReturnResult.FAILUER(result.getMsg());
        }
		return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.59"));
    }
	
	/*
	 * 下架广告
	 */
	@ResponseBody
	@CheckRepeatSubmit
    @RequestMapping("/putOffAdvert")
    public ReturnResult putOffAdvert(
    		@RequestParam(required = true) Integer id
    		) {
		JSONObject jsonObject = new JSONObject();
		
		OtcAdvert advert = otcAdvertService.selectAdvertById(id);
		if (advert == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.69"));
		}
		if (Constant.OTC_ADVERT_OFF.equals(advert.getStatus())) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.75"));
		}
		FUser userInfo = getCurrentUserInfoByToken();
		ReturnResult check = check(advert, userInfo);
		if (check != null) {
			return check;
		}
		Date date = new Date();
		advert.setUpdateTime(date);
		advert.setStatus(Constant.OTC_ADVERT_OFF);
		try {
			Result result = otcAdvertService.putOffAdvert(advert);
			if (result.getSuccess()) {
	            return ReturnResult.SUCCESS(jsonObject);
	        } else if (result.getCode().equals(Result.FAILURE)) {
	            logger.error("put off advert fail, {}", result.getMsg());
	            return ReturnResult.FAILUER(result.getMsg());
	        }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.61"));
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.62"));
	}
	
	/*
	 * 激活广告
	 */
	@ResponseBody
    @RequestMapping("/activityAdvert")
    public ReturnResult activityAdvert(
    		@RequestParam(required = true) Integer id
    		) {
		JSONObject jsonObject = new JSONObject();
		
		Map<String, String> param = otcAdvertService.getParam();
		// 场外OTC手续费
		String feeRate = param.get("feeRate");
		// 买入订单超时期限
		String maxPaytime = param.get("maxPaytime");
		// 广告有效期设置
		Integer existenceTime = Integer.parseInt(param.get("existenceTime"));
		
		OtcAdvert advert = otcAdvertService.selectAdvertById(id);
		if (advert == null) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.69"));
		}
		FUser userInfo = getCurrentUserInfoByToken();
		ReturnResult check = check(advert, userInfo);
		if (check != null) {
			return check;
		}
		
		advert.setFeeRate(new BigDecimal(feeRate));
		advert.setMaxPaytime(Integer.parseInt(maxPaytime));
		
		Date date = new Date();
		advert.setUpdateTime(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, existenceTime);
		advert.setOverdueTime(calendar.getTime());
		advert.setStatus(Constant.OTC_ADVERT_OFF);
		try {
			Result result = otcAdvertService.activityAdvert(advert);
			if (result.getSuccess()) {
	            return ReturnResult.SUCCESS(jsonObject);
	        } else if (result.getCode().equals(Result.FAILURE)) {
	            logger.error("activate advert fail, {}", result.getMsg());
	            return ReturnResult.FAILUER(result.getMsg());
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.67"));
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.68"));
	}
	
	private void setPriceAndPayIcons(Collection<OtcAdvert> advertList) {
		List<OtcPayment> paymentList = redisHelper.getOpenOtcPaymentList();
		for (OtcAdvert element : advertList) {
			//设置价格
			setPrice(element);
			//转换支付图标
			setPayIcons(element, paymentList);
			//修改数量精度
			setVolume(element);
		}
	}
	 
	/**
	 * 设置价格
	 * @param otcAdvert
	 * @param averagePrice
	 * @param binancePrice
	 * @param houbiPrice
	 */
	private void setPrice(OtcAdvert otcAdvert) {
		Integer amountDigit = Constant.OTC_AMOUNT_DIGIT;
		if (otcAdvert.getPriceType() == Constant.OTC_FIXED_PRICE) {
			//固定价格广告
			otcAdvert.setPrice(otcAdvert.getFixedPrice());
		} else {
			BigDecimal acceptablePrice = otcAdvert.getAcceptablePrice();
			//浮动价格广告
			switch (otcAdvert.getFloatMarket()) {
			case 1:
				BigDecimal averagePrice = otcAdvertService.getAveragePrice(otcAdvert.getCoinName(), otcAdvert.getCurrencyName());
				//平均价
				BigDecimal price1 = MathUtils.toScaleNum(MathUtils.mul(otcAdvert.getPriceRate(), averagePrice), amountDigit);
				if (otcAdvert.getAcceptablePrice().compareTo(BigDecimal.ZERO) > 0) {
					if (Constant.OTC_BUY.equals(otcAdvert.getSide())) {
						price1 = price1.compareTo(acceptablePrice) <= 0? price1:acceptablePrice;
					} else {
						price1 = price1.compareTo(acceptablePrice) > 0? price1:acceptablePrice;
					}
				}
				otcAdvert.setPrice(price1);
				break;
			case 2:
				BigDecimal hotcoinPrice = otcAdvertService.getHotcoinPrice(otcAdvert.getCoinId());
				//币安价
				BigDecimal price2 = MathUtils.toScaleNum(MathUtils.mul(otcAdvert.getPriceRate(), hotcoinPrice), amountDigit);
				if (otcAdvert.getAcceptablePrice().compareTo(BigDecimal.ZERO) != 0) {
					if (Constant.OTC_BUY.equals(otcAdvert.getSide())) {
						price2 = price2.compareTo(acceptablePrice) <= 0? price2:acceptablePrice;
					} else {
						price2 = price2.compareTo(acceptablePrice) > 0? price2:acceptablePrice;
					}
				}
				otcAdvert.setPrice(price2);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 转换支付图标
	 * @param otcAdvert
	 */
	private void setPayIcons(OtcAdvert otcAdvert, List<OtcPayment> paymentList) {
		List<String> payIcons = new ArrayList<>();
		for (OtcPayment otcPayment : paymentList) {
			if (otcPayment.getId() == otcAdvert.getBankinfoFirstId()) {
				payIcons.add(otcPayment.getPicture());
			} else if (otcPayment.getId() == otcAdvert.getBankinfoSecondId()) {
				payIcons.add(otcPayment.getPicture());
			} else if (otcPayment.getId() == otcAdvert.getBankinfoThirdId()) {
				payIcons.add(otcPayment.getPicture());
			}
		}
		otcAdvert.setPayIcons(payIcons);
	}
	
	/**
	 * 设置数量精度
	 * @param otcAdvert
	 */
	private void setVolume(OtcAdvert otcAdvert) {
		Integer countDigit = Constant.OTC_COUNT_DIGIT;
		otcAdvert.setVolume(MathUtils.toScaleNum(otcAdvert.getVolume(), countDigit));
		otcAdvert.setVisiableVolume(MathUtils.toScaleNum(otcAdvert.getVisiableVolume(), countDigit));
		otcAdvert.setTradingVolume(MathUtils.toScaleNum(otcAdvert.getTradingVolume(), countDigit));
		otcAdvert.setFrozenVolume(MathUtils.toScaleNum(otcAdvert.getFrozenVolume(), countDigit));
	}
	
	/**
	 * 校验币对是否开放
	 * @param coinId
	 * @return
	 */
	private boolean checkCoin(Integer coinId) {
		List<SystemCoinTypeVO> coinTypeList = ModelMapperUtils.mapper(
				redisHelper.getOpenOtcCoinTypeList(), SystemCoinTypeVO.class);
		for (SystemCoinTypeVO systemCoinTypeVO : coinTypeList) {
			if (systemCoinTypeVO.getId() == coinId) {
				return true;
			}
		}
		return true;
	}
	
	/**
	 * 操作广告前置校验
	 * @param advert
	 * @param userInfo
	 * @return
	 */
	private ReturnResult check(OtcAdvert advert, FUser userInfo) {
		int uid = userInfo.getFid();
		if (advert.getIsFrozen() == Constant.OTC_ADVERT_FROZEN) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.70"));
		}
		if (!checkCoin(advert.getCoinId())) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.71"));
		}
		if (advert.getUserId() != uid) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.72"));
		}
		if (!otcAdvertService.checkUser(userInfo.getFid())) {
			return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.73"));
		}
		return null;
	}
	
	/**
     * 获取支付方式列表
     * @param element
     * @return
     */
    private OtcPayment getOtcPayment(OtcPayment element) {
        OtcPayment otcPayment = new OtcPayment();
        otcPayment.setId(element.getId());
        otcPayment.setPicture(element.getPicture());
        otcPayment.setChineseName(redisHelper.getMultiLangMsg(element.getNameCode()));
        return otcPayment;
    }
    
    /**
     * 一键交易弹框
     */
    @PassToken
    @ResponseBody
    @RequestMapping("/quickTradeIndex")
    public ReturnResult quickTradeIndex(
    		@RequestParam(required = true) String side,
    		@RequestParam(required = true) BigDecimal amount,
    		@RequestParam(required = false, defaultValue="1") Integer type,
    		@RequestParam(required = true) Integer coinId,
    		@RequestParam(required = false, defaultValue = "1") Integer currencyId) {
    	try{
    		FUser userInfo = getCurrentUserInfoByToken();
    		if (Constant.OTC_BUY.equals(side)) {
    			if (userInfo == null) {
    				return ReturnResult.FAILUER(I18NUtils.getString("OtcAdvertController.4"));
				}
//    			BigDecimal otcAccountBalance = otcCoinWalletService.getOtcAccountBalance(userInfo.getFid(), coinId);
//    			if (type == 2 && amount.) {
//					
//				}
//    			jsonObject.put("balance", MathUtils.toScaleNum(otcAccountBalance, countDigit));
    		}
    		//填充数据
    		OtcAdvert otcAdvert = new OtcAdvert();
    		otcAdvert.setSide(side);
    		otcAdvert.setAmount(amount);
    		otcAdvert.setCoinId(coinId);
    		String coinName = otcAdvertService.getCoinName(coinId);
    		String currencyName = otcAdvertService.getCurrencyName(currencyId);
    		//市场平均价
    		BigDecimal averagePrice = otcAdvertService.getAveragePrice(coinName, currencyName);
    		//热币价
    		BigDecimal hotcoinPrice = otcAdvertService.getHotcoinPrice(coinId);
    		//查询各支付方式对应的最佳广告
    		List<OtcAdvert> advertList = otcAdvertService.selectBestAdvertList(otcAdvert, averagePrice, hotcoinPrice, type);
    		List<BestOtcAdvert> bestAdvertList = new ArrayList<>();
    		for (OtcAdvert element : advertList) {
    			//设置价格
    			setPrice(element);
    			BestOtcAdvert bestAdvert = new BestOtcAdvert();
    			bestAdvert.setPayment(element.getPayment());
    			List<OtcPayment> paymentList = redisHelper.getOpenOtcPaymentList();
    			for (OtcPayment otcPayment : paymentList) {
    				if (otcPayment.getId() == bestAdvert.getPayment()) {
    					String msg = redisHelper.getMultiLangMsg(otcPayment.getNameCode());
    					bestAdvert.setPayIcon(otcPayment.getPicture());
    					bestAdvert.setPayName(msg);
    				}
    			}
				bestAdvert.setId(element.getId());
				bestAdvert.setPrice(element.getPrice());
				bestAdvertList.add(bestAdvert);
			}
    		//最佳广告按价格排序，并计算单价最优
    		if (Constant.OTC_SELL.equals(side)) {
    			//买币价格顺序
    			bestAdvertList.sort((o1, o2)->o1.getPrice().compareTo(o2.getPrice()));
    			for (int i = 0; i < bestAdvertList.size(); i++) {
					if (MathUtils.compareTo(bestAdvertList.get(i).getPrice(), bestAdvertList.get(0).getPrice()) <= 0) {
						bestAdvertList.get(i).setIsBest(true);
					} else {
						bestAdvertList.get(i).setIsBest(false);
					}
				}
			} else {
				//卖币价格倒序
				bestAdvertList.sort((o1, o2)->o2.getPrice().compareTo(o1.getPrice()));
				for (int i = 0; i < bestAdvertList.size(); i++) {
					if (MathUtils.compareTo(bestAdvertList.get(i).getPrice(), bestAdvertList.get(0).getPrice()) >= 0) {
						bestAdvertList.get(i).setIsBest(true);
					} else {
						bestAdvertList.get(i).setIsBest(false);
					}
				}
			}
    		JSONObject result = new JSONObject();
    		result.put("bestAdvertList", bestAdvertList);
    		return ReturnResult.SUCCESS(result);
    	} catch (Exception e){
			logger.error("一键买币页面错误", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.130"));
    }
    
}
