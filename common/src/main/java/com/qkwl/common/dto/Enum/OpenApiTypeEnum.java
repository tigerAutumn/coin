package com.qkwl.common.dto.Enum;


public enum OpenApiTypeEnum {

    READONLY(1,"只读"),
    TRADE(2,"允许交易"),
    WITHDRAW(3,"允许提现"),
    ;

    private Integer code;
    private String desc;


    OpenApiTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OpenApiTypeEnum getByCode(Integer code) {
        for (OpenApiTypeEnum  e: OpenApiTypeEnum.values()) {
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
