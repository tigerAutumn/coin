package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.FPeriod;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.vo.TradeAmountRankVo;
import hotcoin.quote.model.wsquote.vo.TradeStarCoinVo;
import hotcoin.quote.utils.MathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.component
 * @ClassName: RedisComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/5/5 15:19
 * @Version: 1.0
 */
@Component
@Slf4j
public class RedisComponent {
    @Autowired
    private RedisHelper redisHelper;
    /**
     * 缓存币对和交易对名称: key(tradeId),value(交易对名称如:btc_usdt)
     */
    private static ConcurrentHashMap<Integer, String> cacheTradeId4SymbolMap = new ConcurrentHashMap<>(128);

    public ConcurrentHashMap<Integer, String> getCacheTradeId4SymbolMap() {
        if (MapUtils.isEmpty(cacheTradeId4SymbolMap)) {
            cacheTradeId4Symbol();
        }
        return cacheTradeId4SymbolMap;
    }

    /**
     * 存储key:BTC_USDT,value :(tradeId)
     */
    private static ConcurrentHashMap<String, Integer> cacheCoinPairsMap = new ConcurrentHashMap<>(128);

    public ConcurrentHashMap<String, Integer> getCacheCoinPairsMap() {
        if (MapUtils.isEmpty(cacheCoinPairsMap)) {
            cacheTradeId4Symbol();
        }
        return cacheCoinPairsMap;
    }

    /**
     * 通过tradeId获取币对名称
     *
     * @param tradeId
     * @return eg: BTC_USDT
     */
    public String getSymbol4CacheMap(Integer tradeId) {
        if (MapUtils.isEmpty(cacheTradeId4SymbolMap)) {
            cacheTradeId4Symbol();
        }
        String symbol = cacheTradeId4SymbolMap.get(tradeId);
        if (StringUtils.isEmpty(symbol)) {
            cacheTradeId4Symbol();
        }
        return cacheTradeId4SymbolMap.get(tradeId);
    }

    /**
     * 获取行情信息通过tradeId
     *
     * @param tradeId
     */
    public TickerData getTickerDataByTradeId(Integer tradeId) {
        String tickerStr = redisHelper.getRedisData(RedisConstant.TICKERE_KEY + tradeId);
        TickerData tickerData = null;
        if (!StringUtils.isEmpty(tickerStr)) {
            tickerData = JSON.parseObject(tickerStr, TickerData.class);
        }
        return tickerData;
    }

    /**
     * 获取币对信息
     * {@link SystemTradeStatusEnum}
     *
     * @param tradeId
     * @return
     */
    public SystemTradeType getCoinPairInfo(Integer tradeId) {
        SystemTradeType systemTradeType = redisHelper.getTradeType(tradeId, WsConstant.ConstantZero);
        // 查询不到该币对或者 币对状态非法时
        if (systemTradeType == null || !SystemTradeStatusEnum.NORMAL.getCode().equals(systemTradeType.getStatus())) {
            return null;
        } else {
            return systemTradeType;
        }
    }

    /**
     * 获取币对信息
     * {@link SystemTradeStatusEnum}
     *
     * @param tradeId
     * @return 返回币对简称eg: BTC_USDT
     */
    public String getCoinPairSymbol(Integer tradeId) {
        SystemTradeType systemTradeType = getCoinPairInfo(tradeId);
        // 查询不到该币对或者 币对状态非法时
        if (systemTradeType == null) {
            return null;
        } else {
            return (systemTradeType.getSellShortName() + WsConstant.SLASH + systemTradeType.getBuyShortName()).toUpperCase();
        }
    }

    /**
     * 根据币对获取交易id
     * symbol:btc_usdt
     */
    public Integer getTradeId4Symbol(String symbol) {
        if (cacheCoinPairsMap == null) {
            cacheTradeId4Symbol();
        }
        Integer tradeId = cacheCoinPairsMap.get(symbol);
        if (tradeId == null) {
            cacheTradeId4Symbol();
        }
        return cacheCoinPairsMap.get(symbol);
    }

