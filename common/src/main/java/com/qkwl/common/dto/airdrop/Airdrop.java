package com.qkwl.common.dto.airdrop;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Airdrop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	//空投活动id
	private String name;
	//名称
	private String title;
	//标题
	private String cover;
	//封面
	private Integer coinId;
	//持有币种
	private BigDecimal minCount;
	//持币最低额度
	private Integer airdropCoinId;
	//空投币种
	private Integer type;
	//空投类型
	private BigDecimal countOrRate;
	//空头数量/比例
	private Date airdropTime;
	//快照时间
	private Date startTime;
	//活动开始时间
	private Date snapshotTime;
	//快照时间
	private Integer snapshotStatus;
	//快照状态
	private Integer isOpen;
	//开启空投
	private Integer status;
	//状态
	private String coinName;
	//持有币种名称
	private String airdropCoinName;
	//空投币种名称
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public Integer getCoinId() {
		return coinId;
	}
	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}
	public BigDecimal getMinCount() {
		return minCount;
	}
	public void setMinCount(BigDecimal minCount) {
		this.minCount = minCount;
	}
	public Integer getAirdropCoinId() {
		return airdropCoinId;
	}
	public void setAirdropCoinId(Integer airdropCoinId) {
		this.airdropCoinId = airdropCoinId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public BigDecimal getCountOrRate() {
		return countOrRate;
	}
	public void setCountOrRate(BigDecimal countOrRate) {
		this.countOrRate = countOrRate;
	}
	public Date getAirdropTime() {
		return airdropTime;
	}
	public void setAirdropTime(Date airdropTime) {
		this.airdropTime = airdropTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getSnapshotTime() {
		return snapshotTime;
	}
	public void setSnapshotTime(Date snapshotTime) {
		this.snapshotTime = snapshotTime;
	}
	public Integer getSnapshotStatus() {
		return snapshotStatus;
	}
	public void setSnapshotStatus(Integer snapshotStatus) {
		this.snapshotStatus = snapshotStatus;
	}
	public Integer getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getAirdropCoinName() {
		return airdropCoinName;
	}
	public void setAirdropCoinName(String airdropCoinName) {
		this.airdropCoinName = airdropCoinName;
	}
	
}
