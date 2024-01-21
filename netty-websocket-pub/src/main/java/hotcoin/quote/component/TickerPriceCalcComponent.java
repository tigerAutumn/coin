package hotcoin.quote.component;

import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.component
 * @ClassName: TickerPriceCalcComponent
 * @Author: hf
 * @Description: 计算cny价格
 * @Date: 2019/7/24 15:07
 * @Version: 1.0
 */
@Component
public class TickerPriceCalcComponent {
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 获取交易对折合币对的tradeId
     *
     * @param tradeId
     * @return
     */
    private Integer getBenchmarkingTradeId(Integer tradeId, String buySymbol) {
        String area = redisComponent.getCachePairsAreaMap().get(tradeId);
        //BTC交易区
        if (TickersConstant.TICKERS_BTC_AREA.equals(area)) {
            return TickersConstant.TICKERS_BTC_GAVC_ID;
        }
        if (TickersConstant.TICKERS_ETH_AREA.equals(area)) {
            return TickersConstant.TICKERS_ETH_GAVC_ID;
        }
        if (TickersConstant.TICKERS_USDT_AREA.equals(area)) {
            return TickersConstant.TICKERS_USDT_GAVC_ID;
        }
        if (TickersConstant.TICKERS_GAVC_AREA.equals(area)) {
            if (buySymbol.equals(TickersConstant.TICKERS_GAVC)) {
                return TickersConstant.TICKERS_GAVC_COIN_ID;
            } else {
                String symbol = buySymbol + WsConstant.SLASH + TickersConstant.TICKERS_GAVC;
                return redisComponent.getTradeId4Symbol(symbol);
            }
        }
        return null;
    }

    /**
     * 获取cny值
     *
     * @param tradeId
     * @param last
     * @return
     */
    private String convert2CnyPrice(Integer tradeId, BigDecimal last) {
        if (tradeId.equals(TickersConstant.TICKERS_GAVC_COIN_ID)) {
            BigDecimal cny = MathUtils.toScaleNum(last, 2);
            return cny.toPlainString();
        } else {
            BigDecimal cnyPrice = redisComponent.getLastPrice(tradeId);
            //当前交易对最新价格
            BigDecimal money = MathUtils.mul(last, cnyPrice);
            BigDecimal calcCnyPrice = MathUtils.toScaleNum(money, 2);
            return calcCnyPrice.toPlainString();
        }
    }


    public String getCnyPrice(Integer tradeId, BigDecimal last, String buySymbol) {
        Integer id = getBenchmarkingTradeId(tradeId, buySymbol);
        if (id == null) {
            return null;
        }
        return convert2CnyPrice(tradeId, last);
    }

}
