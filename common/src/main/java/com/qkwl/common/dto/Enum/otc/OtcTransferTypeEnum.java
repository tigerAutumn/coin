package com.qkwl.common.dto.Enum.otc;

import com.qkwl.common.dto.Enum.c2c.C2CBusinessTypeEnum;

public enum OtcTransferTypeEnum {

	transferToOtc(1, "账户划转otc"),
    otcTransferTo(2, "otc划转账户"),
    otcBuy(3, "otc买入"),
    otcSell(4, "otc卖出"),
	otcTransferToInnovate(5, "otc转入创新区"),
	otcMerchantDeposit(6, "otc商户押金");
	private Integer code;
    private String value;
	
    private OtcTransferTypeEnum(Integer code, String value) {
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
        for (C2CBusinessTypeEnum e: C2CBusinessTypeEnum.values()) {
            if (e.getCode().equals(type)) {
                return e.getValue();
            }
        }
        return "";
    }
}
