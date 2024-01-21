package com.qkwl.web.utils;

import java.math.BigDecimal;

public class WebConstant {

    public static int BCAgentId = 0;

    /**
     * 资产图片上传路径
     */
    public static String QUALITYASSETS = "hk/upload/assets/";

    /**
     * 最小充值金额
     */
    public static final BigDecimal MINRECHARGECNY = new BigDecimal("100");
    /**
     * 最大充值金额
     */
    public static final BigDecimal MAXRECHARGECNY = new BigDecimal("1000000");


    /**
     * 明星币topic
     */
    public static final String TRADE_STAR_COIN_STR = "market.trade.star.coin";

    /**
     * 成交额榜 topic
     */
    public static final String TRADE_AMOUNT_RANK_STR = "market.trade.amount.rank";


    /**
     * 涨幅行情数据
     */
    public static final String TRADE_TICKERS_UP_STR = "market.trade.up.tickers";
    /**
     * 跌幅行情数据
     */
    public static final  String TRADE_TICKERS_DOWN_STR = "market.trade.down.tickers";

    /**
     * 创新区topic
     */
    public static final  String TRADE_TICKERS_INNOVATE_AREA_STR = "market.trade.innovate_area.tickers";
    /**
     * gavc区topic
     */
    public static final  String TRADE_TICKERS_GAVC_AREA_STR = "market.trade.gavc_area.tickers";
    /**
     * btc区topic
     */
    public static final  String TRADE_TICKERS_BTC_AREA_STR = "market.trade.btc_area.tickers";
    public static final  String TRADE_TICKERS_ETH_AREA_STR = "market.trade.eth_area.tickers";
    public static final  String TRADE_TICKERS_USDT_AREA_STR = "market.trade.usdt_area.tickers";
}
