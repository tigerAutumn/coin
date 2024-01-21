package com.qkwl.common.Enum;


public enum OpenApiStatusEnum {

    NORMAL(1,"正常"),
    DISABLE(2,"禁用"),
    ;

    private Integer code;
    private String desc;


    OpenApiStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OpenApiStatusEnum getByCode(Integer code) {
        for (OpenApiStatusEnum  e: OpenApiStatusEnum.values()) {
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
