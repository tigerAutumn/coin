package com.qkwl.common.dto.dict;

import java.io.Serializable;

public class DictItemAttr implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer Id;
	//字典id
	private String dictitemid;
	//语言类别
	private String langtype;
	//字典名称翻译
	private String dictname;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getDictitemid() {
		return dictitemid;
	}

	public void setDictitemid(String dictitemid) {
		this.dictitemid = dictitemid;
	}

	public String getLangtype() {
		return langtype;
	}

	public void setLangtype(String langtype) {
		this.langtype = langtype;
	}

	public String getDictname() {
		return dictname;
	}

	public void setDictname(String dictname) {
		this.dictname = dictname;
	}
	
}
