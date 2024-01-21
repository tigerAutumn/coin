package com.qkwl.common.Enum.validate;

/**
 * 模板业务类型枚举
 * Created by ZKF on 2017/5/4.
 */
public enum BusinessTypeEnum {

    /**
     * 短信
     */
    SMS_APPLY_API(101, "API申请", ""),
    SMS_BING_MOBILE(102, "绑定手机", "bind_mobile"),
    SMS_UNBIND_MOBILE(103, "解绑手机", ""),
    SMS_CNY_WITHDRAW(104, "人民币提现", ""),
    SMS_COIN_WITHDRAW(105, "虚拟币提现", "withdraw_coin"),
    SMS_MODIFY_LOGIN_PASSWORD(106, "修改登录密码", "modify_login_password"),
    SMS_MODIFY_TRADE_PASSWORD(107, "修改交易密码", "modify_trading_password"),
    SMS_COIN_WITHDRAW_ACCOUNT(108, "虚拟币提现地址设置", ""),
    SMS_FIND_PHONE_PASSWORD(109, "找回登录密码-手机找回", "reset_login_password"),
    SMS_CNY_ACCOUNT_WITHDRAW(110, "设置人民币提现账号", ""),
    SMS_WEB_REGISTER(111, "网页端注册帐号", "register"),
    SMS_APP_REGISTER(112, "手机端注册帐号", "register"),
    SMS_PUSHASSET(113, "PUSH资产", ""),
    SMS_FINANCES(114, "存币理财", ""),
    SMS_TRANSFER(115, "资产转移", ""),
    SMS_PRICE_CLOCK(116, "价格闹钟", ""),
    SMS_NEW_IP_LOGIN(117, "新IP登录", "login"),
    SMS_CNY_RECHARGE(118, "人民币充值", ""),
    SMS_RISKMANAGE(119, "风控短信", ""),
    SMS_FIND_EMAIL_PASSWORD(120, "找回登录密码-邮箱找回", ""),
    SMS_MODIFY_LOGIN_REMIND(121, "修改登录密码-短信提醒", ""),
    SMS_MODIFY_TRADE_REMIND(122, "修改交易密码-短信提醒", ""),
    SMS_AUDIT_RISKMANAGE(123, "审核风控短信", ""),
    SMS_SEND_CODE(124, "用户修改绑定手机", "modify_mobile"),
    SMS_BIND_NEW_PHONE(125, "绑定新手机验证码", ""),
    SMS_ADMIN_LOGIN(130, "admin登录", ""),
    SMS_MODIFY_GOOGLE(131, "用户修改谷歌验证", "modify_google_authenticator"),
    SMS_MODIFY_SAFETY(132, "用户修改安全验证", "modify_safety_verification"),
    SMS_FIND_TRADE_PASSWORD(133, "找回交易密码-手机找回", "reset_trading_password"),
    SMS_NOTIFY_SUCC(134, "用户实名认证成功", "real_name_auth_success"),
    SMS_NOTIFY_FAIL(135, "用户实名认证失败", "real_name_auth_failed"),
    SMS_WITHDRAW_TO_ACCOUNT(136, "用户提币到账", "withdraw_coin_to_account"),
    SMS_CHARGE_TO_ACCOUNT(137, "用户充币到账", "charge_coin_to_account"),
    SMS_ACCOUNT_LOCKED(138, "用户账户被冻结", "account_locked"),
    SMS_OPEN_SECONDARY_VERIFICATION(139, "开启登录二次验证", "open_secondary_verification"),
    SMS_CLOSE_SECONDARY_VERIFICATION(140, "关闭登录二次验证", "close_secondary_verification"),
    SMS_SECONDARY_VERIFICATION(141, "登录二次验证", "secondary_verification"),
    SMS_ADD_PAYMENT_METHED(142, "新增收款方式", "add_payment_method"),
    SMS_UPDATE_PAYMENT_METHED(143, "修改收款方式", "update_payment_method"),
    SMS_C2C_BUY(144, "C2C买入成功", "c2c_buy"),
    SMS_C2C_SELL(145, "C2C卖出成功", "c2c_sell"),
    SMS_SET_ANTI_PHISHING_CODE(146, "设置反钓鱼码", "set_anti_phishing_code"),
    SMS_MODIFY_ANTI_PHISHING_CODE(147, "修改反钓鱼码", "modify_anti_phishing_code"),
    CODE_LOGIN(148, "验证码登录", "code_login"),
    
    /**
     * 邮件
     */
    EMAIL_VALIDATE_BING(201, "绑定邮件", ""),
    EMAIL_FIND_PASSWORD(202, "找回登录密码", ""),
    EMAIL_REGISTER_CODE(203, "注册验证码", ""),
    EMAIL_PRICE_CLOCK(204, "价格闹钟", ""),
    EMAIL_NEW_IP_LOGIN(205, "登录IP异常", ""),
    FINANCE_BALANCE(206, "对账邮件", ""),
    EMAIL_APP_BIND(207,"手机端绑定邮件", ""),
	EMAIL_SEND_CODE(208,"邮件验证码", ""),
	EMAIL_SET_ANTI_PHISHING_CODE(209, "设置反钓鱼码", ""),
	EMAIL_MODIFY_ANTI_PHISHING_CODE(210, "修改反钓鱼码", ""),
	/*,
	EMAIL_COIN_WITHDRAW(209, "虚拟币提现")*/
	
	UPDATE_API_KEY(300,"修改API Key","update_api_key");
	

    private Integer code;
    private String value;
    private String type;

    BusinessTypeEnum(Integer code, String value, String type) {
        this.code = code;
        this.value = value;
        this.type = type;
    }

    public static String getValueByCode(Integer code) {
        for (BusinessTypeEnum e : BusinessTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }
    
    public static BusinessTypeEnum getBusinessTypeByCode(Integer code){
	    for(BusinessTypeEnum businessTypeEnum : BusinessTypeEnum.values()){
	      if(code.equals(businessTypeEnum.getCode())){
	        return businessTypeEnum;
	      }
	    }
	    return null;
	  }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
    
    public String getType() {
    	return type;
    }
}
