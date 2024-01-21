package com.qkwl.common.dto.lever;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class LeverTrade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BigInteger orderId;
	
	private BigInteger succId;
	
	private Date time;
	
	private String count;
	
	private String price;
	
	private String fee;
	
	private String symbol;
	
	private String orderType;

	public BigInteger getOrderId() {
		return orderId;
	}

	public void setOrderId(BigInteger orderId) {
		this.orderId = orderId;
	}

	public BigInteger getSuccId() {
		return succId;
	}

	public void setSuccId(BigInteger succId) {
		this.succId = succId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
}
