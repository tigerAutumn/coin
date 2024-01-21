package com.hotcoin.coin.enums;

/**
 * Description:
 *      ETH系列主链枚举
 * Date: 2019/7/18 17:11
 * Created by luoyingxiong
 */
public enum EthChainEnum {

    ETH(1,"ETH"),
//    ETC(2,"ETC");
//    MOAC(3,"MOAC"),
//    FOD(4,"FOD"),
//    VCC(5,"VCC");
    CET(6,"CET");

    private String name;
    private int code;

    EthChainEnum(int code, String name) {
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
