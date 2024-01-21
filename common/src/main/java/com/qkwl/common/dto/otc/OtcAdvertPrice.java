package com.qkwl.common.dto.otc;

import java.io.Serializable;
import java.math.BigDecimal;

public class OtcAdvertPrice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//最低固定出售价
	private BigDecimal minFixedSellPrice;
	
	//最高固定购买价
	private BigDecimal maxFixedBuyPrice;
	
	//市场最低浮动出售比例
	private BigDecimal minAverageSellRate;
	
	//热币最低浮动出售比例
	private BigDecimal minHotcoinSellRate;
	
	//市场最高浮动购买比例
	private BigDecimal maxAverageBuyRate;
	
	//热币最高浮动购买比例
	private BigDecimal maxHotcoinBuyRate;
	
	
	public BigDecimal getMinFixedSellPrice() {
		return minFixedSellPrice;
	}

	public void setMinFixedSellPrice(BigDecimal minFixedSellPrice) {
		this.minFixedSellPrice = minFixedSellPrice;
	}

	public BigDecimal getMaxFixedBuyPrice() {
		return maxFixedBuyPrice;
	}

	public void setMaxFixedBuyPrice(BigDecimal maxFixedBuyPrice) {
		this.maxFixedBuyPrice = maxFixedBuyPrice;
	}

	public BigDecimal getMinAverageSellRate() {
		return minAverageSellRate;
	}

	public void setMinAverageSellRate(BigDecimal minAverageSellRate) {
		this.minAverageSellRate = minAverageSellRate;
	}

	public BigDecimal getMaxAverageBuyRate() {
		return maxAverageBuyRate;
	}

	public void setMaxAverageBuyRate(BigDecimal maxAverageBuyRate) {
		this.maxAverageBuyRate = maxAverageBuyRate;
	}

	public BigDecimal getMinHotcoinSellRate() {
		return minHotcoinSellRate;
	}

	public void setMinHotcoinSellRate(BigDecimal minHotcoinSellRate) {
		this.minHotcoinSellRate = minHotcoinSellRate;
	}

	public BigDecimal getMaxHotcoinBuyRate() {
		return maxHotcoinBuyRate;
	}

	public void setMaxHotcoinBuyRate(BigDecimal maxHotcoinBuyRate) {
		this.maxHotcoinBuyRate = maxHotcoinBuyRate;
	}

}
