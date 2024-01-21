package hotcoin.quote.model.wsquote;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeTickersDTO
 * @Author: hf
 * @Description: 网络传输数据对象
 * @Date: 2019/7/24 10:45
 * @Version: 1.0
 */
@Getter
@Setter
public class TradeTickersDTO {

    /**
     * 交易id
     */
    private Integer tradeId;
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
     * 最新价
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
     * 保留小数点
     */
    private String digit;
    /**
     * 币种图片地址
     */
    private String imageUrl;

    /**
     * 时间戳
     */
    private Long ts;

    /**
     * 所在分区:
     * GAVC(1, "对GAVC交易区", "GAVC"),
     * BTC(2, "对BTC交易区", "BTC"),
     * ETH(3, "对ETH交易区", "ETH"),
     * USDT(4, "对USDT交易区", "USDT"),
     * INNOVATION_AREA(5, "创新区", "创新区");
     */
    private Integer type;

    @Override
    public String toString() {
        return "TradeTickersDTO{" +
                "tradeId=" + tradeId +
                ", buy='" + buy + '\'' +
                ", buyShortName='" + buyShortName + '\'' +
                ", buySymbol='" + buySymbol + '\'' +
                ", cny='" + cny + '\'' +
                ", lever='" + lever + '\'' +
                ", last='" + last + '\'' +
                ", change='" + change + '\'' +
                ", sell='" + sell + '\'' +
                ", sellShortName='" + sellShortName + '\'' +
                ", sellSymbol='" + sellSymbol + '\'' +
                ", volume='" + volume + '\'' +
                ", digit='" + digit + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                '}';
    }
}
