package com.qkwl.common.dto.transDict;

import java.io.Serializable;

public class SystemTransDict implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer Id;
	
	private String groupid;
	
	private String dictid;
	
	private String outerDictid;
	
	private String dictname;
	
	private String sortorder;

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

	public String getDictid() {
		return dictid;
	}

	public void setDictid(String dictid) {
		this.dictid = dictid;
	}

	public String getOuterDictid() {
		return outerDictid;
	}

	public void setOuterDictid(String outerDictid) {
		this.outerDictid = outerDictid;
	}

	public String getDictname() {
		return dictname;
	}

	public void setDictname(String dictname) {
		this.dictname = dictname;
	}

	public String getSortorder() {
		return sortorder;
	}

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}
	
}
