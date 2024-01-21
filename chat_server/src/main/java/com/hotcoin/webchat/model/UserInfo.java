package com.hotcoin.webchat.model;


import com.hotcoin.webchat.Enum.ClientTypeEnum;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

public class UserInfo implements Serializable{
	private static final long serialVersionUID = 3562768188264006800L;

	private Long fid;

	private String fnickname;

	private String orderId;

	private String clientType;

	private transient ChannelHandlerContext ctx;

	public UserInfo(Long fid,String orderId, String fnickname, ClientTypeEnum clientTypeEnum,ChannelHandlerContext ctx) {
        this.fid = fid;
        this.fnickname = fnickname;
        this.orderId = orderId;
        this.clientType = clientTypeEnum.getValue();
        this.ctx = ctx;
	}

	public Long getFid() {
		return fid;
	}

	public void setFid(Long fid) {
		this.fid = fid;
	}

	public String getFnickname() {
		return fnickname;
	}

	public void setFnickname(String fnickname) {
		this.fnickname = fnickname;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
}
