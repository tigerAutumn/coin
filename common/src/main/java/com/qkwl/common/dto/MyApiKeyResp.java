/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "MyApiKeyResp", description = "我的API Key")
public class MyApiKeyResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id")
	private Integer id;
	@ApiModelProperty(value = "Access Key")
	private String accessKey;
	@ApiModelProperty(value = "权限")
	private String types;
	@ApiModelProperty(value = "权限描述")
	private String typesStr;
	@ApiModelProperty(value = "绑定IP地址")
	private String ip;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "备注")
	private String remark;
	@ApiModelProperty(value = "剩余有效期，单位天")
	private Integer remainPeriod;
	@ApiModelProperty(value = "状态")
	private String statusStr;
	@ApiModelProperty(value = "费率")
	private String rate;
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getTypesStr() {
		return typesStr;
	}
	public void setTypesStr(String typesStr) {
		this.typesStr = typesStr;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getRemainPeriod() {
		return remainPeriod;
	}
	public void setRemainPeriod(Integer remainPeriod) {
		this.remainPeriod = remainPeriod;
	}
	public String getStatusStr() {
		return statusStr;
	}
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	
}
