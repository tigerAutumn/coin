package com.qkwl.common.dto.Enum.otc;

public enum OtcAppealResultEnum {
	PAID_TOKEN(1, "打币"),
	CANCEL(2, "取消");
	private Integer code;
    private String value;
	
    private OtcAppealResultEnum(Integer code, String value) {
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
        for (OtcAppealResultEnum e: OtcAppealResultEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
