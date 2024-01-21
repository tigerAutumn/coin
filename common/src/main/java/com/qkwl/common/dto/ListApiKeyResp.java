/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
public class ListApiKeyResp implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private Integer uid;
	
	private String accessKey;
	
	private String ip;
	
	private Integer remainPeriod; 
	
	private String remark;
	
	private Date createTime;
	
	private Integer status;
	
	private String statusStr;
	
	private String types;
	
	private String typesStr;
	
	private String rate;

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


	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getRemainPeriod() {
		return remainPeriod;
	}

	public void setRemainPeriod(Integer remainPeriod) {
		this.remainPeriod = remainPeriod;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
	
	
	

}
