package com.qkwl.common.dto.Enum;

/**
 * 
 * @author huangjinfeng
 */
public enum UserCoinWalletEnum {

   ID("id"),
   UID("uid"),
   COINID("coinId"),
   TOTAL("total"),
   FROZEN("frozen"),
   BORROW("borrow"),
   ICO("ico"),
   GMTCREATE("gmtCreate"),
   GMTMODIFIED("gmtModified"),
   VERSION("version"),
   DEPOSITFROZEN("depositFrozen"),
   DEPOSITFROZENTOTAL("depositFrozenTotal");

    private String code;

    private UserCoinWalletEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
