package com.hotcoin.common.Enum;

public enum SystemCodeEnum {
    HOTCOIN_COIN(100, "hotcoin_coin", "币币交易系统"),
    HTOCOIN_LEVEL(101, "hotcoin_level", "杠杆交易系统"),
    GS(901,"gs","港盛系统"),
    RISK(801, "risk", "风控系统");

    private Integer code;
    private String name;
    private String descr;

    private SystemCodeEnum(Integer code, String name, String descr) {
        this.code = code;
        this.name = name;
        this.descr = descr;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public static SystemCodeEnum getSystemCodeEnum(Integer code) {
        for (SystemCodeEnum systemCodeEnum: values()) {
            if(systemCodeEnum.getCode().equals(code)){
                return systemCodeEnum;
            }
        }
        return null;
    }
}
