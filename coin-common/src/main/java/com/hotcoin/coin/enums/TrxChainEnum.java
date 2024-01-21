package com.hotcoin.coin.enums;

/**
 * Description:
 *  波场 主链
 *
 * Date: 2019/10/16 9:55
 * Created by luoyingxiong
 */
public enum TrxChainEnum {
    TRX(1,"TRX");

    private String name;
    private int code;

    TrxChainEnum(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static boolean inValues(String symbol){
        for (EthChainEnum chain :EthChainEnum.values()) {
            if (chain.getName().equals(symbol)){
                return true;
            }
        }
        return false;
    }
}
