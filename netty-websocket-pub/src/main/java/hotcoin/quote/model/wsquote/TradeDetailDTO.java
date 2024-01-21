package hotcoin.quote.model.wsquote;

import hotcoin.quote.constant.WsConstant;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote
 * @ClassName: TradeDetailDTO
 * @Author: hf
 * @Description:
 * @Date: 2019/8/8 15:30
 * @Version: 1.0
 */
public class TradeDetailDTO {
    /**
     * 交易id
     */
    private Integer tradeId;
    /**
     * 交易数量
     */
    private String amount;
    /**
     * 交易时间 13位
     */
    private String ts;
    /**
     * 交易价格
     */
    private String price;
    /**
     * {@link WsConstant}  sell or buy
     * 买卖类型
     */
    private String direction;

    public TradeDetailDTO() {
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public void setTradeId(Integer tradeId) {
        this.tradeId = tradeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
