package com.hotcoin.coin.enums;

/**
 * Description:
 *      EOS系列主链枚举
 * Date: 2019/7/18 17:11
 * Created by luoyingxiong
 */
public enum EosChainEnum {

    EOS(1,"EOS");

    private String name;
    private int code;

    EosChainEnum(int code, String name) {
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
        for (EosChainEnum chain : EosChainEnum.values()) {
            if (chain.getName().equals(symbol)){
                return true;
            }
        }
        return false;
    }
}
