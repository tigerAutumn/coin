/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;

/**
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
public class OrderMatchResultsResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long createdAt;
	
	private String filledAmount;
	
	private String filledFees;
	
	private long id;
	
	private long matchId;
	
	private long orderId;
	
	private String price;
	
	private String type;
	
	private String role;

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getFilledAmount() {
		return filledAmount;
	}

	public void setFilledAmount(String filledAmount) {
		this.filledAmount = filledAmount;
	}

	public String getFilledFees() {
		return filledFees;
	}

	public void setFilledFees(String filledFees) {
		this.filledFees = filledFees;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
