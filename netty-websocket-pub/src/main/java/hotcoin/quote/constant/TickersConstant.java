package hotcoin.quote.constant;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.constant
 * @ClassName: TickersConstant
 * @Author: hf
 * @Description: 涨跌幅相关常量
 * @Date: 2019/7/17 9:37
 * @Version: 1.0
 */
public interface TickersConstant {
    /**
     * 涨幅key
     */
    String TICKERS_UP_KEY = "up";
    /**
     * 跌幅key
     */
    String TICKERS_DOWN_KEY = "down";
    /**
     * 涨跌幅订阅条数(控制推送到前端的条数)
     */
    int TICKERS_SUB_NUM = 10;

    /**
     * 分区  1:GSET交易区, 2: BTC交易区,3:ETH交易区,4:USDT交易区,5:创新区(INNOVATE_MAP)
     */
    String[] TICKERS_AREA_NAME = {"gavc_area", "btc_area", "eth_area", "usdt_area", "innovate_area"};
    /**
     * 1:GSET交易区
     */
    String TICKERS_GAVC_AREA = "GAVC_AREA";
    /**
     * 2: BTC交易区
     */
    String TICKERS_BTC_AREA = "BTC_AREA";
    /**
     * 3:ETH交易区
     */
    String TICKERS_ETH_AREA = "ETH_AREA";
    /**
     * 4:USDT交易区
     */
    String TICKERS_USDT_AREA = "USDT_AREA";
    /**
     * 5:创新区(INNOVATE_MAP)
     */
    String TICKERS_INNOVATE_AREA = "INNOVATE_AREA";

    /**
     * 币种对标GAVC的tradeId(GAVC当做cny),计算cny价格用
     */
     int TICKERS_BTC_GAVC_ID = 8;

     int TICKERS_GAVC_COIN_ID = 9;

     int TICKERS_ETH_GAVC_ID = 11;

     int TICKERS_USDT_GAVC_ID = 57;

    String TICKERS_GAVC = "GAVC";

}
