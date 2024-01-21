package com.qkwl.common.dto.otc;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.boot.SpringApplication;

import com.qkwl.common.dto.Enum.otc.OtcUserTypeEnum;
import com.qkwl.common.match.MathUtils;

public class OtcUserExt {
    private Integer id;

    private Integer userId;

    //成交总金额（btc）
    private BigDecimal succAmt;
    //订单成交笔数
    private Integer cmpOrders;
    //好评订单笔数
    private Integer goodEvaluation;
    //差评订单笔数
    private Integer badEvaluation;
    //申诉赢的数量
    private Integer winAppeal;
    //申诉总数量
    private Integer sumAppeal;
    
    //用户类型 
    private Integer otcUserType;
    private String otcUserTypeString;
    
    // 昵称
 	private String nickname;
	// 是否实名认证
	private Boolean hasrealvalidate;
	// 是否绑定手机
	private Boolean istelephonebind;
	// 是否绑定邮箱
	private Boolean ismailbind;
	// 是否绑定谷歌
	private Boolean googlebind;
	// 头像
	private String photo;
	//注册时间
	private Date registertime;
	
	//好评率
	private String applauseRate;
	//押金
	private BigDecimal deposit;
	

    private Date createTime;

    private Date updateTime;
    //30天订单完成率
    private String completionRate;
    //买单成交数
    private Integer buyCount;
    //卖单成交数
    private Integer sellCount;

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

    public BigDecimal getSuccAmt() {
        return succAmt;
    }

    public void setSuccAmt(BigDecimal succAmt) {
        this.succAmt = succAmt;
    }

    public Integer getCmpOrders() {
        return cmpOrders;
    }

    public void setCmpOrders(Integer cmpOrders) {
        this.cmpOrders = cmpOrders;
    }

    public Integer getGoodEvaluation() {
        return goodEvaluation;
    }

    public void setGoodEvaluation(Integer goodEvaluation) {
        this.goodEvaluation = goodEvaluation;
    }


    public Integer getBadEvaluation() {
		return badEvaluation;
	}

	public void setBadEvaluation(Integer badEvaluation) {
		this.badEvaluation = badEvaluation;
	}

	public Integer getWinAppeal() {
        return winAppeal;
    }

    public void setWinAppeal(Integer winAppeal) {
        this.winAppeal = winAppeal;
    }

    public Integer getSumAppeal() {
        return sumAppeal;
    }

    public void setSumAppeal(Integer sumAppeal) {
        this.sumAppeal = sumAppeal;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Boolean getHasrealvalidate() {
		return hasrealvalidate;
	}

	public void setHasrealvalidate(Boolean hasrealvalidate) {
		this.hasrealvalidate = hasrealvalidate;
	}

	public Boolean getIstelephonebind() {
		return istelephonebind;
	}

	public void setIstelephonebind(Boolean istelephonebind) {
		this.istelephonebind = istelephonebind;
	}

	public Boolean getIsmailbind() {
		return ismailbind;
	}

	public void setIsmailbind(Boolean ismailbind) {
		this.ismailbind = ismailbind;
	}

	public Boolean getGooglebind() {
		return googlebind;
	}

	public void setGooglebind(Boolean googlebind) {
		this.googlebind = googlebind;
	}

	public Integer getOtcUserType() {
		return otcUserType;
	}

	public void setOtcUserType(Integer otcUserType) {
		this.otcUserType = otcUserType;
	}

	public String getOtcUserTypeString() {
		return OtcUserTypeEnum.getValueByCode(otcUserType);
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Date getRegistertime() {
		return registertime;
	}

	public void setRegistertime(Date registertime) {
		this.registertime = registertime;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public String getApplauseRate() {
		if(goodEvaluation == null || goodEvaluation == 0) {
			return "0%";
		}
		if(badEvaluation == null) {
			badEvaluation = 0;
		}
		BigDecimal divide = MathUtils.mul(MathUtils.div(new BigDecimal(goodEvaluation), MathUtils.add(new BigDecimal(goodEvaluation), new BigDecimal(badEvaluation))),new BigDecimal(100));
		divide = MathUtils.toScaleNum(divide, 2);
		return divide.toString() + "%";
	}

	public String getCompletionRate() {
		return completionRate;
	}

	public void setCompletionRate(String completionRate) {
		this.completionRate = completionRate;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}

	public Integer getSellCount() {
		return sellCount;
	}

	public void setSellCount(Integer sellCount) {
		this.sellCount = sellCount;
	}
}