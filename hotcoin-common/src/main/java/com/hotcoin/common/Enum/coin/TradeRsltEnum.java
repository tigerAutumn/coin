package com.hotcoin.common.Enum.coin;

public enum TradeRsltEnum {
    INIT(10, "I", "初始化"),
    FAIELD(40, "F", "交易失败"),
    SUCCESS(80, "S", "交易成功"),
    EXCEPTION(90, "E", "交易异常或者处理中"),
    INVILD(98, "I", "无效交易(取消交易)"),
    NO_EXIST(99,"N","交易不存在");
	private int code;
    private String name;
    private String descr;

    private TradeRsltEnum(int code, String name, String descr) {
        this.code = code;
        this.name = name;
        this.descr = descr;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public static TradeRsltEnum getTradeRsltEnum(int code) {
        for (TradeRsltEnum tradeRsltEnum : values()) {
            if (tradeRsltEnum.getCode() == code) {
                return tradeRsltEnum;
            }
        }
        return null;
    }
}
