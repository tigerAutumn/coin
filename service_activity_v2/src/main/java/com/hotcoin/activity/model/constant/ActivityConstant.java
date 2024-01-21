package com.hotcoin.activity.model.constant;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.constant
 * @ClassName: ActivityConstant
 * @Author: hf
 * @Description:
 * @Date: 2019/6/13 19:32
 * @Version: 1.0
 */
public interface ActivityConstant {
    String USERID = "userId";
    String COINNAME = "coinName";
    String AMOUNT = "amount";
    String EXTOBJECT = "extObject";
    String SELLSHORTNAME = "sellShortName";
    String BUYSHORTNAME = "buyShortName";
    String TRADKEY = "TRADE_";
    String COINKEY = "COIN_";
    String UNDERLINE = "_";
    String SHORTNAME = "shortName";
    String STATUS = "status";
    /**
     * 币币充值
     */
    String RECHARGE_TYPE_COIN = "coin";
    /**
     * 法币充值(C2C)
     */
    String RECHARGE_TYPE_LEGAL_MONEY = "legalMoney";
}
