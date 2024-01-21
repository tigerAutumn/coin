package com.qkwl.common.dto.user;

import java.io.Serializable;
import java.util.Date;

public class UserInfoExtend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 主键Id
	private Integer id;
	
	//用户id
	private Integer userId;
	
	//平台id
	private Integer platform;
	
	//状态 0 关闭 1 开启
	private Integer status;
	
	//创建时间
	private Date createTime;
	
	//更新时间
	private Date updateTime;
	
	//友盟device token
	private String deviceToken;

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

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
