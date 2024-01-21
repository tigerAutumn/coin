package com.qkwl.web.front.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeStatusEnum;
import com.qkwl.common.dto.Enum.RedEnvelopeTypeEnum;
import com.qkwl.common.dto.capital.RedEnvelope;
import com.qkwl.common.dto.capital.RedEnvelopeRecord;
import com.qkwl.common.dto.capital.RedEnvelopeResp;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.capital.IRedEnvelopeService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.Utils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class RedEnvelopeController extends JsonBaseController {
	
	@Autowired
	private IUserWalletService userWalletService;
	@Autowired
	private IRedEnvelopeService redEnvelopeService;
	@Autowired
	private IUserService userService;
	
	@RequestMapping(value="/v1/redEnvelope/send",method = {RequestMethod.GET,RequestMethod.POST})
    public RespData<JSONObject> send(
    		@RequestParam(required = true) String content,
    		@RequestParam(required = true) Integer type,
    		@RequestParam(required = true) Integer coinId,
    		@RequestParam(required = true) BigDecimal amount,
    		@RequestParam(required = true) Integer count,
    		@RequestParam(required = true) String tradePassword) {
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
        }
		// 校验交易密码
		FUser fuser = userService.selectUserByFid(user.getFid());
		//检查账户是否异常
		if(fuser.getFiscny().equals(2) || fuser.getFiscoin().equals(2) || !fuser.isOtcAction()) {
			throw new BizException(ErrorCodeEnum.SEND_RED_ENVELOPE_DISABLED);
		}
		String md5 = Utils.MD5(tradePassword);
		if (!md5.equals(fuser.getFtradepassword())) {
			throw new BizException(ErrorCodeEnum.TRADE_PASSWORD_ERROR);
		}
		Integer uid = user.getFid();
		UserCoinWallet userCoinWallet = userWalletService.getUserCoinWallet(uid, coinId);
		if (RedEnvelopeTypeEnum.NORMAL.getCode().equals(type)) {
			amount = MathUtils.mul(amount, new BigDecimal(count));
		}
		if (userCoinWallet.getTotal().compareTo(amount) < 0) {
			throw new BizException(ErrorCodeEnum.TOTAL_NOT_ENOUGH);
		}
		
		RedEnvelope redEnvelope = new RedEnvelope();
		redEnvelope.setUid(uid);
		redEnvelope.setNickname(user.getFnickname());
		redEnvelope.setPhoto(user.getPhoto());
		content = content.replaceAll("& #", "&#");
		redEnvelope.setContent(HtmlUtils.htmlUnescape(content));
		redEnvelope.setType(type);
		redEnvelope.setCoinId(coinId);
		redEnvelope.setAmount(amount);
		redEnvelope.setCount(count);
		
		String id = redEnvelopeService.send(redEnvelope);
		JSONObject result = new JSONObject();
		result.put("id", id);
		return RespData.ok(result);
	}
	
	@PassToken
	@RequestMapping(value="/v1/redEnvelope/getDetail",method = {RequestMethod.GET,RequestMethod.POST})
	public RespData<JSONObject> getDetail(
			@RequestParam(required = true) String id) {
		JSONObject result = new JSONObject();
		RedEnvelope detail = redEnvelopeService.getDetail(id);
		result.put("nickname", detail.getNickname());
		result.put("photo", detail.getPhoto());
		result.put("content", detail.getContent());
		result.put("coinName", detail.getCoinName());
		if (detail.getOverdueTime().before(new Date())) {
			result.put("status", RedEnvelopeStatusEnum.OVERDUE.getCode());
		} else {
			result.put("status", detail.getStatus());
		}
		FUser user = getUser();
		if (user != null) {
			//查询用户是否领取过
			RedEnvelopeRecord param = new RedEnvelopeRecord();
			param.setUid(user.getFid());
			param.setRedEnvelopeNo(id);
			int isReceived = redEnvelopeService.getIsReceived(param);
			result.put("isReceived", isReceived);
		}
		return RespData.ok(result);
	}
	
	@PassToken
	@RequestMapping(value="/v1/redEnvelope/receive",method = {RequestMethod.GET,RequestMethod.POST})
    public RespData<RedEnvelopeResp> receive(
    		@RequestParam(required = true) String id) {
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.ACTIVITY_NOT_LOGIN);
        }
		
		RedEnvelopeResp redEnvelopeResp = redEnvelopeService.receive(user.getFid(), id);
		return RespData.ok(redEnvelopeResp);
	}
	
	@PassToken
	@RequestMapping(value="/v1/redEnvelope/getReceiveDetail",method = {RequestMethod.GET,RequestMethod.POST})
    public RespData<RedEnvelopeResp> getReceiveDetail(
    		@RequestParam(required = true) String id) {
		RedEnvelopeResp redEnvelopeResp = redEnvelopeService.getReceiveDetail(id);
		return RespData.ok(redEnvelopeResp);
	}
	
	@PassToken
	@RequestMapping(value="/v1/redEnvelope/getRecordList",method = {RequestMethod.GET,RequestMethod.POST})
    public RespData<JSONObject> getRecordList(
    		@RequestParam(required = true) String time,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "20") Integer numPerPage) throws Exception {
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.ACTIVITY_NOT_LOGIN);
        }
		Pagination<RedEnvelopeRecord> pageParam = new Pagination<RedEnvelopeRecord>(currentPage, numPerPage);

		JSONObject result = new JSONObject();
		RedEnvelopeRecord param = new RedEnvelopeRecord();
		param.setUid(user.getFid());
		DateFormat format = new SimpleDateFormat("yyyy");
		param.setReceiveTime(format.parse(time));
		Pagination<RedEnvelopeRecord> pagination = redEnvelopeService.getRecordList(pageParam, param);
		// 查询币种名称
		result.put("nickname", user.getFnickname());
		result.put("photo", user.getPhoto());
		//查询总金额
		BigDecimal total = redEnvelopeService.getRecordTotal(param);
		//查询最佳手气红包数
		int bestCount = redEnvelopeService.getBestCount(param);
		result.put("total", total);
		result.put("count", pagination.getTotalRows());
		result.put("bestCount", bestCount);
		result.put("list", pagination.getData());
		return RespData.ok(result);
	}
	
	@PassToken
	@RequestMapping(value="/v1/redEnvelope/getEnvelopeList",method = {RequestMethod.GET,RequestMethod.POST})
	public RespData<JSONObject> getEnvelopeList(
    		@RequestParam(required = true) String time,
    		@RequestParam(required = false, defaultValue = "1") Integer currentPage,
    		@RequestParam(required = false, defaultValue = "20") Integer numPerPage) throws Exception {
		FUser user = getUser();
		if (user == null) {
			throw new BizException(ErrorCodeEnum.ACTIVITY_NOT_LOGIN);
		}
		Pagination<RedEnvelope> pageParam = new Pagination<RedEnvelope>(currentPage, numPerPage);
		JSONObject result = new JSONObject();
		RedEnvelope param = new RedEnvelope();
		param.setUid(user.getFid());
		DateFormat format = new SimpleDateFormat("yyyy");
		param.setCreateTime(format.parse(time));
		Pagination<RedEnvelope> pagination = redEnvelopeService.getEnvelopeList(pageParam, param);
		result.put("nickname", user.getFnickname());
		result.put("photo", user.getPhoto());
		//查询总金额
		BigDecimal total = redEnvelopeService.getEnvelopeTotal(param);
		result.put("total", total);
		result.put("count", pagination.getTotalRows());
		result.put("list", pagination.getData());
		return RespData.ok(result);
	}
	
}
