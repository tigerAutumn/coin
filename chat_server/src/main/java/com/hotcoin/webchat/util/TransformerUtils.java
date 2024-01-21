package com.hotcoin.webchat.util;

import com.hotcoin.webchat.Enum.MessageSendTypeEnum;
import com.hotcoin.webchat.Enum.MessageTypeEnum;
import com.hotcoin.webchat.model.ChatMessage;
import com.hotcoin.webchat.model.FUser;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.vo.ChatMessageVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

public class TransformerUtils {

    public static ChatMessage getChatMessage(ChatMessageVo chatMessageVo, UserInfo sender,String messageType,MessageSendTypeEnum messageSendTypeEnum,Date sendTime,Integer sendState){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(chatMessageVo.getSender().getFid());
        Map<String, UserInfo> to = chatMessageVo.getReceiver();
        StringBuffer receiver = new StringBuffer();
        StringBuffer receFnickName = new StringBuffer();
        for (Map.Entry<String,UserInfo> toer: to.entrySet()) {
            receiver.append(",").append(toer.getValue().getFid());
            receFnickName.append(",").append(toer.getValue().getFnickname());
        }
        chatMessage.setReceiver(receiver.substring(1));
        chatMessage.setOrderId(chatMessageVo.getOrderId());
        chatMessage.setMessage(chatMessageVo.getMessage());
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(sendState);
        chatMessage.setMsgType(messageType);
        chatMessage.setSenderFnickname(sender.getFnickname());
        chatMessage.setReceiverFnickname(receFnickName.substring(1));
        chatMessage.setCreateTime(chatMessageVo.getCreateTime());
        chatMessage.setSendTime(sendTime);
        chatMessage.setSendType(messageSendTypeEnum.getType());
        return chatMessage;
    }

/*
    public static ChatMessage getChatMessageByReceiverNotExist(ChatMessageVo chatMessageVo, UserInfo sender,String recvId,String recvNickName,String messageType,MessageSendTypeEnum messageSendTypeEnum,Date sendTime,Integer sendState){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(chatMessageVo.getSender().getFid());
        Map<String, UserInfo> to = chatMessageVo.getReceiver();
        *//*StringBuffer receiver = new StringBuffer();
        StringBuffer receFnickName = new StringBuffer();
        for (Map.Entry<String,UserInfo> toer: to.entrySet()) {
            receiver.append(",").append(toer.getValue().getFid());
            receFnickName.append(",").append(toer.getValue().getFnickname());
        }*//*
        chatMessage.setReceiver(recvId);
        chatMessage.setOrderId(chatMessageVo.getOrderId());
        chatMessage.setMessage(chatMessageVo.getMessage());
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(sendState);
        chatMessage.setMsgType(messageType);
        chatMessage.setSenderFnickname(sender.getFnickname());
        chatMessage.setReceiverFnickname(recvNickName);
        chatMessage.setCreateTime(chatMessageVo.getCreateTime());
        chatMessage.setSendTime(sendTime);
        chatMessage.setSendType(messageSendTypeEnum.getType());
        return chatMessage;
    }*/

    public static ChatMessage getChatMessageByReceiverNotExist(UserInfo sender, FUser receiver, String orderId, String message, String messageType, String clientType, Date sendTime, Integer sendState){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender.getFid());
        chatMessage.setReceiver(receiver.getFid().toString());
        chatMessage.setOrderId(orderId);
        chatMessage.setMessage(message);
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(sendState);
        chatMessage.setMsgType(messageType);
        chatMessage.setSenderFnickname(sender.getFnickname());
        String recvNickName = receiver.getFnickname();
        if (StringUtils.isBlank(recvNickName)) {
            recvNickName = receiver.getFid().toString();
        }
        chatMessage.setReceiverFnickname(recvNickName);
        chatMessage.setCreateTime(sendTime);
        chatMessage.setSendTime(sendTime);
        chatMessage.setSendType(MessageSendTypeEnum.UNICAST.getType());
        return chatMessage;
    }

    public static ChatMessage getChatMessageByReceiverNotExist(FUser sender, FUser receiver, String orderId, String message, String messageType, String clientType, Date sendTime, Integer sendState){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(Long.valueOf(sender.getFid()));
        chatMessage.setReceiver(receiver.getFid().toString());
        chatMessage.setOrderId(orderId);
        chatMessage.setMessage(message);
        chatMessage.setExtendsJson("");
        chatMessage.setSendState(sendState);
        chatMessage.setMsgType(messageType);
        chatMessage.setSenderFnickname(sender.getFnickname());
        String recvNickName = receiver.getFnickname();
        if (StringUtils.isBlank(recvNickName)) {
            recvNickName = receiver.getFid().toString();
        }
        chatMessage.setReceiverFnickname(recvNickName);
        chatMessage.setCreateTime(sendTime);
        chatMessage.setSendTime(sendTime);
        chatMessage.setSendType(MessageSendTypeEnum.UNICAST.getType());
        return chatMessage;
    }



    public static ChatMessageVo getChatMessageVo(ChatMessage chatMessage, UserInfo sender, Map<String, UserInfo> receiver, MessageTypeEnum messageTypeEnum){
        ChatMessageVo chatMessageVo = new ChatMessageVo();
        chatMessageVo.setCreateTime(chatMessage.getCreateTime());
        chatMessageVo.setSender(sender);
        chatMessageVo.setReceiver(receiver);
        chatMessageVo.setMessage(chatMessage.getMessage());
        chatMessageVo.setOrderId(chatMessage.getOrderId());
        chatMessageVo.setMessageType(messageTypeEnum.getType());
        return chatMessageVo;
    }
}
