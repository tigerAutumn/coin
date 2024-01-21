/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */

@ApiModel(value = "MessageTemplateResp", description = "列出信息模版返回对象")
public class MessageTemplateResp implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "主键")
	private Integer id;
	@ApiModelProperty(value = "语言")
	private String lang;
	@ApiModelProperty(value = "业务类型")
	private String businessType;
	@ApiModelProperty(value = "模版类型(1-短信 2-邮件)")
	private Integer type;
	@ApiModelProperty(value = "系统")
	private String platform;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	@ApiModelProperty(value = "模版内容")
	private String template;
	@ApiModelProperty(value = "描述")
	private String description;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
