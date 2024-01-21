package com.qkwl.common.dto.otc;

import java.math.BigDecimal;
import java.util.Date;

import com.qkwl.common.dto.Enum.otc.OtcOrderStatusEnum;

public class OtcOrder {
    private Long id;
    //订单编号
    private String orderNo;
    //广告id
    private Long adId;
    //广告手续费
    private BigDecimal adFee;
    //订单用户id
    private Integer userId;
    //广告主id
    private Integer adUserId;
    //购买数量
    private BigDecimal amount;
    //单价
    private BigDecimal price;
    //总成交金额
    private BigDecimal totalAmount;

    private Byte status;
    private String statusString;
    private String statusStringColor;

    private String remark;

    private Date limitTime;

    private Date createTime;

    private Date updateTime;
    
    private Date appealTime;
    
    //支付方式
  	private Integer payment;
  	
  	//支付表id
  	private Integer bankInfoId;
  	
  	//交易类型
  	private String side;
  	
  	//交易类型，显示用
  	private String sideType;
  	
    private String nickName;
    
    private Integer coinId;
    
    private String coinName;
    
    private String currencyName;
    
    private boolean isExtendTime;
    //支付方式
    private OtcPayment otcPayment;
    
    private String adNickname;
    
    private boolean isUserEvaluation;
    
    private boolean isAdUserEvaluation;
    
    private int userEvaluationType;
    
    private int adUserEvaluationType;
    
    private String adUserPhoto;
    
    //买方id
    private Integer buyerId;
    //卖方id
    private Integer sellerId;
    
    private String payCode;
    
    public Integer getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Integer buyerId) {
		this.buyerId = buyerId;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public String getAdUserPhoto() {
		return adUserPhoto;
	}

	public void setAdUserPhoto(String adUserPhoto) {
		this.adUserPhoto = adUserPhoto;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	private String userPhoto;
    
	public int getUserEvaluationType() {
		return userEvaluationType;
	}

	public void setUserEvaluationType(int userEvaluationType) {
		this.userEvaluationType = userEvaluationType;
	}

	public int getAdUserEvaluationType() {
		return adUserEvaluationType;
	}

	public void setAdUserEvaluationType(int adUserEvaluationType) {
		this.adUserEvaluationType = adUserEvaluationType;
	}

	public boolean isUserEvaluation() {
		return isUserEvaluation;
	}

	public void setUserEvaluation(boolean isUserEvaluation) {
		this.isUserEvaluation = isUserEvaluation;
	}

	public boolean isAdUserEvaluation() {
		return isAdUserEvaluation;
	}

	public void setAdUserEvaluation(boolean isAdUserEvaluation) {
		this.isAdUserEvaluation = isAdUserEvaluation;
	}

	public String getSideType() {
		return sideType;
	}

	public void setSideType(String sideType) {
		this.sideType = sideType;
	}

	public Date getAppealTime() {
		return appealTime;
	}

	public void setAppealTime(Date appealTime) {
		this.appealTime = appealTime;
	}

	public String getAdNickname() {
		return adNickname;
	}

	public void setAdNickname(String adNickname) {
		this.adNickname = adNickname;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public OtcPayment getOtcPayment() {
		return otcPayment;
	}

	public void setOtcPayment(OtcPayment otcPayment) {
		this.otcPayment = otcPayment;
	}

	public boolean isExtendTime() {
		return isExtendTime;
	}

	public void setExtendTime(boolean isExtendTime) {
		this.isExtendTime = isExtendTime;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

	public Integer getBankInfoId() {
		return bankInfoId;
	}

	public void setBankInfoId(Integer bankInfoId) {
		this.bankInfoId = bankInfoId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public BigDecimal getAdFee() {
		return adFee;
	}

	public void setAdFee(BigDecimal adFee) {
		this.adFee = adFee;
	}

	public Integer getAdUserId() {
		return adUserId;
	}

	public void setAdUserId(Integer adUserId) {
		this.adUserId = adUserId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setAdId(Long adId) {
		this.adId = adId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Long getAdId() {
        return adId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
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

	public String getStatusString() {
		if(status!= null && status >0) {
			return OtcOrderStatusEnum.getValueByCode(status.intValue());
		}else {
			return "";
		}
	}

	public String getStatusStringColor() {
		if(status!= null && status >0) {
			if(status.intValue() == OtcOrderStatusEnum.APPEAL.getCode()) {
				return "red";
			}
			if(status.intValue() == OtcOrderStatusEnum.WAIT.getCode() || status.intValue() == OtcOrderStatusEnum.PAID.getCode()) {
				return "green";
			}
			return "black";
		}else {
			return "";
		}
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
}