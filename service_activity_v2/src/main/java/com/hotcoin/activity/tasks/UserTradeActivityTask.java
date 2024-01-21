package com.hotcoin.activity.tasks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.activity.component.RedisComponent;
import com.hotcoin.activity.model.constant.ActivityConstant;
import com.hotcoin.activity.model.em.AirDropTypeEnum;
import com.hotcoin.activity.model.param.FEntrustLogDto;
import com.hotcoin.activity.model.po.AdminActivityTradePo;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.model.resp.FEntrustLogResp;
import com.hotcoin.activity.service.AdminActivityTradeService;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.service.FEntrustLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.tasks
 * @ClassName: UserTradeActivityTask
 * @Author: hf
 * @Description: 交易活动
 * @Date: 2019/6/13 18:41
 * @Version: 1.0
 */
@Component
@Slf4j
public class UserTradeActivityTask extends BaseTask {
    @Autowired
    private AirdropActivityDetailV2Service advs;
    @Autowired
    private AdminActivityTradeService adminActivityTradeService;
    @Autowired
    private FEntrustLogService fEntrustLogService;
    @Autowired
    private RedisComponent redisComponent;

    public void runTradeActivityTask() {
        try {
            // 读取admin配置
            List<AdminActivityTradePo> list = adminActivityTradeService.query(new AdminActivityTradePo(1));
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (AdminActivityTradePo ap : list) {
                Date startTime = ap.getStartTime();
                Date endTime = ap.getEndTime();
                String activityCoin = ap.getTradeCoin();
                String airDropCoin = ap.getAirDropCoin();
                String airDropAmount = ap.getAirDropAmount();
                Double airDropTotal = Double.valueOf(ap.getAirDropTotal());
                String linkPairsStr = ap.getLinkCoinPairs();
                Double ruleAmount = ap.getAirDropRule();
                String tradeType = ap.getAirDropType();
                log.info("start foreach  tradeActivity ,airDropCoin is[{}],activityCoin is[{}]", airDropCoin, activityCoin);
                if (startTime.compareTo(endTime) >= 0) {
                    log.info("config trade startTime invalid");
                    continue;
                }
                // 没到时间不统计
                if (!isReadyTime(endTime)) {
                    log.info("is not time to tradeActivity ,airDropCoin is[{}],activityCoin is[{}]", airDropCoin, activityCoin);
                    continue;
                }
                // 判断交易发放是否超过总额
                boolean flag = isOverConfigAmount(airDropTotal, ruleAmount, tradeType, activityCoin, airDropCoin, endTime);
                if (flag) {
                    continue;
                }
                // 获取该段时间内交易成功的所有交易记录
                List<FEntrustLogResp> tradeRecordList = fEntrustLogService.queryTradeByParam(new FEntrustLogDto(startTime, endTime));
                log.info("query tradeRecord result in match range [{}]-[{}],result: [{}]", startTime.getTime(), endTime.getTime(), JSON.toJSONString(tradeRecordList));
                if (CollectionUtils.isEmpty(tradeRecordList)) {
                    continue;
                }
                // 去掉自成交数据
                log.info("before removeTradeWithSelf data,result is:[{}]", JSON.toJSONString(tradeRecordList));
                tradeRecordList = removeTradeWithSelf(tradeRecordList);
                log.info("after removeTradeWithSelf data,result is:[{}]", JSON.toJSONString(tradeRecordList));
                // 获取计算后的数据
                List<JSONObject> tradeList = getUserTradeAmount(linkPairsStr, activityCoin, tradeRecordList);
                log.info("get stat tradeList result [{}],ruleAmount is[{}]", JSON.toJSONString(tradeList), ruleAmount);
                if (CollectionUtils.isEmpty(tradeList)) {
                    continue;
                }
                for (JSONObject item : tradeList) {
                    Integer userId = item.getInteger(ActivityConstant.USERID);
                    String coinName = item.getString(ActivityConstant.COINNAME);
                    Double amount = item.getDouble(ActivityConstant.AMOUNT);
                    if (!activityCoin.equals(coinName)) {
                        continue;
                    }
                    // 再次判断是否已经超过总额度
                    boolean b = isOverConfigAmount(airDropTotal, ruleAmount, tradeType, activityCoin, airDropCoin, endTime);
                    if (b) {
                        continue;
                    }
                    //[{"activityCoin":"BTC","activityType":3,"airDropCoin":"BTC","airDropTime":1562825911000,"rule":0.0,"typeDetail":"amount","userId":1600049}]
                    // 是否领取过该类型的奖励
                    AirdropActivityDetailV2Po activityDetailV2Po = new AirdropActivityDetailV2Po(userId, AirDropTypeEnum.TRADE_ACTIVITY.getCode(), tradeType, ruleAmount, airDropCoin);
                    activityDetailV2Po.setActivityCoin(activityCoin);
                    activityDetailV2Po.setAirDropTime(endTime);
                    log.info("service:tradeActivity class query user activity detail param [{}]", JSON.toJSONString(activityDetailV2Po));
                    List<AirdropActivityDetailV2Po> detailV2PoList = advs.query(activityDetailV2Po);
                    log.info("service:tradeActivity class->query user activity detail record result:[{}]", JSON.toJSONString(detailV2PoList));
                    if (!CollectionUtils.isEmpty(detailV2PoList)) {
                        continue;
                    }
                    if (amount < ruleAmount) {
                        continue;
                    }
                    // 记录存入发放奖励表
                    AirdropActivityDetailV2Po result = wrapSaveData(endTime, AirDropTypeEnum.TRADE_ACTIVITY.getCode(), tradeType, activityCoin, airDropCoin, airDropAmount, userId, ruleAmount);
                    log.info("tradeActivity task start to insert activity detail record to table,coin is [{}],data is[{}]", activityCoin, JSON.toJSONString(result));
                    advs.insertSelective(result);
                    log.info("tradeActivity task start to insert activity detail record success");
                }
            }
        } catch (Exception e) {
             log.error("run tradeActivity task fail:[{}]",e);
        }

    }

