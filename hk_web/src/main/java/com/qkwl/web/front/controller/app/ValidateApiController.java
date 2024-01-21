package com.qkwl.web.front.controller.app;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;
import com.qkwl.common.Enum.validate.LocaleEnum;
import com.qkwl.common.Enum.validate.PlatformEnum;
import com.qkwl.common.Enum.validate.SendTypeEnum;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.LogUserActionEnum;
import com.qkwl.common.dto.Enum.ScoreTypeEnum;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.validate.ValidateParamInfo;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.framework.validate.ValidateHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.user.IUserSecurityService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.rpc.v2.UserSecurityService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.PhoneValiUtil;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.APISignPermission;
import com.qkwl.web.permission.annotation.PassToken;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 短信接口
 */
@Controller
@ApiIgnore
public class ValidateApiController extends JsonBaseController {

    private static final Logger logger = LoggerFactory.getLogger(ValidateApiController.class);

    @Autowired
    IUserService userService;

    @Autowired
    private ValidateHelper validateHelper;

    @Autowired
    private IUserSecurityService userSecurityService;
    
    @Autowired
	private UserSecurityService userSecurityV2Service;

    /**
     * 发送业务短信
     *
     * @param area  区号
     * @param phone 手机号
     * @param type  112手机端注册、102绑定手机、
     * @return
     */
    @ResponseBody
    @PassToken
    @ApiOperation("")
	@RequestMapping(value="/v1/validate/send",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult sendMessage(String area, String phone, Integer type) {
        if (TextUtils.isEmpty(area) || TextUtils.isEmpty(phone)) {
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.0")); 
        }

        if (!PhoneValiUtil.checkPhoneNumber(phone, area)) {
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.1")); 
        }
        area = area.replace("+", "");  //$NON-NLS-2$
        ReturnResult checkResult = checkPhoneIsValidate(phone, type);
        if (checkResult.getCode() != ReturnResult.SUCCESS) {
            return checkResult;
        }
        boolean result;
        try {
            int sendType = "86".equals(area) ? SendTypeEnum.SMS_TEXT.getCode():SendTypeEnum.SMS_INTERNATIONAL.getCode(); 
            result = validateHelper.smsValidateCode(0, area, phone, sendType,
                    PlatformEnum.BC.getCode(), type, LocaleEnum.ZH_TW.getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }
        if (result) {
            return ReturnResult.SUCCESS();
        } else {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
        }

    }

    /**
     * 发送需要签名的短信
     *
     * @param type 104人民币提现、105虚拟币提现、106修改登录密码、107修改交易密码
     * @return
     */
    @ResponseBody
    @APISignPermission
    @ApiOperation("")
	@RequestMapping(value = "/v1/validate/sign", method = RequestMethod.POST)
    public ReturnResult sendNeedTokenMessage(Integer type) {
        FUser userInfo = getCurrentUserInfoByApiToken();
        if (!userInfo.getFistelephonebind()) {
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.6")); 
        }
        boolean result = false;
        try {
            int sendType = "86".equals(userInfo.getFareacode()) ? SendTypeEnum.SMS_TEXT.getCode():SendTypeEnum.SMS_INTERNATIONAL.getCode(); 
            result = validateHelper.smsValidateCode(userInfo.getFid(),
                    userInfo.getFareacode(), userInfo.getFtelephone(), sendType,
                    PlatformEnum.BC.getCode(), type, LocaleEnum.ZH_TW.getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }
        if (result) {
            return ReturnResult.SUCCESS();
        } else {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
        }
    }

    /**
     * 发送绑定邮件验证码
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v1/email/send",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult sendemail(
            @RequestParam(required = true, defaultValue = "0") String address,
            @RequestParam(required = false, defaultValue = "1") Integer msgtype) throws Exception {
        FUser fuser = getCurrentUserInfoByApiToken();
        if (address.equals("0")) { 
            //判断邮件是否为空
            return ReturnResult.FAILUER(I18NUtils.getString("user.security.error.1003")); 
        }
        
        
        FUser user = getUser();
		
		if(user == null) {
			return ReturnResult.FAILUER(ReturnResult.FAULURE_USER_NOT_LOGIN,I18NUtils.getString("ValidateV2Controller.24"));
		}
		
		if(user.getFismailbind()) {
			//客户已绑定邮箱
			return ReturnResult.FAILUER(I18NUtils.getString("com.validate.error.11020")); 
		}
		if (!address.matches(Constant.EmailReg)) {
			//邮箱错误
			throw new BizException(ErrorCodeEnum.PLEASE_INPUT_CORRECT_EMAIL_ADDRESS);
		}
		
		user.setFemail(address);
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


    /**
     * 邮件绑定
     */
	@ResponseBody
	@ApiOperation("")
	@RequestMapping(value = "/v1/email/add", method = { RequestMethod.GET, RequestMethod.POST })
	public ReturnResult mailValidate(@RequestParam(required = true) String code) throws Exception {
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

    /**
     * 检查手机号有效性
     *
     * @param phone        手机号
     * @param businessType 业务类型
     * @return
     */
    private ReturnResult checkPhoneIsValidate(String phone, Integer businessType) {
        FUser fuser = new FUser();
        if (businessType == BusinessTypeEnum.SMS_APP_REGISTER.getCode().intValue()) {
            fuser.setFtelephone(phone);
            if (this.userService.selectIsExistByParam(fuser)) {
                return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.17")); 
            }
        } else if (businessType == BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode()) {
            //提现验证码
            FUser userInfo = getCurrentUserInfoByApiToken();
            if (!userInfo.getFistelephonebind()) {
                return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.18")); 
            }
        } else {
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.19")); 

        }
        return ReturnResult.SUCCESS();

    }

    /**
     *  绑定银行卡和人民币提现申请都需要这个验证码
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/v1/user/send_bank_sms",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult sendBindBankCode() throws Exception {
        FUser userInfo = getCurrentUserInfoByApiToken();
        if (userInfo == null){
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.20")); 
        }
        if (!userInfo.getFistelephonebind()){
            return ReturnResult.FAILUER(I18NUtils.getString("ValidateApiController.21")); 
        }
        boolean result = false;
        try {
            result = validateHelper.smsValidateCode(userInfo.getFid(),
                    userInfo.getFareacode(),userInfo.getFtelephone(), SendTypeEnum.SMS_TEXT.getCode(),
                    PlatformEnum.BC.getCode(),BusinessTypeEnum.SMS_CNY_WITHDRAW.getCode(),super.getLanEnum().getCode());
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }
        if (result) {
            return ReturnResult.SUCCESS();
        } else {
            return ReturnResult.FAILUER(I18NUtils.getString("com.public.error.10000")); 
        }
    }


}
