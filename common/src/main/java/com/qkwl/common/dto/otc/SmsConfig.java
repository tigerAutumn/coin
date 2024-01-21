package com.qkwl.common.dto.otc;

import java.io.Serializable;

public class SmsConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String smsClazz;
	
	private Integer isActivity;
	
	private String action;
	
	private Integer priority;
	
	private String description;

	public String getSmsClazz() {
		return smsClazz;
	}

	public void setSmsClazz(String smsClazz) {
		this.smsClazz = smsClazz;
	}

	public Integer getIsActivity() {
		return isActivity;
	}

	public void setIsActivity(Integer isActivity) {
		this.isActivity = isActivity;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
