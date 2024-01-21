package com.hotcoin.webchat.Enum;

public enum ClientTypeEnum {
    PLATFORM("platform","1"),
    ADROID("android","2"),
    IOS("ios","3"),
    H5("H5","4"),
    OTC_SYS("OTC_SYS","1001");

    private String type;

    private String value;

    ClientTypeEnum(String type, String value){
        this.type = type;
        this.value =value;
    }

    public String getType() {
        return type;
    }


    public String getValue() {
        return value;
    }


    public static ClientTypeEnum fromType(String type) {
        for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values() ) {
            if (clientTypeEnum.getType().equalsIgnoreCase(type)) {
                return clientTypeEnum;
            }
        }
        return null;
    }


    public static ClientTypeEnum fromValue(String value) {
        for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values() ) {
            if (clientTypeEnum.getValue().equalsIgnoreCase(value)) {
                return clientTypeEnum;
            }
        }
        return null;
    }
}
