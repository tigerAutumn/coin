package com.hotcoin.activity.tasks;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.activity.component.RedisComponent;
import com.hotcoin.activity.model.constant.ActivityConstant;
import com.hotcoin.activity.model.em.AirDropTypeEnum;
import com.hotcoin.activity.model.param.UserC2cEntrustDto;
import com.hotcoin.activity.model.param.VirtualCapitalOperationDto;
import com.hotcoin.activity.model.po.AdminActivityRechargePo;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.model.resp.UserC2cEntrustResp;
import com.hotcoin.activity.model.resp.VirtualCapitalOperationResp;
import com.hotcoin.activity.service.AdminActivityRechargeService;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.service.UserC2cEntrustService;
import com.hotcoin.activity.service.VirtualCapitalOperationService;
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
 * @ClassName: UserRechargeActivityTask
 * @Author: hf
 * @Description: 充值活动任务
 * @Date: 2019/6/13 13:57
 * @Version: 1.0
 */
@Component
@Slf4j
public class UserRechargeActivityTask extends BaseTask {
    @Autowired
    private AdminActivityRechargeService rechargeService;
    @Autowired
    private AirdropActivityDetailV2Service advs;
    @Autowired
    private VirtualCapitalOperationService vcos;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private UserC2cEntrustService userC2cEntrustService;

    public void runRechargeActivityTask() {
        try {
            // 获取所有已开启的充值活动配置
            List<AdminActivityRechargePo> list = rechargeService.query(new AdminActivityRechargePo(1));
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            log.info("get recharge config ->{}", JSON.toJSONString(list));
            for (AdminActivityRechargePo ap : list) {
                Date startTime = ap.getStartTime();
                Date endTime = ap.getEndTime();
                String activityCoin = ap.getRechargeCoin();
                String airDropAmount = ap.getAirDropAmount();
                Double airDropTotal = Double.valueOf(ap.getAirDropTotal());
                Double ruleAmount = ap.getAirDropRule();
                String airDropType = ap.getAirDropType();
                String airDropCoin = ap.getAirDropCoin();
                if (startTime.compareTo(endTime) >= 0) {
                    log.info("config recharge startTime invalid");
                    continue;
                }
                // 没到时间不统计
                if (!isReadyTime(endTime)) {
                    continue;
                }

                // 判断充值发放是否超过总额
                boolean flag = isOverConfigAmount(airDropTotal, ruleAmount, airDropType, activityCoin, airDropCoin, startTime, endTime);
                if (flag) {
                    continue;
                }
                List<JSONObject> statList = new ArrayList<>();
                // 币币充值(f_virtual_capital_operation)->根据用户和币对进行统计
                if (airDropType.equals(ActivityConstant.RECHARGE_TYPE_COIN)) {
                    List<VirtualCapitalOperationResp> coin2CoinRecordList = getCoin2CoinRechargeAndWithdrawRecord(startTime, endTime);
                    log.info("query coin2coin recharge result in match range [{}]-[{}],result [{}]", startTime.getTime(), endTime.getTime(), JSON.toJSONString(coin2CoinRecordList));
                    if (CollectionUtils.isEmpty(coin2CoinRecordList)) {
                        continue;
                    }
                    statList = getUserCoinAirDropCoinAmount(coin2CoinRecordList, activityCoin);
                } else {
                    // c2c充值
                    List<UserC2cEntrustResp> c2cEntrustRespList = getC2CRechargeAndWithdrawRecord(startTime, endTime);
                    log.info("query c2c recharge result in match range [{}]-[{}],result [{}]", startTime.getTime(), endTime.getTime(), JSON.toJSONString(c2cEntrustRespList));
                    if (CollectionUtils.isEmpty(c2cEntrustRespList)) {
                        continue;
                    }
                    statList = getUserC2CAirDropCoinAmount(c2cEntrustRespList, activityCoin);
                }

                if (CollectionUtils.isEmpty(statList)) {
                    continue;
                }
                log.info("get activityCoin [{}] stat recharge result [{}],ruleAmount is[{}]", activityCoin, JSON.toJSONString(statList), ruleAmount);
                for (JSONObject item : statList) {
                    Integer userId = item.getInteger(ActivityConstant.USERID);
                    String coinName = item.getString(ActivityConstant.COINNAME);
                    Double amount = item.getDouble(ActivityConstant.AMOUNT);
                    log.info("start to check coin :[{}]", coinName);
                    if (amount == null || amount <= 0 || !activityCoin.equals(coinName)) {
                        continue;
                    }

                    // 再次判断充值发放是否超过总额
                    boolean b = isOverConfigAmount(airDropTotal, ruleAmount, airDropType, activityCoin, airDropCoin, startTime, endTime);
                    if (b) {
                        continue;
                    }
                    // 是否领取过该类型的奖励
                    AirdropActivityDetailV2Po data = new AirdropActivityDetailV2Po(userId, AirDropTypeEnum.RECHARGE_ACTIVITY.getCode(), airDropType, ruleAmount, coinName);
                    data.setAirDropTime(endTime);
                    data.setActivityCoin(activityCoin);
                    data.setAirDropCoin(airDropCoin);
                    log.info("rechargeActivity query user activity detail record param:[{}]", JSON.toJSONString(data));
                    List<AirdropActivityDetailV2Po> detailV2PoList = advs.query(data);
                    log.info("service:rechargeActivity class->query user activity detail record result:[{}]", JSON.toJSONString(detailV2PoList));
                    if (!CollectionUtils.isEmpty(detailV2PoList)) {
                        continue;
                    }
                    if (amount < ruleAmount) {
                        continue;
                    }
                    // 记录存入发放奖励表
                    AirdropActivityDetailV2Po result = wrapSaveData(endTime, AirDropTypeEnum.RECHARGE_ACTIVITY.getCode(), airDropType, activityCoin, airDropCoin, airDropAmount, userId, ruleAmount);
                    log.info("recharge task start to insert coin [{}]into activity detail record table,data is [{}]", activityCoin, JSON.toJSONString(result));
                    advs.insertSelective(result);
                    log.info("recharge task insert coin [{}] activity detail record success", activityCoin);
                }
            }
        } catch (Exception e) {
            log.error("run recharge activity task fail:[{}]", e);
        }
    }

