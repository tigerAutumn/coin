/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
public class DelMessageTemplateReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ApiModelProperty(value = "id",required = true)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
