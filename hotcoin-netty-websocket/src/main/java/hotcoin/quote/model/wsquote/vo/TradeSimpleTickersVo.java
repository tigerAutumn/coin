package hotcoin.quote.model.wsquote.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeSimpleTickersVo
 * @Author: hf
 * @Description:
 * @Date: 2019/6/19 19:55
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeSimpleTickersVo {
    private int tradeId;
    private String sellShortName;
    private String buyShortName;
    private String cny;
    private String change;
    /**
     * 保留字段,汇率
     */
    private double fxRate;
    public TradeSimpleTickersVo() {
    }

    public TradeSimpleTickersVo(TradeAreaTickersVo tickersVo) {
        this.tradeId = tickersVo.getTradeId();
        this.sellShortName = tickersVo.getSellShortName();
        this.buyShortName = tickersVo.getBuyShortName();
        this.cny = tickersVo.getCny();
        this.change = tickersVo.getChange();
    }
}
