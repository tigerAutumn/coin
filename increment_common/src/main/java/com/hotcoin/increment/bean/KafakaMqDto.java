package com.hotcoin.increment.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class KafakaMqDto implements Serializable{
	private static final long serialVersionUID = 1L;
	//服务名
	private String bizServiceName;
	//业务类型
	private String type;
	//mq创建时间
	private long sendTime;
	//扩展字段
	private Object extObj;
	
	public String getBizServiceName() {
		return bizServiceName;
	}
	public void setBizServiceName(String bizServiceName) {
		this.bizServiceName = bizServiceName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	public Object getExtObj() {
		return extObj;
	}
	public void setExtObj(Object extObj) {
		this.extObj = extObj;
	}
	
	
	
}