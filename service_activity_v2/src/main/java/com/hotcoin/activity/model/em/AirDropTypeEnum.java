package com.hotcoin.activity.model.em;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.em
 * @ClassName: AirDropTypeEnum
 * @Author: hf
 * @Description:
 * @Date: 2019/7/3 10:13
 * @Version: 1.0
 */
public enum AirDropTypeEnum {
    REGISTER_ACTIVITY(1, "register", "注册空投活动"),
    RECHARGE_ACTIVITY(2, "recharge", "充值空投活动"),
    TRADE_ACTIVITY(3, "trade", "交易空投活动");

    private Integer code;
    private String value;
    private String name;


    AirDropTypeEnum(Integer code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public static String getValueByCode(Integer code) {
        for (AirDropTypeEnum e : AirDropTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
