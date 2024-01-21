package com.hotcoin.sms.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信信息
 * @author XXX
 *
 */
public class SmsMessage implements Serializable {
	    /**自增ID*/
	    private String sendId;
	    /**手机号*/
	    private String mobile;
	    /*短信内容**/
	    private String content;
	    /**SMS_TEXT(1, "普通短信"),*/
	    private Long sendtype;
	    /**短信商渠道*/
	    private String sendchannel;
	    /**短信来源系统*/
	    private Long platform;
	    /**短信发送的状态*/
	    private Long status;
	    /**创建时间*/
	    private String createtime;

	public String getSendId() {
		return sendId;
	}

	public void setSendId(String sendId) {
		this.sendId = sendId;
	}

	public String getMobile() {
	        return mobile;
	    }
	    public void setMobile(String mobile) {
	        this.mobile = mobile;
	    }
	    public String getContent() {
	        return content;
	    }
	    public void setContent(String content) {
	        this.content = content;
	    }
	    public Long getSendtype() {
	        return sendtype;
	    }
	    public void setSendtype(Long sendtype) {
	        this.sendtype = sendtype;
	    }
	    public String getSendchannel() {
	        return sendchannel;
	    }
	    public void setSendchannel(String sendchannel) {
	        this.sendchannel = sendchannel;
	    }
	    public Long getPlatform() {
	        return platform;
	    }
	    public void setPlatform(Long platform) {
	        this.platform = platform;
	    }
	    public Long getStatus() {
	        return status;
	    }
	    public void setStatus(Long status) {
	        this.status = status;
	    }
	    public String getCreatetime() {
	        return createtime;
	    }
	    public void setCreatetime(String createtime) {
	        this.createtime = createtime;
	    }
	
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("SmsMessage").append("[ ");
	    	sb.append("sendId=").append(sendId).append(", ");
	    	sb.append("mobile=").append(mobile).append(", ");
	    	sb.append("content=").append(content).append(", ");
	    	sb.append("sendtype=").append(sendtype).append(", ");
	    	sb.append("sendchannel=").append(sendchannel).append(", ");
	    	sb.append("platform=").append(platform).append(", ");
	    	sb.append("status=").append(status).append(", ");
	    	sb.append("createtime=").append(createtime).append(", ");
			sb.replace(sb.length() - 2, sb.length(), " ]");
			return sb.toString();
	    }
	}
