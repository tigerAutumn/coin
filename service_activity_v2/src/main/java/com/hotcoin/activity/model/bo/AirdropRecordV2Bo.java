package com.hotcoin.activity.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:46
 */
@Setter
@Getter
public class AirdropRecordV2Bo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 活动类型:recharge(充值),trade(交易)
     */
    private String activityType;
    /**
     * 活动币种
     */
    private String activityCoin;
    /**
     * 充值或者交易的币种数量
     */
    private Double linkCoinAmount;
    /**
     * 关联的币种的总额
     */
    private Double linkCoinVolume;
    /**
     * 关联的币对,当为交易空投活动时,该字段不为空,充值时为空
     */
    private String linkPair;
    /**
     * 活动总额统计
     */
    private Double activityAmountSum;
    /**
     * -1:表示未满足空投奖励,0:表示满足活动要求,但未入activity-detail表,1:表示已入activity_detail表
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
}
