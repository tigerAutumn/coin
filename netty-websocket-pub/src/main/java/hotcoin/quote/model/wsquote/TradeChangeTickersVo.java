package hotcoin.quote.model.wsquote;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeChangeTickersVo
 * @Author: hf
 * @Description: 涨跌幅Vo
 * @Date: 2019/7/24 10:07
 * @Version: 1.0
 */
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
     * 最新价
     */
    private String last;
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
     *     所在分区:
     *     GAVC(1, "对GAVC交易区", "GAVC"),
     *     BTC(2, "对BTC交易区", "BTC"),
     *     ETH(3, "对ETH交易区", "ETH"),
     *     USDT(4, "对USDT交易区", "USDT"),
     *     INNOVATION_AREA(5, "创新区", "创新区");
     */
    private Integer type;

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getBuyShortName() {
        return buyShortName;
    }

    public void setBuyShortName(String buyShortName) {
        this.buyShortName = buyShortName;
    }

    public String getBuySymbol() {
        return buySymbol;
    }

    public void setBuySymbol(String buySymbol) {
        this.buySymbol = buySymbol;
    }

    public String getDigit() {
        return digit;
    }

    public void setDigit(String digit) {
        this.digit = digit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getSellShortName() {
        return sellShortName;
    }

    public void setSellShortName(String sellShortName) {
        this.sellShortName = sellShortName;
    }

    public String getSellSymbol() {
        return sellSymbol;
    }

    public void setSellSymbol(String sellSymbol) {
        this.sellSymbol = sellSymbol;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
