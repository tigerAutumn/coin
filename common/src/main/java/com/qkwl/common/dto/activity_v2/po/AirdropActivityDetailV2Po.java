package com.qkwl.common.dto.activity_v2.po;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
public class AirdropActivityDetailV2Po implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    private Integer activityType;
    /**
     * amount etc
     */
    private String typeDetail;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 活动币种
     */
    private String activityCoin;
    /**
     * 空投数量
     */
    private Double airDropTotal;
    /**
     * 空投时间
     */
    private Date airDropTime;
    /**
     * 发放状态:-1:发放失败,0:待发放,1发放成功
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 生成该条记录时间
     */
    private Date createTime;
    /**
     * 空投规则
     */
    private Double rule;

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
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    /**
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public Integer getActivityType() {
        return activityType;
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
     * 空投币种
     */
    public void setAirDropCoin(String airDropCoin) {
        this.airDropCoin = airDropCoin;
    }

    /**
     * 空投币种
     */
    public String getAirDropCoin() {
        return airDropCoin;
    }


    public Double getAirDropTotal() {
        return airDropTotal;
    }

    public void setAirDropTotal(Double airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    /**
     * 空投时间
     */
    public void setAirDropTime(Date airDropTime) {
        this.airDropTime = airDropTime;
    }

    /**
     * 空投时间
     */
    public Date getAirDropTime() {
        return airDropTime;
    }

    /**
     * 发放状态:-1:发放失败,0:待发放,1发放成功
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 发放状态:-1:发放失败,0:待发放,1发放成功
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 生成该条记录时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 生成该条记录时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    public Double getRule() {
        return rule;
    }

    public void setRule(Double rule) {
        this.rule = rule;
    }

    public String getActivityCoin() {
        return activityCoin;
    }

    public void setActivityCoin(String activityCoin) {
        this.activityCoin = activityCoin;
    }

    /**
     * 用于注册活动根据类型查询活动详情
     * activityType 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public AirdropActivityDetailV2Po(Integer activityType, String airDropCoin) {
        this.airDropCoin = airDropCoin;
        this.activityType = activityType;
    }

    /**
     * 用于根据类型查询活动详情
     * activityType 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public AirdropActivityDetailV2Po(Integer activityType, String airDropCoin, Double rule) {
        this.airDropCoin = airDropCoin;
        this.activityType = activityType;
        this.rule = rule;
    }

    /**
     * @param userId       用户id
     * @param coin         空投币种:BTC etc.
     * @param activityType 活动类型: 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public AirdropActivityDetailV2Po(int userId, String coin, Integer activityType) {
        this.userId = userId;
        this.airDropCoin = coin;
        this.activityType = activityType;
    }

    /**
     * @param userId       用户id
     * @param coin         空投币种:BTC etc.
     * @param activityType 活动类型: 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    public AirdropActivityDetailV2Po(int userId, String coin, Integer activityType, Double rule) {
        this.userId = userId;
        this.airDropCoin = coin;
        this.activityType = activityType;
        this.rule = rule;
    }

    public AirdropActivityDetailV2Po(int status) {
        this.status = status;
    }

    public AirdropActivityDetailV2Po() {
    }
    public AirdropActivityDetailV2Po(Integer activityType,String airDropType,String activityCoin, Double rule) {
        this.activityType = activityType;
        this.typeDetail = airDropType;
        this.activityCoin = activityCoin;
        this.rule = rule;
    }
    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }
}
