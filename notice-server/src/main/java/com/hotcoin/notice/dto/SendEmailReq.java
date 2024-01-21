package com.hotcoin.notice.dto;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SendEmailReq", description = "发送邮件参数")
public class SendEmailReq implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "邮箱",required = true)
	@NotBlank
	private String email;
	
	@ApiModelProperty(value = "参数")
	private Map<String, Object> params;
	
	@ApiModelProperty(value = "发送来源（一般是系统名称）",required = true)
	@NotBlank
	private String platform;
	
	@ApiModelProperty(value = "业务类型",required = true)
	@NotBlank
	private String businessType;
	
	@ApiModelProperty(value = "语言",required = true)
	@Pattern(regexp="zh_CN|en_US|ko_KR",message="语言可选zh_CN、en_US、ko_KR")
	private String lang;



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}


	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	
	
}