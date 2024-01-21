package com.qkwl.web.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.vo
 * @ClassName: TradeRankCoinReq
 * @Author: hf
 * @Description: 排行榜类结构
 * @Date: 2019/11/5 11:00
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeRankCoinReq {
    /**
     * 交易id
     */
    private Integer tradeId;

    /**
     * 最新价
     */
    private String cny;

    /**
     * 币种简称:如(btc_eth) 则buyShortSymbol为eth
     */
    private String buyShortName;

    /**
     * 买方币种全称:eg:bitcoin
     */
    private String buySymbol;

    /**
     * 卖方币种简称:eg:BTC
     */
    private String sellShortName;
    /**
     * 卖方币种全称:eg:bitcoin
     */
    private String sellSymbol;

    /**
     * 变化率(涨跌幅)(负数表示跌,正数表示涨)
     */
    private String change;

    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 精度
     */
    private String digit;
    /**
     * 是否开盘0未开盘,1已开盘
     */
    private String isOpen;
    /**
     * 原始价
     */
    private String last;
    /**
     * 成交额
     */
    private String totalAmount;

    /**
     * 成交量
     */
    private String volume;


    public TradeRankCoinReq() {
    }
}
