package com.qkwl.common.dto.Enum.otc;


public enum OtcPaymentTypeEnum {

	Bankcard(1, "银行卡"),
	MobilePayment(2, "移动支付"),
	NonContinentalPayment(3, "非大陆支付")
	;
	private Integer code;
    private String value;
	
    private OtcPaymentTypeEnum(Integer code, String value) {
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
        for (OtcPaymentTypeEnum e: OtcPaymentTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
