/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huangjinfeng
 */
public class ListOpenApiResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private String url;
	
	private Integer status;
	
	private String statusStr;
	
	private String types;
	
	private String typesStr;
	
	private boolean ifSignVerify;
	
	private String description;
	
	private Date createTime;
	
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getTypesStr() {
		return typesStr;
	}

	public void setTypesStr(String typesStr) {
		this.typesStr = typesStr;
	}

	public boolean isIfSignVerify() {
		return ifSignVerify;
	}

	public void setIfSignVerify(boolean ifSignVerify) {
		this.ifSignVerify = ifSignVerify;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
