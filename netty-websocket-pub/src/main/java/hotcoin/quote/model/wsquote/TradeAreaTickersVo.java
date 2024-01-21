package hotcoin.quote.model.wsquote;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeAreaTickersVo
 * @Author: hf
 * @Description: 交易区数据vo
 * @Date: 2019/6/19 19:04
 * @Version: 1.0
 */

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
     * 交易id
     */
    private Integer tradeId;

    public TradeAreaTickersVo() {
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
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

    public String getCny() {
        return cny;
    }

    public void setCny(String cny) {
        this.cny = cny;
    }

    public String getLever() {
        return lever;
    }

    public void setLever(String lever) {
        this.lever = lever;
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

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }
}
