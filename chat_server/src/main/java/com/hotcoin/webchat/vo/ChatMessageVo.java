package com.hotcoin.webchat.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotcoin.webchat.common.ChatConstants;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.util.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatMessageVo {

    //发送消息则
    private UserInfo sender;

    //发送内容
    private String message;

    private String messageType;

    private String orderId;

    private String clientType;

    //接收者列表
    @JsonIgnore
    private Map<String, UserInfo> receiver;

    //发送时间
    private Date createTime;

    public ChatMessageVo() {

    }

    public ChatMessageVo(UserInfo sender,String orderId,String message,String messageType,String clientType,Date sendTime) {
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        this.orderId = orderId;
        Map<String, UserInfo> onlineUser = ChatConstants.onlinesUsers;
        this.receiver = new HashMap<>(onlineUser.size());
        for (Map.Entry<String, UserInfo> onLineTemp : onlineUser.entrySet()) {
            UserInfo userInfoTemp = onLineTemp.getValue();
            receiver.put(Utils.getUserClientTypeKey(userInfoTemp.getOrderId(),userInfoTemp.getClientType()),userInfoTemp);
        }
        this.clientType = clientType;
        this.createTime = sendTime;
    }

    public ChatMessageVo(UserInfo sender,UserInfo receiver,String orderId,String message,String messageType,String clientType,Date sendTime) {
        this.sender = sender;
        Map<String, UserInfo> toMap = new HashMap<>(1);
        toMap.put(Utils.getUserClientTypeKey(receiver.getOrderId(), receiver.getClientType()),receiver);
        this.receiver = toMap;
        this.message = message;
        this.orderId = orderId;
        this.messageType = messageType;
        this.createTime = sendTime;
        this.clientType = clientType;
    }

    public UserInfo getSender() {
        return sender;
    }

    public void setSender(UserInfo sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, UserInfo> getReceiver() {
        return receiver;
    }

    public void setReceiver(Map<String, UserInfo> receiver) {
        this.receiver = receiver;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
