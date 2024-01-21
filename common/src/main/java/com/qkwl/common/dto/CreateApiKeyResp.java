/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "CreateApiKeyResp", description = "创建新的API Key返回信息")
public class CreateApiKeyResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "Access Key")
	private String accessKey;
	@ApiModelProperty(value = "Secret Key")
	private String secretKey;
	@ApiModelProperty(value = "权限设置")
	private String typesStr;
	@ApiModelProperty(value = "绑定IP地址")
	private String ip;
	@ApiModelProperty(value = "费率")
	private String rate;
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
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
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	
}
