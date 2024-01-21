/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@ApiModel(value = "SaveOpenApiReq", description = "添加open api参数")
public class SaveOpenApiReq  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "url",required = true)
	@NotBlank
	private String url;
	
	@ApiModelProperty(value = "types",required = true)
	@NotBlank
	private String types;
	
	@ApiModelProperty(value = "types",required = true)
	@Pattern(regexp="[1-2]",message="状态(1,2)")
	private String status;
	
	@ApiModelProperty(value = "types",required = true)
	@Pattern(regexp="[0-1]",message="状态(0,1)")
	private String ifSignVerify;
	
	@ApiModelProperty(value = "description",required = true)
	@NotBlank
	private String description;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIfSignVerify() {
		return ifSignVerify;
	}

	public void setIfSignVerify(String ifSignVerify) {
		this.ifSignVerify = ifSignVerify;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
