package com.hotcoin.coin.enums;

/**
 * Description:
 *  华特东方 主链
 *     山寨的COSMOS-ATOM 但是有差异
 * Date: 2019/10/10 9:55
 * Created by luoyingxiong
 */
public enum  HtdfChainEnum {
    HTDF(1,"HTDF");

    private String name;
    private int code;

    HtdfChainEnum(int code, String name) {
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
