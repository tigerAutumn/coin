package com.qkwl.common.dto.Enum.otc;

public enum OtcAppealStatusEnum {
	WAIT(1, "待处理"),
	PROCESSING(2, "处理中"),
	CANCEL(3, "已取消"),
	COMPLETE(4, "已解决");
	private Integer code;
    private String value;
	
    private OtcAppealStatusEnum(Integer code, String value) {
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
        for (OtcAppealStatusEnum e: OtcAppealStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
