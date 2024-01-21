package hotcoin.quote.model.wsquote;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeSimpleTickersVo
 * @Author: hf
 * @Description:
 * @Date: 2019/6/19 19:55
 * @Version: 1.0
 */
public class TradeSimpleTickersVo {
    private int tradeId;
    private String sellShortName;
    private String buyShortName;
    private String last;
    private String change;
    /**
     * 保留字段,汇率
     */
    private double fxRate;

    public int getTradeId() {
        return tradeId;
    }

    public void setTradeId(int tradeId) {
        this.tradeId = tradeId;
    }

    public String getSellShortName() {
        return sellShortName;
    }

    public void setSellShortName(String sellShortName) {
        this.sellShortName = sellShortName;
    }

    public String getBuyShortName() {
        return buyShortName;
    }

    public void setBuyShortName(String buyShortName) {
        this.buyShortName = buyShortName;
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

    public double getFxRate() {
        return fxRate;
    }

    public void setFxRate(double fxRate) {
        this.fxRate = fxRate;
    }

    public TradeSimpleTickersVo() {
    }

    public TradeSimpleTickersVo(TradeAreaTickersVo tickersVo) {
        this.tradeId = tickersVo.getTradeId();
        this.sellShortName = tickersVo.getSellShortName();
        this.buyShortName = tickersVo.getBuyShortName();
        this.last = tickersVo.getLast();
        this.change = tickersVo.getChange();
    }
}