    /**
     * 去掉自成交数据
     *
     * @param list
     * @return
     */
    private List<FEntrustLogResp> removeTradeWithSelf(List<FEntrustLogResp> list) {
        if (list.size() < 1) {
            return list;
        }
        List<Integer> removeIdList = new ArrayList<>();
        // 检查自成交数据
        for (FEntrustLogResp item : list) {
            Integer fid = item.getFid();
            Integer entrustId = item.getFentrustid();
            Integer fUid = item.getFuid();
            for (FEntrustLogResp other : list) {
                Integer otherFid = other.getFid();
                Integer fmatchid = other.getFmatchid();
                Integer otherUid = other.getFuid();
                if (fid.equals(otherFid)) {
                    continue;
                }
                if (fUid.equals(otherUid) && entrustId.equals(fmatchid)) {
                    if (!CollectionUtils.isEmpty(removeIdList) && removeIdList.contains(otherFid)) {
                        continue;
                    } else {
                        removeIdList.add(fid);
                        removeIdList.add(otherFid);
                    }
                }
            }
        }
        // 删除自成交数据
        if (!CollectionUtils.isEmpty(removeIdList)) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                FEntrustLogResp item = (FEntrustLogResp) it.next();
                if (removeIdList.contains(item.getFid())) {
                    it.remove();
                }
            }
        }
        return list;
    }

    /**
     * 根据用户+币种名称分类,统计用户交易数量
     *
     * @param linkPairs       涉及到的币对
     * @param configTradeCoin 参与活动的币种名称
     * @param tradeRecordList
     * @return 返回含有用户充值某币对数量的List
     */
    private List<JSONObject> getUserTradeAmount(String linkPairs, String configTradeCoin, List<FEntrustLogResp> tradeRecordList) {
        Map<String, Double> map = new HashMap<>(16);
        for (FEntrustLogResp record : tradeRecordList) {
            Integer userId = record.getFuid();
            Integer tradeId = record.getFtradeid();
            // 从redis获取coinId的缩写如BTC
            Double amount = record.getFcount().doubleValue();
            JSONObject jsonObject = redisComponent.getCoinPairInfo4Redis(ActivityConstant.TRADKEY + tradeId + ActivityConstant.UNDERLINE + 0);
            if (jsonObject == null) {
                continue;
            }
            String sellShortName = jsonObject.get(ActivityConstant.SELLSHORTNAME).toString();
            String buyShortName = jsonObject.get(ActivityConstant.BUYSHORTNAME).toString();
            String coinPair = sellShortName + ActivityConstant.UNDERLINE + buyShortName;
            log.info("get tradeId from redis tradeId is:[{}],coinPair is:[{}]", tradeId, JSON.toJSONString(coinPair));
            // 过滤不在配置内的
            JSONArray linkPairsArr = JSON.parseArray(linkPairs);
            log.info("sellShortName is{},coinPair is:{},configTradeCoin IS {},linkPairs is {},bool is{},userId is:{}", sellShortName, coinPair, configTradeCoin, linkPairsArr, !linkPairsArr.contains(coinPair), userId);
            if (StringUtils.isEmpty(sellShortName) || !configTradeCoin.equals(sellShortName) || !isContainCoinPair(linkPairsArr, coinPair)) {
                continue;
            }
            String key = userId + ActivityConstant.UNDERLINE + coinPair;
            Double count = map.get(key);
            if (count != null) {
                map.put(key, amount + count);
            } else {
                map.put(key, amount);
            }
        }
        log.info("stat result'map:[{}]", JSON.toJSONString(map));
        return wrapTempData(map);
    }

    private boolean isContainCoinPair(JSONArray jsonArray, String coinPair) {
        boolean b = false;
        if (jsonArray.size() < 1) {
            return false;
        }
        for (Object item : jsonArray) {
            if (item.toString().equals(coinPair)) {
                b = true;
                break;
            }
        }
        return b;
    }

    private double convertAmount(int type, BigDecimal amount) {
        if (type == 1) {
            return amount.doubleValue();
        } else {
            return -amount.doubleValue();
        }
    }

    /**
     * 是否超过总额度
     *
     * @return
     */
    public boolean isOverConfigAmount(Double airDropTotal, Double ruleAmount, String tradeType, String activityCoin, String airDropCoin, Date endTime) {
        // 判断充值发放是否超过总额
        AirdropActivityDetailV2Po queryTotalParam = new AirdropActivityDetailV2Po();
        queryTotalParam.setActivityCoin(activityCoin);
        queryTotalParam.setActivityType(AirDropTypeEnum.TRADE_ACTIVITY.getCode());
        queryTotalParam.setTypeDetail(tradeType);
        queryTotalParam.setAirDropCoin(airDropCoin);
        queryTotalParam.setRule(ruleAmount);
        queryTotalParam.setAirDropTime(endTime);
        Double issuedAmount = advs.queryTotalByTypeAndCoin(queryTotalParam);
        if (issuedAmount >= airDropTotal) {
            return true;
        }
        return false;
    }
}
