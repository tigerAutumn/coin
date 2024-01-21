package com.qkwl.common.dto.Enum.lever;

public enum LeverRecordTypeEnum {

	lEVER_TO_COIN(1, "杠杆转币币"),
	COIN_TO_LEVER(2,"币币转杠杆"),
    BORROW(3,"借贷"),
    REPAY(4,"还贷"),
    INTEREST(5,"利息");
	
	private Integer code;
    private String value;
	
    private LeverRecordTypeEnum(Integer code, String value) {
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
        for (LeverRecordTypeEnum e: LeverRecordTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
    
    public static LeverRecordTypeEnum getTypeByCode(Integer type){
        for (LeverRecordTypeEnum e: LeverRecordTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e;
            }
        }
        return null;
    }
}
