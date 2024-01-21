package hotcoin.quote.model.wsquote;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.vo
 * @ClassName: TradeKlineVo
 * @Author: hf
 * @Description:
 * @Date: 2019/8/14 17:03
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeKlineVo {
    /**
     * //[时间,开盘价,最高价,最低价,收盘价,成交量]
     */
    private List<List<String>> data;
    /**
     * 粒度 1m,5m,15m,1h,1d,1w,1mo
     */
    private String period;
    /**
     * 交易id
     */
    private Integer tradeId;

    public TradeKlineVo() {
    }
}
