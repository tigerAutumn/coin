package com.qkwl.common.dto.orepool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrepoolRecord implements Serializable {

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
	
	//状态 1 锁仓成功 2 计息中 3已解锁 4释放完成
	private Integer status;
	
	//创建时间
	private Date createTime;
	
	//更新时间
	private Date updateTime;
	
	//收益利率
	private BigDecimal incomeRate;
	
	//币种名称
	private String coinName;
	
	//币种图标
	private String coinIcon;
	
	//锁仓周期
	private Integer lockPeriod;
	
	//预计收益
	private BigDecimal profit;
	
	//昨日收益
	private BigDecimal yesterdayProfit;
	
	//累计总收益
	private BigDecimal totalProfit;
	
	//锁仓时间
	private Date lockTime;
	
	//计息时间
	private Date countTime;
	
	//解锁时间
	private Date unlockTime;
	
	//项目名称
	private String name;
	
	//锁仓币种名称
	private String lockCoinName;
	
	//收益币种名称
	private String incomeCoinName;
	
	//锁仓类型 1 定期，2 活期，3 创新区活期锁仓
	private Integer type;
	
	//收益周期
	private Integer incomePeriod;
	
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public BigDecimal getIncomeRate() {
		return incomeRate;
	}

	public void setIncomeRate(BigDecimal incomeRate) {
		this.incomeRate = incomeRate;
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

	public Integer getLockPeriod() {
		return lockPeriod;
	}

	public void setLockPeriod(Integer lockPeriod) {
		this.lockPeriod = lockPeriod;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getYesterdayProfit() {
		return yesterdayProfit;
	}

	public void setYesterdayProfit(BigDecimal yesterdayProfit) {
		this.yesterdayProfit = yesterdayProfit;
	}

	public BigDecimal getTotalProfit() {
		return totalProfit;
	}

	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit = totalProfit;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public Date getCountTime() {
		return countTime;
	}

	public void setCountTime(Date countTime) {
		this.countTime = countTime;
	}

	public Date getUnlockTime() {
		return unlockTime;
	}

	public void setUnlockTime(Date unlockTime) {
		this.unlockTime = unlockTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIncomePeriod() {
		return incomePeriod;
	}

	public void setIncomePeriod(Integer incomePeriod) {
		this.incomePeriod = incomePeriod;
	}
	
}
