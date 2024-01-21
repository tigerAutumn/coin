package com.qkwl.common.dto.Enum;


public enum ApiKeyTypeEnum {

    READONLY(1,"只读"),
    TRADE(2,"允许交易"),
    WITHDRAW(3,"允许提现"),
    ;

    private Integer code;
    private String desc;


    ApiKeyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ApiKeyTypeEnum getByCode(Integer code) {
        for (ApiKeyTypeEnum  e: ApiKeyTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
