package hotcoin.quote.constant;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model
 * @ClassName: SubHeaderEnum
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 10:32
 * @Version: 1.0
 */
public enum SubHeaderEnum {
    Depth("深度", "depth"),
    TradeDetail("交易详情", "detail"),
    Kline("K线", "kline");

    /**
     * 主题中文名称
     */
    private String chName;
    /**
     * 主题值
     */
    private String value;

    private SubHeaderEnum(String chName, String value) {
        this.chName = chName;
        this.value = value;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
