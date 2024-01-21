package com.qkwl.common.dto.activity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class NetTradeRankActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//活动名称
	private String name;
	
	//活动交易对
	private Integer tradeId;
	
	//交易对名称
	private String tradeName;
	
	//活动开始时间
	private Date startTime;
	
	//活动结束时间
	private Date endTime;
	
	//活动快照时间
	private Date snapshotTime;
	
	//最小净交易
	private BigDecimal minNetTrade;
	
	//最小持仓
	private BigDecimal minPosition;
	
	//快照状态 0 未快照 1 已快照
	private Integer snapshotStatus;
	
	//活动状态 0 未开启 1 已开启
	private Integer status;
	
	//买方币种
	private String buyShortName;
	
	//卖方币种
	private String sellShortName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTradeId() {
		return tradeId;
	}

	public void setTradeId(Integer tradeId) {
		this.tradeId = tradeId;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getSnapshotTime() {
		return snapshotTime;
	}

	public void setSnapshotTime(Date snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	public BigDecimal getMinNetTrade() {
		return minNetTrade;
	}

	public void setMinNetTrade(BigDecimal minNetTrade) {
		this.minNetTrade = minNetTrade;
	}

	public BigDecimal getMinPosition() {
		return minPosition;
	}

	public void setMinPosition(BigDecimal minPosition) {
		this.minPosition = minPosition;
	}

	public Integer getSnapshotStatus() {
		return snapshotStatus;
	}

	public void setSnapshotStatus(Integer snapshotStatus) {
		this.snapshotStatus = snapshotStatus;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getBuyShortName() {
		return buyShortName;
	}

	public void setBuyShortName(String buyShortName) {
		this.buyShortName = buyShortName;
	}

	public String getSellShortName() {
		return sellShortName;
	}

	public void setSellShortName(String sellShortName) {
		this.sellShortName = sellShortName;
	}
	
}
