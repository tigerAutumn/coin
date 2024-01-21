package com.qkwl.common.dto.Enum.lever;

public enum LeverRecordStatusEnum {

	PROCESSING(1, "处理中"),
	SUCC(2,"成功"),
    FAIL(3,"失败");
	
	private Integer code;
    private String value;
	
    private LeverRecordStatusEnum(Integer code, String value) {
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
        for (LeverRecordStatusEnum e: LeverRecordStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
