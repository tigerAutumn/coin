package com.hotcoin.webchat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hotcoin.webchat.Enum.ClientTypeEnum;
import com.hotcoin.webchat.Enum.MessageTypeEnum;
import com.hotcoin.webchat.common.ChatConstants;
import com.hotcoin.webchat.conf.SMSConfig;
import com.hotcoin.webchat.model.ChatMessage;
import com.hotcoin.webchat.model.FUser;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.util.TransformerUtils;
import com.hotcoin.webchat.util.Utils;
import com.hotcoin.webchat.vo.ChatMessageVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelSendService{

    private static final Logger logger = LoggerFactory.getLogger(ChannelSendService.class);

        private String messageType;
        private String recvId;
        private String orderId;
        private Date sendTime;
        private String msgText;
        private String clientType;
        private UserInfo send;
        private ChatMessageService chatMessageService;

        public ChannelSendService(String messageType, String recvId, String orderId, Date sendTime, String msgText, String clientType, UserInfo send,ChatMessageService chatMessageService) {
            this.messageType = messageType;
            this.recvId = recvId;
            this.orderId = orderId;
            this.sendTime = sendTime;
            this.msgText = msgText;
            this.clientType = clientType;
            this.send = send;
            this.chatMessageService = chatMessageService;
        }

    /**
     * send 一定不为空
     */
    public void send() {
        if (StringUtils.isNotBlank(recvId)) {
            String onelineKey = Utils.getOnlineKey(recvId,orderId,clientType);
            UserInfo recv = ChatConstants.onlinesUsers.get(onelineKey);
            ChatMessageVo messagevo = new ChatMessageVo(send, send, orderId,"对不起，对方不在线！", MessageTypeEnum.NOTIC.getType(), clientType, sendTime);
            FUser sendFuser = chatMessageService.selectByPrimaryKey(send.getFid());
            FUser recvFuser = chatMessageService.selectByPrimaryKey(Long.valueOf(recvId));
            //保存消息
            if(null != recvFuser) {
                saveChatMessageVo(sendFuser, recvFuser);
            }else{
                logger.error("recvId:{} not exist",recvId);
                messagevo = new ChatMessageVo(send, send, orderId,"非法请求！", MessageTypeEnum.NOTIC.getType(), clientType, sendTime);
                sendChangeMessageVo(messagevo, send);
                return;
            }

            if (null != recv) {
                messagevo = new ChatMessageVo(send, recv, orderId, msgText, messageType, clientType, sendTime);
                sendChangeMessageVo(messagevo, recv);
            }
            sendChangeMessageVo(messagevo, send);
        }else {
            // 广播取消
           /* ChatMessageVo messagevo = new ChatMessageVo(send, orderId,msgText, messageType, clientType, sendTime);
            ChatMessage chatMessage = TransformerUtils.getChatMessage(messagevo, send, messageType, MessageSendTypeEnum.BROADCAST, sendTime,2);
            chatMessageService.insert(chatMessage);
            logger.info("ChannelSendUtils message:{}", JSON.toJSONString(messagevo, SerializerFeature.DisableCircularReferenceDetect));
            ChatServer.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messagevo, SerializerFeature.DisableCircularReferenceDetect)));*/
        }
    }


    public void send(FUser sendFuser,FUser recvFuser) {
        logger.info("ChannelSendUtils sendAndEmail orderId:{}, sendId:{} recvId:{},msgText:{}",orderId, sendFuser.getFid(), recvId,msgText);
        ChatMessageVo messagevo = null;
        UserInfo recv = null;
        if (StringUtils.isNotBlank(recvId)) {
            String onlineKey = Utils.getOnlineKey(recvId, orderId,clientType);
            recv = ChatConstants.onlinesUsers.get(onlineKey);
        }
        saveChatMessageVo(sendFuser,recvFuser);
        if (null!=send) {
            if(null!=recv) {
                logger.info("ChannelSendUtils sendAndEmail sendMessageToRecv orderId:{}. message:{}",orderId, JSON.toJSONString(messagevo, SerializerFeature.DisableCircularReferenceDetect));
                messagevo = new ChatMessageVo(send, recv, orderId, msgText, messageType, clientType, sendTime);
                sendChangeMessageVo(messagevo, recv);
                sendChangeMessageVo(messagevo, send);
            }else{
                logger.info("ChannelSendUtils sendAndEmail sendMessageToSend orderId:{}. message:{}",orderId,"对不起，对方不在线");
                messagevo = new ChatMessageVo(send, send, orderId, "对不起，对方不在线！", MessageTypeEnum.NOTIC.getType(), clientType, sendTime);
                sendChangeMessageVo(messagevo, send);
            }
        }else{
            if(null!=recv) {
                logger.info("ChannelSendUtils sendAndEmail sendMessageToRecv sendId not exist orderId:{}. message:{}",orderId,messageType);
                messagevo = new ChatMessageVo(recv, recv, orderId, msgText, messageType, clientType, sendTime);
                sendChangeMessageVo(messagevo, recv);
            }else{
                logger.info("ChannelSendUtils sendAndEmail not sendMessage");
            }
        }

    }



    /**
     * 保存消息
     * @param sendFuser
     * @param recvFuser
     */
    private  void saveChatMessageVo(FUser sendFuser,FUser recvFuser) {
        //用户不在线保存消息
        ChatMessage chatMessage = TransformerUtils.getChatMessageByReceiverNotExist(sendFuser, recvFuser, orderId, msgText, messageType, clientType, sendTime, 1);
        chatMessageService.insert(chatMessage);
        logger.info("ChannelSendUtils saveOfflineChatMessageVo orderId:{},msgText:{}", orderId,msgText);
    }

    /**
     * 发送信息到用户
     * @param messagevo
     * @param userInfo
     */
    private void sendChangeMessageVo(ChatMessageVo messagevo, UserInfo userInfo) {
        ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>> ordermap =  ChatConstants.UserOrderClientTypeMap.get(userInfo.getFid().toString());
        if(null != ordermap){
            ConcurrentHashMap<String, ChannelHandlerContext> clientType = ordermap.get(userInfo.getOrderId());
            for (ClientTypeEnum clientTypeEnum : ClientTypeEnum.values() ) {
                if(null != clientType.get(clientTypeEnum.getValue())) {
                    messagevo.setClientType(clientTypeEnum.getValue());
                    logger.info("ChannelSendUtils sendChangeMessageVo send to orderId:{},userId :{}  message:{},clientTyp:{}", userInfo.getOrderId(),userInfo.getFid(),JSON.toJSONString(messagevo),clientTypeEnum.getValue());
                    clientType.get(clientTypeEnum.getValue()).channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messagevo, SerializerFeature.DisableCircularReferenceDetect)));
                }
            }
        }

    }

}
