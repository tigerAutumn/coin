package hotcoin.quote.model.wsquote.vo;

import hotcoin.quote.model.wsquote.dto.TradeKlineHistoryDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.vo
 * @ClassName: TradeKlineHistoryVo
 * @Author: hf
 * @Description:
 * @Date: 2019/8/29 11:28
 * @Version: 1.0
 */
@Getter
@Setter
public class TradeKlineHistoryVo {
    /**
     * 连接的全局id,方便查找返回数据给哪个client
     */
    private String id;
    /**
     * 请求类型:kline
     */
    private String req;
    /**
     * 交易id
     */
    private int tradeId;
    /**
     * 币对:btc_usdt
     */
    private String symbol;
    /**
     * 13位
     */
    private String from;

    private String to;
    /**
     * limit 获取条数,最多允许 1000条
     */
    private Integer limit;

    public TradeKlineHistoryVo() {
    }

    public TradeKlineHistoryVo(TradeKlineHistoryDTO dto) {
        this.from = dto.getFrom();
        this.limit = dto.getLimit();
        this.req = dto.getReq();
        this.symbol = dto.getSymbol();
        this.id = dto.getId();
    }
}
