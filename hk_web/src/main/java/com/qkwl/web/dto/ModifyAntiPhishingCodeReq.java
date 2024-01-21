/**
 * 
 */
package com.qkwl.web.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */

@ApiModel(value = "ModifyAntiPhishingCodeReq", description = "修改钓鱼码参数")
public class ModifyAntiPhishingCodeReq implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@ApiModelProperty(value = "旧反钓鱼码",required = true)
	 @NotBlank
	private String oldAntiPhishingCode;
	
	@ApiModelProperty(value = "新反钓鱼码",required = true)
	 @NotBlank
	 @Pattern(regexp="\\w{4,20}",message="4到20位以内的英文、数字或下划线，英文包含大小写")
	private String newAntiPhishingCode;
	
	@ApiModelProperty(value = "手机验证码",required = false)
	private String mobileValidCode;
	
	@ApiModelProperty(value = "邮箱验证码",required = false)
	private String mailValidCode;
	
	@ApiModelProperty(value = "谷歌验证码",required = false)
	private String googleValidCode;
	

	public String getOldAntiPhishingCode() {
		return oldAntiPhishingCode;
	}
	public void setOldAntiPhishingCode(String oldAntiPhishingCode) {
		this.oldAntiPhishingCode = oldAntiPhishingCode;
	}
	public String getNewAntiPhishingCode() {
		return newAntiPhishingCode;
	}
	public void setNewAntiPhishingCode(String newAntiPhishingCode) {
		this.newAntiPhishingCode = newAntiPhishingCode;
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
}
