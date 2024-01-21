package com.qkwl.common.dto.system;

import java.io.Serializable;
import java.util.Date;

public class HistoricVersion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 主键id
	private Integer id;
	// 版本号
	private String version;
	// 发布日期
	private Date createTime;
	// 安卓下载地址
	private String androidUrl;
	// 苹果下载地址
	private String iosUrl;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getAndroidUrl() {
		return androidUrl;
	}
	public void setAndroidUrl(String androidUrl) {
		this.androidUrl = androidUrl;
	}
	public String getIosUrl() {
		return iosUrl;
	}
	public void setIosUrl(String iosUrl) {
		this.iosUrl = iosUrl;
	}
}
