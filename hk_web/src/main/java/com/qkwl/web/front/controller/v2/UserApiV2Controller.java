package com.qkwl.web.front.controller.v2;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.IdentityStatusEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.UserLoginType;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.DeviceInfo;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.user.FUserFavoriteTrade;
import com.qkwl.common.dto.user.FUserIdentity;
import com.qkwl.common.dto.user.LoginResponse;
import com.qkwl.common.dto.user.ModifyAntiPhishingCodeDTO;
import com.qkwl.common.dto.user.RequestUserInfo;
import com.qkwl.common.dto.user.SetAntiPhishingCodeDTO;
import com.qkwl.common.dto.user.UserInfoExtend;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.oss.OssHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.http.HttpUtil;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserWalletBalanceLogService;
import com.qkwl.common.rpc.capital.IUserWalletService;
import com.qkwl.common.rpc.user.IUserExtendService;
import com.qkwl.common.rpc.user.IUserFavoriteService;
import com.qkwl.common.rpc.user.IUserIdentityService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.rpc.v2.UserSecurityService;
import com.qkwl.common.util.ArgsConstant;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.DateUtils;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.PhoneValiUtil;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.common.util.SecurityUtil;
import com.qkwl.common.util.Utils;
import com.qkwl.web.config.FaceIdProperties;
import com.qkwl.web.config.RSAProperties;
import com.qkwl.web.dto.ModifyAntiPhishingCodeReq;
import com.qkwl.web.dto.SetAntiPhishingCodeReq;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.APISignPermission;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.GeetestLib;
import com.qkwl.web.utils.GeetestProperties;
import com.qkwl.web.utils.RSAUtils;
import com.qkwl.web.utils.WebConstant;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class UserApiV2Controller extends JsonBaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(UserApiV2Controller.class);
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private RedisHelper redisHelper;
	
	@Autowired
	private IUserIdentityService userIdentityService;

	@Autowired
	private UserSecurityService userSecurityService;
	@Autowired
	private IUserWalletService userWalletService;
	
	@Autowired
    private FaceIdProperties faceIdProperties;
	
	@Autowired
	private OssHelper ossHelper;
	
	@Autowired
	private GeetestProperties geetestProperties;
	
	@Autowired
	private MemCache memCache;
	@Autowired
	private IUserWalletBalanceLogService userWalletBalanceLogService;
	
	@Autowired
	private RSAProperties rsaProperties;
	
	@Autowired
	private ValidateHelper validateHelper;
	
	@Autowired
	private IUserExtendService userExtendService;
	
	@Autowired
    private IUserFavoriteService userFavoriteService;
	
	//新版获取实名认证接口token
		@ApiOperation("")
	@RequestMapping(value="/v2/getValidateToken",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult getValidateToken() {
		FUser user = getUser();
		
		if (user == null) {
			throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        }
		
		//如果用户实名认证过则返回错误
		if(user.getFhasrealvalidate()) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
		}
		
		String loginToken = null;
		String webToken = sessionContextUtils.getContextToken("token"); 
	    String appToken = sessionContextUtils.getContextApiToken();
		
		if(StringUtils.isNotEmpty(webToken)) {
			loginToken = webToken;
		}else {
			loginToken = appToken;
		}
		
		Map<String, String> param = new HashMap<>();
    	param.put("api_key", faceIdProperties.getApi_key()); 
    	param.put("api_secret", faceIdProperties.getApi_secret()); 
    	param.put("return_url", faceIdProperties.getReturn_url()+"?biz_no="+user.getFid());
    	param.put("notify_url", faceIdProperties.getNotify_url()+"?biz_no="+user.getFid()+"&token="+loginToken);
    	param.put("biz_no", user.getFid()+"");
    	param.put("comparison_type", faceIdProperties.getComparison_type()); 
    	param.put("idcard_mode", faceIdProperties.getIdcard_mode()); 
    	param.put("web_title", faceIdProperties.getIdcard_mode()); 
		
    	//判断用户限制，每天只能认证3次
    	String key = "faceId_token_limit_"+user.getFid(); 
        //从redis获取count
        String countStr = redisHelper.get(key);
        if(StringUtils.isNotEmpty(countStr)) {
        	int count = Integer.parseInt(countStr);
            
            if(count > 3) {
            	return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.16")); 
            }
        }
		//请求旷视API获取token
		String resultStr = HttpUtil.sendPost(faceIdProperties.getToken_url(), param);
		JSONObject result = JSONObject.parseObject(resultStr);
		String token = result.getString("token"); 
		//token过期时间2小时
		int expireSeconds = 2 * 60 * 60;
		redisHelper.setRedisData("faceId_token_"+user.getFid(), result, expireSeconds); 
		return ReturnResult.SUCCESS(200,"https://api.megvii.com/faceid/lite/do?token="+token); 
	}
	
	//新版实名认证返回页面
	
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/returnUrl",method = {RequestMethod.GET,RequestMethod.POST})
	public void returnUrl(String biz_no, HttpServletResponse response) throws IOException {
			FUserIdentity identity = new FUserIdentity();
			identity.setFuid(Integer.valueOf(biz_no));
			identity.setFcountry("中国大陆(China)"); 
			identity.setFtype(0);
			identity.setFstatus(IdentityStatusEnum.WAIT.getCode());
			Date now = new Date();
			identity.setFcreatetime(now);
			identity.setFupdatetime(now);
			identity.setNewValidate(true);
			userIdentityService.updateNormalIdentity(identity);
			response.sendRedirect(faceIdProperties.getReturn_redirect_url());
	}
	
	//新版获取实名认证接口token
	
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/getValidateStatus",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult getValidateStatus() {
		FUser user = getUser();
		if (user == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.0"));
		}
		//查询数据库里实名认证状态
		FUserIdentity userIdentity = userIdentityService.selectByUser(user.getFid());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fstatus", userIdentity.getFstatus()); 
		return ReturnResult.SUCCESS(jsonObject);
	}
	
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/notifyValidate",method = {RequestMethod.GET,RequestMethod.POST})
	public void notifyValidate(String biz_no, HttpServletRequest request, HttpServletResponse response) {
			String data = request.getParameter("data");
			logger.info("=====================data:"+data);
			FUserIdentity userIdentity = userIdentityService.selectByUser(Integer.valueOf(biz_no));
			//非大陆的返回
			if (userIdentity != null && !"中国大陆(China)".equals(userIdentity.getFcountry())) { 
				return;
			}

			//初始认为失败
			Integer businessType = BusinessTypeEnum.SMS_NOTIFY_FAIL.getCode();
			FUser fuser = userService.selectUserById(Integer.parseInt(biz_no));
			FUserExtend userExtend = userExtendService.selectByUid(Integer.parseInt(biz_no));
			Integer langCode = 1;
			if (userExtend != null) {
				langCode = LocaleEnum.getCodeByName(userExtend.getLanguage());
			}
			
			FUserIdentity identity = new FUserIdentity();
			identity.setFuid(Integer.parseInt(biz_no));
			identity.setFcountry("中国大陆(China)"); 
			identity.setFtype(0);
			identity.setFstatus(IdentityStatusEnum.NOTPASS.getCode());
			identity.setNewValidate(true);

			JSONObject resultObj = new JSONObject();
			resultObj.put("code", 300); 
			resultObj.put("msg", I18NUtils.getString("UserApiV2Controller.28"));  //$NON-NLS-2$

			// 用户实名认证状态key
			int expireSeconds = 2 * 60 * 60;
			String statusKey = "faceId_result_" + biz_no; 
			String key = "faceId_token_limit_" + biz_no; 

			String resultStr = redisHelper.getRedisData("faceId_token_" + biz_no); 
			if (StringUtils.isEmpty(resultStr)) {
				int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
				redisHelper.getIncrByKey(key,sec);
				userIdentityService.updateNormalIdentity(identity);
				resultObj.put("msg", I18NUtils.getString("UserApiV2Controller.33"));  //$NON-NLS-2$
				redisHelper.setRedisData(statusKey, resultObj, expireSeconds);
				//送短信
				validateHelper.smsSensitiveInfo(fuser.getFareacode(), fuser.getFtelephone(), langCode, 
						PlatformEnum.BC.getCode(), businessType);
				return;
			}

			JSONObject result = JSONObject.parseObject(resultStr);
			String biz_id = result.getString("biz_id"); 

			JSONObject dataJson = JSONObject.parseObject(data);
			if (dataJson != null) {
				// 判断实名认证状态，如果不为ok的话则是正在进行中或者
				String status = dataJson.getString("status"); 
				if (StringUtils.isNotEmpty(status)) {
					// 如果是在进行中PROCESSING状态
					if (status.equals("OK")) { 
						// 如果验证流程走完了，则需要保存用户实名认证信息
						// 获取verify_result ，判断error_message
						// 1、判断error_message是否为空
						JSONObject verify_result = dataJson.getJSONObject("verify_result"); 
						if (verify_result != null) {
							String error_message = verify_result.getString("error_message"); 
							// 如果error_message为空则说明没有任何错误，
							if (StringUtils.isEmpty(error_message)) {
								// 获取result_faceid
								JSONObject result_faceid = verify_result.getJSONObject("result_faceid"); 
								if (result_faceid != null) {
									String confidenceStr = result_faceid.getString("confidence"); 
									Float confidence = Float.valueOf(confidenceStr);
									// 分数大于60
									if (confidence.compareTo(60f) > 0 || confidence.compareTo(60f) == 0) {
										JSONObject idcard_info = dataJson.getJSONObject("idcard_info"); 
										if (idcard_info != null) {
											// 查重
											JSONObject front_side = idcard_info.getJSONObject("front_side"); 
											if (front_side != null) {
												JSONObject ocr_result = front_side.getJSONObject("ocr_result"); 
												if (ocr_result != null) {
													String name = ocr_result.getString("name"); 
													String id_card_number = ocr_result.getString("id_card_number"); 
													int id = Integer.parseInt(biz_no);

													FUserIdentity identityParam = userIdentityService
															.selectByCode(id_card_number);
													if (identityParam != null && !identityParam.getFuid().equals(id)) {
														int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
														redisHelper.getIncrByKey(key,sec);

														resultObj.put("msg", I18NUtils.getString("UserApiV2Controller.51"));  //$NON-NLS-2$
														redisHelper.setRedisData(statusKey, resultObj, expireSeconds);
														userIdentityService.updateNormalIdentity(identity);
														//送短信
														validateHelper.smsSensitiveInfo(fuser.getFareacode(), fuser.getFtelephone(), langCode, 
																PlatformEnum.BC.getCode(), businessType);
														return;
													}

													FUser param = new FUser();
													param.setFidentityno(id_card_number);
													List<FUser> users = userService.selectUserListByParam(param);
													if (users != null && users.size() > 0 && !users.get(0).getFid().equals(id)) {
														int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
														redisHelper.getIncrByKey(key,sec);

														resultObj.put("msg", I18NUtils.getString("UserApiV2Controller.53"));  //$NON-NLS-2$
														redisHelper.setRedisData(statusKey, resultObj, expireSeconds);
														userIdentityService.updateNormalIdentity(identity);
														//送短信
														validateHelper.smsSensitiveInfo(fuser.getFareacode(), fuser.getFtelephone(), langCode, 
																PlatformEnum.BC.getCode(), businessType);
														return;
													}

													identity.setFstatus(IdentityStatusEnum.PASS.getCode());
													identity.setFname(name);
													identity.setFcode(id_card_number);
													identity.setBizId(biz_id);
													
													FUser user = userService.selectUserById(id);
													user.setFhasrealvalidate(true);
													user.setFidentityno(id_card_number);
													user.setFidentitytype(0);
													user.setFrealname(name);
													user.setFhasrealvalidatetime(Utils.getTimestamp());
													userService.updateByPrimaryKey(user);
													updateUserInfo(user);
													
													resultObj.put("code", 200); 
													resultObj.put("msg", I18NUtils.getString("UserApiV2Controller.1"));  //$NON-NLS-2$
													businessType = BusinessTypeEnum.SMS_NOTIFY_SUCC.getCode();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (identity.getFstatus() == IdentityStatusEnum.NOTPASS.getCode()) {
				int sec = DateUtils.getOffSeconds_abs(new Date(),DateUtils.getCurrentDay());
				redisHelper.getIncrByKey(key,sec);
			}
			userIdentityService.updateNormalIdentity(identity);
			redisHelper.setRedisData(statusKey, resultObj, expireSeconds);
			//送短信
			validateHelper.smsSensitiveInfo(fuser.getFareacode(), fuser.getFtelephone(), langCode, 
					PlatformEnum.BC.getCode(), businessType);
	}
	
	/**
	 * 新版登录
	 */
	@Deprecated
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v1/login",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult login_v1(
			@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = true) String password,
			@RequestParam(required = false) Integer appPlatform,
			@RequestParam(required = false) String deviceToken,
			@RequestParam(required = false) String imgCode
	){
		// 获取IP地址
		String ip = getIpAddr();

		if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)){
			return ReturnResult.FAILUER(I18NUtils.getString("user.login.error.1000")); 
		}
		
		DeviceInfo deviceInfo = getDeviceInfo();
		
		// 登录参数
		RequestUserInfo requestUserInfo = new RequestUserInfo();
		requestUserInfo.setFloginname(loginName);
		requestUserInfo.setType(type);
		requestUserInfo.setFagentid(WebConstant.BCAgentId);
		requestUserInfo.setPlatform(PlatformEnum.BC);
		requestUserInfo.setDeviceInfo(deviceInfo);
		// 登录
			requestUserInfo.setFloginpassword(password);
			Result result = userService.updateCheckLogin(requestUserInfo, UserLoginType.getUserLoginTypeByCode(deviceInfo.getPlatform()), ip, super.getLanEnum());
			if(result.getCode() == 200){
				LoginResponse login = (LoginResponse) result.getData();
				// 设置登录成功的Token
				sessionContextUtils.addContextToken("token",login.getToken()); 
				//返回对象去除谷歌
				login.getUserinfo().setFgoogleauthenticator(""); 
				login.getUserinfo().setFgoogleurl(""); 
				
				if(StringUtils.isNotEmpty(imgCode)) {
					String session_code = memCache.get(Constant.GEETEST_SUCC+imgCode);
					if ((session_code == null) || (!imgCode.equalsIgnoreCase(session_code))) {
						throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
					}
				}else {
					return ReturnResult.FAILUER(401,I18NUtils.getString("UserApiV2Controller.62")); 
				}
				
				//记录app用户的device token
				if (appPlatform != null && deviceToken != null) {
					UserInfoExtend userInfoExtend = new UserInfoExtend();
					userInfoExtend.setUserId(login.getUserinfo().getFid());
					userInfoExtend.setPlatform(appPlatform);
					userInfoExtend.setDeviceToken(deviceToken);
					userService.saveUserInfoExtend(userInfoExtend);
				}
				
				return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login);
			} else if(result.getCode() > 200 && result.getCode() < 1000){
				return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
			} else if(result.getCode() >= 1000 && result.getCode() < 10000){
				return ReturnResult.FAILUER(I18NUtils.getString("user.login.error." + result.getCode(), result.getData())); 
			} else{
				return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			}
	}
	
	/**
	 * 新版登录 非对称加密RSA
	 * @throws Exception 
	 */
	@PassToken
	@RequestMapping(value="/v1/getRegisterStatus",method = {RequestMethod.GET,RequestMethod.POST})
	public RespData<JSONObject> getRegisterStatus(
			@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = false) String imgCode) {
		// 极验
		if(geetestProperties.isVerify()) {
			if(StringUtils.isNotEmpty(imgCode)) {
				String session_code = memCache.get(Constant.GEETEST_SUCC+imgCode);
				if ((session_code == null) || (!imgCode.equalsIgnoreCase(session_code))) {
					throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
				}
			} else {
				throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
			}
		}
		// 登录参数
		FUser user = new FUser();
		if (type == 0) {
            user.setFtelephone(loginName);
        } else {
            user.setFemail(loginName);
        }
		int status = userService.getRegisterStatus(user);
		JSONObject result = new JSONObject();
		result.put("isRegister", status);
		return RespData.ok(result);
	}
	
 	/**
	 * 新版登录 非对称加密RSA
	 * @throws Exception 
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/login",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult login_v2(
			@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = true) String password,
			@RequestParam(required = false) Integer appPlatform,
			@RequestParam(required = false) String deviceToken,
			@RequestParam(required = false) String imgCode,
			@RequestParam(required = false) String code,
			@RequestParam(required = false, defaultValue = "1") Integer loginType
	) throws Exception {
		// 极验
		if(geetestProperties.isVerify() && loginType == 1 && StringUtils.isEmpty(code)) {
			if(StringUtils.isNotEmpty(imgCode)) {
				String session_code = memCache.get(Constant.GEETEST_SUCC+imgCode);
				if ((session_code == null) || (!imgCode.equalsIgnoreCase(session_code))) {
					throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
				}
			} else {
				return ReturnResult.FAILUER(401,I18NUtils.getString("UserApiV2Controller.73")); 
			}
		}
		
		// 获取IP地址
		String ip = getIpAddr();

		if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)){
			return ReturnResult.FAILUER(I18NUtils.getString("user.login.error.1000")); 
		}
		
		DeviceInfo deviceInfo = getDeviceInfo();
		
		// 登录参数
		RequestUserInfo requestUserInfo = new RequestUserInfo();
		requestUserInfo.setFloginname(loginName);
		requestUserInfo.setType(type);
		requestUserInfo.setFagentid(WebConstant.BCAgentId);
		requestUserInfo.setPlatform(PlatformEnum.BC);
		requestUserInfo.setDeviceInfo(deviceInfo);
		requestUserInfo.setCode(code);
		requestUserInfo.setLoginType(loginType);
		// 登录
		//RSA获取密码
		byte[] decryptByPrivateKey = RSAUtils.decryptByPrivateKey(java.util.Base64.getDecoder().decode(password), rsaProperties.getPriKey());
		String passwordStr = new String(decryptByPrivateKey);
		String passwordMd5 = Utils.MD5(passwordStr);
		requestUserInfo.setFloginpassword(passwordMd5);

		Result result = userService.updateCheckLogin(requestUserInfo, UserLoginType.getUserLoginTypeByCode(deviceInfo.getPlatform()), ip, super.getLanEnum());
		if(result.getCode() == 200){
			LoginResponse login = (LoginResponse) result.getData();
			// 设置登录成功的Token
			sessionContextUtils.addContextToken("token",login.getToken()); 
			//数据脱敏
			SecurityUtil.fuzzyUserData(login.getUserinfo());
			
			//记录app用户的device token
			if (appPlatform != null && deviceToken != null) {
				UserInfoExtend userInfoExtend = new UserInfoExtend();
				userInfoExtend.setUserId(login.getUserinfo().getFid());
				userInfoExtend.setPlatform(appPlatform);
				userInfoExtend.setDeviceToken(deviceToken);
				userService.saveUserInfoExtend(userInfoExtend);
			}
			//logger.info("v2-login login:{}", JSON.toJSONString(login));
			return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login);
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			if (result.getCode().equals(ReturnResult.FAILURE_SECOND_VERIFY_CODE)) {
				String[] split = result.getMsg().split("_");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("area", split[0]);
				jsonObject.put("phone", split[1]); 
				return ReturnResult.FAILUER(ReturnResult.FAILURE_SECOND_VERIFY_CODE, "", jsonObject);
			}
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("user.login.error." + result.getCode(), result.getData())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}
	
	/**
	 * 活动登陆
	 * @throws Exception 
	 */
	@PassToken
	@RequestMapping(value="/v1/activityLogin",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult activityLogin(
			@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = false) Integer appPlatform,
			@RequestParam(required = false) String deviceToken,
			@RequestParam(required = false) String code
	) throws Exception {

		// 获取IP地址
		String ip = getIpAddr();

		if(StringUtils.isEmpty(loginName)){
			return ReturnResult.FAILUER(I18NUtils.getString("user.login.error.1000")); 
		}
		
		DeviceInfo deviceInfo = getDeviceInfo();
		
		// 登录参数
		RequestUserInfo requestUserInfo = new RequestUserInfo();
		requestUserInfo.setFloginname(loginName);
		requestUserInfo.setType(type);
		requestUserInfo.setFagentid(WebConstant.BCAgentId);
		requestUserInfo.setPlatform(PlatformEnum.BC);
		requestUserInfo.setDeviceInfo(deviceInfo);
		requestUserInfo.setCode(code);
		// 登录
		Result result = userService.updateCheckActivityLogin(requestUserInfo, UserLoginType.getUserLoginTypeByCode(deviceInfo.getPlatform()), ip, super.getLanEnum());
		if(result.getCode() == 200){
			LoginResponse login = (LoginResponse) result.getData();
			// 设置登录成功的Token
			sessionContextUtils.addContextToken("token",login.getToken()); 
			//数据脱敏
			SecurityUtil.fuzzyUserData(login.getUserinfo());
			
			//记录app用户的device token
			if (appPlatform != null && deviceToken != null) {
				UserInfoExtend userInfoExtend = new UserInfoExtend();
				userInfoExtend.setUserId(login.getUserinfo().getFid());
				userInfoExtend.setPlatform(appPlatform);
				userInfoExtend.setDeviceToken(deviceToken);
				userService.saveUserInfoExtend(userInfoExtend);
			}
			return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login);
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("user.login.error." + result.getCode(), result.getData())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	
	}
	
	/**
	 * 初始化极验
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v1/initGeetest",method = {RequestMethod.GET,RequestMethod.POST})
	public String initGeetest(HttpServletRequest request,
			HttpServletResponse response) {

		GeetestLib gtSdk = new GeetestLib(geetestProperties.getGeetestId(), geetestProperties.getGeetestKey(), 
				true);

		String resStr = "{}"; 
		
		//自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>();

		//进行验证预处理
		gtSdk.preProcess(param);
		
		resStr = gtSdk.getResponseStr();
		return resStr;

	}
	
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v1/verifyGeetest",method = {RequestMethod.GET,RequestMethod.POST})
	public String verifyGeetest(HttpServletRequest request,
			HttpServletResponse response, @RequestBody String body){

		GeetestLib gtSdk = new GeetestLib(geetestProperties.getGeetestId(), geetestProperties.getGeetestKey(), 
				true);
		
		String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
		String validate = request.getParameter(GeetestLib.fn_geetest_validate);
		String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
		if (challenge == null) {
			JSONObject jsonObject = JSONObject.parseObject(body);
			challenge = jsonObject.getString(GeetestLib.fn_geetest_challenge);
			validate = jsonObject.getString(GeetestLib.fn_geetest_validate);
			seccode = jsonObject.getString(GeetestLib.fn_geetest_seccode);
		}
		
		//自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>(); 
		
		int gtResult = 0;
		gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);

		JSONObject data = new JSONObject();
		if (gtResult == 1) {
			// 验证成功
			data.put("status", "success");  //$NON-NLS-2$
			memCache.set(Constant.GEETEST_SUCC+challenge, challenge, 60);
		}
		else {
			// 验证失败
			data.put("status", "fail");  //$NON-NLS-2$
		}
		data.put("version", gtSdk.getVersionInfo()); 
		return data.toString();
	}
	
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/logout",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult logout(){
        // 删除登陆中的用户
        super.deleteUserInfo();
        super.deleteApiUserInfo();
        return ReturnResult.SUCCESS();
    }
	
	/**
	 * 注册
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v1/register",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult regIndex_v1(
			@RequestParam(required = false, defaultValue = "0") String password,
			@RequestParam(required = false, defaultValue = "0") String regName,
			@RequestParam(required = false, defaultValue = "0") Integer regType,
			@RequestParam(required = false, defaultValue = "0") String pcode,
			@RequestParam(required = false, defaultValue = "0") String ecode,
			@RequestParam(required = false, defaultValue = "86") String areaCode,
			@RequestParam(required = false, defaultValue = "") String intro_user,
			@RequestParam(required = false, defaultValue = "") String nickname
			)  {
		// 获取IP
		String ip = getIpAddr();
		
		//获取设备信息
		DeviceInfo deviceInfo = getDeviceInfo();
		
		// 区号
		areaCode = areaCode.replace("+", "");  //$NON-NLS-2$
		// 检测密码强度
		if (!password.matches(Constant.passwordReg)) {
			return ReturnResult.FAILUER(-10, I18NUtils.getString("com.validate.error.11009")); 
		}
		// 检测开放注册
		String isOpenReg = redisHelper.getSystemArgs("isOpenReg").trim(); 
		if (!isOpenReg.equals("1")) { 
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11014")); 
		}

		RequestUserInfo userInfo = new RequestUserInfo();
		// 推广推荐ID
		if(StringUtils.isEmpty(intro_user)) {
			if(redisHelper.getSystemArgs(ArgsConstant.ISMUSTINTROL).equals("1")) 
				return ReturnResult.FAILUER(I18NUtils.getString("user.reg.error.1006")); 
		}else{
			userInfo.setIntrocode(intro_user);
		}
		userInfo.setFareacode(areaCode);
		if(regType == 0){
			userInfo.setCode(pcode);
		}else{
			userInfo.setCode(ecode);
		}
		userInfo.setFloginname(regName);
		userInfo.setType(regType);
		userInfo.setFagentid(WebConstant.BCAgentId);
		userInfo.setFloginpassword(Utils.MD5(password));
		userInfo.setPlatform(PlatformEnum.BC);
		userInfo.setIp(ip);
		userInfo.setNickname(nickname);

		Result result = this.userService.insertRegister(userInfo, UserLoginType.getUserLoginTypeByCode(deviceInfo.getPlatform()));
		if(result.getCode() == 200){
			LoginResponse login = (LoginResponse) result.getData();
			// 设置登录成功的Token
            sessionContextUtils.addContextToken("token", login.getToken()); 
			return ReturnResult.SUCCESS();
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("user.reg.error." + result.getCode())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}

	/**
	 * 活动注册
	 */
	@PassToken
	@RequestMapping(value="/v1/activityRegister",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult activityRegister(
			@RequestParam(required = false, defaultValue = "0") String password,
			@RequestParam(required = false, defaultValue = "0") String regName,
			@RequestParam(required = false, defaultValue = "0") Integer regType,
			@RequestParam(required = false, defaultValue = "0") String pcode,
			@RequestParam(required = false, defaultValue = "0") String ecode,
			@RequestParam(required = false, defaultValue = "86") String areaCode
			)  {
		// 获取IP
		String ip = getIpAddr();
		
		//获取设备信息
		DeviceInfo deviceInfo = getDeviceInfo();
		
		// 区号
		areaCode = areaCode.replace("+", "");  //$NON-NLS-2$
		// 检测密码强度
		if (!password.matches(Constant.passwordReg)) {
			return ReturnResult.FAILUER(-10, I18NUtils.getString("com.validate.error.11009")); 
		}
		// 检测开放注册
		String isOpenReg = redisHelper.getSystemArgs("isOpenReg").trim(); 
		if (!isOpenReg.equals("1")) { 
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11014")); 
		}

		RequestUserInfo userInfo = new RequestUserInfo();
		userInfo.setFareacode(areaCode);
		if(regType == 0){
			userInfo.setCode(pcode);
		}else{
			userInfo.setCode(ecode);
		}
		userInfo.setFloginname(regName);
		userInfo.setType(regType);
		userInfo.setFagentid(WebConstant.BCAgentId);
		userInfo.setFloginpassword(Utils.MD5(password));
		userInfo.setPlatform(PlatformEnum.BC);
		userInfo.setIp(ip);
		userInfo.setNickname(regName);

		Result result = this.userService.insertActivityRegister(userInfo, UserLoginType.getUserLoginTypeByCode(deviceInfo.getPlatform()));
		if(result.getCode() == 200){
			LoginResponse login = (LoginResponse) result.getData();
			// 设置登录成功的Token
            sessionContextUtils.addContextToken("token", login.getToken()); 
            //数据脱敏
			SecurityUtil.fuzzyUserData(login.getUserinfo());
			
			return ReturnResult.SUCCESS(ReturnResult.SUCCESS, login);
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("user.reg.error." + result.getCode())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}
	
	/**
	 * 获取用户安全信息
	 */
	@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/getSecuritySetting",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult getSecuritySetting(String loginName){
		FUser user = getUser();
		
		if (user == null) {
			if(StringUtils.isNotEmpty(loginName)) {
				//当用户等于空的时候则使用loginName查询用户，查询为空的话则报错
				FUser param = new FUser();
				param.setFloginname(loginName);
				user = userService.selectUserByParam(param);
				if(user == null) {
					return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
				}
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
        }
		
		FUser fuser = userService.selectUserById(user.getFid());
		updateUserInfo(fuser);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("isOpenEmailValidate", fuser.isOpenEmailValidate()); 
		jsonObject.put("isOpenGoogleValidate", fuser.isOpenGoogleValidate()); 
		jsonObject.put("isOpenPhoneValidate", fuser.isOpenPhoneValidate()); 
		jsonObject.put("email", fuser.getFemail()); 
		if (StringUtils.isNotEmpty(loginName)) {
			jsonObject.put("phone", fuser.getFtelephone()); 
		} else {
			jsonObject.put("phone", SecurityUtil.getStr(fuser.getFtelephone())); 
		}
		return ReturnResult.SUCCESS(jsonObject);
	}
	
	/**
	 * 获取用户安全设置信息
	 */
	@ApiOperation("")
	@RequestMapping(value="/v2/userSecurity",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult userSecurity(){
			FUser userInfo = getCurrentUserInfoByToken();
			if(userInfo != null) {
				FUser fuser = userService.selectUserById(userInfo.getFid());
				
				 FUserFavoriteTrade fUserFavoriteTrade = userFavoriteService.selectByUid(fuser.getFid());
	             if(null !=fUserFavoriteTrade) {
	            	 fuser.setfFavoriteTradeList(fUserFavoriteTrade.getFfavoritetradelist());
	             }
				//设置收藏列表
	            JSONArray array = new JSONArray();
	            if(StringUtils.isEmpty(fuser.getfFavoriteTradeList())){
	            	fuser.setTradeList(array);
	            }else {
	                array = JSONArray.parseArray(fuser.getfFavoriteTradeList());
	                fuser.setTradeList(array);
	            }
				updateUserInfo(fuser);

				FUserIdentity identity = userIdentityService.selectByUser(fuser.getFid());
				String device_name = Constant.GoogleAuthName + "--" + fuser.getFloginname(); 

				boolean isBindGoogle = fuser.getFgooglebind() == null ? false : fuser.getFgooglebind();
				boolean isBindTelephone = fuser.getFistelephonebind() == null ? false : fuser.getFistelephonebind();
				boolean isEmail = fuser.getFismailbind() == null ? false : fuser.getFismailbind();
				boolean isTradePass = fuser.getFtradepassword() != null && !fuser.getFtradepassword().equals(""); 
				boolean isLoginPass = fuser.getFloginpassword() != null && !fuser.getFloginpassword().equals(""); 
				boolean isIdentity = identity != null && identity.getFstatus() == 1;

				int bindcount = 0;
				if (isBindGoogle) {
					bindcount++;
				}
				if (isBindTelephone) {
					bindcount++;
				}
				if (isEmail) {
					bindcount++;
				}
				if (isIdentity){
					bindcount++;
				}

				JSONObject jsonObject=new JSONObject();
				jsonObject.put("loginName", SecurityUtil.getLoginname(fuser.getFloginname())); 
				jsonObject.put("device_name", ""); 
				jsonObject.put("securityLevel", bindcount); 
				jsonObject.put("isBindGoogle", isBindGoogle); 
				jsonObject.put("isBindTelephone", isBindTelephone); 
				jsonObject.put("isBindEmail", isEmail); 
				jsonObject.put("isTradePass", isTradePass); 
				jsonObject.put("isLoginPass", isLoginPass); 
				jsonObject.put("isIdentity", isIdentity); 
				if(identity != null) {
					jsonObject.put("identityStatus", identity.getFstatus()); 
				}else {
					//新用户identity为空的状态，
					jsonObject.put("identityStatus", 3); 
				}
				jsonObject.put("phone", SecurityUtil.getStr(fuser.getFtelephone())); 
				jsonObject.put("email", fuser.getFemail()); 
				jsonObject.put("isOpenEmailValidate", fuser.isOpenEmailValidate()); 
				jsonObject.put("isOpenGoogleValidate", fuser.isOpenGoogleValidate()); 
				jsonObject.put("isOpenPhoneValidate", fuser.isOpenPhoneValidate()); 
				FUserExtend userExtend = userExtendService.selectByUid(userInfo.getFid());
				Integer secondaryVerification = 0;
				if (userExtend != null) {
					secondaryVerification = userExtend.getSecondaryVerification();
				}
				jsonObject.put("secondaryVerification", secondaryVerification); 
				jsonObject.put("antiPhishingCode", Optional.ofNullable(userExtend).map(e->e.getAntiPhishingCode()).orElse(null)); 
				return ReturnResult.SUCCESS(jsonObject);
			}else {
				return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.126")); 
			}
	}
	
	/**
	 * 设置用户安全信息
	 */
	@ApiOperation("")
	@RequestMapping(value="/v2/setSecurity",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult setSecurity(
			@RequestParam(required = true) Boolean isOpenEmailValidate,
			@RequestParam(required = true) Boolean isOpenGoogleValidate,
			@RequestParam(required = true) Boolean isOpenPhoneValidate,
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode
			) {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			if(!isOpenEmailValidate && !isOpenGoogleValidate && !isOpenPhoneValidate ) {
				return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1023")); 
			}
			FUser fuser = userService.selectUserById(user.getFid());
			
			if(isOpenEmailValidate) {
				if(!fuser.getFismailbind() || StringUtils.isEmpty(fuser.getFemail())) {
					return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1020")); 
				}
			}
			if(isOpenGoogleValidate) {
				if(!fuser.getFgooglebind()) {
					return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1021")); 
				}
			}
			if(isOpenPhoneValidate) {
				if(!fuser.getFistelephonebind() ||  StringUtils.isEmpty(fuser.getFtelephone())) {
					return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1022")); 
				}
			}
			LogUserActionEnum action = LogUserActionEnum.MODIFY_SECURITY_SETTINGS;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailCode(emailCode);
			paramInfo.setBusinessType(BusinessTypeEnum.SMS_MODIFY_SAFETY);
			paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
	
			fuser.setFupdatetime(Utils.getTimestamp());
			fuser.setIp(getIpAddr());
			fuser.setOpenEmailValidate(isOpenEmailValidate);
			fuser.setOpenGoogleValidate(isOpenGoogleValidate);
			fuser.setOpenPhoneValidate(isOpenPhoneValidate);
			
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				updateUserInfo(fuser);
				return ReturnResult.SUCCESS(I18NUtils.getString("com.public.succeed.10000")); 
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
		
	}
	
	/**
	 * 修改谷歌验证
	 * 第一步，获取新的谷歌验证码
	 */
	@ApiOperation("")
	@RequestMapping(value="/v2/get_google",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult getGoogle() {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			LogUserActionEnum action = LogUserActionEnum.GET_GOOGLE;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			Result result = this.userSecurityService.updateUserSecurityInfo(user, paramInfo, action, null);
			if(result.getSuccess()) {
				Map<String, String> map = (Map<String, String>) result.getData();
				String wechatGoogleUrl = ""; 
				String systemArgs = redisHelper.getSystemArgs(ArgsConstant.WECHAR_GOOGLE_URL);
				if(!StringUtils.isEmpty(systemArgs)) {
					wechatGoogleUrl = systemArgs.replace("#googleUrl#", map.get("url"));  //$NON-NLS-2$
				}
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("code", 0); 
				jsonObject.put("qecode", map.get("url"));  //$NON-NLS-2$
				jsonObject.put("totpKey", map.get("secret"));  //$NON-NLS-2$
				//塞redis
				redisHelper.set(RedisConstant.UPDATE_GOOGLE+user.getFid(), jsonObject.toJSONString(), 5*60);
				
				jsonObject.put("wechatGoogleUrl", wechatGoogleUrl); 
				return ReturnResult.SUCCESS(jsonObject);
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.validate.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
	}
	
	/**
	 * 修改谷歌验证
	 * 第二步，验证新的验证码是否有误
	 */
	@ApiOperation("")
	@RequestMapping(value="/v2/validate_google",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult validateGoogle(			
			@RequestParam(required = false, defaultValue = "0") String googleCode
			) {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			String string = redisHelper.get(RedisConstant.UPDATE_GOOGLE+user.getFid());
			if(StringUtils.isEmpty(string)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			JSONObject parseObject = JSON.parseObject(string);
			
			LogUserActionEnum action = LogUserActionEnum.VALIDATE_GOOGLE;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setTotpKey(parseObject.getString("totpKey")); 
			Result result = this.userSecurityService.updateUserSecurityInfo(user, paramInfo, action, null);
			if(result.getSuccess()) {
				//如果确认修改无误则刷到缓存
				parseObject.put("code", 1); 
				redisHelper.set(RedisConstant.UPDATE_GOOGLE+user.getFid(), parseObject.toJSONString(), 5*60);
				return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.validate.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
		
	}
	
	
	
	/**
	 * 修改谷歌验证
	 * 第三步，绑定新的谷歌验证
	 */
		@ApiOperation("")
	@RequestMapping(value="/v2/update_google",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateGoogle(
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode
			) throws Exception {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			if(user.isOpenEmailValidate() && StringUtils.isEmpty(emailCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10122")); 
			}
			if(user.isOpenGoogleValidate() && StringUtils.isEmpty(googleCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10115")); 
			}
			if(user.isOpenPhoneValidate() && StringUtils.isEmpty(phoneCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10114")); 
			}
			
			String string = redisHelper.get(RedisConstant.UPDATE_GOOGLE+user.getFid());
			if(StringUtils.isEmpty(string)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			JSONObject parseObject = JSON.parseObject(string);
			
			//如果没有操作到第二步，则返回错误
			if(parseObject.getInteger("code") != 1) { 
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}

			LogUserActionEnum action = LogUserActionEnum.BIND_NEW_GOOGLE;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailCode(emailCode);
			paramInfo.setBusinessType(BusinessTypeEnum.SMS_MODIFY_GOOGLE);
			paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
			
			user.setFgoogleauthenticator(parseObject.getString("totpKey")); 
			user.setFgoogleurl(parseObject.getString("qecode")); 
			
			Result result = this.userSecurityService.updateUserSecurityInfo(user, paramInfo, action, null);
			if(result.getSuccess()) {
				redisHelper.remove(RedisConstant.UPDATE_GOOGLE+user.getFid());
				updateUserInfo(user);
				return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
		
	}
	
	
	/**
	 * 修改手机，第一步，验证新手机验证码
	 */
		@ApiOperation("")
	@RequestMapping(value="/v2/validate_phone",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult validatePhone(
			@RequestParam(required = true) String phone,
			@RequestParam(required = true) String area,
			@RequestParam(required = true) String phoneCode
			) {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			if(StringUtils.isEmpty(phoneCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10114")); 
			}
			if(PhoneValiUtil.checkPhoneNumber(phone, area));
			LogUserActionEnum action = LogUserActionEnum.VALIDATE_PHONE;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
			paramInfo.setPhone(phone);
			paramInfo.setAreaCode(area);
			
			user.setFtelephone(phone);
			user.setFareacode(area);
			Result result = this.userSecurityService.updateUserSecurityInfo(user, paramInfo, action, null);
			if(result.getSuccess()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("phone", phone); 
				jsonObject.put("area", area); 
				redisHelper.set(RedisConstant.UPDATE_PHONE+user.getFid(), jsonObject.toJSONString(), 5*60);
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.validate.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
	}
	
	/**
	 * 修改手机，第二步，绑定新手机
	 */
		@ApiOperation("")
	@RequestMapping(value="/v2/update_phone",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updatePhone(
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode
			) {
			FUser user = getUser();
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			if(user.isOpenEmailValidate() && StringUtils.isEmpty(emailCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10122")); 
			}
			if(user.isOpenGoogleValidate() && StringUtils.isEmpty(googleCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10115")); 
			}
			if(user.isOpenPhoneValidate() && StringUtils.isEmpty(phoneCode)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.error.10114")); 
			}
			
			String string = redisHelper.get(RedisConstant.UPDATE_PHONE+user.getFid());
			if(StringUtils.isEmpty(string)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10001")); 
			}
			
			JSONObject parseObject = JSON.parseObject(string);

			LogUserActionEnum action = LogUserActionEnum.BIND_NEW_PHONE;
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailCode(emailCode);
			paramInfo.setBusinessType(BusinessTypeEnum.SMS_SEND_CODE);
			paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
			
			user.setFtelephone(parseObject.getString("phone")); 
			user.setFareacode(parseObject.getString("area")); 
			
			Result result = this.userSecurityService.updateUserSecurityInfo(user, paramInfo, action, null);
			if(result.getSuccess()) {
				redisHelper.remove(RedisConstant.UPDATE_PHONE+user.getFid());
				updateUserInfo(user);
				return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.validate.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
		
	}
	
	/**
	 * 修改登录和交易密码
	 */
		@ApiOperation("")
	@RequestMapping(value="/v2/modifyPwd",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult modifyPwd(
			@RequestParam(required = true) String newPwd,
			@RequestParam(required = true) String originPwd,
			@RequestParam(required = true) String reNewPwd,
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") Integer pwdType,
			@RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode)throws Exception{

		FUser fuser = getCurrentUserInfoByToken();
		
		if (!newPwd.equals(reNewPwd)) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11010")); 
		}
		// 检测密码强度
		if(pwdType == 0) {
			if (!newPwd.matches(Constant.passwordReg)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
		}else {
			if (newPwd.length() < 6) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
		}
		
		LogUserActionEnum action = LogUserActionEnum.MODIFY_LOGINPWD;
		BusinessTypeEnum msgType = BusinessTypeEnum.SMS_MODIFY_LOGIN_PASSWORD;
		//是否需要发送短信提醒
		if (pwdType == 0) {
			// 修改登录密码
			fuser.setFloginpassword(Utils.MD5(newPwd));
		} else {
			// 修改交易密码
			action = LogUserActionEnum.BIND_TRADEPWD;
			
			//如果交易密码不为空则是修改交易密码
			if (fuser.getFtradepassword() != null && fuser.getFtradepassword().trim().length() > 0) {
				action = LogUserActionEnum.MODIFY_TRADEPWD;
				fuser.setFtradepwdtime(Utils.getTimestamp());
			}
			fuser.setFtradepassword(Utils.MD5(newPwd));
			//发送短信的类型
			msgType = BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD;
		}

		String ip = HttpRequestUtils.getIPAddress();

		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setCode(phoneCode);
		paramInfo.setTotpCode(googleCode);
		paramInfo.setIp(ip);
		paramInfo.setPlatform(PlatformEnum.BC);
		paramInfo.setBusinessType(msgType);
		paramInfo.setLocale(getLanEnum());
		paramInfo.setOriginLoginPwd(Utils.MD5(originPwd));
		paramInfo.setEmailCode(emailCode);
		paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);

		fuser.setFupdatetime(Utils.getTimestamp());
		fuser.setIp(ip);
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				if(action == LogUserActionEnum.MODIFY_LOGINPWD){
					deleteUserInfo();
				} else {
					updateUserInfo(fuser);
				}
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
	}
	
	/**
	 * 修改登录和交易密码,APP端需要签名
	 */
		@APISignPermission
	@ApiOperation("")
	@RequestMapping(value="/v2/modifyPwdSign",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult modifyPwdSign(
			@RequestParam(required = true) String newPwd,
			@RequestParam(required = true) String originPwd,
			@RequestParam(required = true) String reNewPwd,
			@RequestParam(required = false, defaultValue = "0") String phoneCode,
			@RequestParam(required = false, defaultValue = "0") Integer pwdType,
			@RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode)throws Exception{

		FUser fuser = getCurrentUserInfoByToken();
		
		if (!newPwd.equals(reNewPwd)) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11010")); 
		}
		// 检测密码强度
		if(pwdType == 0) {
			if (!newPwd.matches(Constant.passwordReg)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
		}else {
			if (newPwd.length() < 6) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
		}
		
		LogUserActionEnum action = LogUserActionEnum.MODIFY_LOGINPWD;
		BusinessTypeEnum msgType = BusinessTypeEnum.SMS_MODIFY_LOGIN_PASSWORD;
		//是否需要发送短信提醒
		if (pwdType == 0) {
			// 修改登录密码
			fuser.setFloginpassword(Utils.MD5(newPwd));
		} else {
			// 修改交易密码
			action = LogUserActionEnum.BIND_TRADEPWD;
			
			//如果交易密码不为空则是修改交易密码
			if (fuser.getFtradepassword() != null && fuser.getFtradepassword().trim().length() > 0) {
				action = LogUserActionEnum.MODIFY_TRADEPWD;
				fuser.setFtradepwdtime(Utils.getTimestamp());
			}
			fuser.setFtradepassword(Utils.MD5(newPwd));
			//发送短信的类型
			msgType = BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD;
		}

		String ip = HttpRequestUtils.getIPAddress();

		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setCode(phoneCode);
		paramInfo.setTotpCode(googleCode);
		paramInfo.setIp(ip);
		paramInfo.setPlatform(PlatformEnum.BC);
		paramInfo.setBusinessType(msgType);
		paramInfo.setLocale(getLanEnum());
		paramInfo.setOriginLoginPwd(Utils.MD5(originPwd));
		paramInfo.setEmailCode(emailCode);
		paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);

		fuser.setFupdatetime(Utils.getTimestamp());
		fuser.setIp(ip);
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				if(action == LogUserActionEnum.MODIFY_LOGINPWD){
					deleteUserInfo();
					deleteApiUserInfo();
				} else {
					updateUserInfo(fuser);
				}
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
	}
	
	/**
	 * 设置交易密码
	 */
		@ApiOperation("")
	@RequestMapping(value="/v2/setTradePwd",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult setTradePwd(
			@RequestParam(required = true) String newPwd,
			@RequestParam(required = true) String reNewPwd){

		FUser fuser = getCurrentUserInfoByToken();
		
		if (!newPwd.equals(reNewPwd)) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11010")); 
		}
		// 检测密码强度
		if (newPwd.length() < 6) {
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
		}
		
		LogUserActionEnum action = LogUserActionEnum.BIND_TRADEPWD;
		BusinessTypeEnum msgType = BusinessTypeEnum.SMS_MODIFY_TRADE_PASSWORD;
		//是否需要发送短信提醒
		//修改交易密码



			fuser.setFtradepassword(Utils.MD5(newPwd));
			//发送短信的类型
		
			String ip = HttpRequestUtils.getIPAddress();
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setIp(ip);
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setBusinessType(msgType);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
		
			fuser.setIp(ip);


			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				updateUserInfo(fuser);
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
	}
	
	/**
	 * 设置更改昵称
	 */
		@PassToken
	@ApiOperation("")
	@RequestMapping(value="/v2/update_nickname",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult updateNickName(
			@RequestParam(required = false) String nickname) {
			FUser user = getUser();
			
			if (user == null) {
	            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	        }
			
			FUser fuser = userService.selectUserById(user.getFid());
			if(fuser.getIsHavedModNickname()) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11021")); 
			}
			
			FUser param = new FUser();
			param.setFnickname(nickname);
			if(userService.selectIsExistByParam(param)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11022")); 
			}
			
			fuser.setFnickname(nickname);
			fuser.setIsHavedModNickname(true);
			fuser.setFupdatetime(new Date());
			Integer updateByPrimaryKey = userService.updateByPrimaryKey(fuser);
			if(updateByPrimaryKey != null  && updateByPrimaryKey != 0 ) {
				updateUserInfo(fuser);
				return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
			}
		return ReturnResult.FAILUER(I18NUtils.getString("common.error.400")); 
	}
	
	/**
     * 获取钱包数据
     *
     * @return
     */
        @ApiOperation("")
	@RequestMapping(value="/v2/balance",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult balance() {
        FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.222"));
        }

        Integer fid = user.getFid();
        JSONObject result = new JSONObject();
        	List<UserCoinWallet> userCoinWallets = userWalletService.listUserCoinWallet(fid);
            Iterator<UserCoinWallet> iterator = userCoinWallets.iterator();
            while (iterator.hasNext()) {
                UserCoinWallet wallet = iterator.next();
                SystemCoinType coinType = redisHelper.getCoinType(wallet.getCoinId());
                if (coinType == null) {
                    iterator.remove();
                    continue;
                }else{
        			wallet.setWithdraw(coinType.getIsWithdraw());
        			wallet.setRecharge(coinType.getIsRecharge());
					wallet.setFrozen(MathUtils.add(wallet.getFrozen(), wallet.getDepositFrozen()));
					wallet.setBorrowFrozen(MathUtils.add(wallet.getBorrow(), wallet.getFrozen()));
					//如果有从币
					if(!StringUtils.isEmpty(coinType.getLinkCoin())) {
						String[] split = coinType.getLinkCoin().split(",");
						List<Object> list = new ArrayList<>();
						for (String string : split) {
							Integer coinid = null;
							SystemCoinType coinTypeSub = null;
							try {
								coinid = Integer.valueOf(string);
								coinTypeSub = redisHelper.getCoinType(coinid);
								if(coinTypeSub == null || !coinTypeSub.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode()) || StringUtils.isEmpty(coinTypeSub.getLinkCoin()) || !Integer.valueOf(coinTypeSub.getLinkCoin()).equals(coinType.getId()) ) {
									continue;
								}
							} catch (Exception e) {
								logger.error("币种配置有误",e);
								continue;
							}
							JSONObject jo = new JSONObject();
							jo.put("coinId", coinTypeSub.getId());
							jo.put("coinName", coinTypeSub.getName());
							jo.put("shortName", coinTypeSub.getShortName());
							jo.put("isUseMemo", coinTypeSub.getIsUseMemo());
							jo.put("recharge", coinTypeSub.getIsRecharge());
							jo.put("withdraw", coinTypeSub.getIsWithdraw());
							jo.put("webLogo", coinTypeSub.getWebLogo());
							jo.put("frozen", wallet.getFrozen());
							jo.put("total", wallet.getTotal());
							jo.put("borrow", wallet.getBorrow());
							list.add(jo);
							wallet.setRecharge(wallet.getRecharge() || coinTypeSub.getIsRecharge());
							wallet.setWithdraw(wallet.getWithdraw() || coinTypeSub.getIsWithdraw());
						}
						wallet.setSubCoinList(list);
					}
				}
                
            }
            BigDecimal totalAssets = getTotalAssets(userCoinWallets);
            BigDecimal btcPrice = redisHelper.getLastPrice(8);
            BigDecimal btcAssets = MathUtils.div(totalAssets, btcPrice);
            
            result.put("netAssets", getNetAssets(userCoinWallets)); 
            result.put("totalAssets", totalAssets); 
            result.put("btcAssets", btcAssets); 
            result.put("userWalletList", userCoinWallets); 
            
        return ReturnResult.SUCCESS(result);
    }
    
    /**
     * 获取币种信息（用于兼容app及h5）
     *
     * @return
     */
        @ApiOperation("")
	@RequestMapping(value="/v2/balance_coin",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult balanceCoin() {
        FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.222"));
        }
        Integer fid = user.getFid();
        JSONObject result = new JSONObject();
        	List<UserCoinWallet> userCoinWallets = userWalletService.listUserCoinWallet(fid);
            List<SystemCoinType> coinTypeCoinList = redisHelper.getCoinTypeCoinList();
            if(userCoinWallets == null || userCoinWallets.size() == 0 || coinTypeCoinList == null || coinTypeCoinList.size() == 0) {
            	return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000"));
            }
            Map<Integer, UserCoinWallet> collect = userCoinWallets.stream().collect(Collectors.toMap(UserCoinWallet::getCoinId, UserCoinWallet -> UserCoinWallet));
            userCoinWallets = new ArrayList<>();
            for (SystemCoinType coin : coinTypeCoinList) {
            	try {
            		//如果不是子币种，而且存在子币种，就不显示
            		if(!coin.getIsSubCoin() && !StringUtils.isEmpty(coin.getLinkCoin())) {
            			continue;
            		}
            		
            		UserCoinWallet wallet = null;
            		if(coin.getIsSubCoin()) {
                		wallet = collect.get(Integer.valueOf(coin.getLinkCoin()));
                		//复制出一个新对象
                		wallet = PojoConvertUtil.convert(wallet,UserCoinWallet.class);
                	}else {
                		wallet = collect.get(coin.getId());
                	}
            		if(wallet == null) {
            			continue;
            		}
            		
            		wallet.setFrozen(MathUtils.add(wallet.getFrozen(), wallet.getDepositFrozen()));
            		wallet.setCoinId(coin.getId());
                	wallet.setCoinName(coin.getCoinName());
                	wallet.setShortName(coin.getShortName());
                	wallet.setIsUseMemo(coin.getIsUseMemo());
                	wallet.setRecharge(coin.getIsRecharge());
                	wallet.setWithdraw(coin.getIsWithdraw());
                	wallet.setWebLogo(coin.getWebLogo());
                	userCoinWallets.add(wallet);
				} catch (Exception e) {
					logger.error("币种配置错误",e);
				}
			}
            result.put("userWalletList", userCoinWallets); 
            
        return ReturnResult.SUCCESS(result);
    }
    
    
    
    /**
     * 找回密码第一步：验证账号以及图片验证码
     */
    	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/resetLoginPwdCheck",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult resetLoginPwdCheck(
    		@RequestParam(required = true, defaultValue = "0") String loginName) 
    {
        if(StringUtils.isEmpty(loginName)) {
            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.300")); 
        }

        FUser param = new FUser();
        param.setFloginname(loginName);
        FUser fuser = this.userService.selectUserByParam(param);
        // 没找到用户
        if (null == fuser) {
        	throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
        }

  
		
		String redisKey = setRedisData("resetToken", loginName); 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resetToken", redisKey); 
        return ReturnResult.SUCCESS(jsonObject);
    }
    
    /**
     * 找回密码第一步：验证账号以及图片验证码
     */
    	@PassToken
    @ApiOperation("")
	@RequestMapping(value="/v2/resetLoginPwd",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult resetLoginPwd(
    		@RequestParam(required = true, defaultValue = "0") String loginName,
    		@RequestParam(required = true, defaultValue = "0") String newPwd,
            @RequestParam(required = true, defaultValue = "0") String reNewPwd,
            @RequestParam(required = true, defaultValue = "0") String resetToken,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode) {
    	FUser param = new FUser();
			if(StringUtils.isNotEmpty(resetToken)) {
				String session_code = memCache.get(Constant.GEETEST_SUCC+resetToken);
				if ((session_code == null) || (!resetToken.equalsIgnoreCase(session_code))) {
					throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
				}
			}else {
				return ReturnResult.FAILUER(401,I18NUtils.getString("UserApiV2Controller.2")); 
			}
			
			param.setFloginname(loginName);
	    	FUser fuser = this.userService.selectUserByParam(param);
	    	
	    	// 没找到用户
	    	if (null == fuser) {
	    		throw new BizException(ErrorCodeEnum.UNKNOWN_ACCOUNT);
	    	}
	    	
	    	if (!newPwd.equals(reNewPwd)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11010")); 
			}
			// 检测密码强度
			if (!newPwd.matches(Constant.passwordReg)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
			
			LogUserActionEnum action = LogUserActionEnum.FIND_LOGINPASS;
			BusinessTypeEnum smsType = BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD;
			
			BusinessTypeEnum emailType = BusinessTypeEnum.EMAIL_SEND_CODE;
			//修改登录密码
			fuser.setFloginpassword(Utils.MD5(newPwd));
			

			String ip = HttpRequestUtils.getIPAddress();

			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(ip);
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setBusinessType(smsType);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailCode(emailCode);
			paramInfo.setEmailBusinessType(emailType);
			fuser.setFupdatetime(Utils.getTimestamp());
			fuser.setIp(ip);
			
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				deleteUserInfo();
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
    }
    
    
    /**
     * 找回资金密码第一步：验证账号以及图片验证码
     */
        @ApiOperation("")
	@RequestMapping(value="/v2/resetTradePwdCheck",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult resetTradePwdCheck(
    		@RequestParam(required = false, defaultValue = "0") Integer type,
			@RequestParam(required = true) String loginName,
			@RequestParam(required = true) String password) 
    {
        FUser fuser = getUser();
        // 没找到用户
        if (null == fuser) {
        	return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.240"));
        }
        loginName = fuser.getFloginname();
        
        FUser checkUser = new FUser();
        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)) {
            return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.241")); 
        } else {
            if (type == 0) {
            	checkUser.setFtelephone(loginName);
            } else {
            	checkUser.setFemail(loginName);
            }
            List<FUser> list = userService.selectUserListByParam(checkUser);
            if (list == null || list.size() <= 0) {
            	return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.242")); 
            }
        }
		
		String redisKey = setRedisData("resetToken", fuser.getFloginname()); 
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resetToken", redisKey); 
        return ReturnResult.SUCCESS(jsonObject);
    }
    
    /**
     * 找回资金密码第二步：重置密码
     */
        @ApiOperation("")
	@RequestMapping(value="/v2/resetTradePwd",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult resetTradePwd(
    		@RequestParam(required = true, defaultValue = "0") String newPwd,
            @RequestParam(required = true, defaultValue = "0") String reNewPwd,
            @RequestParam(required = true, defaultValue = "0") String resetToken,
            @RequestParam(required = false, defaultValue = "0") String phoneCode,
            @RequestParam(required = false, defaultValue = "0") String googleCode,
			@RequestParam(required = false, defaultValue = "0") String emailCode) 
    {
			FUser fuser = getUser();
	        // 没找到用户
	        if (null == fuser) {
	        	return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.245"));
	        }
	    	
	    	if (!newPwd.equals(reNewPwd)) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11010")); 
			}
			// 检测密码强度
			if (newPwd.length() < 6) {
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11009")); 
			}
			
			LogUserActionEnum action = LogUserActionEnum.FIND_TRADE_PWD;
			BusinessTypeEnum smsType = BusinessTypeEnum.SMS_FIND_TRADE_PASSWORD;
			
			BusinessTypeEnum emailType = BusinessTypeEnum.EMAIL_SEND_CODE;
			String ip = HttpRequestUtils.getIPAddress();

			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setCode(phoneCode);
			paramInfo.setTotpCode(googleCode);
			paramInfo.setIp(ip);
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setBusinessType(smsType);
			paramInfo.setLocale(getLanEnum());
			paramInfo.setEmailCode(emailCode);
			paramInfo.setEmailBusinessType(emailType);
			
			fuser.setIp(ip);
			fuser.setFtradepassword(Utils.MD5(newPwd));
			fuser.setFtradepwdtime(Utils.getTimestamp());
			fuser.setFupdatetime(Utils.getTimestamp());
			
			Result result = this.userSecurityService.updateUserSecurityInfo(fuser, paramInfo, action, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				updateUserInfo(fuser);
				return ReturnResult.SUCCESS();
			} else if(result.getCode() >= 10000){
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
			} else {
				return ReturnResult.FAILUER(result.getCode(),I18NUtils.getString("user.security.error." + result.getCode())); 
			}
    }
    
    /**
     * 返回用户信息
     */
        @ApiOperation("")
	@RequestMapping(value="/v2/getUserInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getUserInfo() 
    {
			FUser fuser = getUser();
	        // 没找到用户
	        if (null == fuser) {
	        	return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("UserApiV2Controller.251")); 
	        }
	        //数据脱敏
			SecurityUtil.fuzzyUserData(fuser);
			return ReturnResult.SUCCESS(fuser);
    }
    
    
    /**
     * 修改头像
     * @param 
     * @return map
     */
	@ApiOperation("")
	@RequestMapping(value = "/v2/editPhoto", method = RequestMethod.POST)
		public ReturnResult editPhoto(HttpServletRequest request,MultipartFile file) {
    	logger.info("/editPhoto:param:"); 
		logger.info("/editPhoto:queryString"+request.getQueryString()); 

        FUser user = getUser();
        
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
        
		if(file!=null){
			logger.debug("用户修改照片: " + request.getQueryString()); 
			String fileName = file.getOriginalFilename();
			logger.debug("fileName="+fileName); 
	        String fileType =file.getContentType();
	        logger.debug("fileType="+fileType); 
	        if (fileType == null || !fileType.startsWith("image")) {
	        	return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.273"));
			}

			if (fileName != null
					&& (fileName.endsWith(".jpg") || fileName.endsWith(".gif") || fileName  //$NON-NLS-2$
							.endsWith(".png")||fileName.endsWith(".jpeg"))) {  //$NON-NLS-2$
				try {
					String keyPrix = "hotcoin/photo"; 
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM"); 
					String forMatDate = formatter.format(new Date());
					String ossKey = keyPrix+"/"+forMatDate+"/";  //$NON-NLS-2$
					logger.info("oss 存储路径："+ossKey); 
			        
			        String result = ossHelper.uploadFile(file, ossKey);
			        FUser fuser = userService.selectUserById(user.getFid());
			        if (StringUtils.isNotEmpty(result)) {
			        	fuser.setPhoto(result);
						fuser.setFupdatetime(new Date());
						Integer updateByPrimaryKey = userService.updateByPrimaryKey(fuser);
						if(updateByPrimaryKey != null  && updateByPrimaryKey != 0 ) {
							updateUserInfo(fuser);
							return ReturnResult.SUCCESS(200,result);
						}
			        } else {
			        	return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.3")); 
			        }
				} catch (Exception e) {
                    logger.error(e.getMessage(), e);
					logger.error("请求路径：{},系统异常：{}",request.getRequestURL(),e.getMessage()); 
				}
			} else {
				return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.273")); 
			}
		}else{
	        // 返回信息status为1，表示成功
			return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.274")); 
		}
		return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.275")); 
	}

	/**
	 * 修改头像 base64
	 * @param request
	 * @param image
	 * @param imageType
	 * @return
	 */
	@ApiOperation("")
	@RequestMapping(value = "/v2/uploadUserPhoto", method = RequestMethod.POST)
		public ReturnResult uploadUserPhoto(HttpServletRequest request,String image,
										  String imageType) {
		FUser user = getUser();
		if (user == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
		}

            String header ="data:image"; 
            String[] imageArr=image.split(","); 
            if(imageArr[0].contains(header)) {//是img的
                // 去掉头部
                image=imageArr[1];
                // 修改图片
                try
                {
                    byte[] imageByte = Base64.decodeBase64(image); // 将字符串格式的image转为二进制流（biye[])的decodedBytes
                    String keyPrix = "hotcoin/photo"; 
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM"); 
                    String forMatDate = formatter.format(new Date());
                    String ossKey = keyPrix + "/" + forMatDate + "/";  //$NON-NLS-2$
                    logger.info("oss 存储路径：" + ossKey); 
                    String result = ossHelper.uploadValidateFile(imageByte, imageType + ".jpg", ossKey); 
                    FUser fuser = userService.selectUserById(user.getFid());
                    if (StringUtils.isNotEmpty(result)) {
                        fuser.setPhoto(result);
                        fuser.setFupdatetime(new Date());
                        Integer updateByPrimaryKey = userService.updateByPrimaryKey(fuser);
                        if (updateByPrimaryKey != null && updateByPrimaryKey != 0) {
                            updateUserInfo(fuser);
                            return ReturnResult.SUCCESS(200, result);
                        }
                    } else {
                        return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.285")); 
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                    logger.error("请求路径："+request.getRequestURL()+"系统异常："+e.getMessage());  //$NON-NLS-2$
                }

            }else{
                return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.288")); 
            }

		return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.289")); 
	}

	
	/**
     * 上传证件照
     * @param 
     * @return map
	 * @deprecated use other the “/uploadBase64” method of OSS_SERVER project  replace
     */
	@Deprecated
	@ApiOperation("")
	@RequestMapping(value = "/v2/uploadVerifyImage", method = RequestMethod.POST)
		public ReturnResult uploadVerifyImage(HttpServletRequest request,String image,
			String imageType) {
        FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
			//System.out.println("length"+image.length());
			//byte[] imageByte = Base64.getDecoder().decode(image.replaceAll("data:image/jpeg;base64,",""));
			byte[] imageByte = Base64.decodeBase64(image.replaceAll("data:image/jpeg;base64,",""));
			String keyPrix = "hotcoin/verifyImage"; 
			if ("otcMerchant".equals(imageType)) {
				keyPrix = "otc/merchant"; 
			}
			String ossKey = keyPrix+"/"+user.getFid()+"/";
			logger.info("oss 存储路径："+ossKey); 
	        String result = ossHelper.uploadValidateFile(imageByte,imageType+".jpg",ossKey); 
			return ReturnResult.SUCCESS(200,result);
	}
	
	
	/**
	 * 非大陆用户实名认证
	 * @param request
	 * @param realName 真实姓名
	 * @param idcardNumber 证件号
	 * @param idcardFrontImage 证件正面照片
	 * @param idcardImage 手持证件照
	 * @return
	 */
	@ApiOperation("")
	@RequestMapping(value = "/v2/nonChineseVerify", method = RequestMethod.POST)
		public ReturnResult nonChineseVerify(HttpServletRequest request,
			@RequestParam(required = true) String realName,
            @RequestParam(required = true) String idcardNumber,
            @RequestParam(required = true) String idcardFrontImage,
            @RequestParam(required = true) String idcardImage,
            @RequestParam(required = true) int type) {
        FUser user = getUser();
        
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
        FUserIdentity userIdentity = userIdentityService.selectByUser(user.getFid());
        if(userIdentity != null) {
        	if(userIdentity.getFstatus().equals(IdentityStatusEnum.WAIT.getCode())) {
        		return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.302")); 
        	}
        	if(userIdentity.getFstatus().equals(IdentityStatusEnum.PASS.getCode())) {
        		return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.303")); 
        	}
        }

		FUserIdentity identityParam = userIdentityService
				.selectByCode(idcardNumber);

		if (identityParam != null && !identityParam.getFuid().equals(user.getFid())) {
			return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.304")); 
		}

		FUser param = new FUser();
		param.setFidentityno(idcardNumber);
		List<FUser> users = userService.selectUserListByParam(param);
		if (users != null && users.size() > 0 && !users.get(0).getFid().equals(user.getFid())) {
			return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.305")); 
		}


		FUserIdentity identity = new FUserIdentity();
		identity.setFuid(user.getFid());
		identity.setFname(realName);
		identity.setFtype(type-1);
		identity.setFcountry(I18NUtils.getString("UserApiV2Controller.306")); 
		identity.setFcode(idcardNumber);
		identity.setFstatus(IdentityStatusEnum.WAIT.getCode());
		identity.setFcreatetime(new Date());
		identity.setIp(getIpAddr());
		identity.setNewValidate(true);
		identity.setImageIdcardFront(idcardFrontImage);
		identity.setImageIdcardBack(idcardImage);
		userIdentityService.updateNormalIdentity(identity);

		FUser paramUser = userService.selectUserById(user.getFid());
		paramUser.setFhasrealvalidate(false);
		paramUser.setFidentityno(idcardNumber);
		paramUser.setFidentitytype(type);
		paramUser.setFrealname(realName);
		paramUser.setFhasrealvalidatetime(Utils.getTimestamp());
		userService.updateByPrimaryKey(paramUser);
		updateUserInfo(paramUser);
		return ReturnResult.SUCCESS();
	}
	
	
	/**
	 * 查询用户其他记录
	 * @param request
	 * @return
	 */
	@ApiOperation("")
	@RequestMapping(value = "/v2/otherAssetRecords", method = RequestMethod.POST)
		public ReturnResult otherAssetRecords(HttpServletRequest request,
			String start,String end,
			@RequestParam(required = false, defaultValue = "1")int page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false) String coinName) {
        FUser user = getUser();
        
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
        Map<String, Object> param = new HashMap<>();
        param.put("uid", user.getFid()); 
        param.put("coinName", coinName); 
        param.put("start", start); 
        param.put("end", end); 
        PageInfo<UserWalletBalanceLog> info = userWalletBalanceLogService.selectList(param, page, pageSize);
        return ReturnResult.SUCCESS(info);
	}
	
	/**
     * otc商户上传证件照、视频
     * @param 
     * @return map
     */
	@RequestMapping(value = "/v2/uploadImage", method = RequestMethod.POST)
		public ReturnResult uploadImage(HttpServletRequest request,MultipartFile file) {
        FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
        
		if(file!=null){
			String fileName = file.getOriginalFilename();
			if (fileName != null) {
				try {
					String keyPrix = "otc/merchant"; 
					String ossKey = keyPrix+"/"+user.getFid()+"/";
					logger.info("oss 存储路径："+ossKey); 
			        
			        String result = ossHelper.uploadFile(file, ossKey);
			        if (StringUtils.isNotEmpty(result)) {
						return ReturnResult.SUCCESS(200,result);
			        } else {
			        	return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.3")); 
			        }
				} catch (Exception e) {
                    logger.error(e.getMessage(), e);
					logger.error("请求路径：{},系统异常：{}",request.getRequestURL(),e.getMessage()); 
				}
			} else {
				return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.273")); 
			}
		}else{
	        // 返回信息status为1，表示成功
			return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.274")); 
		}
		return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.275")); 
	}
	
	@RequestMapping(value="/user/changeLanguage",method = {RequestMethod.GET,RequestMethod.POST})
		public ReturnResult changeLanguage() {
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
        userService.changeLanguage(user.getFid());
        return ReturnResult.SUCCESS();
	}
	
		@RequestMapping(value="/user/modifySecondaryVerification",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult modifySecondaryVerification(
			@RequestParam(required = true) String tradePassword,
			@RequestParam(required = true) Integer status,
			@RequestParam(required = true) String code) {
		FUser user = getUser();
        if (user == null) {
            return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
        }
    	user.setFtradepassword(Utils.MD5(tradePassword));
		Result result = this.userService.modifySecondaryVerification(user, status, code);
		if(result.getCode() == 200){
			return ReturnResult.SUCCESS();
		} else if(result.getCode() > 200 && result.getCode() < 1000){
			return ReturnResult.FAILUER(I18NUtils.getString("common.error." + result.getCode())); 
		} else if(result.getCode() >= 1000 && result.getCode() < 10000){
			return ReturnResult.FAILUER(I18NUtils.getString("user.second.verify.error." + result.getCode())); 
		} else{
			return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
		}
	}
	
	/**
	 * 修改其他登录
	 * @throws Exception 
	 */
		@RequestMapping(value="/user/modifyOtherLogin",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult modifyOtherLogin(@RequestParam(required = true) String password) throws Exception{
		FUser user = getUser();
	    if (user == null) {
	    	return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("com.public.error.10001"));
	    }
			//RSA获取密码
			byte[] decryptByPrivateKey = RSAUtils.decryptByPrivateKey(java.util.Base64.getDecoder().decode(password), rsaProperties.getPriKey());
			String passwordStr = new String(decryptByPrivateKey);
			String passwordMd5 = Utils.MD5(passwordStr);

			FUser fuser = userService.selectUserByFid(user.getFid());
			if (passwordMd5.equals(fuser.getFloginpassword())) {
				return ReturnResult.SUCCESS();
			} else {
				return ReturnResult.FAILUER(I18NUtils.getString("UserApiV2Controller.74")); 
			}
	}
	
	
	@ApiOperation("设置钓鱼码")
	@PostMapping("/user/setAntiPhishingCode")
	public RespData<Void> setAntiPhishingCode(@Valid  SetAntiPhishingCodeReq req){
		FUser fuser = getCurrentUserInfoByToken();
		logger.info("设置钓鱼码,用户:{}",JSON.toJSONString(fuser));
		if (fuser == null) {
            throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
        }
		
		String ip = HttpRequestUtils.getIPAddress();

		SetAntiPhishingCodeDTO dto=new SetAntiPhishingCodeDTO();
		dto.setUserId(fuser.getFid());
		dto.setAntiPhishingCode(req.getAntiPhishingCode());
		dto.setGoogleValidCode(req.getGoogleValidCode());
		dto.setMailValidCode(req.getMailValidCode());
		dto.setMobileValidCode(req.getMobileValidCode());
		dto.setIp(ip);
		userService.setAntiPhishingCode(dto);
		return RespData.ok();
	}
	
	
		@ApiOperation("修改钓鱼码")
	@PostMapping("/user/modifyAntiPhishingCode")
	public RespData<Void> modifyAntiPhishingCode(@Valid  ModifyAntiPhishingCodeReq req){
		FUser fuser = getCurrentUserInfoByToken();
		if (fuser == null) {
            throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
        }
		
		String ip = HttpRequestUtils.getIPAddress();

		ModifyAntiPhishingCodeDTO dto=new ModifyAntiPhishingCodeDTO();
		dto.setUserId(fuser.getFid());
		dto.setOldAntiPhishingCode(req.getOldAntiPhishingCode());
		dto.setNewAntiPhishingCode(req.getNewAntiPhishingCode());
		dto.setGoogleValidCode(req.getGoogleValidCode());
		dto.setMailValidCode(req.getMailValidCode());
		dto.setMobileValidCode(req.getMobileValidCode());
		dto.setIp(ip);
		userService.modifyAntiPhishingCode(dto);
		return RespData.ok();
	}
	
	
	
}

