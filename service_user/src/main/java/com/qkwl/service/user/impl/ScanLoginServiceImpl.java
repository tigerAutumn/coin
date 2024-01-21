package com.qkwl.service.user.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.crypto.MD5Util;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.LimitTypeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.user.FUserFavoriteTrade;
import com.qkwl.common.dto.user.FUserScore;
import com.qkwl.common.dto.user.GenerateQRCodeTokenResp;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.limit.LimitHelper;
import com.qkwl.common.framework.mq.ScoreHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.redis.RedisObject;
import com.qkwl.common.rpc.user.IScanLoginService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.GUIDUtils;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.Ip2Long;
import com.qkwl.common.util.UUIDUtils;
import com.qkwl.service.user.config.HkWebProperties;
import com.qkwl.service.user.dao.FUserExtendMapper;
import com.qkwl.service.user.dao.FUserFavoriteTradeMapper;
import com.qkwl.service.user.dao.FUserMapper;
import com.qkwl.service.user.dao.FUserScoreMapper;
import com.qkwl.service.user.utils.MQSend;

/**
 * 扫码登录服务
 * 
 * @author huangjinfeng
 */
@Service("scanLoginService")
public class ScanLoginServiceImpl implements IScanLoginService {

	private static final Logger logger = LoggerFactory.getLogger(ScanLoginServiceImpl.class);

	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private FUserMapper userMapper;
	@Autowired
	private FUserFavoriteTradeMapper userFavoriteTradeMapper;
	@Autowired
	private FUserScoreMapper userScoreMapper;
	@Autowired
	private MemCache memCache;
	@Autowired
	private ScoreHelper scoreHelper;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private MQSend mqSend;
	@Autowired
	private LimitHelper limitHelper;
	@Autowired
	private FUserExtendMapper userExtendMapper;
	@Autowired
	private HkWebProperties hkWebProperties;

	@Override
	public GenerateQRCodeTokenResp generateQRCodeToken() {
		String qrCodeToken =UUIDUtils.get32UUID();
		redisHelper.set(RedisConstant.SCAN_LOGIN_DB.concat(qrCodeToken), "", RedisConstant.SCAN_LOGIN_QR_CODE_EXPIRE_TIME);
		GenerateQRCodeTokenResp resp = new GenerateQRCodeTokenResp();
		resp.setQrCodeToken(String.format("%s?%s=%s", hkWebProperties.getScanUrl(),"qrCodeToken",qrCodeToken));
		return resp;
	}

	@Override
	public void determine(Integer userId, String qrCodeToken) {
		if (!redisHelper.exists(RedisConstant.SCAN_LOGIN_DB.concat(qrCodeToken))) {
			throw new BizException(ErrorCodeEnum.SCAN_LOGIN_QR_CODE_EXPIRE);
		}
		redisHelper.set(RedisConstant.SCAN_LOGIN_DB.concat(qrCodeToken), String.valueOf(userId), RedisConstant.SCAN_LOGIN_QR_CODE_EXPIRE_TIME);
	}

