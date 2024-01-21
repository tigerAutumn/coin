package com.qkwl.common.dto.otc;

import java.io.Serializable;
import java.math.BigDecimal;

public class BestOtcAdvert implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//广告id
	private Integer id;
	//支付方式
	private Integer payment;
	//是否单价最优
	private Boolean isBest;
	//成交单价
	private BigDecimal price;
	//支付名称
	private String payName;
	//支付图标
	private String payIcon;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

	public Boolean getIsBest() {
		return isBest;
	}

	public void setIsBest(Boolean isBest) {
		this.isBest = isBest;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public String getPayIcon() {
		return payIcon;
	}

	public void setPayIcon(String payIcon) {
		this.payIcon = payIcon;
	}
	
}
