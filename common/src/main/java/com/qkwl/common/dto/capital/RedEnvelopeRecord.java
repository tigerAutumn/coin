package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RedEnvelopeRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键id
	private Integer id;
	// 用户id
	private Integer uid;
	// 昵称
	private String nickname;
	// 账号
	private String loginName;
	// 头像
	private String photo;
	// 红包编号
	private String redEnvelopeNo;
	// 币种id
	private Integer coinId;
	// 金额
	private BigDecimal amount;
	// 是否最多 0 否 1 是
	private Integer isMost;
	// 是否被领取 0 否 1 是
	private Integer isReceived;
	// 创建日期
	private Date createTime;
	// 领取日期
	private Date receiveTime;
	// 币种名称
	private String coinName;
	// 发红包者昵称
	private String sendNickname;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getRedEnvelopeNo() {
		return redEnvelopeNo;
	}
	public void setRedEnvelopeNo(String redEnvelopeNo) {
		this.redEnvelopeNo = redEnvelopeNo;
	}
	public Integer getCoinId() {
		return coinId;
	}
	public void setCoinId(Integer coinId) {
		this.coinId = coinId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Integer getIsMost() {
		return isMost;
	}
	public void setIsMost(Integer isMost) {
		this.isMost = isMost;
	}
	public Integer getIsReceived() {
		return isReceived;
	}
	public void setIsReceived(Integer isReceived) {
		this.isReceived = isReceived;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getCoinName() {
		return coinName;
	}
	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}
	public String getSendNickname() {
		return sendNickname;
	}
	public void setSendNickname(String sendNickname) {
		this.sendNickname = sendNickname;
	}
}
