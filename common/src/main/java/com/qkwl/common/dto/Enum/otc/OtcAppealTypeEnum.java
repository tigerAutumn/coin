package com.qkwl.common.dto.Enum.otc;

public enum OtcAppealTypeEnum {
	UNPAID(1, "买家未付款"),
	UNPAID_TOKEN(2, "卖家不释放数字币"),
	TIMEOUT(3, "长时间无回应"),
	SUSPECTED_FRAUD(4, "涉嫌诈骗"),
	SUDPECTED_MONEY_LAUNDERING(5, "涉嫌洗钱"),
	OTHER(6, "其他");
	private Integer code;
    private String value;
	
    private OtcAppealTypeEnum(Integer code, String value) {
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
        for (OtcAppealTypeEnum e: OtcAppealTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
