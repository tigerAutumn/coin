package com.hotcoin.activity.tasks;

import com.alibaba.fastjson.JSONObject;
import com.hotcoin.activity.model.constant.ActivityConstant;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.tasks
 * @ClassName: BaseTask
 * @Author: hf
 * @Description:
 * @Date: 2019/6/13 18:12
 * @Version: 1.0
 */
@Slf4j
public abstract class BaseTask {
    public AirdropActivityDetailV2Po wrapSaveData(Date endTime, int activityType, String typeDetail, String activityCoin, String airDropCoin, String airDropAmount, Integer userId, Double ruleAmount) {
        AirdropActivityDetailV2Po detailV2Po = new AirdropActivityDetailV2Po();
        Date date = new Date();
        detailV2Po.setActivityType(activityType);
        detailV2Po.setTypeDetail(typeDetail);
        detailV2Po.setAirDropCoin(airDropCoin);
        detailV2Po.setActivityCoin(activityCoin);
        detailV2Po.setAirDropTime(endTime);
        detailV2Po.setAirDropTotal(Double.valueOf(airDropAmount));
        detailV2Po.setUserId(userId);
        detailV2Po.setCreateTime(date);
        detailV2Po.setUpdateTime(date);
        detailV2Po.setStatus(0);
        detailV2Po.setRule(ruleAmount);
        return detailV2Po;
    }

    /**
     * 封装数据方便调用
     */
    public List<JSONObject> wrapTempData(Map<String, Double> map) {
        List<JSONObject> list = new ArrayList<>();
        for (String key : map.keySet()) {
            JSONObject item = new JSONObject(3);
            String[] keyArr = key.split("[_]");
            int userId = Integer.valueOf(keyArr[0]);
            String coinName = keyArr[1];
            Double amount = map.get(key);
            item.put(ActivityConstant.USERID, userId);
            item.put(ActivityConstant.COINNAME, coinName);
            item.put(ActivityConstant.AMOUNT, amount);
            list.add(item);
        }
        return list;
    }
    /**
     * 是否到达配置时间
     *
     * @return
     */
    public boolean isReadyTime( Date endTime ) {
        try {
            return new Date().compareTo(DateUtil.getDateByHoursNum(endTime, 10)) >= 0 ? true : false;
        } catch (Exception e) {
            log.error("get invalid date,obj param is :[{}],error is[{}}",endTime.getTime(), e);
            return false;
        }
    }
}
