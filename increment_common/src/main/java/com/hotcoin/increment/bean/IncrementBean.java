package com.hotcoin.increment.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class IncrementBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//用户id
	private Integer userId;
	//币种id
    private Integer coinId;
    //交易花费
    private BigDecimal tradeCost;
    //交易收入
    private BigDecimal tradeIncome;
    //交易手续费
    private BigDecimal tradeFee;


	public IncrementBean() {
		super();
	}

	public IncrementBean(Integer userId, Integer coinId) {
		super();
		this.userId = userId;
		this.coinId = coinId;
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
		if(tradeCost == null) {
			return BigDecimal.ZERO;
		}
		return tradeCost;
	}

	public void setTradeCost(BigDecimal tradeCost) {
		this.tradeCost = tradeCost;
	}

	public BigDecimal getTradeIncome() {
		if(tradeIncome == null) {
			return BigDecimal.ZERO;
		}
		return tradeIncome;
	}

	public void setTradeIncome(BigDecimal tradeIncome) {
		this.tradeIncome = tradeIncome;
	}

	public BigDecimal getTradeFee() {
		if(tradeFee == null) {
			return BigDecimal.ZERO;
		}
		return tradeFee;
	}

	public void setTradeFee(BigDecimal tradeFee) {
		this.tradeFee = tradeFee;
	}
    
    


}
