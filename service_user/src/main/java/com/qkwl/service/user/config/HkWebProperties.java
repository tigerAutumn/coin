package com.qkwl.service.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hkweb")
public class HkWebProperties {
	
	private String scanUrl;

	public String getScanUrl() {
		return scanUrl;
	}

	public void setScanUrl(String scanUrl) {
		this.scanUrl = scanUrl;
	}

	

}
