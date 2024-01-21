package com.hotcoin.coin.enums;

/**
 * Description:
 *      BTC系列主链枚举
 * Date: 2019/7/18 17:11
 * Created by luoyingxiong
 */
public enum BtcChainEnum {

    BTC(1,"BTC"),
    LTC(2,"LTC"),
    DOGE(3,"DOGE"),
    QTUM(4,"QTUM"),
    DASH(5,"DASH"),
    VAS(6,"VAS");
//    BCH(5,"BCH");

    private String name;
    private int code;

    BtcChainEnum(int code, String name) {
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
        for (BtcChainEnum chain :BtcChainEnum.values()) {
            if (chain.getName().equals(symbol)){
                return true;
            }
        }
        return false;
    }
}