    public void cacheTradeId4Symbol() {
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        if (CollectionUtils.isEmpty(coinsList)) {
            return;
        }
        for (SystemTradeType tradeTypeTemp : coinsList) {
            if (null == tradeTypeTemp) {
                continue;
            }
            Integer status = tradeTypeTemp.getStatus();
            if (!status.equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            Integer tradeId = tradeTypeTemp.getId();
            String buyShortName = tradeTypeTemp.getBuyShortName();
            String sellShortName = tradeTypeTemp.getSellShortName();
            String value = sellShortName + WsConstant.SLASH + buyShortName;
            cacheTradeId4SymbolMap.put(tradeId, value);
            cacheCoinPairsMap.put(value, tradeId);
        }
    }

    /**
     * 获取币对的最新价
     *
     * @param tradeId
     * @return
     */
    public BigDecimal getLastPrice(Integer tradeId) {
        return redisHelper.getLastPrice(tradeId);
    }

    /**
     * 获取本期明星币
     *
     * @return
     */
    public List<TradeStarCoinVo> getStarCoinsList() {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WsConstant.ConstantZero);
        List<TradeStarCoinVo> resultList = new ArrayList<>(list.size());
        for (SystemTradeType item : list) {
            if (null == item || !item.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            String last = getLastPrice(item.getId()).toPlainString();
            TradeStarCoinVo tv = new TradeStarCoinVo(item, last);

            FPeriod fPeriod = redisHelper.getLastKline(item.getId(), 10080);
            if (fPeriod == null || fPeriod.getFkai().compareTo(BigDecimal.ZERO) == 0) {
                tv.setChange(WsConstant.TRADE_CONSTANT_ZERO);
            } else {
                tv.setChange(fPeriod.getFshou().subtract(fPeriod.getFkai()).divide(fPeriod.getFkai(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).toPlainString());
            }
            resultList.add(tv);
        }
        return resultList.stream().sorted(Comparator.comparing(TradeStarCoinVo::getChange, Comparator.comparing(Double::valueOf)).reversed()).limit(10).collect(Collectors.toList());
    }

    /**
     * 获取成交额排行榜
     *
     * @return
     */
    public List<TradeAmountRankVo> getAmountRankList() {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WsConstant.ConstantZero);
        List<TradeAmountRankVo> resultList = new ArrayList<>(list.size());
        for (SystemTradeType item : list) {
            if (null == item || !item.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            TickerData data = getTickerDataByTradeId(item.getId());
            String cnyPrice = convert2CnyPrice(item.getId(), data.getVol());
            resultList.add(new TradeAmountRankVo(item, cnyPrice, data.getVol()));
        }
        return resultList.stream().sorted(Comparator.comparing(TradeAmountRankVo::getTotalAmount, Comparator.comparing(BigDecimal::new)).reversed()).limit(10).collect(Collectors.toList());
    }

    /**
     * 获取交易对折合币对的tradeId
     *
     * @param tradeId
     * @return
     */
    private Integer getBenchmarkingTradeId(Integer tradeId) {
        SystemTradeType systemTradeType = getCoinPairInfo(tradeId);
        if (systemTradeType == null) {
            return null;
        }
        String buySymbol = systemTradeType.getBuyShortName();
        if (buySymbol.equals(TickersConstant.TICKERS_GAVC)) {
            return TickersConstant.TICKERS_GAVC_COIN_ID;
        } else {
            String symbol = buySymbol + WsConstant.SLASH + TickersConstant.TICKERS_GAVC;
            return getTradeId4Symbol(symbol);
        }

    }


    /**
     * 获取cny值
     *
     * @param tradeId
     * @param newPrice :最新价,为未转换为cny的价格 如 btc_usdt价格为 9000 此时newPrice价格则为 9000,
     * @return
     */
    private String convert2CnyPrice(Integer tradeId, BigDecimal newPrice) {
        if (tradeId.equals(TickersConstant.TICKERS_GAVC_COIN_ID)) {
            BigDecimal cny = MathUtils.toScaleNum(newPrice, 2);
            return cny.toPlainString();
        } else {
            BigDecimal cnyPrice = getLastPrice(tradeId);
            //当前交易对最新价格
            BigDecimal money = MathUtils.mul(newPrice, cnyPrice);
            BigDecimal calcCnyPrice = MathUtils.toScaleNum(money, 2);
            return calcCnyPrice.toPlainString();
        }
    }

    /**
     * 根据最新价获取cny价格
     *
     * @param tradeId
     * @param last
     * @return
     */
    public String getCnyPrice(Integer tradeId, BigDecimal last) {
        Integer matchTradeId = getBenchmarkingTradeId(tradeId);
        if (matchTradeId == null) {
            return WsConstant.TRADE_CONSTANT_ZERO;
        }
        return convert2CnyPrice(matchTradeId, last);
    }


    /**
     * 根据tradeId获取折算成人民币的最新价
     *
     * @param tradeId
     * @return
     */
    public String getLast2CnyPrice(Integer tradeId) {
        TickerData tickerData = getTickerDataByTradeId(tradeId);
        if (tickerData == null) {
            return WsConstant.TRADE_CONSTANT_ZERO;
        }
        return getCnyPrice(tradeId, tickerData.getLast());
    }
}
