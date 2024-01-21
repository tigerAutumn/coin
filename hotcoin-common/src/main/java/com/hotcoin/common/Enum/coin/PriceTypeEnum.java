package com.hotcoin.common.Enum.coin;

public enum PriceTypeEnum {
    LIMIT_PRICE(1, "限价"),

    MARKET_PRICE(2, "市价");

    private int code;

    private String descr;

    private PriceTypeEnum(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public static PriceTypeEnum getPriceTypeEnum(int code) {
        for (PriceTypeEnum priceTypeEnum : values()) {
            if (priceTypeEnum.getCode() == code) {
                return priceTypeEnum;
            }
        }
        return null;
    }
}
