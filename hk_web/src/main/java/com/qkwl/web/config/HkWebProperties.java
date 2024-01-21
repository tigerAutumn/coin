package com.qkwl.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hkweb")
public class HkWebProperties {
	
	
	private String website;
	
	private String downLoadUrl;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDownLoadUrl() {
		return downLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}
}
