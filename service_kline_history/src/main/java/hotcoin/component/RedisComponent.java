package hotcoin.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import hotcoin.model.constant.KlineHistoryConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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
     * 存储key:btc_usdt,value :(tradeId)
     */
    private static ConcurrentHashMap<String, Integer> cacheCoinPairsMap = new ConcurrentHashMap<>(128);

    public List<?> getLastKlineData(Integer tradeId, String period) {
        List<String> result = new ArrayList<>();
        int step = getStep(period);
        String klineStr = redisHelper.getRedisData(RedisConstant.LASTKLINE_KEY + tradeId + "_" + step);
        if (org.apache.commons.lang3.StringUtils.isBlank(klineStr)) {
            return result;
        }
        JSONArray array = JSON.parseArray(klineStr);
        if (array != null && !array.isEmpty()) {
            JSONArray tempArray = array.getJSONArray(0);
            String ts = tempArray.get(0).toString();
            String open = tempArray.get(1).toString();
            String high = tempArray.get(2).toString();
            String low = tempArray.get(3).toString();
            String close = tempArray.get(4).toString();
            String vol = tempArray.get(5).toString();
            result.add(ts);
            result.add(open);
            result.add(high);
            result.add(low);
            result.add(close);
            result.add(vol);
        }
        return result;
    }


    /**
     * 根据传入的period(1m,5m,15m,1h,1d,1w,1mo)得到某个int 类型的值,该值在取最新一条k线有用,
     * 获取最后一条kline数据为祖传代码,具体参数设计需要参考push代码
     *
     * @param period
     * @return
     */
    private int getStep(String period) {
        switch (period) {
            case KlineHistoryConstant.KLINE_HIS_PERIOD_5M:
                return 5;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_15M:
                return 15;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_30M:
                return 30;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1H:
                return 60;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1D:
                return 24 * 60;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1W:
                return 7 * 24 * 60;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1MO:
                return 30 * 24 * 60;
            default:
                return 1;

        }
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
        SystemTradeType systemTradeType = redisHelper.getTradeType(tradeId, KlineHistoryConstant.KLINE_HIS_ZERO);
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
            return (systemTradeType.getSellShortName() + KlineHistoryConstant.KLINE_HIS_SLASH + systemTradeType.getBuyShortName()).toUpperCase();
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



    public ConcurrentHashMap<String, Integer> getCacheCoinPairsMap() {
        if (MapUtils.isEmpty(cacheCoinPairsMap)) {
            cacheTradeId4Symbol();
        }
        return cacheCoinPairsMap;
    }


    /**
     * 缓存币对
     */
    private void cacheTradeId4Symbol() {
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
            String value = sellShortName + KlineHistoryConstant.KLINE_HIS_SLASH + buyShortName;
            cacheCoinPairsMap.put(value, tradeId);
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
}
