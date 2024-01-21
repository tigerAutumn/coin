package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
     * 存储币对分区信息: key(币对tradeId),value(分区名称如:btc_area)
     */
    private static ConcurrentHashMap<Integer, String> cachePairsAreaMap = new ConcurrentHashMap<>(256);
    /**
     * 缓存币对和交易对名称: key(tradeId),value(交易对名称如:btc_usdt)
     */
    private static ConcurrentHashMap<Integer, String> cacheTradeId4SymbolMap = new ConcurrentHashMap<>(256);

    public ConcurrentHashMap<Integer, String> getCachePairsAreaMap() {
        if (MapUtils.isEmpty(cachePairsAreaMap)) {
            cacheAllPairsAreaMap();
        }
        return cachePairsAreaMap;
    }

    public ConcurrentHashMap<Integer, String> getCacheTradeId4SymbolMap() {
        if (MapUtils.isEmpty(cacheTradeId4SymbolMap)) {
            cacheTradeId4Symbol();
        }
        return cacheTradeId4SymbolMap;
    }

    /**
     * 通过tradeId获取币对名称
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
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        Integer tradeId = null;
        if (CollectionUtils.isEmpty(coinsList)) {
            return tradeId;
        }
        for (SystemTradeType tradeTypeTemp : coinsList) {
            if (null == tradeTypeTemp) {
                continue;
            }
            Integer status = tradeTypeTemp.getStatus();
            if (!status.equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            String buyShortName = tradeTypeTemp.getBuyShortName();
            String sellShortName = tradeTypeTemp.getSellShortName();
            if ((sellShortName + WsConstant.SLASH + buyShortName).equals(symbol.toUpperCase())) {
                tradeId = tradeTypeTemp.getId();
                break;
            }
        }
        return tradeId;
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
        }
    }


    /**
     * 获取所有币对与其所在分区组成的map
     *
     * @return
     */
    public ConcurrentHashMap<Integer, String> getAllPairsAndAreaMap() {
        if (cachePairsAreaMap == null || cachePairsAreaMap.isEmpty()) {
            cacheAllPairsAreaMap();
        }
        return cachePairsAreaMap;
    }

    /**
     * 获取某个币对属于哪个分区
     *
     * @return
     */
    public String getAreaByTradeId(Integer tradeId) {
        if (tradeId == null) {
            return null;
        }
        if (cachePairsAreaMap == null || cachePairsAreaMap.isEmpty()) {
            cacheAllPairsAreaMap();
        }
        return cachePairsAreaMap.get(tradeId);
    }

    /**
     * 缓存所有币对与其所在分区组成的map
     *
     * @return
     */
    private void cacheAllPairsAreaMap() {
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        if (CollectionUtils.isEmpty(coinsList)) {
            return;
        }
        for (SystemTradeType tradeTypeTemp : coinsList) {
            if (tradeTypeTemp == null) {
                continue;
            }
            Integer status = tradeTypeTemp.getStatus();
            if (!status.equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            Integer tradeId = tradeTypeTemp.getId();
            Integer type = tradeTypeTemp.getType();
            if (type.equals(SystemTradeTypeEnum.BTC.getCode())) {
                cachePairsAreaMap.put(tradeId, TickersConstant.TICKERS_BTC_AREA);
            } else if (type.equals(SystemTradeTypeEnum.ETH.getCode())) {
                cachePairsAreaMap.put(tradeId, TickersConstant.TICKERS_ETH_AREA);
            } else if (type.equals(SystemTradeTypeEnum.GAVC.getCode())) {
                cachePairsAreaMap.put(tradeId, TickersConstant.TICKERS_GAVC_AREA);
            } else if (type.equals(SystemTradeTypeEnum.USDT.getCode())) {
                cachePairsAreaMap.put(tradeId, TickersConstant.TICKERS_USDT_AREA);
            } else if (type.equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
                cachePairsAreaMap.put(tradeId, TickersConstant.TICKERS_INNOVATE_AREA);
            }
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
}
