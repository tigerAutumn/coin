package hotcoin.quote.model.wsquote.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeAreaTickersVo
 * @Author: hf
 * @Description: 交易区数据vo
 * @Date: 2019/6/19 19:04
 * @Version: 1.0
 */

@Getter
@Setter
public class TradeAreaTickersVo {
    /**
     * 买1价
     */
    private String buy;
    /**
     * 币种简称:如(btc_eth) 则buyShortSymbol为eth
     */
    private String buyShortName;
    /**
     * 老字段,该字段意思未知,暂时和buySymbol一样
     */
    private String buySymbol;
    /**
     * 等价于人民币价格
     */
    private String cny;
    /**
     * 杠杆倍数
     */
    private String lever;
    /**
     * 最新价(未换算之前的价格)
     */
    private String last;
    /**
     * 变化率(涨跌幅)(负数表示跌,正数表示涨)
     */
    private String change;
    /**
     * 卖1价
     */
    private String sell;
    /**
     * 币种简称:如(BTC_ETH) sellShortSymbol为BTC
     */
    private String sellShortName;
    /**
     * 老字段,该字段意思未知,暂时和sellSymbol一样
     */
    private String sellSymbol;
    /**
     * 成交量
     */
    private String volume;
    /**
     * 交易id
     */
    private Integer tradeId;
    /**
     * 币种图片地址
     */
    private String imageUrl;
    /**
     * 保留小数点
     */
    private String digit;

    public TradeAreaTickersVo() {
    }

}
