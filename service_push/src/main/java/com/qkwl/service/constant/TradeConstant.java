package com.qkwl.service.constant;

/**
 * @ProjectName: service_push
 * @Package: com.qkwl.service.constant
 * @ClassName: TradeConstant
 * @Author: hf
 * @Description: 行情交易相关常量
 * @Date: 2019/5/17 9:58
 * @Version: 1.0
 */
public interface TradeConstant {
    String TRADE_EXT_OBJECT = "extObject";
    String TRADE_DIGIT = "digit";
    /**
     * 保留位数
     */
    String TRADE_DECIMAL_DIGIT = "2#4";
    /**
     * #
     */
    String TRADE_HASH = "#";
    /**
     * 卖盘深度
     */
    String TRADE_ASKS = "asks";
    /**
     * 买盘深度
     */
    String TRADE_BIDS = "bids";

    /**
     * 最新价
     */
    String TRADE_LAST = "last";
    /**
     * 开盘价(老接口定义)
     */
    String TRADE_KAI = "kai";
    /**
     * 开盘价
     */
    String TRADE_OPEN = "open";
    /**
     * 成交量
     */
    String TRADE_VOL = "vol";
    /**
     * 最高价
     */
    String TRADE_HIGH = "high";
    /**
     * 最低价
     */
    String TRADE_LOW = "low";
    /**
     * 卖出币种名称 eg: BTC
     */
    String TRADE_SELLSHORTNAME = "sellShortName";
    /**
     * 买入币种名称 eg:BTC
     */
    String TRADE_BUYSHORTNAME = "buyShortName";
    /**
     * 卖出币种logo
     */
    String TRADE_SELLAPPLOGO = "sellAppLogo";
    /**
     * 交易币对id,通过该字段可以从redis获取 币种名称(sellShortName etc)
     */
    String TRADE_TRADEID = "tradeId";

    /**
     * 买
     */
    String TRADE_BUY = "buy";
    /**
     * 卖
     */
    String TRADE_SELL = "sell";
    /**
     * 交易id
     */
    String TRADE_ID = "tradeId";

    /**
     * 交易详情key(给kafka消费方使用)
     */
    String TRADE_DETAIL = "tradeDetail";

    /**
     * ws数据对象(给kafka消费者使用)
     */
    String TRADE_DATA = "data";

    /**
     * 粒度(给kafka消费者使用)1,5,30 以分钟为单位
     */
    String TRADE_PERIOD = "period";

    int PERIOD_MIN = 30;
    int PERIOD_HOUR = 60;
    int PERIOD_DAY = 24 * 60;
    int PERIOD_WEEK = 7 * 24 * 60;
}
