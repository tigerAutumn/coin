package com.qkwl.common.dto.lever;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Formatter.BigDecimalLayoutForm;

public class LeverRecord {
    private Integer id;

    private String requestId;

    private Integer userId;

    private Integer coinId;

    private Integer type;

    private BigDecimal amount;

    private Integer status;

    private Date createTime;

    private Date updateTime;
    
  //用于资产平衡统计
    private BigDecimal amountIn;
    private BigDecimal amountOut;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId == null ? null : requestId.trim();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

	public BigDecimal getAmountIn() {
		if(amountIn == null) {
			return BigDecimal.ZERO;
		}
		return amountIn;
	}

	public void setAmountIn(BigDecimal amountIn) {
		this.amountIn = amountIn;
	}

	public BigDecimal getAmountOut() {
		if(amountOut == null) {
			return BigDecimal.ZERO;
		}
		return amountOut;
	}

	public void setAmountOut(BigDecimal amountOut) {
		this.amountOut = amountOut;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    
}