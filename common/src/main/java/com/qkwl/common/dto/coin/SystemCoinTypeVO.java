package com.qkwl.common.dto.coin;

import java.io.Serializable;

/**
 * 币种表前端展示实体
 * @author LY
 *
 */
public class SystemCoinTypeVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
    private Integer id;

    private String name;
	// 类型
	private Integer type;

    private String shortname;

    private String weblogo;

    private String applogo;

    private String symbol;
	// 是否提现
	private Boolean isWithdraw;
	// 是否充值
	private Boolean isRecharge;
	// 状态
	private Integer status;
    // 充值到账确认数
    private Integer confirmations;
    // 是否使用地址标签
    private boolean isUseMemo;
    // 是否开启otc
    private boolean isOpenOtc;
    //交易对的数量精度
    private Integer countDigit;
    //交易对的金额精度
    private Integer amountDigit;
    //交易对的价格精度
    private Integer priceDigit;
	

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

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getWeblogo() {
		return weblogo;
	}

	public void setWeblogo(String weblogo) {
		this.weblogo = weblogo;
	}

	public String getApplogo() {
		return applogo;
	}

	public void setApplogo(String applogo) {
		this.applogo = applogo;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Boolean getWithdraw() {
		return isWithdraw;
	}

	public void setWithdraw(Boolean withdraw) {
		isWithdraw = withdraw;
	}

	public Boolean getRecharge() {
		return isRecharge;
	}

	public void setRecharge(Boolean recharge) {
		isRecharge = recharge;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getIsWithdraw() {
		return isWithdraw;
	}

	public void setIsWithdraw(Boolean isWithdraw) {
		this.isWithdraw = isWithdraw;
	}

	public Boolean getIsRecharge() {
		return isRecharge;
	}

	public void setIsRecharge(Boolean isRecharge) {
		this.isRecharge = isRecharge;
	}

	public Integer getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(Integer confirmations) {
		this.confirmations = confirmations;
	}

	public boolean getIsUseMemo() {
		return isUseMemo;
	}

	public void setIsUseMemo(boolean isUseMemo) {
		this.isUseMemo = isUseMemo;
	}

	public Integer getCountDigit() {
		return countDigit;
	}

	public void setCountDigit(Integer countDigit) {
		this.countDigit = countDigit;
	}

	public Integer getAmountDigit() {
		return amountDigit;
	}

	public void setAmountDigit(Integer amountDigit) {
		this.amountDigit = amountDigit;
	}

	public Integer getPriceDigit() {
		return priceDigit;
	}

	public void setPriceDigit(Integer priceDigit) {
		this.priceDigit = priceDigit;
	}


}