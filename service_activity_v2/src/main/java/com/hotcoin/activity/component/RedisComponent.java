package com.hotcoin.activity.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.activity.model.constant.ActivityConstant;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

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
     * 根据交易id获取币对信息
     * {@link SystemTradeStatusEnum}
     *
     * @param tradeId
     * @return
     */
    public String getCoinPairInfo(Integer tradeId) {
        SystemTradeType systemTradeType = redisHelper.getTradeType(tradeId, 0);
        // 查询不到该币对或者 币对状态非法时
        if (systemTradeType == null || !SystemTradeStatusEnum.NORMAL.getCode().equals(systemTradeType.getStatus())) {
            return null;
        } else {
            return (systemTradeType.getSellShortName() + "_" + systemTradeType.getBuyShortName()).toLowerCase();
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
            if ((sellShortName + "_" + buyShortName).equals(symbol.toUpperCase())) {
                tradeId = tradeTypeTemp.getId();
                break;
            }
        }
        return tradeId;
    }

    /**
     * 根据名称获取coinId
     *
     * @param name
     * @return
     */
    public Integer getCoinIdByName(String name) {
        String symbol = name.toUpperCase();
        Integer result = null;
        Map<Integer, String> coinsMap = redisHelper.getCoinTypeShortNameMap();
      //  log.info("get coins map from redis the map :[{}]", JSON.toJSONString(coinsMap));
        for (Integer coinId : coinsMap.keySet()) {
            String coinName = coinsMap.get(coinId);
            if (coinName.equals(symbol)) {
                result = coinId;
                break;
            }
        }
        return result;
    }
    /**
     * 从redis获取币对信息
     *
     * @param key
     * @return
     */
    public JSONObject getCoinPairInfo4Redis(String key) {
        try {
            String coinStr = redisHelper.get(key);
            if (StringUtils.isEmpty(coinStr)) {
                log.error("get coinPair info from redis fail,key is->{}", key);
                return null;
            } else {
               // log.info("get coinPair info from redis ->{}", coinStr);
                JSONObject jsonObject = (JSONObject) JSON.parse(coinStr);
                JSONObject result = jsonObject.getJSONObject(ActivityConstant.EXTOBJECT);
                if (result != null && result.getInteger(ActivityConstant.STATUS).equals(1)) {
                    return result;
                } else {
                    log.info("get coinPair info from redis coinInfo is null or status is invalid ->{}", coinStr);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("get coinPair by tradeId from redis fail->{}", e);
            return null;
        }
    }

    /**
     * 从redis获取币种信息
     *
     * @param key
     * @return
     */
    public JSONObject getCoinInfo4Redis(String key) {
        try {
            String coinStr = redisHelper.get(key);
            if (StringUtils.isEmpty(coinStr)) {
                log.error("get coin info from redis fail,key is->{}", key);
                return null;
            } else {
                //log.info("get coinName info from redis ->{}", coinStr);
                JSONObject jsonObject = (JSONObject) JSON.parse(coinStr);
                JSONObject result = jsonObject.getJSONObject(ActivityConstant.EXTOBJECT);
                if (result != null && result.getInteger(ActivityConstant.STATUS).equals(1)) {
                    return result;
                } else {
                    log.info("get coinName info from redis coinInfo is null or status is invalid ->{}", coinStr);
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("get coinName by coinId from redis fail->{}", e);
            return null;
        }
    }
}
