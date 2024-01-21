package com.hotcoin.notice.Enum;


public enum SendTypeEnum {

    SMS_TEXT("SMS_TEXT","普通短信"),
    SMS_VOICE("SMS_VOICE","语音短信"),
    SMS_INTERNATIONAL("SMS_INTERNATIONAL","国际短信")
    ;

    private String code;
    private String desc;


    SendTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getValueByCode(String code) {
        for (SendTypeEnum  e: SendTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
