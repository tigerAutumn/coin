package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.otc.OtcMerchantStatusEnum;
import com.qkwl.common.dto.otc.OtcMerchant;
import com.qkwl.common.dto.otc.OtcOrder;
import com.qkwl.common.dto.otc.OtcUserExt;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.exceptions.BCException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.rpc.otc.IOtcUserService;
import com.qkwl.common.rpc.otc.OtcOrderService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

@Controller
@RequestMapping("/otc")
public class OtcUserController extends JsonBaseController {
	private static final Logger logger = LoggerFactory.getLogger(OtcUserController.class);
	
	@Autowired
	private IOtcUserService otcUserService;
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private IOtcAdvertService otcAdvertService;
	@Autowired
	private OtcOrderService otcOrderService;
	
	/**
     * 获取商户详情
     */
    @ResponseBody
    @RequestMapping("/otc_user_details")
    public ReturnResult merchantDetails(@RequestParam(required = false) Integer userId) {
    	try{
    		FUser userInfo = getCurrentUserInfoByToken();
    		if(userInfo == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10008"));
    		}
    		if(userId == null) {
    			userId = userInfo.getFid();
    		} else {
    			if (!userId.equals(userInfo.getFid())) {
    				// 查询用户是否发布了广告
    				Integer advertCount = otcAdvertService.countAdvertByUid(userId);
    				if (advertCount == 0) {
    					return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10008"));
					}
				}
    		}
    		OtcUserExt otcUserExt = otcUserService.getOtcUserExt(userId);
    		if(otcUserExt == null) {
    			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10008"));
    		}
    		//买卖数
    		Integer buyCount = otcOrderService.countSuccBuyOrder(userId);
    		otcUserExt.setBuyCount(buyCount);
    		Integer sellCount = otcOrderService.countSuccSellOrder(userId);
    		otcUserExt.setSellCount(sellCount);
    		//30天交易次数
    		Integer cmpOrders = otcOrderService.getCmpOrders(otcUserExt.getUserId());
    		otcUserExt.setCmpOrders(cmpOrders);
    		//30天订单完成率
    		String completionRate = otcOrderService.getCompletionRate(otcUserExt.getUserId());
    		otcUserExt.setCompletionRate(completionRate);
    		return ReturnResult.SUCCESS(otcUserExt);
		}catch(Exception e){
			logger.error("获取商户详情异常",e);
		}
    	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000"));
    }
	
	/**
     * 获取商户状态
     */
    @ResponseBody
    @RequestMapping("/getMerchantStatus")
    public ReturnResult getMerchantStatus() {
    	FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
        }
        
        OtcMerchant otcMerchant = otcUserService.getMerchantByUid(user.getFid());
        Integer status = 0;
        if (otcMerchant != null) {
			status = otcMerchant.getStatus();
		}
        JSONObject result = new JSONObject();
        result.put("status", status);
        return ReturnResult.SUCCESS(result);
    }
	
	/**
     * 认证商户申请
     */
    @ResponseBody
    @RequestMapping("/applyAuthentication")
    public ReturnResult applyAuthentication(
    		@RequestParam(required = true) String phone,
    		@RequestParam(required = true) String email,
    		@RequestParam(required = true) String emergencyName,
    		@RequestParam(required = true) String emergencyPhone,
    		@RequestParam(required = true) String relationship,
    		@RequestParam(required = true) String address,
    		@RequestParam(required = true) String image,
    		@RequestParam(required = true) String video) {
    	FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
        }
        String lang = I18NUtils.getCurrentLang();
        if (StringUtils.isBlank(lang)) {
        	logger.info("===========商户语言未配置==========");
        	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
		}
        OtcMerchant otcMerchant = new OtcMerchant();
        otcMerchant.setUid(user.getFid());
        otcMerchant.setName(user.getFrealname());
        otcMerchant.setPhone(StringUtils.isNotEmpty(user.getFtelephone())?user.getFtelephone():phone);
        otcMerchant.setEmail(StringUtils.isNotEmpty(user.getFemail())?user.getFemail():email);
        otcMerchant.setEmergencyName(emergencyName);
        otcMerchant.setEmergencyPhone(emergencyPhone);
        otcMerchant.setRelationship(relationship);
        otcMerchant.setAddress(address);
        otcMerchant.setImage(image);
        otcMerchant.setVideo(video);
        otcMerchant.setStatus(OtcMerchantStatusEnum.Inited.getCode());
        Date now = new Date();
        otcMerchant.setApplyTime(now);
        String depositStr = redisHelper.getSystemArgs(ArgsConstant.OTC_MERCHANT_DEPOSIT);
		BigDecimal deposit = new BigDecimal("500");
		if (depositStr != null) {
			deposit = new BigDecimal(depositStr);
		}
		otcMerchant.setDeposit(deposit);
		otcMerchant.setLanguage(lang);
        try {
        	Result result = otcUserService.applyAuthentication(otcMerchant);
        	if (result.getSuccess()) {
        		if (result.getMsg().equals("first")) {
        			return ReturnResult.SUCCESS(I18NUtils.getString("OtcUserController.1", deposit.toString()));
				} else {
					return ReturnResult.SUCCESS(I18NUtils.getString("OtcUserController.3"));
				}
	        } else if (result.getCode().equals(Result.FAILURE)) {
	            logger.error("applyAuthentication fail, {}", result.getMsg());
	            return ReturnResult.FAILUER(result.getMsg());
	        }
        } catch (BCException e) {
			return ReturnResult.FAILUER(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnResult.FAILUER(I18NUtils.getString("OtcUserController.2"));
		}
        return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000"));
    }
    
    /**
     * 修改商户
     */
    @ResponseBody
    @RequestMapping("/updateMerchant")
    public ReturnResult updateMerchant() {
    	FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
        }
        
        OtcMerchant otcMerchant = otcUserService.getMerchantByUid(user.getFid());
        JSONObject result = new JSONObject();
        result.put("otcMerchant", otcMerchant);
        return ReturnResult.SUCCESS(result);
    }
    
}
