/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
public class UpdateMessageTemplateReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@ApiModelProperty(value = "id",required = true)
    private Integer id;
	
	
	@ApiModelProperty(value = "语言")
	@Pattern(regexp="zh_CN|en_US|ko_KR",message="语言可选zh_CN、en_US、ko_KR")
	private String lang;
	
	@ApiModelProperty(value = "业务类型")
	private String businessType;
	
	@Pattern(regexp="1|2",message="1或者2")
	@ApiModelProperty(value = "模版类型(1-短信 2-邮件)")
	private String type;
	

	@ApiModelProperty(value = "系统")
	private String platform;
	

	@ApiModelProperty(value = "模版")
	private String template;
	
	@ApiModelProperty(value = "描述")
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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
