/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
public class UpdateSmsConfigReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@ApiModelProperty(value = "id",required = true)
	private Integer id;
	
	@ApiModelProperty(value = "是否启用")
	private Boolean enable;
	
	@ApiModelProperty(value = "短信提供商name",required = true)
	private String name;
	
	@ApiModelProperty(value = "短信提供商权重，数值越大概率越大，默认0")
	@Min(0)
	private Integer weight;
	
	
	@ApiModelProperty(value = "描述",required = true)
	private String description;
	
	@Pattern(regexp="SMS_TEXT|SMS_VOICE|SMS_INTERNATIONAL",message="可选SMS_TEXT、SMS_VOICE、SMS_INTERNATIONAL")
	private String action;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
	
	

}
