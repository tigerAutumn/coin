package com.qkwl.service.mqtt.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.service.mqtt.job.SystemTradeTypeJob;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskUtils {
    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static BigDecimal getCny(int cnyTradeId, String p_newStr, MemCache memCache) {
        if(SystemTradeTypeJob.GAVC_CONVERSION_ID == cnyTradeId){
            BigDecimal p_new = new BigDecimal(p_newStr);
            BigDecimal cny = MathUtils.toScaleNum(p_new, 2);
            if(cny.compareTo(BigDecimal.ZERO)<=0){
                return validateCny(p_new);
            }
            return cny;
        }else {
            //取BTC/GSET交易对价格计算
            String tradeTicker = memCache.get(RedisConstant.TICKERE_KEY + cnyTradeId);
            JSONObject tradeTickerJson = JSONObject.parseObject(tradeTicker).getJSONObject("extObject");
            String lastStr = tradeTickerJson.getString("last");
            //BTC/GSET交易对最新价格
            BigDecimal cny = new BigDecimal(lastStr);

            //当前交易对最新价格
            BigDecimal p_new = new BigDecimal(p_newStr);
            BigDecimal money = MathUtils.mul(p_new, cny);
            BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
            if(newMoney.compareTo(BigDecimal.ZERO)<=0){
                return validateCny(money);
            }
            return newMoney;
        }
    }

    public static BigDecimal validateCny(BigDecimal p_new){
        String p_new_str = p_new.toPlainString();
        String tmp_p_new =p_new_str.split("\\.")[1];
        String newStr = tmp_p_new.replaceAll("^0*", "");
        if(StringUtils.isNotBlank(newStr)){
            int point = p_new_str.indexOf(newStr);
            if(point>2){
                return MathUtils.toScaleNum(p_new , point+1);
            }else {
                return MathUtils.toScaleNum(p_new, point);
            }
        }
        return p_new;
    }

    public static BigDecimal getCnyByCoinId(int tradeId, int buyCoinId,int sellCoinId,String p_newStr, RedisHelper redisHelper) {
        SystemCoinType coinType = new SystemCoinType();
        if(buyCoinId == 9) {
            coinType = redisHelper.getCoinType(sellCoinId);
            return new BigDecimal(p_newStr);
        }else {
            //取BTC/GSET交易对价格计算
            coinType = redisHelper.getCoinType(buyCoinId);
        }

        String symbol = coinType.getShortName()+"_GAVC";
        String[] symbols = symbol.split("_");
        List<SystemTradeType> tradeTypeList = redisHelper.getTradeTypeList(0);
        SystemTradeType systemTradeType = new SystemTradeType();
        for (SystemTradeType tradeType : tradeTypeList) {
            if (tradeType.getSellShortName().toLowerCase().equals(symbols[0].toLowerCase())
                    && tradeType.getBuyShortName().toLowerCase().equals(symbols[1].toLowerCase())) {
                systemTradeType = tradeType;
                break;
            }
        }

        //BTC/GSET交易对最新价格
        //BigDecimal cny = redisHelper.getLastPrice(systemTradeType.getId());
        logger.debug("tradeId:{},systemTradeType:{},systemTradeType.getId():{}",tradeId,JSON.toJSONString(systemTradeType),systemTradeType.getId());
        if(null == systemTradeType.getId()){
            logger.error("buyCoinId:{},sellCoinId:{},symbol:{} not get systemTradeType",buyCoinId,sellCoinId,symbol);
            return null;
        }
        BigDecimal cny = redisHelper.getLastPrice(systemTradeType.getId());

        //当前交易对最新价格
        BigDecimal p_new = new BigDecimal(p_newStr);
        BigDecimal money = MathUtils.mul(p_new, cny);
        BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
        return newMoney;
    }

    public static void sort(List<Object> list) {
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                JSONArray o1Array = JSON.parseArray(o1.toString());
                JSONArray o2Array = JSON.parseArray(o2.toString());
                //o1单价
                BigDecimal o1Price = new BigDecimal(o1Array.get(0).toString());
                //o2单价
                BigDecimal o2Price = new BigDecimal(o2Array.get(0).toString());

                if(o1Price.compareTo(o2Price)<0) {
                    return 1;
                }else if(o1Price.compareTo(o2Price)==0) {
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

}
