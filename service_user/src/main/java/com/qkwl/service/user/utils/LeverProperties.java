package com.qkwl.service.user.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lever")
public class LeverProperties {

	//机构编号
	private int clientNo;
	//操作员编号
	private int clientId;
	//key
	private String key;
	//地址
	private String url;
	//端口
	private int port;

	public int getClientNo() {
		return clientNo;
	}

	public void setClientNo(int clientNo) {
		this.clientNo = clientNo;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
