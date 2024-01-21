package hotcoin.quote.model.wsquote;

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
public class TradeDepthVo {
    /**
     * 买方深度
     * 数据结构: [["1","2"],["3","4"]]
     * 数据描述: arr[0] :数量  arr[1] : 价格
     */

    private List<List<String>> bids;
    /**
     * 卖方深度
     * 数据结构同上 数据描述同上
     */
    private List<List<String>> asks;


    public List<List<String>> getBids() {
        return bids;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }

    public List<List<String>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }



    public TradeDepthVo(List<List<String>> asksList, List<List<String>> bidsList) {
        this.asks = asksList;
        this.bids = bidsList;
    }

    public TradeDepthVo() {
    }
}
