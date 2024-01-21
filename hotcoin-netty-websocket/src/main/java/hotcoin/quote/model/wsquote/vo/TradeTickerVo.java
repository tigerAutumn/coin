package hotcoin.quote.model.wsquote.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeTickerVo
 * @Author: hf
 * @Description: 盘口数据推送
 * @Date: 2019/7/25 16:11
 * @Version: 1.0
 */
@Getter
@Setter
public class TradeTickerVo {
    /**
     * 卖方币种简称:eg:BTC
     */
    private String sellShortName;
    /**
     * 卖方币种全称:eg:bitcoin
     */
    private String sellSymbol;
    /**
     * 买方币种简称:eg:ETH
     */
    private String buyShortName;
    /**
     * 买方币种全称:eg:ethereum
     */
    private String buySymbol;
    /**
     * 最高价
     */
    private String high;
    /**
     * 开盘价
     */
    private String open;
    /**
     * 最低价
     */
    private String low;
    /**
     * 收盘价
     */
    private String close;
    /**
     * 交易量
     */
    private String volume;
    /**
     * 涨跌幅
     */
    private String change;
    /**
     * 折合人民币的最新价
     */
    private String cny;
    /**
     * 币对id
     */
    private Integer tradeId;

    /**
     * 最新价(原始价格)
     */
    private String last;

    private String imageUrl;
    /**
     * 保留小数点
     */
    private String digit;

    public TradeTickerVo() {
    }
}
