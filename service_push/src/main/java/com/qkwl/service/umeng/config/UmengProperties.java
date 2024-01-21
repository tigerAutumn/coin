package com.qkwl.service.umeng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "umeng")
public class UmengProperties {

	private String androidKey;
	private String androidSecret;
	private String iosKey;
	private String iosSecret;
	private Boolean iosIsProd;

	public String getAndroidKey() {
		return androidKey;
	}

	public void setAndroidKey(String androidKey) {
		this.androidKey = androidKey;
	}

	public String getAndroidSecret() {
		return androidSecret;
	}

	public void setAndroidSecret(String androidSecret) {
		this.androidSecret = androidSecret;
	}

	public String getIosKey() {
		return iosKey;
	}

	public void setIosKey(String iosKey) {
		this.iosKey = iosKey;
	}

	public String getIosSecret() {
		return iosSecret;
	}

	public void setIosSecret(String iosSecret) {
		this.iosSecret = iosSecret;
	}

	public Boolean isIosIsProd() {
		return iosIsProd;
	}

	public void setIosIsProd(Boolean iosIsProd) {
		this.iosIsProd = iosIsProd;
	}
	
}
