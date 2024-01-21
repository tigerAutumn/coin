package com.hotcoin.notice.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分页参数.
 *
 * @author huangjinfeng
 */
@ApiModel(value = "PageDTO", description = "分页参数")
public class PageDTO implements  Serializable{
    
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "页码(默认1)",example = "1")
	private Integer pageNum=1;
    
	@ApiModelProperty(value = "页大小(默认10)",example = "10")
    private Integer pageSize=10;
    
	
	public Integer getPageNum() {
		return pageNum;
	}
	
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}