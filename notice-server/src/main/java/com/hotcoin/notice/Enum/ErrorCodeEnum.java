package com.hotcoin.notice.Enum;

/**
 * 异常错误码
 * 
 * @author huangjinfeng
 */
public enum ErrorCodeEnum {

  DEFAULT(-1, "系统错误:{0}"),
  PARAM_ERROR(1000, "参数绑定异常【{0}】"), 
  AUTH_ERROR(1001, "验证失败"),
  PASS_ERROR(1002,"密码输入错误{0}次,{1}次错误将限制登录"), 
  NOT_LOGGED_IN(1003, "未登录"), 
  NO_PERMISSON(1004,"无权限,不能访问"), 
  CAPTCHA_ERROR(1005, "验证码错误"), 
  USER_HAVE_REGISTERED(1006, "用户已经注册"),
  EXCESSIVE_ATTEMPTS(1007,"密码输错{0}次,帐号{1}分钟后可登录"), 
  UNKNOWN_ACCOUNT(1008,"用户不存在"),
  LOCKED_ACCOUNT(1009, "帐户已被锁定"), 
  SMS_LIMIT_ERROR(1010,"短信发送过于频繁"),
  EMAIL_LIMIT_ERROR(1011,"邮件发送过于频繁"),
  SMS_PROVIDER_NOT_FOUND(2001,"短信提供商未找到"),
  SMS_TEMPLATE_NOT_FOUND(2002,"短信模版未找到"), 
  EMAIL_TEMPLATE_NOT_FOUND(2003,"邮件模版未找到"), 
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

  public String getDesc() {
    return desc;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public void setMsg(String desc) {
    this.desc = desc;
  }
}
