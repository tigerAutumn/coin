package com.qkwl.common.dto.activity_v2.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:44
 */
public class AdminActivityTradeBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 交易币种
     */
    private String tradeCoin;
    /**
     * 涉及交易对
     */
    private String linkCoinPairs;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 空投数量
     */
    private String airDropAmount;
    /**
     * 空投总额度
     */
    private String airDropTotal;
    /**
     * 空投类型:交易量(amount),交易额(volume)
     */
    private String airDropType;
    /**
     *
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
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTradeCoin() {
        return tradeCoin;
    }

    public void setTradeCoin(String tradeCoin) {
        this.tradeCoin = tradeCoin;
    }

    public String getLinkCoinPairs() {
        return linkCoinPairs;
    }

    public void setLinkCoinPairs(String linkCoinPairs) {
        this.linkCoinPairs = linkCoinPairs;
    }

    public String getAirDropCoin() {
        return airDropCoin;
    }

    public void setAirDropCoin(String airDropCoin) {
        this.airDropCoin = airDropCoin;
    }

    public String getAirDropAmount() {
        return airDropAmount;
    }

    public void setAirDropAmount(String airDropAmount) {
        this.airDropAmount = airDropAmount;
    }

    public String getAirDropTotal() {
        return airDropTotal;
    }

    public void setAirDropTotal(String airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    public String getAirDropType() {
        return airDropType;
    }

    public void setAirDropType(String airDropType) {
        this.airDropType = airDropType;
    }

    public Double getAirDropRule() {
        return airDropRule;
    }

    public void setAirDropRule(Double airDropRule) {
        this.airDropRule = airDropRule;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
