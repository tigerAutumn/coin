package com.qkwl.common.dto.Enum.otc;


public enum OtcUserTypeEnum {

	Ordinary_Merchant(1, "普通商户"),
	Certified_Merchant(2, "认证商户"),
	;
	private Integer code;
    private String value;
	
    private OtcUserTypeEnum(Integer code, String value) {
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
        for (OtcUserTypeEnum e: OtcUserTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
