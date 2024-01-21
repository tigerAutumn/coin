package com.qkwl.common.dto.capital;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class RedEnvelopeResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 发红包者昵称
	private String senderNickname;
	// 发红包者头像
	private String senderPhoto;
	// 红包备注
	private String content;
	// 红包类型
	private Integer type;
	// 红包状态
	private Integer status;
	// 账号
	private String loginname;
	// 领取数量
	private BigDecimal amount;
	// 红包总个数
	private Integer count;
	// 红包领取个数
	private Integer receivedCount;
	// 红包被领完时间
	private Long time;
	// 币种id
	private Integer coinId;
	// 币种名称
	private String coinName;
	// 红包记录
	private List<RedEnvelopeRecord> list;

	public String getSenderNickname() {
		return senderNickname;
	}

	public void setSenderNickname(String senderNickname) {
		this.senderNickname = senderNickname;
	}

	public String getSenderPhoto() {
		return senderPhoto;
	}

	public void setSenderPhoto(String senderPhoto) {
		this.senderPhoto = senderPhoto;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getReceivedCount() {
		return receivedCount;
	}

	public void setReceivedCount(Integer receivedCount) {
		this.receivedCount = receivedCount;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
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

	public List<RedEnvelopeRecord> getList() {
		return list;
	}

	public void setList(List<RedEnvelopeRecord> list) {
		this.list = list;
	}

}
