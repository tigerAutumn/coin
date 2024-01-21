package com.qkwl.common.dto.otc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OtcAdvert implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//用户id
	private Integer userId;
	
	//广告状态 1 上架中，2 下架，3 过期
	private Integer status;
	
	//是否冻结 0 否 1 是
	private Integer isFrozen;
	
	//交易类型
	private String side;
	
	//交易币种id
	private Integer coinId;
	
	//支付货币id
	private Integer currencyId;
	
	//价格类型 1 浮动价格，2 固定价格
	private Integer priceType;
	
	//浮动市场 1 平均 2 币安 3 火币
	private Integer floatMarket;
	
	//价格比例，报价=市场价*价格比例
	private BigDecimal priceRate;
	
	//可接受价格
	private BigDecimal acceptablePrice;
	
	//固定价格
	private BigDecimal fixedPrice;
	
	//总数量
	private BigDecimal volume;
	
	//可用数量
	private BigDecimal visiableVolume;
	
	//成交数量
	private BigDecimal tradingVolume;
	
	//冻结数量
	private BigDecimal frozenVolume;
	
	//费率
	private BigDecimal feeRate;
	
	//支付方式1
	private Integer bankinfoFirstId;
	
	//支付方式2
	private Integer bankinfoSecondId;
	
	//支付方式3
	private Integer bankinfoThirdId;
	
	//最小限额
	private BigDecimal minAmount;
	
	//最大限额
	private BigDecimal maxAmount;
	
	//最大付款时间
	private Integer maxPaytime;
	
	//广告说明
	private String description;
	
	//交易备注
	private String note;
	
	//问候语
	private String greetings;
	
	//结束语
	private String tag;
	
	//最大处理订单数
	private Integer maxProcessing;
	
	//必须成交次数
	private Integer successCount;
	
	//创建时间
	private Date createTime;
	
	//失效时间
	private Date overdueTime;
	
	//更新时间
	private Date updateTime;
	
//	扩展
	//支付方式
	private Integer bankinfoType;
	
	//额度
	private BigDecimal amount;
	
	//用户昵称
	private String nickname;
	
	//用户头像
	private String photo;
	
	//交易笔数
	private BigDecimal cmpOrders;
	
	//好评率
	private String applauseRate;
	
	//广告价格
	private BigDecimal price;
	
	//交易币种名称
	private String coinName;
	
	//法币名称
	private String currencyName;
	
	//支付图片
	private List<String> payIcons;
	
	//支付id
	private List<Integer> payIds;
	
	//支付信息
	private List<OtcPayment> pay;
	
	//币种图标
	private String coinIcon;
	
	//是否受限
	private Integer isLimited;
	
	//溢价比例
	private BigDecimal premiumRate;
	
	private String currencyChineseName;
	
	//otc用户类型
	private Integer otcUserType;
	
	//30内订单完成率
	private String completionRate;
	
	//最佳支付方式
	private Integer payment;
	
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getIsFrozen() {
		return isFrozen;
	}

	public void setIsFrozen(Integer isFrozen) {
		this.isFrozen = isFrozen;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getFloatMarket() {
		return floatMarket;
	}

	public void setFloatMarket(Integer floatMarket) {
		this.floatMarket = floatMarket;
	}

	public BigDecimal getPriceRate() {
		return priceRate;
	}

	public void setPriceRate(BigDecimal priceRate) {
		this.priceRate = priceRate;
	}

	public BigDecimal getAcceptablePrice() {
		return acceptablePrice;
	}

	public void setAcceptablePrice(BigDecimal acceptablePrice) {
		this.acceptablePrice = acceptablePrice;
	}

	public BigDecimal getFixedPrice() {
		return fixedPrice;
	}

	public void setFixedPrice(BigDecimal fixedPrice) {
		this.fixedPrice = fixedPrice;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getVisiableVolume() {
		return visiableVolume;
	}

	public void setVisiableVolume(BigDecimal visiableVolume) {
		this.visiableVolume = visiableVolume;
	}

	public BigDecimal getTradingVolume() {
		return tradingVolume;
	}

	public void setTradingVolume(BigDecimal tradingVolume) {
		this.tradingVolume = tradingVolume;
	}

	public BigDecimal getFrozenVolume() {
		return frozenVolume;
	}

	public void setFrozenVolume(BigDecimal frozenVolume) {
		this.frozenVolume = frozenVolume;
	}

	public BigDecimal getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public Integer getBankinfoFirstId() {
		return bankinfoFirstId;
	}

	public void setBankinfoFirstId(Integer bankinfoFirstId) {
		this.bankinfoFirstId = bankinfoFirstId;
	}

	public Integer getBankinfoSecondId() {
		return bankinfoSecondId;
	}

	public void setBankinfoSecondId(Integer bankinfoSecondId) {
		this.bankinfoSecondId = bankinfoSecondId;
	}

	public Integer getBankinfoThirdId() {
		return bankinfoThirdId;
	}

	public void setBankinfoThirdId(Integer bankinfoThirdId) {
		this.bankinfoThirdId = bankinfoThirdId;
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

	public Integer getMaxPaytime() {
		return maxPaytime;
	}

	public void setMaxPaytime(Integer maxPaytime) {
		this.maxPaytime = maxPaytime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getGreetings() {
		return greetings;
	}

	public void setGreetings(String greetings) {
		this.greetings = greetings;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getMaxProcessing() {
		return maxProcessing;
	}

	public void setMaxProcessing(Integer maxProcessing) {
		this.maxProcessing = maxProcessing;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getOverdueTime() {
		return overdueTime;
	}

	public void setOverdueTime(Date overdueTime) {
		this.overdueTime = overdueTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public BigDecimal getCmpOrders() {
		return cmpOrders;
	}

	public void setCmpOrders(BigDecimal cmpOrders) {
		this.cmpOrders = cmpOrders;
	}

	public String getApplauseRate() {
		return applauseRate;
	}

	public void setApplauseRate(String applauseRate) {
		this.applauseRate = applauseRate;
	}

	public Integer getBankinfoType() {
		return bankinfoType;
	}

	public void setBankinfoType(Integer bankinfoType) {
		this.bankinfoType = bankinfoType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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

	public List<String> getPayIcons() {
		return payIcons;
	}

	public void setPayIcons(List<String> payIcons) {
		this.payIcons = payIcons;
	}

	public List<Integer> getPayIds() {
		return payIds;
	}

	public void setPayIds(List<Integer> payIds) {
		this.payIds = payIds;
	}

	public List<OtcPayment> getPay() {
		return pay;
	}

	public void setPay(List<OtcPayment> pay) {
		this.pay = pay;
	}

	public String getCoinIcon() {
		return coinIcon;
	}

	public void setCoinIcon(String coinIcon) {
		this.coinIcon = coinIcon;
	}

	public Integer getIsLimited() {
		return isLimited;
	}

	public void setIsLimited(Integer isLimited) {
		this.isLimited = isLimited;
	}

	public BigDecimal getPremiumRate() {
		return premiumRate;
	}

	public void setPremiumRate(BigDecimal premiumRate) {
		this.premiumRate = premiumRate;
	}

	public String getCurrencyChineseName() {
		return currencyChineseName;
	}

	public void setCurrencyChineseName(String currencyChineseName) {
		this.currencyChineseName = currencyChineseName;
	}

	public Integer getOtcUserType() {
		return otcUserType;
	}

	public void setOtcUserType(Integer otcUserType) {
		this.otcUserType = otcUserType;
	}

	public String getCompletionRate() {
		return completionRate;
	}

	public void setCompletionRate(String completionRate) {
		this.completionRate = completionRate;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

}
