package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

public class FUserExtend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 主键Id
	private Integer id;
	// 用户Id
	private Integer uid;
	// 语言
	private String language;
	// 反钓鱼码
	private String antiPhishingCode;
	// 是否开启二次验证
	private Integer secondaryVerification;
	// 创建时间
	private Date createTime;
	// 更新时间
	private Date updateTime;
	
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getAntiPhishingCode() {
		return antiPhishingCode;
	}
	public void setAntiPhishingCode(String antiPhishingCode) {
		this.antiPhishingCode = antiPhishingCode;
	}
	public Integer getSecondaryVerification() {
		return secondaryVerification;
	}
	public void setSecondaryVerification(Integer secondaryVerification) {
		this.secondaryVerification = secondaryVerification;
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
	
}
