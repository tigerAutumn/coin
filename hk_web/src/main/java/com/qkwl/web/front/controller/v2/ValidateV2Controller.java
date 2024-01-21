package com.qkwl.web.front.controller.v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.user.FUserExtend;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IUserExtendService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.rpc.v2.UserSecurityService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.PhoneValiUtil;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.ValidateImage.VerifyCodeUtils;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@Api(tags="验证码")
public class ValidateV2Controller extends JsonBaseController {
	private static final Logger logger = LoggerFactory.getLogger(ValidateV2Controller.class);
	@Autowired
	private IUserService userService;
	@Autowired
	private ValidateHelper validateHelper;
	@Autowired
	private UserSecurityService userSecurityV2Service;
	@Autowired
	private RedisHelper redisHelper;
	@Autowired
	private MemCache memCache;
	@Autowired
	private IUserExtendService userExtendService;

	@ResponseBody
	@PassToken
    @ApiIgnore
	@RequestMapping(value="/v2/validateImage",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 生成图片
        int w = 100, h = 39;
        VerifyCodeUtils.outputImage(w, h, out, verifyCode);
        
        byte[] bytes = out.toByteArray();
        
        String base64 = Base64.encodeBase64String(bytes);
        super.deletRedisData("CHECKCODE"); 
        String token = super.setRedisData("CHECKCODE", verifyCode); 
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", base64); 
        jsonObject.put("imgToken", token); 
        
        return ReturnResult.SUCCESS(jsonObject);
    }
	
	
	@ResponseBody
	@PassToken
	@ApiOperation("发送短信验证码")
	@PostMapping(value={ "/v2/sendPhone"})
	public RespData<Void> sendPhone(
			@RequestParam(required = false, defaultValue= "") String phone,
			@RequestParam(required = false) String area,
			@RequestParam(required = false) String imgCode,
			@RequestParam(required = true) int type) {
		FUser user = getUser();
		
		//判断是否需要传手机号
		if (type == BusinessTypeEnum.SMS_BING_MOBILE.getCode()
				|| type == BusinessTypeEnum.SMS_WEB_REGISTER.getCode()
				|| type == BusinessTypeEnum.SMS_APP_REGISTER.getCode()
				|| type == BusinessTypeEnum.SMS_FIND_PHONE_PASSWORD.getCode()
				|| type == BusinessTypeEnum.SMS_SECONDARY_VERIFICATION.getCode()
				|| type == BusinessTypeEnum.CODE_LOGIN.getCode()) {
			if (StringUtils.isEmpty(phone)) {
				throw new BizException(ErrorCodeEnum.PHONE_NUM_NOT_ALLOW_EMPTY);
			}
		} else {
			if (user == null) {
				throw new BizException(ErrorCodeEnum.NOT_LOGGED_IN);
			}
			phone = user.getFtelephone();
		}
		
		if(user == null) {
			FUser param = new FUser();
			param.setFtelephone(StringUtils.isEmpty(phone)?"":phone); 
			user = userService.selectUserByParam(param);
			
			if(user == null) {
				if(StringUtils.isNotEmpty(imgCode)) {
					String session_code = memCache.get(Constant.GEETEST_SUCC+imgCode);
					if ((session_code == null) || (!imgCode.equalsIgnoreCase(session_code))) {
						throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
					}
				}else {
					throw new BizException(ErrorCodeEnum.IMG_CODE_ERROR);
				}
			}
		}
		
		//未输入区号取用户区号，用户不存在时默认为
		if(StringUtils.isEmpty(area) || area.equals("0")) { 
			area = "86"; 
			if(user != null) {
				if(StringUtils.isNotEmpty(user.getFareacode())) {
					area = user.getFareacode();
				}
			}
		} else {
			area = area.replace("+", "");  
		}

		if ("86".equals(area)) {
			if (!phone.matches(Constant.PhoneReg)) {
				throw new BizException(ErrorCodeEnum.PHONE_NUM_FORMAT_INCORRECT);
			}
		} else if (!PhoneValiUtil.checkPhoneNumber(phone, area)) {
			throw new BizException(ErrorCodeEnum.PHONE_NUM_FORMAT_INCORRECT);
		}
		
		int sendType = "86".equals(area) ? SendTypeEnum.SMS_TEXT.getCode():SendTypeEnum.SMS_INTERNATIONAL.getCode();
		boolean isSuccess = this.validateHelper.smsValidateCode(Integer.valueOf(0), area, phone,
				sendType, PlatformEnum.BC.getCode(),
				type, super.getLanEnum().getCode());
		if (!isSuccess) {
			throw new BizException(ErrorCodeEnum.DEFAULT);
		}
		return RespData.ok();
	}


	
	@ResponseBody
	@PassToken
	@ApiOperation("发送邮箱验证码")
	@PostMapping({ "/v2/sendEmail" })
	public RespData<Void> sendEmail(@RequestParam(required = true) String email,
			@RequestParam(required = false) String imgCode,
			@RequestParam(required = true) int type) throws Exception {
		if (!email.matches(Constant.EmailReg)) {
			throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_EMAIL_ADDRESS);
		}
		