    /**
     * 根据用户+币种名称分类,统计用户充值数量
     *
     * @return 返回含有用户充值某币对(c2c充值)数量的List
     */
    private List<JSONObject> getUserC2CAirDropCoinAmount(List<UserC2cEntrustResp> c2cEntrustRespList, String activityCoin) {
        Map<String, Double> map = new HashMap<>(16);
        // 统计数据
        for (UserC2cEntrustResp cerl : c2cEntrustRespList) {
            Integer userId = cerl.getUserId();
            Integer coinId = cerl.getCoinId();

            double amount = convertAmount(cerl.getType(), cerl.getAmount());
            // 从redis 获取 coinId的缩写如BTC
            JSONObject jsonObject = redisComponent.getCoinInfo4Redis(ActivityConstant.COINKEY + coinId + ActivityConstant.UNDERLINE + 1);
            if (jsonObject == null) {
                log.error("c2c get coin info from redis fail,coinId is->{}", coinId);
                continue;
            }
            String coinName = jsonObject.get(ActivityConstant.SHORTNAME).toString();
            if (StringUtils.isEmpty(coinName) || !coinName.equals(activityCoin)) {
                continue;
            }
            map = calcAmount(map, amount, getCalcKey(userId, coinName));
        }
        return wrapTempData(map);
    }

    /**
     * @return
     */
    private String getCalcKey(Integer userId, String coinName) {
        return userId + ActivityConstant.UNDERLINE + coinName;
    }

    /**
     * 根据用户+币种名称分类,统计用户充值数量
     *
     * @return 返回含有用户充值某币对(币币充值)数量的List
     */
    private List<JSONObject> getUserCoinAirDropCoinAmount(List<VirtualCapitalOperationResp> rechargeRecordList, String activityCoin) {
        Map<String, Double> map = new HashMap<>(16);
        // 统计数据
        for (VirtualCapitalOperationResp vcop : rechargeRecordList) {
            Integer userId = vcop.getFuid();
            Integer coinId = vcop.getWalletCoinId();
            Integer type = vcop.getFtype();
            // 总额=充值数量+手续费+btc手续费
            BigDecimal amountResult = vcop.getFamount().add(vcop.getFfees()).add(vcop.getFbtcfees());
            double amount = convertAmount(type, amountResult);
            // 从redis 获取 coinId的缩写如BTC
            JSONObject jsonObject = redisComponent.getCoinInfo4Redis(ActivityConstant.COINKEY + coinId + ActivityConstant.UNDERLINE + 1);
            if (jsonObject == null) {
                log.error("get coin info from redis fail,coinId is->{}", coinId);
                continue;
            }
            String coinName = jsonObject.get(ActivityConstant.SHORTNAME).toString();
            if (StringUtils.isEmpty(coinName) || !coinName.equals(activityCoin)) {
                continue;
            }
            map = calcAmount(map, amount, getCalcKey(userId, coinName));
        }
        return wrapTempData(map);
    }

