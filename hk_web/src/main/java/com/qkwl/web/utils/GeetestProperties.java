package com.qkwl.web.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "geetest")
public class GeetestProperties {

	private String geetestId;
	private String geetestKey;
	private boolean verify;
	
	public String getGeetestId() {
		return geetestId;
	}
	public void setGeetestId(String geetestId) {
		this.geetestId = geetestId;
	}
	public String getGeetestKey() {
		return geetestKey;
	}
	public void setGeetestKey(String geetestKey) {
		this.geetestKey = geetestKey;
	}
	public boolean isVerify() {
		return verify;
	}
	public void setVerify(boolean verify) {
		this.verify = verify;
	}
}

