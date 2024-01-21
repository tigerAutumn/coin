package com.qkwl.common.dto.carousel;

import java.io.Serializable;

import com.qkwl.common.dto.PageDTO;

public class ListCarouselReq extends  PageDTO implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	
	private String  name;
	
	private String lang;
	
	private String orderField="id";
	
	private String orderDirection="asc";



	public String getOrderField() {
		return orderField;
	}


	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}


	public String getOrderDirection() {
		return orderDirection;
	}


	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLang() {
		return lang;
	}


	public void setLang(String lang) {
		this.lang = lang;
	}
	
	
	
	
   
}