package com.qkwl.common.dto.statistic;

import java.math.BigDecimal;
import java.util.Date;

public class UserYearIncrement {
    private Integer id;

    private Integer userId;

    private Integer coinId;

    private BigDecimal tradeCost;

    private BigDecimal tradeIncome;

    private BigDecimal tradeFee;

    private BigDecimal freezingOfInnovationZone;

    private BigDecimal dividendOfInnovationZone;

    private BigDecimal rewardOfInnovationZone;

    private BigDecimal orepoolLock;

    private BigDecimal orepoolUnlock;

    private BigDecimal orepoolIncome;

    private Date statisticsTime;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getTradeCost() {
        return tradeCost;
    }

    public void setTradeCost(BigDecimal tradeCost) {
        this.tradeCost = tradeCost;
    }

    public BigDecimal getTradeIncome() {
        return tradeIncome;
    }

    public void setTradeIncome(BigDecimal tradeIncome) {
        this.tradeIncome = tradeIncome;
    }

    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    public BigDecimal getFreezingOfInnovationZone() {
        return freezingOfInnovationZone;
    }

    public void setFreezingOfInnovationZone(BigDecimal freezingOfInnovationZone) {
        this.freezingOfInnovationZone = freezingOfInnovationZone;
    }

    public BigDecimal getDividendOfInnovationZone() {
        return dividendOfInnovationZone;
    }

    public void setDividendOfInnovationZone(BigDecimal dividendOfInnovationZone) {
        this.dividendOfInnovationZone = dividendOfInnovationZone;
    }

    public BigDecimal getRewardOfInnovationZone() {
        return rewardOfInnovationZone;
    }

    public void setRewardOfInnovationZone(BigDecimal rewardOfInnovationZone) {
        this.rewardOfInnovationZone = rewardOfInnovationZone;
    }

    public BigDecimal getOrepoolLock() {
        return orepoolLock;
    }

    public void setOrepoolLock(BigDecimal orepoolLock) {
        this.orepoolLock = orepoolLock;
    }

    public BigDecimal getOrepoolUnlock() {
        return orepoolUnlock;
    }

    public void setOrepoolUnlock(BigDecimal orepoolUnlock) {
        this.orepoolUnlock = orepoolUnlock;
    }

    public BigDecimal getOrepoolIncome() {
        return orepoolIncome;
    }

    public void setOrepoolIncome(BigDecimal orepoolIncome) {
        this.orepoolIncome = orepoolIncome;
    }

    public Date getStatisticsTime() {
        return statisticsTime;
    }

    public void setStatisticsTime(Date statisticsTime) {
        this.statisticsTime = statisticsTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}