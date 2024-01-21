package com.hotcoin.webchat.model;

import java.io.Serializable;
import java.util.Date;

/**
 * OTC聊天
 * @author XXX
 *
 */
public class ChatMessage implements Serializable {
	/**聊天消息主体*/
	private Long msgId;
	/**发送者*/
	private Long sender;
	/**发送者昵称*/
	private String senderFnickname;
	/**接收者(多人逗号,分割)*/
	private String receiver;
	/**收件人昵称*/
	private String receiverFnickname;
	/**订单号*/
	private String orderId;
	/**消息主体*/
	private String message;
	/**text，img，audio，file，addr，video*/
	private String msgType;
	/**扩展字段*/
	private String extendsJson;
	/**1未发送，2已发送*/
	private Integer sendState;
	/**创建时间*/
	private Date createTime;
	/**发送时间*/
	private Date sendTime;
	/**1单播,2组播,3广播*/
	private Integer sendType;

	public Long getMsgId() {
		return msgId;
	}
	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
	public Long getSender() {
		return sender;
	}
	public void setSender(Long sender) {
		this.sender = sender;
	}
	public String getSenderFnickname() {
		return senderFnickname;
	}
	public void setSenderFnickname(String senderFnickname) {
		this.senderFnickname = senderFnickname;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getReceiverFnickname() {
		return receiverFnickname;
	}
	public void setReceiverFnickname(String receiverFnickname) {
		this.receiverFnickname = receiverFnickname;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getExtendsJson() {
		return extendsJson;
	}
	public void setExtendsJson(String extendsJson) {
		this.extendsJson = extendsJson;
	}
	public Integer getSendState() {
		return sendState;
	}
	public void setSendState(Integer sendState) {
		this.sendState = sendState;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Integer getSendType() {
		return sendType;
	}
	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChatMessage").append("[ ");
		sb.append("msgId=").append(msgId).append(", ");
		sb.append("sender=").append(sender).append(", ");
		sb.append("senderFnickname=").append(senderFnickname).append(", ");
		sb.append("receiver=").append(receiver).append(", ");
		sb.append("receiverFnickname=").append(receiverFnickname).append(", ");
		sb.append("orderId=").append(orderId).append(", ");
		sb.append("message=").append(message).append(", ");
		sb.append("msgType=").append(msgType).append(", ");
		sb.append("extendsJson=").append(extendsJson).append(", ");
		sb.append("sendState=").append(sendState).append(", ");
		sb.append("createTime=").append(createTime).append(", ");
		sb.append("sendTime=").append(sendTime).append(", ");
		sb.append("sendType=").append(sendType).append(", ");
		sb.replace(sb.length() - 2, sb.length(), " ]");
		return sb.toString();
	}
}
