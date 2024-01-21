package com.qkwl.common.dto.statistic;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Administrator
 *
 */
public class UserPosition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//用户id
	private Integer userId;
	
	//手机号码
	private String telephone;
	
	//邮箱
	private String email;
	
	//充币数量
	private BigDecimal chargingNumber;
	
	//持仓量
	private BigDecimal position;
	
	//净持仓量
	private BigDecimal netPosition;
	
	//排序
	private Integer sort;
	
	//筛选币种
	private Integer coinId;
	
	//筛选时间
	private String choosenDate;

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

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getChargingNumber() {
		return chargingNumber;
	}

	public void setChargingNumber(BigDecimal chargingNumber) {
		this.chargingNumber = chargingNumber;
	}

	public BigDecimal getPosition() {
		return position;
	}

	public void setPosition(BigDecimal position) {
		this.position = position;
	}

	public BigDecimal getNetPosition() {
		return netPosition;
	}

	public void setNetPosition(BigDecimal netPosition) {
		this.netPosition = netPosition;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getCoinId() {
		return coinId;
	}

	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}

	public String getChoosenDate() {
		return choosenDate;
	}

	public void setChoosenDate(String choosenDate) {
		this.choosenDate = choosenDate;
	}
	
	
}
