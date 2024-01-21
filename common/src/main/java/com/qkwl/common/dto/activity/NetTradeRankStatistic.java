package com.qkwl.common.dto.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class NetTradeRankStatistic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//活动id
	private Integer activityId;
	
	//用户id
	private Integer userId;
	
	//手机号码
	private String telephone;
	
	//邮箱
	private String email; 
	
	//累计买入数量
	private BigDecimal buyCount;
	
	//累计卖出数量
	private BigDecimal sellCount;
	
	//净交易数量
	private BigDecimal netCount;
	
	//持仓数量
	private BigDecimal position;
	
	//创建时间
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(BigDecimal buyCount) {
		this.buyCount = buyCount;
	}

	public BigDecimal getSellCount() {
		return sellCount;
	}

	public void setSellCount(BigDecimal sellCount) {
		this.sellCount = sellCount;
	}

	public BigDecimal getNetCount() {
		return netCount;
	}

	public void setNetCount(BigDecimal netCount) {
		this.netCount = netCount;
	}

	public BigDecimal getPosition() {
		return position;
	}

	public void setPosition(BigDecimal position) {
		this.position = position;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
