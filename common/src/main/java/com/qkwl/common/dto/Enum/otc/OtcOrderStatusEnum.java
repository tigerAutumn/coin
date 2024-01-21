package com.qkwl.common.dto.Enum.otc;

public enum OtcOrderStatusEnum {
	WAIT(1, "待支付"),
	PAID(2,"已支付"),
    COMPLETED(3,"交易完成"),
    CANCELLED(4,"已取消"),
    TIMEOUT(5,"超时关闭"),
    APPEAL(6,"申诉中");
//    APPEAL_COMPLETED(7,"申诉处理完成"),
//    APPEAL_COMPLETED_CANCELLED(8,"申诉取消");
	
	private Integer code;
    private String value;
	
    private OtcOrderStatusEnum(Integer code, String value) {
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
        for (OtcOrderStatusEnum e: OtcOrderStatusEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
