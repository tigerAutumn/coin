/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@ApiModel(value = "ListApiKeyReq", description = "管理台列出用户API Key")
public class ListApiKeyReq extends PageDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "uid",required = false)
	private String uid;
	
	@ApiModelProperty(value = "status",required = false)
	@Pattern(regexp="[0-3]",message="状态(0,1,2,3)")
	private String status="0";

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
