package com.hotcoin.activity.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author hf
 * @date 2019-06-10 09:20:42
 */
@Setter
@Getter
public class AdminActivityRechargeBo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 充值币种
     */
    private String rechargeCoin;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 空投数量
     */
    private String airDropAmount;
    /**
     * 空投总数
     */
    private String airDropTotal;
    /**
     * 空投类型:交易量(amount),交易额(volume)
     */
    private String airDropType;
    /**
     * 空投规则
     */
    private Double airDropRule;
    /**
     * 活动开始时间
     */
    private String startTime;
    /**
     * 活动结束时间
     */
    private String endTime;
    /**
     * 活动状态::-1:禁用,0:结束1:开始
     */
    private Integer status;

}
