/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "NewCoinCountdownResp", description = "新币倒计时返回信息")
public class NewCoinCountdownResp implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "项目简介url")
	private String projectBrief ;
	@ApiModelProperty(value = "项目官网url")
	private String  projectWebsite;
	@ApiModelProperty(value = "倒计时、单位秒")
	private long countdown;
	
	public String getProjectBrief() {
		return projectBrief;
	}
	public void setProjectBrief(String projectBrief) {
		this.projectBrief = projectBrief;
	}
	public String getProjectWebsite() {
		return projectWebsite;
	}
	public void setProjectWebsite(String projectWebsite) {
		this.projectWebsite = projectWebsite;
	}
	public long getCountdown() {
		return countdown;
	}
	public void setCountdown(long countdown) {
		this.countdown = countdown;
	}
}
