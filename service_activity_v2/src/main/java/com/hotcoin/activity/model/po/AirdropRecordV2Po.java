package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:46
 */
public class AirdropRecordV2Po implements Serializable {
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

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 活动类型:recharge(充值),trade(交易)
     */
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    /**
     * 活动类型:recharge(充值),trade(交易)
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * 活动币种
     */
    public void setActivityCoin(String activityCoin) {
        this.activityCoin = activityCoin;
    }

    /**
     * 活动币种
     */
    public String getActivityCoin() {
        return activityCoin;
    }

    /**
     * 充值或者交易的币种数量
     */
    public void setLinkCoinAmount(Double linkCoinAmount) {
        this.linkCoinAmount = linkCoinAmount;
    }

    /**
     * 充值或者交易的币种数量
     */
    public Double getLinkCoinAmount() {
        return linkCoinAmount;
    }

    /**
     * 关联的币种的总额
     */
    public void setLinkCoinVolume(Double linkCoinVolume) {
        this.linkCoinVolume = linkCoinVolume;
    }

    /**
     * 关联的币种的总额
     */
    public Double getLinkCoinVolume() {
        return linkCoinVolume;
    }

    /**
     * 关联的币对,当为交易空投活动时,该字段不为空,充值时为空
     */
    public void setLinkPair(String linkPair) {
        this.linkPair = linkPair;
    }

    /**
     * 关联的币对,当为交易空投活动时,该字段不为空,充值时为空
     */
    public String getLinkPair() {
        return linkPair;
    }

    /**
     * 活动总额统计
     */
    public void setActivityAmountSum(Double activityAmountSum) {
        this.activityAmountSum = activityAmountSum;
    }

    /**
     * 活动总额统计
     */
    public Double getActivityAmountSum() {
        return activityAmountSum;
    }

    /**
     * -1:表示未满足空投奖励,0:表示满足活动要求,但未入activity-detail表,1:表示已入activity_detail表
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * -1:表示未满足空投奖励,0:表示满足活动要求,但未入activity-detail表,1:表示已入activity_detail表
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }
}
