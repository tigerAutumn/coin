package com.hotcoin.coin.enums;

/**
 * Description:
 *      NEO系列主链枚举
 * Date: 2019/7/29 11:50
 * Created by luoyingxiong
 */
public enum NeoChainEnum {

    NEO(1,"NEO");

    private String name;
    private int code;

    NeoChainEnum(int code, String name) {
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
        for (NeoChainEnum chain : NeoChainEnum.values()) {
            if (chain.getName().equals(symbol)){
                return true;
            }
        }
        return false;
    }
}
