package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.Enum.otc.OtcOrderStatusEnum;
import com.qkwl.common.dto.capital.FUserBankinfoDTO;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.otc.OtcAdvert;
import com.qkwl.common.dto.otc.OtcAppeal;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.rpc.otc.OtcOrderService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.web.Handler.CheckRepeatSubmit;
import com.qkwl.web.front.controller.base.JsonBaseController;
@Controller
@RequestMapping(value="/otc")
public class OtcOrderController extends JsonBaseController{
	private static final Logger logger = LoggerFactory.getLogger(OtcOrderController.class);
	@Autowired
	private OtcOrderService otcOrderService;
	@Autowired
	private IOtcAdvertService otcAdvertService;
	@Autowired
	private IOtcUserService otcUserService;
	@Autowired
	private RedisHelper redisHelper;
	
	/**
     * 创建订单
     */
    @ResponseBody
    @RequestMapping("/order")
    public ReturnResult createOrder(
    		@RequestParam(required = true) Long adId,
    		@RequestParam(required = true) BigDecimal totalAmount,
    		@RequestParam(required = true) Integer payment
    		)
    {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.0"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setAdId(adId);
    		param.setTotalAmount(totalAmount);
    		param.setPayment(payment);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.createOrder(param);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("createOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("创建订单报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.4"));
    }
    
    /**
     * 确认支付
     */
    @ResponseBody
    @RequestMapping("/payOrder")
    public ReturnResult payOrder(
    		@RequestParam(required = true) Long orderId) 
    {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.5"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.payOrder(param);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("confirmOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("确认订单报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.9"));
    }
    
