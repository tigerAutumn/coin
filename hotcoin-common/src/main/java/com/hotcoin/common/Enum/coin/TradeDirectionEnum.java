package com.hotcoin.common.Enum.coin;

public enum TradeDirectionEnum {

    IN(1,"in","入金"),
    OUT(0,"out","出金");

    private int code;
    private String name;
    private String descr;

    TradeDirectionEnum(int code, String name, String descr) {
        this.code = code;
        this.name = name;
        this.descr = descr;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public static TradeDirectionEnum getTradeDirectionEnum(int code) {
        for (TradeDirectionEnum tradeDirectionEnum: values()) {
            if(tradeDirectionEnum.getCode() == code){
                return tradeDirectionEnum;
            }
        }
        return null;
    }


    public static TradeDirectionEnum getTradeDirectionEnumByName(String name) {
        for (TradeDirectionEnum tradeDirectionEnum: values()) {
            if(tradeDirectionEnum.getName().equals(name)){
                return tradeDirectionEnum;
            }
        }
        return null;
    }
}
