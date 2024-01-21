package com.qkwl.service.validate.model;

import java.io.Serializable;
import java.util.Map;

public class SendSms implements Serializable {

	private static final long serialVersionUID = 1L;

	private String phone;
	
	private Map<String, Object> params;
	
	private String platform;
	
	private String businessType;
	
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
