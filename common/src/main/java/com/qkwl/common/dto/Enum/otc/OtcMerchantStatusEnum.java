package com.qkwl.common.dto.Enum.otc;

public enum OtcMerchantStatusEnum {

	Inited(1, "未审核"),
	Passed(2, "已通过"),
	Refused(3, "未通过"),
	Prohibited(4, "已禁用"),
	Removed(5, "已解除");
	private Integer code;
    private String value;
	
    private OtcMerchantStatusEnum(Integer code, String value) {
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
        for (OtcMerchantStatusEnum e: OtcMerchantStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
