/**
 * 
 */
package com.qkwl.common.dto.user;

import java.io.Serializable;

/**
 * @author huangjinfeng
 */
public class SetAntiPhishingCodeDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer userId;
	
	private String ip;
	
	private String antiPhishingCode;
	
	private String mobileValidCode;
	
	private String mailValidCode;
	
	private String googleValidCode;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getAntiPhishingCode() {
		return antiPhishingCode;
	}

	public void setAntiPhishingCode(String antiPhishingCode) {
		this.antiPhishingCode = antiPhishingCode;
	}

	public String getMobileValidCode() {
		return mobileValidCode;
	}

	public void setMobileValidCode(String mobileValidCode) {
		this.mobileValidCode = mobileValidCode;
	}

	public String getMailValidCode() {
		return mailValidCode;
	}

	public void setMailValidCode(String mailValidCode) {
		this.mailValidCode = mailValidCode;
	}

	public String getGoogleValidCode() {
		return googleValidCode;
	}

	public void setGoogleValidCode(String googleValidCode) {
		this.googleValidCode = googleValidCode;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	

}
