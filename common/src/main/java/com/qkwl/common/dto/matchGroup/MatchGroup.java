package com.qkwl.common.dto.matchGroup;

import java.io.Serializable;
import java.util.Date;

public class MatchGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	//交易对组名
	private String groupName;
	
	//交易对id
	private String tradeIds;
	
	//创建时间
	private Date createTime;
	
	//更新时间
	private Date updateTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTradeIds() {
		return tradeIds;
	}

	public void setTradeIds(String tradeIds) {
		this.tradeIds = tradeIds;
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
	
}
