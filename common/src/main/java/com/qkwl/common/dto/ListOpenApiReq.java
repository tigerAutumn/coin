/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@ApiModel(value = "ListOpenApiReq", description = "open api列表参数")
public class ListOpenApiReq extends PageDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "url",required = false)
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
