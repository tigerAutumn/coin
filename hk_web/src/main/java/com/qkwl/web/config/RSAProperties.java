package com.qkwl.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rsa")
public class RSAProperties {

	private String pubKey;
	private String priKey;
	
	public String getPubKey() {
		return pubKey;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
	public String getPriKey() {
		return priKey;
	}
	public void setPriKey(String priKey) {
		this.priKey = priKey;
	}
}
