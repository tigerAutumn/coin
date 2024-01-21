package com.hotcoin.activity.tasks;

import com.alibaba.fastjson.JSON;
import com.hotcoin.activity.component.RedisComponent;
import com.hotcoin.activity.component.WalletOptComponent;
import com.hotcoin.activity.model.em.AirDropTypeEnum;
import com.hotcoin.activity.model.param.FUserDto;
import com.hotcoin.activity.model.po.AdminActivityRechargePo;
import com.hotcoin.activity.model.po.AdminActivityTradePo;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.service.AdminActivityRechargeService;
import com.hotcoin.activity.service.AdminActivityTradeService;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.service.FUserService;
import com.hotcoin.activity.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.tasks
 * @ClassName: GiveCandyTask
 * @Author: hf
 * @Description:
 * @Date: 2019/6/13 20:01
 * @Version: 1.0
 */
@Slf4j
@Component
public class GiveCandyTask extends BaseTask {
    @Autowired
    private AirdropActivityDetailV2Service detailV2Service;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private WalletOptComponent walletOptComponent;
    @Autowired
    private AdminActivityRechargeService rechargeService;
    @Autowired
    private AdminActivityTradeService adminActivityTradeService;
    @Autowired
    private FUserService fUserService;

    public void runGiveCandyActivityTask() {
        List<AirdropActivityDetailV2Po> list = detailV2Service.selectNotSuccessData();
        for (AirdropActivityDetailV2Po advp : list) {
            Double amount = advp.getAirDropTotal();
            String airDropCoin = advp.getAirDropCoin();
            Integer activityType = advp.getActivityType();
            Integer userId = advp.getUserId();
            try {
                log.info("start to check  readyTime to give candy,amount is [{}],airDropCoin is [{}],activityType is[{}]", amount, airDropCoin, activityType);
                if (!AirDropTypeEnum.REGISTER_ACTIVITY.getCode().equals(activityType)) {
                    List userList = fUserService.getUserListByDtoParam(new FUserDto(userId, 1));
                    if (CollectionUtils.isEmpty(userList)) {
                        log.info("user status is not hasRealValidate userId is:[{}]", userId);
                        continue;
                    }
                    if (!isReadyTime(advp)) {
                        log.info("is not up to the give candy time!");
                        continue;
                    }
                    log.info("check pass----------------------------------------");
                }
                Integer coinId = redisComponent.getCoinIdByName(airDropCoin);
                log.info("get coinId use symbol,coin:[{}],coinId[{}],amount[{}] from redis", airDropCoin, coinId, amount);
                if (coinId == null || amount <= 0) {
                    continue;
                }
                walletOptComponent.updateMultipleRecord(userId, coinId, BigDecimal.valueOf(amount), advp.getId());
            } catch (Exception e) {
                recordGiveCandyFail(advp.getId());
                log.error("update about candy record fail[{}]", e);
            }
        }
    }

    private void recordGiveCandyFail(Integer id) {
        AirdropActivityDetailV2Po detailV2Po = new AirdropActivityDetailV2Po();
        detailV2Po.setId(id);
        detailV2Po.setStatus(-1);
        detailV2Po.setUpdateTime(new Date());
        detailV2Service.updateByPrimaryKey(detailV2Po);
    }

    /**
     * 充值和交易需要等到项目结束后才发糖
     * 判断是否到了发糖时间
     *
     * @return
     */
    public boolean isReadyTime(AirdropActivityDetailV2Po ad) {
        try {
            String airDropCoin = ad.getAirDropCoin();
            Double rule = ad.getRule();
            Integer type = ad.getActivityType();
            String detail = ad.getTypeDetail();
            String activityCoin = ad.getActivityCoin();
            Date now = new Date();
            Date endTime = null;
            if (AirDropTypeEnum.RECHARGE_ACTIVITY.getCode().equals(type)) {
                endTime = getRechargeCandyActivityEndDate(activityCoin, airDropCoin, detail, rule);
            } else {
                endTime = getTradeCandyActivityEndDate(activityCoin, airDropCoin, detail, rule);
            }
            return now.compareTo(DateUtil.getDateByHoursNum(endTime, 10)) >= 0 ? true : false;
        } catch (Exception e) {
            log.error("get invalid date,obj param is :[{}],error is[{}}", e, JSON.toJSONString(ad));
            return false;
        }
    }//now is [1562560533368],endTime+20s is[1565233961000],will return :[false]

    public Date getRechargeCandyActivityEndDate(String activityCoin, String airDropCoin, String detail, Double rule) {
        log.info("get rechargeActivity table maxDate,param: airDropCoin ->[{}],rule->[{}]", airDropCoin, rule);
        AdminActivityRechargePo activityRechargePo = new AdminActivityRechargePo();
        activityRechargePo.setAirDropCoin(airDropCoin);
        activityRechargePo.setAirDropRule(rule);
        activityRechargePo.setAirDropType(detail);
        activityRechargePo.setRechargeCoin(activityCoin);
        List<AdminActivityRechargePo> list = rechargeService.query(activityRechargePo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0).getEndTime();
        }
    }

    public Date getTradeCandyActivityEndDate(String activityCoin, String airDropCoin, String airDropType, Double rule) {
        AdminActivityTradePo activityRechargePo = new AdminActivityTradePo();
        activityRechargePo.setAirDropCoin(airDropCoin);
        activityRechargePo.setAirDropRule(rule);
        activityRechargePo.setTradeCoin(activityCoin);
        activityRechargePo.setAirDropType(airDropType);
        List<AdminActivityTradePo> list = adminActivityTradeService.query(activityRechargePo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0).getEndTime();
        }
    }
}
