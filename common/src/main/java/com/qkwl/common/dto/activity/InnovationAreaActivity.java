package com.qkwl.common.dto.activity;

import java.math.BigDecimal;
import java.util.Date;

public class InnovationAreaActivity {
    private Long id;

    private Integer coinId;

    private BigDecimal rate;

    private BigDecimal amount;
    
    private BigDecimal balance;

    private Date startTime;

    private Date endTime;

    private Byte status;

    private Date createTime;

    private String coinShortName;
    
    public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCoinShortName() {
		return coinShortName;
	}

	public void setCoinShortName(String coinShortName) {
		this.coinShortName = coinShortName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}