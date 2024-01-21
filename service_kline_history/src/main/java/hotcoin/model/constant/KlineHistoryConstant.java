package hotcoin.model.constant;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model.constant
 * @ClassName: KlineHistoryConstant
 * @Author: hf
 * @Description:
 * @Date: 2019/9/6 9:29
 * @Version: 1.0
 */
public interface KlineHistoryConstant {
    String KLINE_HIS_SLASH = "_";
    String KLINE_HIS_TRADE_ID = "tradeId";
    String KLINE_HIS_CREATE_TIME = "createTime";
    int KLINE_HIS_DESC = -1;
    int KLINE_HIS_ASC = 1;
    int KLINE_HIS_ZERO = 0;
    int KLINE_HIS_SECOND = 2;
    String KLINE_HIS_PERIOD = "period";
    int KLINE_HIS_DEFAULT_LIMIT = 300;
    int KLINE_HIS_MAX_LIMIT = 1000;

    int KLINE_HIS_MILLIS = 1000;
    /**
     * 时间相关
     */
    String KLINE_HIS_MIN = "min";
    String KLINE_HIS_HOUR = "hour";
    String KLINE_HIS_DAY = "day";
    String KLINE_HIS_WEEK = "week";
    String KLINE_HIS_MONTH = "month";
    String KLINE_HIS_YEAR = "year";

    String KLINE_HIS_PERIOD_1M = "1m";

    String KLINE_HIS_PERIOD_5M = "5m";

    String KLINE_HIS_PERIOD_15M = "15m";

    String KLINE_HIS_PERIOD_30M = "30m";

    String KLINE_HIS_PERIOD_1H = "1h";
    String KLINE_HIS_PERIOD_1D = "1d";
    String KLINE_HIS_PERIOD_1W = "1w";
    String KLINE_HIS_PERIOD_1MO = "1mo";

    /**
     * 综合表后缀
     */
    String KLINE_HIS_MIX = "mix";

    /**
     * 表中各个字段
     */

    String KLINE_HIS_HIGH = "high";
    String KLINE_HIS_OPEN = "open";
    String KLINE_HIS_LOW = "low";
    String KLINE_HIS_CLOSE = "close";
    String KLINE_HIS_VOLUME = "volume";
    String KLINE_HIS_CREATETIME = "createTime";
}
