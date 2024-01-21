package com.qkwl.common.dto.lever;

import java.io.Serializable;

public class LeverCoin implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//币种编号
	private int id;
	
	//币对
	private String symbol;
	
	//价格精度
	private int priceDigit;
	
	//数量精度
	private int countDigit;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getPriceDigit() {
		return priceDigit;
	}

	public void setPriceDigit(int priceDigit) {
		this.priceDigit = priceDigit;
	}

	public int getCountDigit() {
		return countDigit;
	}

	public void setCountDigit(int countDigit) {
		this.countDigit = countDigit;
	}
	
}