    /**
     * 确认订单
     */
    @ResponseBody
    @CheckRepeatSubmit
    @RequestMapping("/confirmOrder")
    public ReturnResult confirmOrder(
    		@RequestParam(required = true) Long orderId) 
    {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER("请登录！");
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.confirmOrder(param,user);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("confirmOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
    		
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("确认订单报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.14"));
    }
    
    
    /**
     * 取消订单
     */
    @ResponseBody
    @RequestMapping("/cancelOrder")
    public ReturnResult cancelOrder(
    		@RequestParam(required = true) Long orderId) 
    {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.15"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.cancelOrder(param);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("cancelOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("取消订单报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.19"));
    }

    /**
     * 订单详情
     */
    @ResponseBody
    @RequestMapping("/orderDetail")
    public ReturnResult orderDetail(
    		@RequestParam(required = true) Long orderId) 
    {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.20"));
    		}
    		
    		//如果orderId为空则报错
    		if(orderId==null || orderId.compareTo(0l) <= 0) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.21"));
    		}
    		
    		//如果orderId查不到对应的订单信息
    		OtcOrder otcOrder = otcOrderService.findById(orderId);
    		if(otcOrder == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.22"));
    		}
    		
    		//如果订单对应的广告id查询不到广告信息
    		if(otcOrder.getAdId() == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.23"));
    		}
    		
    		//只有订单对应的用户或者广告主才能查询
    		if(!otcOrder.getUserId().equals(user.getFid())
    				&&!otcOrder.getAdUserId().equals(user.getFid()) ) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.24"));
    		}
    		
    		//广告信息
    		OtcAdvert otcAdvert = otcAdvertService.selectAdvertById(otcOrder.getAdId().intValue());
    		
    		if(otcAdvert == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.25"));
    		}
    		
    		int userId = 0;
    		//购买广告则显示用户的支付方式
			if(otcAdvert.getSide().equals(Constant.OTC_BUY)) {
				userId = otcOrder.getUserId();
			}
			else if(otcAdvert.getSide().equals(Constant.OTC_SELL)) {
				//出售广告显示广告主的
				userId = otcAdvert.getUserId();
			}
    		
    		//广告主扩展信息
			int adUserId = 0;
			if(otcOrder.getUserId().equals(user.getFid())) {
				adUserId = otcOrder.getAdUserId();
			}else if (otcOrder.getAdUserId().equals(user.getFid())) {
				adUserId = otcOrder.getUserId();
			}
			
    		OtcUserExt otcUserExt = otcUserService.getOtcUserExt(adUserId);
    		JSONObject result = new JSONObject();
    		//对方信息
    		JSONObject adUser = new JSONObject();
    		JSONObject payment = new JSONObject();
    		adUser.put("nickname", otcUserExt.getNickname());
    		FUser otherUser = otcOrderService.getUser(adUserId);
    		adUser.put("realname", otherUser.getFrealname());
    		//30天交易次数
    		Integer cmpOrders = otcOrderService.getCmpOrders(otcUserExt.getUserId());
    		adUser.put("cmpOrders", cmpOrders);
    		adUser.put("adUserId", adUserId);
    		
    		//历史成交额，根据需求需要换算成BTC
    		//获取BTC实时成交价
			adUser.put("btcAmount", MathUtils.toScaleNum(otcUserExt.getSuccAmt(), 8));
    		
    		adUser.put("photo", otcUserExt.getPhoto());
    		adUser.put("otcUserType", otcUserExt.getOtcUserType());
    		//好评率=好评除以总订单数
    		if(otcUserExt.getCmpOrders()!= null &&otcUserExt.getCmpOrders() > 0
    				&&otcUserExt.getGoodEvaluation()!= null && otcUserExt.getGoodEvaluation()>0) {
    			adUser.put("goodRate", otcUserExt.getApplauseRate());
    		}
    		//30天订单完成率
    		String completionRate = otcOrderService.getCompletionRate(otcOrder.getAdUserId());
    		adUser.put("completionRate", completionRate);
    		
    		if(otcOrder.getOtcPayment() != null) {
				OtcPayment otcPayment = otcOrder.getOtcPayment();
				//查询具体支付方式的账号等信息
				//如果是买入则显示广告主的支付信息
				
				List<FUserBankinfoDTO> list = otcOrderService.getBankInfoListByUser(userId, otcOrder.getPayment());
				if(list != null && list.size() >0) {
					FUserBankinfoDTO bankInfo = list.get(0);
					String msg = redisHelper.getMultiLangMsg(otcPayment.getNameCode());
    				payment.put("chineseName", msg);
    				payment.put("picture", otcPayment.getPicture());
    				payment.put("englishName", otcPayment.getEnglishName());
    				payment.put("bankName", bankInfo.getFname());
    				payment.put("bankNumber", bankInfo.getFbanknumber());
    				payment.put("address", bankInfo.getFprov()+bankInfo.getFcity()+bankInfo.getFaddress());
    				payment.put("realname", bankInfo.getFrealname());
    				payment.put("qrcodeImg", bankInfo.getQrcodeImg());
    				payment.put("type", otcOrder.getPayment());
				}
    		}
    		
    		//申诉信息
    		OtcAppeal otcAppeal = otcOrderService.selectByOrderId(orderId,user.getFid());
    		if(otcAppeal != null) {
    			result.put("appealUserId", otcAppeal.getUserId());
    		}else {
    			result.put("appealUserId", "");
    		}
    		
    		String adCoinName = otcAdvertService.getCoinName(otcAdvert.getCoinId());
    		String symbol = adCoinName+"_GAVC";
    		String[] symbols = symbol.split("_");
            List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
            SystemTradeType systemTradeType = new SystemTradeType();
            for (SystemTradeType tradeType : tradeTypeList) {
                if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                        && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
                	systemTradeType = tradeType;
                }
            }

    		JSONObject otcOrderJson = JSONObject.parseObject(JSONObject.toJSONString(otcOrder));
    		Integer priceDigit = Constant.OTC_AMOUNT_DIGIT;
    		if (otcAdvert.getCoinId().equals(9)) {
    			priceDigit = Constant.OTC_PRICE_DIGIT;
    		}
    		otcOrderJson.put("price",MathUtils.toScaleNum(otcOrderJson.getBigDecimal("price"), priceDigit));
    		otcOrderJson.put("totalAmount",MathUtils.toScaleNum(otcOrderJson.getBigDecimal("totalAmount"), Constant.OTC_AMOUNT_DIGIT));
    		otcOrderJson.put("amount",MathUtils.toScaleNum(otcOrderJson.getBigDecimal("amount"), Constant.OTC_COUNT_DIGIT));
    		if (otcOrder.getAdUserId().equals(user.getFid())) {
    			//广告方才能看到手续费
    			BigDecimal adFee = MathUtils.toScaleNum(otcOrderJson.getBigDecimal("adFee"), Constant.OTC_COUNT_DIGIT);
    			otcOrderJson.put("adFee", adFee);
			} else {
				otcOrderJson.put("adFee", BigDecimal.ZERO);
			}
    		otcOrderJson.put("maxPaytime",otcAdvert.getMaxPaytime());
    		otcOrderJson.remove("otcPayment");
    		result.put("payment", payment);
    		result.put("adUser", adUser);
    		result.put("order", otcOrderJson);
    		result.put("currencyName", otcAdvert.getCurrencyName());
    		result.put("coinName", otcAdvert.getCoinName());
    		result.put("description", otcAdvert.getDescription());
    		result.put("note", otcAdvert.getNote());
    		return ReturnResult.SUCCESS(result);
		}catch (Exception e){
			logger.error("获取订单详情报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.66"));
    }
    
    /**
     * 订单列表
     */
    @ResponseBody
    @RequestMapping("/listOrder")
    public ReturnResult listOrder(
    		@RequestParam(required = false) String sideType,
    		@RequestParam(required = false) Integer coinId,
    		@RequestParam(required = false) Integer currencyId,
    		@RequestParam(required = false) Byte status,
    		@RequestParam(required = true,defaultValue="0") Integer page) {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.67"));
    		}
    		
    		Map<String, Object> param = new HashMap<>();
    		param.put("sideType", sideType);
    		param.put("coinId", coinId);
    		param.put("currencyId", currencyId);
    		param.put("status", status);
    		param.put("userId", user.getFid());
    		
    		PageInfo<OtcOrder> info = otcOrderService.listOrder(param, page, 10);
    		List<OtcOrder> list = info.getList();
    		List<OtcOrder> newList = new ArrayList<OtcOrder>();
    		
    		for(OtcOrder otcOrder : list) {
    			if(otcOrder != null) {
    				//是当前用户下的订单则是增项
    				if(otcOrder.getUserId().equals(user.getFid())) {
    					if(otcOrder.getSide().equals("BUY")) {
    						otcOrder.setSide("SELL");
    					}else if(otcOrder.getSide().equals("SELL")) {
    						otcOrder.setSide("BUY");
    					}
    					otcOrder.setAdFee(BigDecimal.ZERO);
    				}
    				
    				if(otcOrder.getBuyerId().equals(user.getFid())) {
    					otcOrder.setSideType("BUY");
    				}else if(otcOrder.getSellerId().equals(user.getFid())) {
    					otcOrder.setSideType("SELL");
    				}
    				Integer countDigit = Constant.OTC_COUNT_DIGIT;
    				Integer priceDigit = Constant.OTC_AMOUNT_DIGIT;
    				if (otcOrder.getCoinId().equals(9)) {
    					priceDigit = Constant.OTC_PRICE_DIGIT;
    				}
    				BigDecimal price = MathUtils.toScaleNum(otcOrder.getPrice(), priceDigit);
    				BigDecimal amount = MathUtils.toScaleNum(otcOrder.getAmount(), countDigit);
    				BigDecimal totalAmount = MathUtils.toScaleNum(otcOrder.getTotalAmount(), Constant.OTC_AMOUNT_DIGIT);
    				BigDecimal adFee = MathUtils.toScaleNum(otcOrder.getAdFee(), countDigit);
    				otcOrder.setPrice(price);
    				otcOrder.setAmount(amount);
    				otcOrder.setTotalAmount(totalAmount);
    				otcOrder.setAdFee(adFee);
    				newList.add(otcOrder);
    			}
    		}
    		info.setList(newList);
	        return ReturnResult.SUCCESS(info);
		}catch (Exception e){
			logger.error("获取订单列表报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.84"));
    }
    
    
    /**
     * 获取用户未完成订单
     */
    @ResponseBody
    @RequestMapping("/countOrder")
    public ReturnResult countOrder() {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.85"));
    		}
    		
    		int count = otcOrderService.countOrder(user.getFid());
	        return ReturnResult.SUCCESS(count);
		}catch (Exception e){
			logger.error("获取订单列表报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.87"));
    }
    
    /**
     * 延长订单付款时间
     */
    @ResponseBody
    @RequestMapping("/extendOrder")
    public ReturnResult extendOrder(
    		@RequestParam(required = true) Long orderId) {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.88"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.extendOrder(param);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("extendOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("获取订单列表报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.92"));
    }
    
    /**
     * 申诉订单
     */
    @ResponseBody
    @RequestMapping("/appealOrder")
    public ReturnResult appealOrder(
    		@RequestParam(required = true) Long orderId,
    		@RequestParam(required = true) Integer type,
    		@RequestParam(required = false) String content) {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.93"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		
    		OtcAppeal otcAppeal = new OtcAppeal();
    		otcAppeal.setType(type.byteValue());
    		otcAppeal.setContent(content);
    		Result result = otcOrderService.appealOrder(param,otcAppeal);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("extendOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("获取订单列表报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.97"));
    }
    
    /**
     * 取消申诉订单
     */
    @ResponseBody
    @RequestMapping("/appealCancel")
    public ReturnResult appealCancel(
    		@RequestParam(required = true) Long orderId) {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.98"));
    		}
    		
    		OtcOrder param = new OtcOrder();
    		param.setId(orderId);
    		param.setUserId(user.getFid());
    		Result result = otcOrderService.appealCancel(param);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("extendOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("获取订单列表报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.102"));
    }
    
    /**
     * 提交评价
     */
    @ResponseBody
    @RequestMapping("/evaluate")
    public ReturnResult evaluate(
    		@RequestParam(required = true)Long orderId,
    		@RequestParam(required = true)Integer type) {
    	try{
			FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.103"));
    		}
    		
    		//1、好评 2、中评 3、差评
    		Result result = otcOrderService.evaluation(orderId,user.getFid(),type);
    		if (result.getSuccess()) {
                return ReturnResult.SUCCESS(result.getData());
            } else if (result.getCode().equals(Result.PARAM)) {
                logger.error("extendOrder is param error, {}", result.getMsg());
                return ReturnResult.FAILUER(result.getCode(),result.getMsg());
            } else if (result.getCode() >= 10000) {
                return ReturnResult.FAILUER(result.getCode(), I18NUtils.getString("com.error." + result.getCode().toString(), result.getData().toString()));
            } else {
                return ReturnResult.FAILUER(result.getCode(), result.getMsg());
            }
		} catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e){
			logger.error("提交评价报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.107"));
    }
    
    /**
     * 获取订单状态
     */
    @ResponseBody
    @RequestMapping("/orderStatus")
    public ReturnResult orderStatus(
    		@RequestParam(required = true)Long orderId) {
    	try{
    		FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.108"));
    		}
    		
    		//如果orderId为空则报错
    		if(orderId.compareTo(0l) <= 0) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.109"));
    		}
    		
    		//如果orderId查不到对应的订单信息
    		OtcOrder otcOrder = otcOrderService.findById(orderId);
    		if(otcOrder == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.110"));
    		}
    		
    		//如果订单对应的广告id查询不到广告信息
    		if(otcOrder.getAdId() == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.111"));
    		}
    		
    		//只有订单对应的用户或者广告主才能查询
    		if(!otcOrder.getUserId().equals(user.getFid())
    				&&!otcOrder.getAdUserId().equals(user.getFid()) ) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.112"));
    		}
    		
    		JSONObject result = new JSONObject();
    		result.put("status", otcOrder.getStatus());
    		return ReturnResult.SUCCESS(otcOrder.getStatus());
		}catch (Exception e){
			logger.error("提交评价报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.115"));
    }
    
    /**
     * 获取订单聊天信息
     */
    @ResponseBody
    @RequestMapping("/orderUserInfo")
    public ReturnResult orderUserInfo(
    		@RequestParam(required = true)Long orderId) {
    	try{
    		FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.116"));
    		}
    		
    		//如果orderId为空则报错
    		if(orderId.compareTo(0l) <= 0) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.117"));
    		}
    		
    		//如果orderId查不到对应的订单信息
    		OtcOrder otcOrder = otcOrderService.findById(orderId);
    		if(otcOrder == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.118"));
    		}
    		
    		//如果订单对应的广告id查询不到广告信息
    		if(otcOrder.getAdId() == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.119"));
    		}
    		
    		//只有订单对应的用户或者广告主才能查询
    		if(!otcOrder.getUserId().equals(user.getFid())
    				&&!otcOrder.getAdUserId().equals(user.getFid()) ) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.120"));
    		}
    		
    		JSONObject adUserJson = new JSONObject();
    		JSONObject orderUserJson = new JSONObject();
    		//广告主信息
    		OtcUserExt adUser = otcUserService.getOtcUserExt(otcOrder.getUserId());
    		adUserJson.put("id", adUser.getUserId());
    		adUserJson.put("nickname", adUser.getNickname());
    		adUserJson.put("photo", adUser.getPhoto());
    		adUserJson.put("otcUserType", adUser.getOtcUserType());
    		//订单用户信息
    		OtcUserExt orderUser = otcUserService.getOtcUserExt(otcOrder.getAdUserId());
    		orderUserJson.put("id", orderUser.getUserId());
    		orderUserJson.put("nickname", orderUser.getNickname());
    		orderUserJson.put("photo", orderUser.getPhoto());
    		orderUserJson.put("otcUserType", orderUser.getOtcUserType());
    		if (otcOrder.getUserId().equals(user.getFid())) {
    			//30天交易次数
        		Integer cmpOrders = otcOrderService.getCmpOrders(otcOrder.getAdUserId());
    			adUserJson.put("cmpOrders", cmpOrders);
    			//30天订单完成率
        		String completionRate = otcOrderService.getCompletionRate(otcOrder.getAdUserId());
        		adUserJson.put("completionRate", completionRate);
			} else {
				//30天交易次数
        		Integer cmpOrders = otcOrderService.getCmpOrders(otcOrder.getUserId());
    			adUserJson.put("cmpOrders", cmpOrders);
				//30天订单完成率
        		String completionRate = otcOrderService.getCompletionRate(otcOrder.getUserId());
        		orderUserJson.put("completionRate", completionRate);
			}
    		JSONObject result = new JSONObject();
    		result.put("adUser", adUserJson);
    		result.put("orderUser", orderUserJson);
    		return ReturnResult.SUCCESS(result);
		}catch (Exception e){
			logger.error("提交评价报错", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.130"));
    }
    
    /**
     * 获取隐私号码
     */
    @ResponseBody
    @RequestMapping("/getPrivacyPhone")
    public ReturnResult getPrivacyPhone(
    		@RequestParam(required = true)Long orderId) {
    	try{
    		FUser user = getUser();
    		if(user == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.116"));
    		}
    		OtcOrder otcOrder = otcOrderService.findById(orderId);
    		if (otcOrder == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.110"));
			}
    		String otherPhone = "";
    		String otherAreacode = "";
    		if (otcOrder.getUserId().equals(user.getFid())) {
    			FUser fuser = otcUserService.getUser(otcOrder.getAdUserId());
    			otherPhone = fuser.getFtelephone();
    			otherAreacode = fuser.getFareacode();
    		} else if (otcOrder.getAdUserId().equals(user.getFid())) {
    			FUser fuser = otcUserService.getUser(otcOrder.getUserId());
    			otherPhone = fuser.getFtelephone();
    			otherAreacode = fuser.getFareacode();
    		} else {
    			//用户不是交易双方，没有权限查看
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.121"));
    		}
    		
    		//判断订单状态，只有未支付、已支付和申诉中才能打电话
    		if (otcOrder.getStatus().equals(OtcOrderStatusEnum.COMPLETED.getCode().byteValue())
    				|| otcOrder.getStatus().equals(OtcOrderStatusEnum.CANCELLED.getCode().byteValue())
    				|| otcOrder.getStatus().equals(OtcOrderStatusEnum.TIMEOUT.getCode().byteValue())) {
    			return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.122")); 
    		}
    		
    		JSONObject result = new JSONObject();
    		result.put("phone", user.getFareacode()+SecurityUtil.getStr(user.getFtelephone()));
    		result.put("privacyPhone", otherAreacode+otherPhone);
    		return ReturnResult.SUCCESS(result);
    	} catch (Exception e){
			logger.error("获取隐私号码报错:{}", e);
			e.printStackTrace();
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("OtcOrderController.130"));
    }
}

