/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */

@ApiModel(value = "AdminUpdateApiKeyReq", description = "管理台编辑API KEY 参数")
public class AdminUpdateApiKeyReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id",required = true)
	@NotNull
	private Integer id;
	
	@ApiModelProperty(value = "费率",required = true)
	 @NotBlank
	 @DecimalMin("0")
	private String rate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	
	
	
}
