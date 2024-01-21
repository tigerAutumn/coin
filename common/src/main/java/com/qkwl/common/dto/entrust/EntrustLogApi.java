package com.qkwl.common.dto.entrust;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class EntrustLogApi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 自增ID
    private BigInteger id;
    // 是否自成交
    private Integer isSelfTrade;
    // 交易对
    private String sysmbol;
    // 委单类型
    private Integer entrustType;
    // 委单ID
    private BigInteger entrustId;
    // 成交ID
    private BigInteger matchId;
    // 成交价
    private BigDecimal amount;
    // 价格
    private BigDecimal prize;
    // 数量
    private BigDecimal count;
	// 创建时间
    private String createTime;
    
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public Integer getIsSelfTrade() {
		return isSelfTrade;
	}
	public void setIsSelfTrade(Integer isSelfTrade) {
		this.isSelfTrade = isSelfTrade;
	}
	public String getSysmbol() {
		return sysmbol;
	}
	public void setSysmbol(String sysmbol) {
		this.sysmbol = sysmbol;
	}
	public Integer getEntrustType() {
		return entrustType;
	}
	public void setEntrustType(Integer entrustType) {
		this.entrustType = entrustType;
	}
	public BigInteger getEntrustId() {
		return entrustId;
	}
	public void setEntrustId(BigInteger entrustId) {
		this.entrustId = entrustId;
	}
	public BigInteger getMatchId() {
		return matchId;
	}
	public void setMatchId(BigInteger matchId) {
		this.matchId = matchId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPrize() {
		return prize;
	}
	public void setPrize(BigDecimal prize) {
		this.prize = prize;
	}
	public BigDecimal getCount() {
		return count;
	}
	public void setCount(BigDecimal count) {
		this.count = count;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
    
}