	@Override
	@Transactional(value = "flexibleTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public LoginResponse scanLogin(String qrCodeToken, String ip, LocaleEnum lan, RequestUserInfo userinfo) {
		qrCodeToken=StringUtils.substringAfterLast(qrCodeToken, "=");
		
		if (!redisHelper.exists(RedisConstant.SCAN_LOGIN_DB.concat(qrCodeToken))) {
			throw new BizException(ErrorCodeEnum.SCAN_LOGIN_QR_CODE_EXPIRE);
		}

		long start = System.currentTimeMillis();
		String userId=null;
		do {
			userId = redisHelper.get(RedisConstant.SCAN_LOGIN_DB.concat(qrCodeToken));
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}while(StringUtils.isBlank(userId)&&System.currentTimeMillis()-start<25*1000);
		
		if (StringUtils.isBlank(userId)) {
			throw new BizException(ErrorCodeEnum.WAITING_FOR_SCAN_LOGIN);
		}

		FUser user = userMapper.selectByPrimaryKey(Integer.valueOf(userId));

		if (user == null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
		}

		FUserFavoriteTrade fUserFavoriteTrade = userFavoriteTradeMapper.selectByUid(user.getFid());
		user.setfFavoriteTradeList(Optional.ofNullable(fUserFavoriteTrade).map(e -> e.getFfavoritetradelist()).orElse(null));

		// 查询会员等级
		FUserScore userScore = userScoreMapper.selectByUid(user.getFid());
		Optional.ofNullable(userScore).orElseThrow(() -> new BizException(ErrorCodeEnum.USER_SCORE_NO_EXIST));

		// 设置等级
		user.setLevel(userScore.getFlevel());
		user.setScore(userScore.getFscore());

		// 设置收藏列表
		JSONArray array = new JSONArray();
		if (StringUtils.isEmpty(user.getfFavoriteTradeList())) {
			user.setTradeList(array);
		} else {
			array = JSONArray.parseArray(user.getfFavoriteTradeList());
			user.setTradeList(array);
		}

		LoginResponse result = new LoginResponse();
		// 限制登录
		String isCanlogin = redisHelper.getSystemArgs(ArgsConstant.ISCANLOGIN);
		if (isCanlogin != null && !isCanlogin.trim().equals("1")) {
			throw new BizException(ErrorCodeEnum.SUSPEND_LOGIN_DURING_SYSTEM_UPGRADE);
		}
		// 验证帐号状态
		if (user.getFstatus() == UserStatusEnum.FORBBIN_VALUE) {
			throw new BizException(ErrorCodeEnum.LOCKED_ACCOUNT);
		}
		// 是否能开启otc功能，默认开启
		user.setCanOpenOtc("1");
		String isOpenOtcValidate = redisHelper.getSystemArgs("isOpenOtcValidate").trim();

		if (isOpenOtcValidate != null && isOpenOtcValidate.equals("1")) {
			String whiteListString = redisHelper.getSystemArgs("otcWhiteList");

			if (whiteListString != null) {
				String[] whiteList = whiteListString.split("#");
				// 白名单里面不包含
				if (!Arrays.asList(whiteList).contains(user.getFid().toString())) {
					user.setCanOpenOtc("0");
				}
			} else {
				user.setCanOpenOtc("0");
			}
		}

		// 是否能开启矿池功能，默认开启
		user.setCanOpenOrepool("1");
		String isOpenOrepoolValidate = redisHelper.getSystemArgs("isOpenOrepoolValidate").trim();

		if (isOpenOrepoolValidate != null && isOpenOrepoolValidate.equals("1")) {
			String whiteListString = redisHelper.getSystemArgs("orepoolWhiteList");

			if (whiteListString != null) {
				String[] whiteList = whiteListString.split("#");
				// 白名单里面不包含
				if (!Arrays.asList(whiteList).contains(user.getFid().toString())) {
					user.setCanOpenOrepool("0");
				}
			} else {
				user.setCanOpenOrepool("0");
			}
		}

		// 是否开启杠杆
		if (null == user.getLeveracctid()) {
			user.setIsOpenLever("0");
		} else {
			user.setIsOpenLever("1");
		}

		LoginResponse resp = new LoginResponse();
		// 组装redis
		String md5AccountId = MD5Util.md5(String.valueOf(user.getFid()));
		String accountTokenInfo = "";
		accountTokenInfo = RedisConstant.ACCOUNT_LOGIN_TOTAL_KEY + md5AccountId + "_";
		String token = accountTokenInfo + GUIDUtils.getGUIDString();
		RedisObject obj = new RedisObject();
		obj.setExtObject(user);

		memCache.removeByPattern(accountTokenInfo);
		memCache.set(token, JSON.toJSONString(obj), Constant.EXPIRETIME);

		// 删除过往交易密码缓存
		String tpStr = RedisConstant.TRADE_NEED_PASSWORD + MD5Util.md5(String.valueOf(user.getFid()));
		memCache.delete(tpStr);

		// 返回登录数据
		result.setToken(token);
		result.setUserinfo(user);

		FUser fuser = result.getUserinfo();
		Long lastIp = fuser.getFlastip();

		// 创建或更新用户语言
		FUserExtend userExtend = userExtendMapper.selectByUid(fuser.getFid());
		if (userExtend == null) {
			FUserExtend record = new FUserExtend();
			record.setUid(fuser.getFid());
			record.setLanguage(I18NUtils.getCurrentLang());
			record.setCreateTime(new Date());
			record.setUpdateTime(new Date());
			userExtendMapper.insert(record);
		} else {
			userExtend.setLanguage(I18NUtils.getCurrentLang());
			userExtend.setUpdateTime(new Date());
			userExtendMapper.updateLanguage(userExtend);
		}

		// 是否更新积分
		int isTodayFirstLogin = 0;
		String last = DateUtils.format(user.getFlastlogintime(), "yyyy-MM-dd");
		String now = DateUtils.format(new Date(), "yyyy-MM-dd");
		if (last.equals(now)) {
			isTodayFirstLogin = 1;
		}

		Date date = new Date();
		user.setFlastlogintime(date);
		user.setFlastip(Ip2Long.ip2Long(ip));
		// 更新登录时间
		userMapper.updateLoginTime(user);

		// 首次登录
		if (isTodayFirstLogin == 0) {
			scoreHelper.SendUserScore(user.getFid(), BigDecimal.ZERO, ScoreTypeEnum.LOGIN.getCode(), "登录");
		}
		// 返回数据
		result.getUserinfo().setIp(ip);
		// MQ_USER_ACTION
		StringBuffer buff = new StringBuffer();
		buff.append(String.valueOf(UserLoginType.WebUser.getValue()));
		mqSend.SendUserAction(user.getFagentid(), user.getFid(), LogUserActionEnum.LOGIN, ip, UserLoginType.WebUser.getCode(), buff.toString());

		// 登录提醒邮件
		if (lastIp != null && !Ip2Long.long2ip(lastIp).equals(ip)) {
			if (user.getFismailbind()) {
				validateHelper.mailSendContent(user.getFemail(), userinfo.getPlatform(), lan, BusinessTypeEnum.EMAIL_NEW_IP_LOGIN, ip, user, Optional.ofNullable(userExtend).map(e -> e.getAntiPhishingCode()).orElse(""));
			} else {
				validateHelper.smsValidateCode(Integer.valueOf(0), user.getFareacode(), user.getFtelephone(), SendTypeEnum.SMS_TEXT.getCode(), PlatformEnum.BC.getCode(), BusinessTypeEnum.SMS_NEW_IP_LOGIN.getCode(), lan.getCode());
			}
		}
		// 清空ip登陆错误次数
		limitHelper.deleteLimit(ip, LimitTypeEnum.LoginPassword.getCode());
		return result;
	}
}
