package com.hotcoin.common.dto;

import java.io.Serializable;

/**
 * 分页参数.
 *
 * @author huangjinfeng
 */
public class PageDTO implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer pageNum=1;
    
    /** The page size. */
    private Integer pageSize=10;
	
	/**
	 * Gets the page num.
	 *
	 * @return the page num
	 */
	public Integer getPageNum() {
		return pageNum;
	}
	
	/**
	 * Sets the page num.
	 *
	 * @param pageNum the new page num
	 */
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}
	
	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public Integer getPageSize() {
		return pageSize;
	}
	
	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}