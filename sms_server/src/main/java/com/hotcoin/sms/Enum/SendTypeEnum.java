package com.hotcoin.sms.Enum;

/**
 * 发送类型枚举
 * Created by ZKF on 2017/5/4.
 */
public enum SendTypeEnum {

    SMS_TEXT(1, "普通短信","SMS_TEXT"),
    SMS_VOICE(2,"语音短信","SMS_VOICE"),
    SMS_INTERNATIONAL(3,"国际短信","SMS_INTERNATIONAL"),
    EMAIL(4,"邮件","EMAIL"),
    SMS_VERIFY(5,"验证码","SMS_VERIFY");

    private Integer code;
    private String value;
    private String text;


    SendTypeEnum(Integer code, String value,String text) {
        this.code = code;
        this.value = value;
        this.text = text;
    }

    public static String getValueByCode(Integer code) {
        for (SendTypeEnum  e: SendTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
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

    public String getText() { return text;  }
}
