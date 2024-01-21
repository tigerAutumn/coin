package com.qkwl.common.dto.orepool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrepoolIncomeRecord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//计划id
	private Integer planId;
	
	//用户id
	private Integer userId;

	//锁仓币种id
	private Integer lockCoinId;
	
	//收益币种id
	private Integer incomeCoinId;
	
	//锁仓数量
	private BigDecimal lockVolume;
	
	//收益数量
	private BigDecimal income;
	
	//创建时间
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getLockCoinId() {
		return lockCoinId;
	}

	public void setLockCoinId(Integer lockCoinId) {
		this.lockCoinId = lockCoinId;
	}

	public Integer getIncomeCoinId() {
		return incomeCoinId;
	}

	public void setIncomeCoinId(Integer incomeCoinId) {
		this.incomeCoinId = incomeCoinId;
	}

	public BigDecimal getLockVolume() {
		return lockVolume;
	}

	public void setLockVolume(BigDecimal lockVolume) {
		this.lockVolume = lockVolume;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "OrepoolIncomeRecord [id=" + id + ", planId=" + planId + ", userId=" + userId + ", lockCoinId="
				+ lockCoinId + ", incomeCoinId=" + incomeCoinId + ", lockVolume=" + lockVolume + ", income=" + income
				+ ", createTime=" + createTime + "]";
	}
	
	
}
