package com.qkwl.common.dto.mq;

import java.io.Serializable;

public class MQUmegOtc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//买方id
	private Integer buyerId;
	
	//买方id
	private Integer sellerId;
	
	//订单id
	private Long orderId;

	public Integer getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Integer buyerId) {
		this.buyerId = buyerId;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
