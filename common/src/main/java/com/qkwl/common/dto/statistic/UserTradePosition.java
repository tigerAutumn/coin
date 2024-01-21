package com.qkwl.common.dto.statistic;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserTradePosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
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
	
	//净交易筛选起点
	private BigDecimal netCountStart;
	
	//净交易筛选终点
	private BigDecimal netCountEnd;
	
	//持仓量
	private BigDecimal position;
	
	//持仓量筛选起点
	private BigDecimal positionStart;
	
	//持仓量筛选终点
	private BigDecimal positionEnd;
	
	//排序
	private Integer sort;
	
	//筛选交易对
	private Integer coinId;
	
	//筛选交易对
	private Integer tradeId;
	
	//筛选开始时间
	private String startDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public BigDecimal getNetCountStart() {
		return netCountStart;
	}

	public void setNetCountStart(BigDecimal netCountStart) {
		this.netCountStart = netCountStart;
	}

	public BigDecimal getNetCountEnd() {
		return netCountEnd;
	}

	public void setNetCountEnd(BigDecimal netCountEnd) {
		this.netCountEnd = netCountEnd;
	}

	public BigDecimal getPosition() {
		return position;
	}

	public void setPosition(BigDecimal position) {
		this.position = position;
	}

	public BigDecimal getPositionStart() {
		return positionStart;
	}

	public void setPositionStart(BigDecimal positionStart) {
		this.positionStart = positionStart;
	}

	public BigDecimal getPositionEnd() {
		return positionEnd;
	}

	public void setPositionEnd(BigDecimal positionEnd) {
		this.positionEnd = positionEnd;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
}
