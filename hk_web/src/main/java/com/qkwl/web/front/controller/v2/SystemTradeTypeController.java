package com.qkwl.web.front.controller.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qkwl.common.dto.NewCoinCountdownResp;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.RespData;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags="币对相关")
public class SystemTradeTypeController extends JsonBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SystemTradeTypeController.class);

	@Autowired
	private RedisHelper redisHelper;

	
	
	@ApiOperation("新币倒计时")
	@GetMapping(value = "/newCoinCountdown")
	@PassToken
	public RespData<NewCoinCountdownResp> newCoinCountdown(@RequestParam Integer tradeId) {
		SystemTradeType systemTradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
		if(systemTradeType==null||systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())||systemTradeType.getListedDateTime()==null) {
			throw new BizException(ErrorCodeEnum.NOT_NEW_COIN);
		}
		
		long countdown=systemTradeType.getListedDateTime().getTime()-System.currentTimeMillis();
		if(countdown<0) {
			countdown=0;
		}
		
		NewCoinCountdownResp resp=new NewCoinCountdownResp();
		resp.setCountdown(countdown/1000);
		resp.setProjectBrief(redisHelper.getSystemArgs(String.format("%s_%s_%s", Constant.PROJECT_BRIEF,systemTradeType.getSellShortName(),systemTradeType.getBuyShortName())));
		resp.setProjectWebsite(redisHelper.getSystemArgs(String.format("%s_%s_%s", Constant.PROJECT_WEBSITE,systemTradeType.getSellShortName(),systemTradeType.getBuyShortName())));
		return RespData.ok(resp);
	}
	
	

}
