package com.hotcoin.notice.dto;

import java.io.Serializable;

import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author huangjinfeng
 */
@ApiModel(value = "SmsConfigResp", description = "列出sms配置返回对象")
public class SmsConfigResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "主键")
	private Integer id;
	
	@ApiModelProperty(value = "短信提供商name")
	private String name;
	
	@ApiModelProperty(value = "是否激活")
	private Boolean enable;
	
	@ApiModelProperty(value = "短信提供商权重，数值越大概率越大，默认0")
	private Integer weight;
	
	@ApiModelProperty(value = "描述")
	private String description;
	
	@ApiModelProperty(value = "普通短信:SMS_TEXT,语音短信:SMS_VOICE,国际短信:SMS_INTERNATIONAL")
	private String action;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
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
