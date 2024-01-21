package com.qkwl.common.dto.statistic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class EBankRank implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	//用户id
	private Integer userId;
	
	//用户账号
	private String loginName;
	
	//24小时成交额
	private BigDecimal todayAmount;
	
	//累计总成交额
	private BigDecimal totalAmount;
	
	//排序
	private Integer sort;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public BigDecimal getTodayAmount() {
		return todayAmount;
	}

	public void setTodayAmount(BigDecimal todayAmount) {
		this.todayAmount = todayAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

}
