package com.qkwl.common.dto.dict;

import java.io.Serializable;

public class DictItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer Id;
	//字典id
	private String dictId;
	//字典名称
	private String dictName;
	//字典组id
	private String groupId;
	//排序字段
	private Integer sortOrder;
	//状态 1- 启用  0 - 禁用
	private Integer status;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getDictId() {
		return dictId;
	}

	public void setDictId(String dictId) {
		this.dictId = dictId;
	}

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
