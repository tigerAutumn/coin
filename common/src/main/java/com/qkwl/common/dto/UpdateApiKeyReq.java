/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */

@ApiModel(value = "UpdateApiKeyReq", description = "编辑 Api Key参数")
public class UpdateApiKeyReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "id",required = true)
	@NotNull
	private Integer id;
	
	@ApiModelProperty(value = "备注",required = true)
	 @NotBlank(message = "{10176}")
	private String remark;
	
	@ApiModelProperty(value = "ip(最多绑定4个ip,以逗号分隔)",required = false)
	@Pattern(regexp="(((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)[,$]?){1,4}",message="{10177}")
	private String ip;
	
	@ApiModelProperty(value = "API权限(1-只读,2-允许交易,3-允许提现,可以多个权限,用逗号分隔)，比如拥有三个权限传1,2,3",required = true)
	@NotBlank
	@Pattern(regexp="1(,2)?(,3)?",message="权限(1-只读,2-允许交易,3-允许提现,可以多个权限,用逗号分隔)，比如拥有三个权限传1,2,3")
	private String types;
	
	@ApiModelProperty(value = "手机验证码",required = false)
	private String mobileValidCode;
	
	@ApiModelProperty(value = "邮箱验证码",required = false)
	private String mailValidCode;
	
	@ApiModelProperty(value = "谷歌验证码",required = false)
	private String googleValidCode;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