		FUser user = getUser();
		if(user == null) {
			FUser param = new FUser();
			param.setFemail(StringUtils.isEmpty(email)?"":email); 
			user = userService.selectUserByParam(param);
			
			if(user == null) {
				if(StringUtils.isNotEmpty(imgCode)) {
					String session_code = memCache.get(Constant.GEETEST_SUCC+imgCode);
					if ((session_code == null) || (!imgCode.equalsIgnoreCase(session_code))) {
						throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_VERIFICATION_CODE);
					}
				}else {
					throw new BizException(ErrorCodeEnum.IMG_CODE_ERROR);
				}
			}
		}
		
		
		//获取钓鱼码
		String antiPhingCode="";
		if(user!=null) {
			FUserExtend fUserExtend = userExtendService.selectByUid(user.getFid());
			antiPhingCode=Optional.ofNullable(fUserExtend).map(FUserExtend::getAntiPhishingCode).orElse("");
		}
		
		this.validateHelper.mailSendCode(email, PlatformEnum.BC,type, super.getLanEnum(), HttpRequestUtils.getIPAddress(),antiPhingCode);
		return RespData.ok(); 
	}
	
	//发送邮箱验证码
	@ResponseBody
	@PassToken
	@ApiIgnore
	@RequestMapping(value="/v2/send_email_bind_code",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult sendEmailCode(@RequestParam(required = true) String email){
			FUser user = getUser();
			
			if(user == null) {
				return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("ValidateV2Controller.24"));
			}
			
			if(user.getFismailbind()) {
				//客户已绑定邮箱
				return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11020")); 
			}
			if (!email.matches(Constant.EmailReg)) {
				//邮箱错误
				throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_EMAIL_ADDRESS);
			}
			
			user.setFemail(email);
			user.setFismailbind(false);
			ValidateParamInfo paramInfo = new ValidateParamInfo();
			paramInfo.setIp(getIpAddr());
			paramInfo.setPlatform(PlatformEnum.BC);
			paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
			paramInfo.setLocale(getLanEnum());
			
			Result result = userSecurityV2Service.updateUserSecurityInfo(user, paramInfo, LogUserActionEnum.ADD_MAIL, null);
			if(result.getSuccess()) {
				//更新redis中的用户信息
				updateUserInfo(user);
				return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
			}   else if (result.getCode() >= 10000) {
	            return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error." + result.getCode(), result.getData())); 
	        } else {
	            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error." + result.getCode())); 
	        }
	}
	
	//通过验证码绑定邮箱
	@ResponseBody
	@PassToken
	@ApiIgnore
	@RequestMapping(value="/v2/email_bind",method = {RequestMethod.GET,RequestMethod.POST})
	public ReturnResult emailBind(@RequestParam(required = true) String code){
		
		FUser user = getUser();
		
		if(user == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("ValidateV2Controller.31"));
		}
		
		if(user.getFismailbind()) {
			//客户已绑定邮箱
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11020")); 
		}
		
		user.setFismailbind(true);
		ValidateParamInfo paramInfo = new ValidateParamInfo();
		paramInfo.setIp(getIpAddr());
		paramInfo.setPlatform(PlatformEnum.BC);
		paramInfo.setBusinessType(BusinessTypeEnum.EMAIL_SEND_CODE);
		paramInfo.setLocale(getLanEnum());
		paramInfo.setEmailCode(code);
		
		Result result = userSecurityV2Service.updateUserSecurityInfo(user, paramInfo, LogUserActionEnum.BIND_MAIL, null);
		if(result.getSuccess()) {
			//更新redis中的用户信息
			updateUserInfo(user);
			return ReturnResult.SUCCESS(I18NUtils.getString("common.succeed.200")); 
		}   else if (result.getCode() >= 10000) {
            return ReturnResult.FAILUER(I18NUtils.getString("com.error." + result.getCode(), result.getData())); 
        } else {
            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error." + result.getCode())); 
        }
	}
	
	@ResponseBody
	@ApiIgnore
	@RequestMapping(value="/v2/send_phone_bind_code",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult postPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area) {
        FUser fuser = getCurrentUserInfoByToken();
        
        if(null == fuser) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("ValidateV2Controller.36"));
		}
        
        if (phone.equals("0")) { 
            //判断手机是否为空
            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1003")); 
        }
        fuser.setFtelephone(phone);
		fuser.setFareacode(area);
        String ip = HttpRequestUtils.getIPAddress();

        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);

        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setAreaCode(area);
        paramInfo.setPhone(phone);

        Result validateResult = this.userSecurityV2Service.updateUserSecurityInfo(fuser, paramInfo, LogUserActionEnum.ADD_PHONE, null);
        if (!validateResult.getSuccess()) {
            return ReturnResult.FAILUER(validateResult.getCode(),I18NUtils.getString("user.security.error." + validateResult.getCode())); 
        }
        //验证短信已经发送，请及时验证！
        return ReturnResult.SUCCESS();
    }

    @ResponseBody
    @ApiIgnore
	@RequestMapping(value="/v2/phone_bind",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult bindPhone(@RequestParam(required = false, defaultValue = "0") String phone,
                                  @RequestParam(required = false, defaultValue = "0") String area,
                                  @RequestParam(required = false, defaultValue = "0") String code) {
        FUser fuser = getCurrentUserInfoByToken();
        
        if(null == fuser) {
        	throw new BizException(ErrorCodeEnum.PLEASE_LOGIN);
		}
        
        if (phone.equals("0")) { 
            //判断手机是否为空
            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1024")); 
        }
        fuser.setFtelephone(phone);
        fuser.setFareacode(area);
        String ip = HttpRequestUtils.getIPAddress();
        fuser.setIp(ip);
        ValidateParamInfo paramInfo = new ValidateParamInfo();
        paramInfo.setIp(ip);
        paramInfo.setPlatform(PlatformEnum.BC);
        paramInfo.setBusinessType(BusinessTypeEnum.SMS_BING_MOBILE);
        paramInfo.setLocale(super.getLanEnum());
        paramInfo.setSecondAreaCode(area);
        paramInfo.setSecondPhone(phone);
        paramInfo.setSecondCode(code);
        paramInfo.setCode(code);
        fuser.setFistelephonebind(true);
        
        Result validateResult = this.userSecurityV2Service.updateUserSecurityInfo(fuser, paramInfo,
                LogUserActionEnum.BIND_PHONE, null);
        if (validateResult.getSuccess()) {
            return ReturnResult.SUCCESS();
        }else if(validateResult.getCode() >= 10000){
			return ReturnResult.FAILUER(validateResult.getCode(),I18NUtils.getString("com.validate.error." + validateResult.getCode(), validateResult.getData())); 
		} else {
			return ReturnResult.FAILUER(validateResult.getCode(),I18NUtils.getString("user.security.error." + validateResult.getCode())); 
		}
        //验证短信已经发送，请及时验证！
    }
}
