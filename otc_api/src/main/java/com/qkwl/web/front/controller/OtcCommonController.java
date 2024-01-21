package com.qkwl.web.front.controller;

import java.util.ArrayList;
import java.util.List;

import com.qkwl.web.permission.annotation.PassToken;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.coin.SystemCoinTypeVO;
import com.qkwl.common.dto.otc.OtcCurrency;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.otc.IOtcAdvertService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.ModelMapperUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;

@Controller
@RequestMapping("/otc/common")
public class OtcCommonController extends JsonBaseController {

	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private IOtcAdvertService otcAdvertService;
	
	/*
 	 * otc 系统参数接口
 	 */
	@PassToken
 	@ResponseBody
    @RequestMapping("/systemArgs")
    public ReturnResult systemArgs() {
 		JSONObject jsonObject = new JSONObject();
//		1. 查询所有法币币种
		List<OtcCurrency> currencyList = new ArrayList<>();
		currencyList = redisHelper.getOpenOtcCurrencyList();

//		2. 查询所有支付方式
		List<OtcPayment> paymentList = new ArrayList<>();
		paymentList = redisHelper.getOpenOtcPaymentList();
		List<OtcPayment> collect = paymentList.stream().filter(p -> p.getStatus() == OtcPaymentStatusEnum.Normal.getCode()).map(e->otcPaymentWithName(e)).collect(Collectors.toList());

//		3. 查询所有开放的币对
		List<SystemCoinTypeVO> coinTypeList = new ArrayList<>();
		coinTypeList = ModelMapperUtils.mapper(redisHelper.getOpenOtcCoinTypeList(), SystemCoinTypeVO.class);

		for (SystemCoinTypeVO systemCoinTypeVO : coinTypeList) {
			if (systemCoinTypeVO.getId().equals(9)) {
				systemCoinTypeVO.setPriceDigit(Constant.OTC_PRICE_DIGIT);
			} else {
				systemCoinTypeVO.setPriceDigit(Constant.OTC_AMOUNT_DIGIT);
			}
			systemCoinTypeVO.setCountDigit(Constant.OTC_COUNT_DIGIT);
			systemCoinTypeVO.setAmountDigit(Constant.OTC_AMOUNT_DIGIT);
		}
		
		jsonObject.put("currencyList", currencyList);
		jsonObject.put("paymentList", paymentList);
		jsonObject.put("coinTypeList", coinTypeList);
 		ReturnResult result = ReturnResult.SUCCESS(jsonObject);
    	return result;
 	}
	
    private OtcPayment otcPaymentWithName(OtcPayment o) {
      String msg = redisHelper.getMultiLangMsg(o.getNameCode());
      o.setName(msg);
      o.setChineseName(msg);
      return o;
    }
 	
}
