package com.qkwl.common.dto.statistic;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserTrade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer userId;
	
	private BigDecimal buyCount;
	
	private BigDecimal sellCount;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(BigDecimal buyCount) {
		this.buyCount = buyCount;
	}

	public BigDecimal getSellCount() {
		return sellCount;
	}

	public void setSellCount(BigDecimal sellCount) {
		this.sellCount = sellCount;
	}
	
}
