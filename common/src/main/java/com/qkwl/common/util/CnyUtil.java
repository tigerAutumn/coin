package com.qkwl.common.util;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CnyUtil {

    private static final int AGENTID = 0;

    private final static ConcurrentHashMap<Integer, Integer> CNYTradeTypeMap = new ConcurrentHashMap<Integer,Integer>();

    private final static ConcurrentHashMap<String, Integer> TradeShortNameMap = new ConcurrentHashMap<String,Integer>();

    private final static int BTC_COIN_ID = 8;

    private final static int GAVC_COIN_ID = 9;

    private final static int ETH_COIN_ID = 11;

    private final static int USDT_COIN_ID = 57;
    /**
     * 当数据更新的时候
     * 1，CNYTradeTypeMap 没有数据
     * 2，CNYTradeTypeMap中存储的数据与之前的不相同时
     * @param systemTradeType
     */
    private static void generateCNYTradeTypeMap(SystemTradeType systemTradeType){
        Integer oldTradeId = CNYTradeTypeMap.get(systemTradeType.getId());
        if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
            if(null == oldTradeId || (!oldTradeId.equals(systemTradeType.getId()))) {
                CNYTradeTypeMap.put(systemTradeType.getId(), GAVC_COIN_ID);
            }
        }
        //BTC交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())  || systemTradeType.getBuyCoinId() == 1) {
            if(null == oldTradeId || (!oldTradeId.equals(BTC_COIN_ID))) {
                CNYTradeTypeMap.put(systemTradeType.getId(), BTC_COIN_ID);
            }
        }
        //ETH交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode()) || systemTradeType.getBuyCoinId() == 4) {
            if(null == oldTradeId || (!oldTradeId.equals(ETH_COIN_ID))) {
                CNYTradeTypeMap.put(systemTradeType.getId(), ETH_COIN_ID);
            }
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode()) || systemTradeType.getBuyCoinId() == 52) {
            if(null == oldTradeId || (!oldTradeId.equals(USDT_COIN_ID))) {
                CNYTradeTypeMap.put(systemTradeType.getId(), USDT_COIN_ID);
            }
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
            int buyCoinId = systemTradeType.getBuyCoinId();
            if (buyCoinId == GAVC_COIN_ID) {
                if(null == oldTradeId || (!oldTradeId.equals(systemTradeType.getId()))) {
                    CNYTradeTypeMap.put(systemTradeType.getId(), GAVC_COIN_ID);
                }
            } else {
                String symbol = systemTradeType.getBuyShortName().toUpperCase() + "_GAVC";
                Integer tradeId = TradeShortNameMap.get(symbol);
                if(null == oldTradeId || (!oldTradeId.equals(tradeId))) {
                    CNYTradeTypeMap.put(systemTradeType.getId(), tradeId);
                }
            }
        }

    }

    /**
     * 获取cny的转换值
     *   BTC/USDT 交易对
     *   cnyTradeId 表示USDT_GAVC的交易对ID
     *   tradeIdLastPrice 表示BTC_USDT交易对的lastprice
     *
     * 判断说明
     *    如果cnytradeID =9 表示为GAVC交易区 直接返回tradeIdLastPrice
     *    否则 返回 cnytradeID *  tradeIdLastPrice
     * @param redisHelper
     * @param cnyTradeId
     * @param tradeIdLastPrice 币对原始的lastprice
     * @return
     */
    private static BigDecimal getMatchCny(RedisHelper redisHelper, int cnyTradeId, BigDecimal tradeIdLastPrice) {
        if (GAVC_COIN_ID==cnyTradeId) {
            BigDecimal cny = MathUtils.toScaleNum(tradeIdLastPrice, 2);
            return cny;
        } else {
            //取BTC/GSET交易对价格计算
            BigDecimal cny = redisHelper.getLastPrice(cnyTradeId);

            //当前交易对最新价格
            BigDecimal money = MathUtils.mul(tradeIdLastPrice, cny);
            BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
            return newMoney;
        }
    }

    private static void generateTradeShortNameMap(List<SystemTradeType> tradeTypes){
        for (SystemTradeType tradeType : tradeTypes) {
            if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                continue;
            }
            TradeShortNameMap.put(tradeType.getSellShortName().toLowerCase() + "_" + tradeType.getBuyShortName().toLowerCase(), tradeType.getId());
        }
    }

    /**
     * 获取CNY价格
     * @param systemTradeType 交易币对信息
     * @param redisHelper redeis
     * @return
     */
    public static String getMatchCny(SystemTradeType systemTradeType,RedisHelper redisHelper) {
        Integer tradeId = systemTradeType.getId();
       if(systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())){
            String symbol = systemTradeType.getBuyShortName().toUpperCase() + "_GAVC";
            tradeId = TradeShortNameMap.get(symbol);
            if(null == tradeId){
                List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(AGENTID);
                generateTradeShortNameMap(tradeTypes);
            }

        }
        generateCNYTradeTypeMap(systemTradeType);
        TickerData tickerData = redisHelper.getTickerData(tradeId);
        int cnyTradeId = CNYTradeTypeMap.get(tradeId);
        return getMatchCny(redisHelper, cnyTradeId, tickerData.getLast()).toPlainString();
    }

}
