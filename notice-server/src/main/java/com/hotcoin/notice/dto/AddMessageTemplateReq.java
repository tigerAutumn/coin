/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */

@ApiModel(value = "AddMessageTemplateReq", description = "添加信息模版参数")
public class AddMessageTemplateReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@Pattern(regexp="zh_CN|en_US|ko_KR",message="语言可选zh_CN、en_US、ko_KR")
	@ApiModelProperty(value = "语言",required = true)
	private String lang;
	
	@NotBlank
	@ApiModelProperty(value = "业务类型",required = true)
	private String businessType;
	
	@Pattern(regexp="1|2",message="1或者2")
	@NotBlank
	@ApiModelProperty(value = "模版类型(1-短信 2-邮件)",required = true)
	private String type;
	
	@NotBlank
	@ApiModelProperty(value = "系统",required = true)
	private String platform;
	
	@NotBlank
	@ApiModelProperty(value = "模版",required = true)
	private String template;
	
	@ApiModelProperty(value = "描述")
	private String description;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
