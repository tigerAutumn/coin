package com.qkwl.common.dto.orepool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrepoolPlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//标题
	private String name;
	
	//锁仓类型 1 定期，2 活期，3 创新区活期锁仓
	private Integer type;
	
	//锁仓币种id
	private Integer lockCoinId;
	
	//收益币种id
	private Integer incomeCoinId;
	
	//项目数量
	private BigDecimal volume;
	
	//剩余数量
	private BigDecimal visibleVolume;
	
	//已购数量
	private BigDecimal lockVolume;
	
	//起投数量
	private BigDecimal minAmount;
	
	//最大数量
	private BigDecimal maxAmount;
	
	//收益利率
	private BigDecimal incomeRate;
	
	//锁仓周期
	private Integer lockPeriod;
	
	//收益周期
	private Integer incomePeriod;
	
	//状态 1 已禁用 2 未开始 3 已开始 4 已结束
	private Integer status;
	
	//排序
	private Integer sort;
	
	//开始时间
	private Date startTime;
	
	//结束时间
	private Date endTime;
	
	//创建时间
	private Date createTime;
	
	//更新时间
	private Date updateTime;
	
	//币种名称
	private String coinName;
	
	//币种图标
	private String coinIcon;
	
	//参与人数
	private Integer lockPerson;
	
	//锁定比例
	private BigDecimal lockPercent;
	
	//收益数量
	private BigDecimal incomeVolume;
	
	//锁仓币种名称
	private String lockCoinName;
	
	//收益币种名称
	private String incomeCoinName;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getVisibleVolume() {
		return visibleVolume;
	}

	public void setVisibleVolume(BigDecimal visibleVolume) {
		this.visibleVolume = visibleVolume;
	}

	public BigDecimal getLockVolume() {
		return lockVolume;
	}

	public void setLockVolume(BigDecimal lockVolume) {
		this.lockVolume = lockVolume;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public BigDecimal getIncomeRate() {
		return incomeRate;
	}

	public void setIncomeRate(BigDecimal incomeRate) {
		this.incomeRate = incomeRate;
	}

	public Integer getLockPeriod() {
		return lockPeriod;
	}

	public void setLockPeriod(Integer lockPeriod) {
		this.lockPeriod = lockPeriod;
	}

	public Integer getIncomePeriod() {
		return incomePeriod;
	}

	public void setIncomePeriod(Integer incomePeriod) {
		this.incomePeriod = incomePeriod;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getCoinIcon() {
		return coinIcon;
	}

	public void setCoinIcon(String coinIcon) {
		this.coinIcon = coinIcon;
	}

	public Integer getLockPerson() {
		return lockPerson;
	}

	public void setLockPerson(Integer lockPerson) {
		this.lockPerson = lockPerson;
	}

	public BigDecimal getLockPercent() {
		return lockPercent;
	}

	public void setLockPercent(BigDecimal lockPercent) {
		this.lockPercent = lockPercent;
	}

	public BigDecimal getIncomeVolume() {
		return incomeVolume;
	}

	public void setIncomeVolume(BigDecimal incomeVolume) {
		this.incomeVolume = incomeVolume;
	}

	public String getLockCoinName() {
		return lockCoinName;
	}

	public void setLockCoinName(String lockCoinName) {
		this.lockCoinName = lockCoinName;
	}

	public String getIncomeCoinName() {
		return incomeCoinName;
	}

	public void setIncomeCoinName(String incomeCoinName) {
		this.incomeCoinName = incomeCoinName;
	}
	
}
