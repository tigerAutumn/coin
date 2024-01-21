package com.qkwl.common.dto.dict;

import java.io.Serializable;

public class DictGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer Id;
	//字典组id
	private String groupid;
	//字典组名称
	private String groupname;
	//状态 1- 启用 0 - 禁用
	private Integer status;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
