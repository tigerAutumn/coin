package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RedEnvelope implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键id
	private Integer id;
	// 红包编号
	private String redEnvelopeNo;
	// 用户id
	private Integer uid;
	// 昵称
	private String nickname;
	// 头像
	private String photo;
	// 备注
	private String content;
	// 类型 1 普通 2 手气
	private Integer type;
	// 币种id
	private Integer coinId;
	// 币种名称
	private String coinName;
	// 领取金额
	private BigDecimal amount;
	// 总金额
	private BigDecimal receiveAmount;;
	// 个数
	private Integer count;
	// 已领取个数
	private Integer receiveCount;
	// 状态 1 未领取完 2 已领取完
	private Integer status;
	// 创建日期
	private Date createTime;
	// 失效时间
	private Date overdueTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRedEnvelopeNo() {
		return redEnvelopeNo;
	}
	public void setRedEnvelopeNo(String redEnvelopeNo) {
		this.redEnvelopeNo = redEnvelopeNo;
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
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}
	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getReceiveCount() {
		return receiveCount;
	}
	public void setReceiveCount(Integer receiveCount) {
		this.receiveCount = receiveCount;
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
	public Date getOverdueTime() {
		return overdueTime;
	}
	public void setOverdueTime(Date overdueTime) {
		this.overdueTime = overdueTime;
	}
}
