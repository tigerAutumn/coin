package com.hotcoin.notice.Enum;

/**
 * 
 * @author huangjinfeng
 */
public enum TemplateTypeEnum {

    SMS(1, "短信"),
    EMAIL(2, "邮件");

    private Integer code;
    private String value;


    TemplateTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (TemplateTypeEnum e : TemplateTypeEnum.values()) {
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
}
