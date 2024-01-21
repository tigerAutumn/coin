package hotcoin.quote.constant;

/**
 * @ProjectName: service_user
 * @Package: hotcoin.quote.constant
 * @ClassName: KlineConstant
 * @Author: hf
 * @Description:
 * @Date: 2019/5/16 14:24
 * @Version: 1.0
 */
public interface KlineConstant {
    String KLINE_PERIOD = "period";
    String KLINE_NAME = "kline";
    String[] KLINE_PERIOD_ARR = {"1m", "5m", "15m", "30m", "1h", "1d", "1w", "1mo"};
    /**
     * 方向: -1 表示拉取历史数据,最大时间为from ,0 表示拉取从from到当前时间点的所有数据,当direction为0时,不需要传入limit
     */
    String KLINE_PULL_DIRECTION = "direction";
    /**
     * 起始时间
     */
    String KLINE_PULL_FROM = "from";

}
