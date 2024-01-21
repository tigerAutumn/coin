package com.qkwl.common.dto.Enum.otc;

public enum OtcCurrencyStatusEnum {

	NORMAL(1, "启用"),
	PROHIBIT(0, "禁用");
	private Integer code;
    private String value;
	
    private OtcCurrencyStatusEnum(Integer code, String value) {
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
