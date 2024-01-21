package com.qkwl.common.dto;

import java.io.Serializable;

/**
 * 分页参数.
 *
 * @author huangjinfeng
 */
public class PageDTO implements  Serializable{
    
	private static final long serialVersionUID = 1L;

	private Integer pageNum=1;
    
    private Integer pageSize=20;
	
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