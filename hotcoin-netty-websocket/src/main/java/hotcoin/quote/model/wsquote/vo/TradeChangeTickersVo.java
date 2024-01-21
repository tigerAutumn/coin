package hotcoin.quote.model.wsquote.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeChangeTickersVo
 * @Author: hf
 * @Description: 涨跌幅Vo
 * @Date: 2019/7/24 10:07
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeChangeTickersVo {
    /**
     * 币对id
     */
    private Integer tradeId;
    /**
     * 买方币种缩写,eg:BTC
     */
    private String buyShortName;
    /**
     * 买方币种全称,eg:bitcoin
     */
    private String buySymbol;
    /**
     * 保留小数点
     */
    private String digit;
    /**
     * 币种图片地址
     */
    private String imageUrl;
    /**
     * 折合人民币最新价
     */
    private String cny;
    /**
     * 涨跌幅
     */
    private String change;
    /**
     * 卖方币种简称:eg:BTC
     */
    private String sellShortName;
    /**
     * 卖方币种全称:eg:bitcoin
     */
    private String sellSymbol;
    /**
     * 最新价
     */
    private String last;
    /**
     * 所在分区:
     * GAVC(1, "对GAVC交易区", "GAVC"),
     * BTC(2, "对BTC交易区", "BTC"),
     * ETH(3, "对ETH交易区", "ETH"),
     * USDT(4, "对USDT交易区", "USDT"),
     * INNOVATION_AREA(5, "创新区", "创新区");
     */
    private Integer type;

}
