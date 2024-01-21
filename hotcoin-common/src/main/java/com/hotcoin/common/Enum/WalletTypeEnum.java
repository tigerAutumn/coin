package com.hotcoin.common.Enum;

public enum WalletTypeEnum {
    LERVER_WALLET(1, "LEVER", "杠杆钱包"),
    COIN_WALLET(2, "COIN", "币币钱包");

    private Integer code;
    private String name;
    private String descr;

    private WalletTypeEnum(Integer code, String name, String descr) {
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

    public static WalletTypeEnum getWalletTypeEnum(Integer code) {
        for (WalletTypeEnum walletTypeEnum: values()) {
            if(walletTypeEnum.getCode().intValue() == code){
                return walletTypeEnum;
            }
        }
        return null;
    }

    public static WalletTypeEnum getWalletTypeEnumByName(String name) {
        for (WalletTypeEnum walletTypeEnum: values()) {
            if(walletTypeEnum.getName().equals(name)){
                return walletTypeEnum;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {

        for (WalletTypeEnum walletTypeEnum: values()) {
            if(walletTypeEnum.getCode().intValue() == code){
                return walletTypeEnum.getName();
            }
        }
        return null;
    }
}
