package com.qkwl.common.dto.Enum;


public enum ApiKeyStatusEnum {

    NORMAL(1,"正常"),
    DISABLE(2,"禁用"),
    OVERDUE(3,"过期"),
    ;

    private Integer code;
    private String desc;


    ApiKeyStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ApiKeyStatusEnum getEnumByCode(Integer code) {
        for (ApiKeyStatusEnum  e: ApiKeyStatusEnum.values()) {
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
