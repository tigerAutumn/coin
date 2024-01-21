package hotcoin.quote.constant;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model
 * @ClassName: WsConstant
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 14:01
 * @Version: 1.0
 */
public interface WsConstant {
    int ConstantZero = 0;
    int ConstantOne = 1;
    int ConstantTwo = 2;
    String BUY = "buy";
    String SELL = "sell";
    String SLASH = "_";
    String LINE = "|";
    String COLON = ":";
    String DATA = "data";
    String STATUS = "status";
    String EXT_OBJECT = "extObject";
    String ID = "id";
    String TYPE = "type";
    /**
     * 交易对id
     */
    String TRADE_ID = "tradeId";
    /**
     *
     */
    String SYMBOL_SPOT = ".";

    /**
     * 用户订阅最大数
     */
    int TRADE_SUB_MAX_COUNT = 1500;

    /**
     * :
     */
    String SYMBOL_COLON = ":";
    /**
     * /
     */
    String SYMBOL_SLASH = "/";
    /**
     * 币对左边
     */
    String BASE_CURRENCY = "base-currency";
    /**
     * 币对右边
     */
    String QUOTE_CURRENCY = "quote-currency";
    /**
     * 币对,格式:BTC_USDT
     */
    String SYMBOL = "symbol";
    /**
     * 默认kline条数
     */
    int KLINE_DEFAULT_LIMIT = 300;
    /**
     * period
     */
    String MIN_KEY = "m";
    String HOUR_KER = "h";
    String DAY_KEY = "d";
    String WEEK_KEY = "w";
    String MONTH_KEY = "mo";
    /**
     * 接收前端数据订阅
     */
    String SUB = "sub";
    /**
     * 取消币对订阅
     */
    String UNSUB = "unsub";

    String TRADE_BUYSYMBOL = "buySymbol";
    String TRADE_SELLSYMBOL = "sellSymbol";
    /**
     * 图片地址
     */
    String TRADE_IMAGE = "image";

    /**
     * 最新价
     */
    String TRADE_LAST = "last";

    /**
     * 开盘价
     */
    String TRADE_OPEN = "open";
    /**
     * 成交量
     */
    String TRADE_VOL = "volume";
    /**
     * 最高价
     */
    String TRADE_HIGH = "high";
    /**
     * 最低价
     */
    String TRADE_LOW = "low";
    /**
     * 24小时变化率
     */
    String TRADE_CHANGE = "change";
    /**
     * 卖出币种名称 eg: BTC
     */
    String TRADE_SELL_SHORTNAME = "sellShortName";
    /**
     * 买入币种名称 eg:BTC
     */
    String TRADE_BUY_SHORTNAME = "buyShortName";
    /**
     *
     */
    String TRADE_CNY = "cny";
    /**
     * ws相关ping,pong相关
     */
    String TRADE_PING = "ping";
    String TRADE_PING_VALUE = "hotcoin";
    String TRADE_TICKERS = "tickers";
    String TRADE_UNSUB_ALL = "unsubAll";
    String TRADE_UNSUB_ALL_MSG = "unsub all success!";


    /**
     * 第一个:coinPair,第二个:period
     */
    String TRADE_KLINE_STR = "market.%s.kline.%s";
    String TRADE_DEPTH_STR = "market.%s.trade.depth";
    String TRADE_DETAIL_STR = "market.%s.trade.detail";
    /**
     * 分区行情数据
     */
    String TRADE_TICKERS_AREA_STR = "market.trade.%s.tickers";
    /**
     * 涨幅行情数据
     */
    String TRADE_TICKERS_UP_STR = "market.trade.up.tickers";
    /**
     * 跌幅行情数据
     */
    String TRADE_TICKERS_DOWN_STR = "market.trade.down.tickers";
    /**
     * 单个币对行情数据
     */
    String TRADE_SINGLE_TICKER_STR = "market.%s.single.ticker";
    /**
     * 精简字段数据
     */
    String TRADE_SIMPLE_TICKERS_STR = "market.%s.simple.tickers";

}
