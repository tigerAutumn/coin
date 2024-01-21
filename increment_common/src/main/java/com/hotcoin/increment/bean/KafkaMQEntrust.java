package com.hotcoin.increment.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class KafkaMQEntrust implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//业务id
	private BigInteger id;
	//用户id
	private Integer uid;
	//买方币种id
	private Integer buyCoinId;
	//卖方币种id
	private Integer sellCoinId;
	//买卖方向
	private int side;
	//变化金额
	private BigDecimal amount;
	//数量
	private BigDecimal count;
	//手续费
	private BigDecimal fee;
	//业务时间
	private long bizTime;
	
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Integer getBuyCoinId() {
		return buyCoinId;
	}
	public void setBuyCoinId(Integer buyCoinId) {
		this.buyCoinId = buyCoinId;
	}
	public Integer getSellCoinId() {
		return sellCoinId;
	}
	public void setSellCoinId(Integer sellCoinId) {
		this.sellCoinId = sellCoinId;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getCount() {
		return count;
	}
	public void setCount(BigDecimal count) {
		this.count = count;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	public long getBizTime() {
		return bizTime;
	}
	public void setBizTime(long bizTime) {
		this.bizTime = bizTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
