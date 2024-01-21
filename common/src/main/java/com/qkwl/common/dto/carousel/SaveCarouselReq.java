package com.qkwl.common.dto.carousel;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;


public class SaveCarouselReq implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	private String name;
	@NotBlank
	private String url;
	@NotBlank
	private String lang;
    private String targetUrl;
	private Integer priority;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

}