package com.qkwl.common.dto.otc;

import java.math.BigDecimal;
import java.util.Date;

public class OtcUserTransfer {
	private Integer id;
	
	private BigDecimal amount;
	
	private Integer userId;
	
	//买入、卖出有对方userid
	private Integer otherUserId;
	
	
	private Integer coinId;
	
	private Date createTime;
	
	//类型：（1，账户划转到otc 2，otc划转到账户  3.买入  4.卖出 5.otc划转到账户创新去）
    private Integer type;
    
    private Integer version;
    
	private String webLogo;
	
	private BigDecimal fee;

    
  //是否使用地址标签
    private Boolean isUseMemo;
    
    // 扩展字段
    private String loginName;
    private String nickName;
    private String realName;
    private String coinName;
    private String shortName;
    
	//用于资产平衡统计
    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private BigDecimal amountFrozen;
    
    //划转方向
    private String transferName;
    

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	public Integer getOtherUserId() {
		return otherUserId;
	}

	public void setOtherUserId(Integer otherUserId) {
		this.otherUserId = otherUserId;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getAmountIn() {
		if(amountIn == null) {
			return BigDecimal.ZERO;
		}
		return amountIn;
	}

	public void setAmountIn(BigDecimal amountIn) {
		this.amountIn = amountIn;
	}

	public BigDecimal getAmountOut() {
		if(amountOut == null) {
			return BigDecimal.ZERO;
		}
		return amountOut;
	}

	public void setAmountOut(BigDecimal amountOut) {
		this.amountOut = amountOut;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getWebLogo() {
		return webLogo;
	}

	public void setWebLogo(String webLogo) {
		this.webLogo = webLogo;
	}

	public Boolean getIsUseMemo() {
		return isUseMemo;
	}

	public void setIsUseMemo(Boolean isUseMemo) {
		this.isUseMemo = isUseMemo;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	
	public BigDecimal getAmountFrozen() {
		if(amountFrozen == null) {
			return BigDecimal.ZERO;
		}
		return amountFrozen;
	}

	public void setAmountFrozen(BigDecimal amountFrozen) {
		this.amountFrozen = amountFrozen;
	}

	public String getTransferName() {
		return transferName;
	}

	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	@Override
	public String toString() {
		return "OtcUserTransfer [amount=" + amount + ", userId=" + userId + ", otherUserId=" + otherUserId + ", coinId="
				+ coinId + ", createTime=" + createTime + ", type=" + type + ", version=" + version + "]";
	}

	
    
    
}
