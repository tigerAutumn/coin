package com.hotcoin.activity.tasks;

import com.alibaba.fastjson.JSON;
import com.hotcoin.activity.model.em.AirDropTypeEnum;
import com.hotcoin.activity.model.po.AdminActivityRegisterPo;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.model.po.FUserPo;
import com.hotcoin.activity.service.AdminActivityRegisterService;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.service.FUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.tasks
 * @ClassName: FUseRegisterTask
 * @Author: hf
 * @Description: 注册活动
 * @Date: 2019/6/12 18:47
 * @Version: 1.0
 */
@Component
@Slf4j
public class UserRegisterActivityTask extends BaseTask {
    @Autowired
    private FUserService fUserService;
    @Autowired
    private AdminActivityRegisterService activityRegisterService;
    @Autowired
    private AirdropActivityDetailV2Service advs;

    public void runRegisterActivityTask() {
        try {
            List<AdminActivityRegisterPo> list = activityRegisterService.query(new AdminActivityRegisterPo(1));
            log.info("get register config ->{}", JSON.toJSONString(list));
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            for (AdminActivityRegisterPo ap : list) {
                Date startTime = ap.getStartTime();
                Date endTime = ap.getEndTime();
                String airDropCoin = ap.getAirDropCoin();
                String airDropAmount = ap.getAirDropAmount();
                Double airDropTotal = Double.valueOf(ap.getAirDropTotal());

                if (startTime.compareTo(endTime) >= 0) {
                    log.info("config register startTime invalid");
                    continue;
                }
                // 判断发放是否超过总额
                log.info("registerActivity task start to query activityDetail totalAmount about coin: ->{},config airDropAmount is[{}]", airDropCoin, airDropTotal);
                Double issuedAmount = advs.queryTotalByTypeAndCoin(new AirdropActivityDetailV2Po(AirDropTypeEnum.REGISTER_ACTIVITY.getCode(), airDropCoin));
                log.info("registerActivity task query activityDetail totalAmount result:[{}] about coin: ->[{}],config airDropAmount is[{}]", issuedAmount, airDropCoin, airDropTotal);
                if (issuedAmount >= airDropTotal) {
                    continue;
                }

                // 获取该段时间内注册且已实名的用户
                log.info("registerActivity task query user from :[{}]to ->[{}]", startTime.getTime(), endTime.getTime());
                List<FUserPo> userPoList = fUserService.getUserListByParam(startTime, endTime);
                log.info("registerActivity task query user result:[{}] from :[{}]to ->[{}]", JSON.toJSONString(userPoList), startTime.getTime(), endTime.getTime());
                if (CollectionUtils.isEmpty(userPoList)) {
                    continue;
                }

                // 判断该用户是否已经发放奖励
                for (FUserPo fUserPo : userPoList) {
                    Double againIssuedAmount = advs.queryTotalByTypeAndCoin(new AirdropActivityDetailV2Po(AirDropTypeEnum.REGISTER_ACTIVITY.getCode(), airDropCoin));
                    if (againIssuedAmount >= airDropTotal) {
                        continue;
                    }
                    int userId = fUserPo.getFid();
                    log.info("registerActivity start to task query userId:[{}] candy condition about coin[{}]", userId, airDropCoin);
                    AirdropActivityDetailV2Po activityDetailV2Po = new AirdropActivityDetailV2Po(userId, airDropCoin, AirDropTypeEnum.REGISTER_ACTIVITY.getCode());
                    List<AirdropActivityDetailV2Po> detailV2PoList = advs.query(activityDetailV2Po);
                    log.info("registerActivity  task query userId:[{}] candy condition about coin[{}],result:[{}]", userId, airDropCoin, JSON.toJSONString(detailV2PoList));
                    if (!CollectionUtils.isEmpty(detailV2PoList)) {
                        continue;
                    }
                    // 记录存入发放奖励表
                    AirdropActivityDetailV2Po result = wrapSaveData(endTime,AirDropTypeEnum.REGISTER_ACTIVITY.getCode(), "", airDropCoin,airDropCoin, airDropAmount, userId, null);
                    log.info("registerActivity task start to insert activity detail record to table,coin is [{}],data is[{}]", airDropCoin, JSON.toJSONString(result));
                    advs.insertSelective(result);
                    log.info("registerActivity task insert activity detail record to table,coin is [{}],data is[{}] success", airDropCoin, JSON.toJSONString(result));
                }
            }
        } catch (Exception e) {
            log.error("run register activity task fail:[{}]",e);
        }
    }
}
