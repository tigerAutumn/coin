/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author huangjinfeng
 */
public class ExchangeRateResp implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String moneySymbol;
	
	private BigDecimal rate;
	
	private String enUSName;
	
	private String koKRName;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoneySymbol() {
		return moneySymbol;
	}

	public void setMoneySymbol(String moneySymbol) {
		this.moneySymbol = moneySymbol;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getEnUSName() {
		return enUSName;
	}

	public void setEnUSName(String enUSName) {
		this.enUSName = enUSName;
	}

	public String getKoKRName() {
		return koKRName;
	}

	public void setKoKRName(String koKRName) {
		this.koKRName = koKRName;
	}
	
	

}
