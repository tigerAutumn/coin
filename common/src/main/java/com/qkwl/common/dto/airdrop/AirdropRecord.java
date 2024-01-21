package com.qkwl.common.dto.airdrop;

import java.math.BigDecimal;
import java.util.Date;

public class AirdropRecord {
    private Integer id;

    private Integer coinId;

    private Integer userId;

    private BigDecimal amount;

    private Integer airdropId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAirdropId() {
        return airdropId;
    }

    public void setAirdropId(Integer airdropId) {
        this.airdropId = airdropId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}