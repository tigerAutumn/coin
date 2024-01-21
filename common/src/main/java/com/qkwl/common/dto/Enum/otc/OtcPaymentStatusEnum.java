package com.qkwl.common.dto.Enum.otc;

import com.qkwl.common.dto.Enum.c2c.C2CBusinessTypeEnum;

public enum OtcPaymentStatusEnum {

	Normal(1, "启用"),
	Prohibit(0, "禁用");
	private Integer code;
    private String value;
	
    private OtcPaymentStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValueByCode(Integer type){
        for (OtcPaymentStatusEnum e: OtcPaymentStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