    private Map<String, Double> calcAmount(Map<String, Double> map, double amount, String key) {
        Double count = map.get(key);
        if (count != null) {
            map.put(key, amount + count);
        } else {
            map.put(key, amount);
        }
        return map;
    }

    /**
     * 转换amount
     *
     * @param type   1:充值,2:提现
     * @param amount
     * @return
     */
    private double convertAmount(int type, BigDecimal amount) {
        if (type == 1) {
            return amount.doubleValue();
        } else {
            return -amount.doubleValue();
        }
    }

    /**
     * 获取币币充值提现记录
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private List<VirtualCapitalOperationResp> getCoin2CoinRechargeAndWithdrawRecord(Date startTime, Date endTime) {
        List<VirtualCapitalOperationResp> rechargeRecordList = vcos.queryRechargeRecordByDtoParam
                (new VirtualCapitalOperationDto(1, 3, startTime, endTime));
        if (CollectionUtils.isEmpty(rechargeRecordList)) {
            return null;
        }
        List<VirtualCapitalOperationResp> withdrawRecordList = vcos.queryRechargeRecordByDtoParam
                (new VirtualCapitalOperationDto(2, 3, startTime, endTime));
        if (CollectionUtils.isEmpty(withdrawRecordList)) {
            return rechargeRecordList;
        } else {
            return combineData(rechargeRecordList, withdrawRecordList);
        }

    }

    /**
     * 获取C2C充值提现记录
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private List<UserC2cEntrustResp> getC2CRechargeAndWithdrawRecord(Date startTime, Date endTime) {
        List<UserC2cEntrustResp> c2cRechargeRespList = userC2cEntrustService.queryByDtoParam
                (new UserC2cEntrustDto(1, 2, startTime, endTime));
        if (CollectionUtils.isEmpty(c2cRechargeRespList)) {
            return null;
        }
        List<UserC2cEntrustResp> c2cWithdrawRespList = userC2cEntrustService.queryByDtoParam
                (new UserC2cEntrustDto(2, 2, startTime, endTime));
        if (CollectionUtils.isEmpty(c2cWithdrawRespList)) {
            return c2cRechargeRespList;
        } else {
            return combineData(c2cRechargeRespList, c2cWithdrawRespList);
        }

    }

    /**
     * 合并数据
     *
     * @param list1
     * @param list2
     * @return
     */
    public List combineData(List list1, List list2) {
        if (!CollectionUtils.isEmpty(list1) && !CollectionUtils.isEmpty(list2)) {
            for (Object item : list2) {
                list1.add(item);
            }
        }
        return list1;
    }

    /**
     * 是否超过总额度
     *
     * @return
     */
    public boolean isOverConfigAmount(Double airDropTotal, Double ruleAmount, String airDropType, String activityCoin, String airDropCoin, Date startTime, Date endTime) {
        // 判断充值发放是否超过总额
        AirdropActivityDetailV2Po activityDetailV2Po = new AirdropActivityDetailV2Po(AirDropTypeEnum.RECHARGE_ACTIVITY.getCode(), airDropType, airDropCoin, ruleAmount);
        activityDetailV2Po.setActivityCoin(activityCoin);
        activityDetailV2Po.setAirDropTime(endTime);
        log.info("query recharge set totalAmount param is[{}],startTime is [{}],endTime is[{}]", JSON.toJSONString(activityDetailV2Po), startTime.getTime(), endTime.getTime());
        Double issuedAmount = advs.queryTotalByTypeAndCoin(activityDetailV2Po);
        log.info("query recharge totalAmount result:[{}],and config airDropTotal is[{}]", issuedAmount, airDropTotal);
        if (issuedAmount >= airDropTotal) {
            return true;
        }
        return false;
    }


}
