package com.qkwl.service.otc.req;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class SendSmsReq implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@NotBlank
	private String phone;

	private Map<String, Object> params;

	@NotBlank
	private String platform;

	@NotBlank
	private String businessType;

	@Pattern(regexp="zh_CN|en_US|ko_KR",message="语言可选zh_CN、en_US、ko_KR")
	private String lang;


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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