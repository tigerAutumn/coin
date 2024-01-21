package hotcoin.quote.model.wsquote.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model
 * @ClassName: TradeDepthVo
 * @Author: hf
 * @Description: 交易深度返回对象
 * @Date: 2019/5/7 14:28
 * @Version: 1.0
 */
@Getter
@Setter
public class TradeDepthVo {
    /**
     * 买方深度
     * 数据结构: [["1","2"],["3","4"]]
     * 数据描述: arr[0]:价格  arr[1] : 数量
     */

    private List<List<String>> bids;
    /**
     * 卖方深度
     * 数据结构同上 数据描述同上
     */
    private List<List<String>> asks;
    /**
     * 最新价
     */
    private String last;
    /**
     * 开盘价
     */
    private String open;
    /**
     * 折合人民币价格
     */
    private String cny;


    public TradeDepthVo(List<List<String>> asksList, List<List<String>> bidsList) {
        this.asks = asksList;
        this.bids = bidsList;
    }

    public TradeDepthVo(List<List<String>> bids, List<List<String>> asks, String last, String open, String cny) {
        this.bids = bids;
        this.asks = asks;
        this.last = last;
        this.open = open;
        this.cny = cny;
    }

    public TradeDepthVo() {
    }
}
