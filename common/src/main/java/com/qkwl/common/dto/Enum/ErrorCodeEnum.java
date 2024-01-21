package com.qkwl.common.dto.Enum;

/**
 * 异常错误码
 * 
 * @author huangjinfeng
 */
public enum ErrorCodeEnum {

  DEFAULT(-1, "系统错误:{0}"),
  
  NOT_LOGGED_IN(401, "未登录"), 
  SIGN_FAILED(603, "签名失败"), 
  
  PARAM_ERROR(1000, "参数绑定异常【{0}】"), 
  AUTH_ERROR(1001, "验证失败"),
  PASS_ERROR(1002,"密码输入错误{0}次,{1}次错误将限制登录"), 
  NO_PERMISSON(1004,"无权限,不能访问"), 
  CAPTCHA_ERROR(1005, "验证码错误"), 
  USER_HAVE_REGISTERED(1006, "用户已经注册"),
  EXCESSIVE_ATTEMPTS(1007,"密码输错{0}次,帐号{1}分钟后可登录"), 
  UNKNOWN_ACCOUNT(1008,"用户不存在"),
  LOCKED_ACCOUNT(1009, "帐户已被锁定"), 
  OLD_PWD_ERROR(1010,"旧密码错误"),
  ROLE_EXIST(1011,"角色已存在"),
  MOBILE_EXIST(1012,"手机号已经使用"),
  LOGIN_NAME_EXIST(1013,"登录名已经使用"),
  SET_ANTI_PHISHING_CODE_ERROR(1014,"设置钓鱼码失败"),
  MODIFY_ANTI_PHISHING_CODE_ERROR(1015,"修改钓鱼码失败"),
  ANTI_PHISHING_CODE_ALREADY_SET(1016,"钓鱼码已经设置"),
  OLD_ANTI_PHISHING_CODE_WRONG(1017,"原钓鱼码输入错误"),
  PHONE_NUM_NOT_ALLOW_EMPTY(1018,"手机号不能为空"),
  PLEASE_INPUT_CORRECT_VERIFICATION_CODE(1019,"请输入正确的图片验证码！"), 
  PHONE_NUM_FORMAT_INCORRECT(1020,"手机号格式不正确"),
  IMG_CODE_ERROR(1021,"验证码错误"),
  PLEASE_LOGIN(1022,"请登录！"),
  EMAIL_VALID_CODE_ERROR(1023,"邮箱验证码错误"),
  PHONE_VALID_CODE_ERROR(1024,"手机验证码错误"),
  GOOGLE_VALID_CODE_ERROR(1025,"谷歌验证码错误"),
  IP_LIMIT(1026,"IP限制"),
  ACTIVITY_NOT_LOGIN(1027, "活动未登陆"),
  
  PHONE_LIMIT_BEYOND_ERROR(10004,"手机验证码错误多次，请2小时后再试！"),
  PHONE_LIMIT_COUNT_ERROR(10005,"手机验证码错误！您还有{0}次机会！"),
  EMAIL_LIMIT_BEYOND_ERROR(10014,"邮箱验证码错误多次，请2小时后再试！"),
  EMAIL_LIMIT_COUNT_ERROR(10015,"邮箱验证码错误！您还有{0}次机会！"),
  PLEASE_INPUT_CORRECT_EMAIL_ADDRESS(10016,"请填写真实邮箱地址！"),
  SEND_EMAIL_MAX_1_IN_TIME(10017,"{0}分钟内只能发送一次邮件！"), 
  EMAIL_ALREADY_SEND(10018,"邮件已经发送"), 
  PHONE_NUM_ALREADY_BIND(10019,"手机已绑定! "), 
  EMAIL_ALREADY_BIND(10020,"邮箱已绑定！"),
  EMAIL_ADDRESS_CAN_NOT_EMPTY(10021,"邮箱地址不能为空"), 
  GOOGLE_ALREADY_BIND(10022,"谷歌已绑定"), 
  NO_BIND_MOBILE_OR_GOOGLE_TO_MODIFY_PWD(10023,"您没有绑定手机或谷歌验证暂不允许修改密码"), 
  TRADE_PWD_NOT_ALLOW_SAME_WITH_LOGIN_PWD(10024,"交易密码不允许与登录密码一致！"), 
  ANTI_PHISHING_CODE_FORMAT_ERROR(10025,"钓鱼码格式错误"),
  SEND_SMS_FREQUENTLY(10026,"发送短信过于频繁"), 
  REGISTER_TIMEOUT(10027,"注册超时"), 
  AREA_CODE_EMPTY(10028,"区号为空"), 
  SCAN_LOGIN_QR_CODE_EXPIRE(10029,"扫码登录二维码已经过期，请重新获取二维码"), 
  WAITING_FOR_SCAN_LOGIN(10030,"等待用户扫码登录"), 
  USER_SCORE_NO_EXIST(10031,"用户积分等级不存在"), 
  SUSPEND_LOGIN_DURING_SYSTEM_UPGRADE(10032,"系统升级中，暂停登录"), 
  NOT_NEW_COIN(10033,"不是新币"), 
  TOTAL_NOT_ENOUGH(10034,"钱包余额不够"), 
  FROZEN_FAILED(10035,"钱包冻结失败"),
  ADD_TOTAL_FAILED(10036,"钱包增加可用失败"),
  SUB_FROZEN_FAILED(10037,"钱包扣除冻结失败"),
  TRADE_PASSWORD_ERROR(10038,"交易密码错误"),
  UNFROZEN_FAILED(10039, "钱包解冻失败"),
  SEND_RED_ENVELOPE_DISABLED(10040, "您已触发资产平衡限制，暂不能使用红包功能，请联系客服处理！"),
  DELETE_API_KEY_FAILED(10150,"删除API key失败"),
  UPDATE_API_KEY_FAILED(10151,"更新API key失败"),
  UNKNOWN_WALLET(10152, "钱包不存在"), 
  UPDATE_API_KEY_STATUS_FAILED(10160,"修改API Key状态失败"), 
  API_NO_OPEN(10170,"API未开放"), 
  API_KEY_NO_PERM_TO_URL(10171,"API KEY没有权限访问当前接口"), 
  SIGNATURE_METHOD_ERROR(10172,"签名方法不正确"), 
  API_KEY_INVALID(10173,"无效API KEY"), 
  VERIFY_SIGNATURE_FAILED(10174,"验证签名失败"), 
  CREATE_API_KEY_MAX_5(10175,"每个用户最多创建5组API Key"),
  CREATE_API_KEY_REMARK_NOT_BLANK(10176,"备注不能为空"),
  IP_MAX_4(10177,"最多绑定4个IP,以半角逗号分隔"),
  FROZEN_WALLET_ERROR(10188,"钱包冻结金额异常【{0}】"), 
  SYMBOL_ERROR(10192,"币对错误"), 
  DATE_INTERVAL_GREATER_THAN_2(10193,"日期间隔大于两天"), 
  DATE_FORMAT_ERROR(10194,"日期格式错误"),
 ;



  public static ErrorCodeEnum getEnumByTxCode(Integer code) {
    ErrorCodeEnum[] allEnums = values();
    for (ErrorCodeEnum item : allEnums) {
      if (item.getCode().equals(code))
        return item;
    }
    return null;
  }

  private Integer code;

  private String desc;

  ErrorCodeEnum(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

public Integer getCode() {
	return code;
}

public void setCode(Integer code) {
	this.code = code;
}

public String getDesc() {
	return desc;
}

public void setDesc(String desc) {
	this.desc = desc;
}
}
