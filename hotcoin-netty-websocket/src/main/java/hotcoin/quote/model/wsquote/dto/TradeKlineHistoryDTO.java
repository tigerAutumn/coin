package hotcoin.quote.model.wsquote.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.dto
 * @ClassName: TradeKlineHistoryDTO
 * @Author: hf
 * @Description:
 * @Date: 2019/8/29 11:40
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeKlineHistoryDTO {
    private String id;
    /**
     * 请求类型:kline
     */
    private String req;
    /**
     * 币对:btc_usdt
     */
    private String symbol;
    /**
     * 交易对id
     */
    private Integer tradeId;
    /**
     * 13位
     */
    private String from;

    private String direction;
    /**
     * limit 获取条数,最多允许 1000条
     */
    private Integer limit;

    public TradeKlineHistoryDTO() {
    }

    @Override
    public String toString() {
        return "TradeKlineHistoryDTO{" +
                "id='" + id + '\'' +
                ", req='" + req + '\'' +
                ", symbol='" + symbol + '\'' +
                ", tradeId=" + tradeId +
                ", from='" + from + '\'' +
                ", direction='" + direction + '\'' +
                ", limit=" + limit +
                '}';
    }
}
