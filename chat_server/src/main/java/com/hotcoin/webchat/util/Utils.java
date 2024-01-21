package com.hotcoin.webchat.util;

import com.alibaba.fastjson.JSON;
import com.hotcoin.webchat.common.ChatConstants;
import com.hotcoin.webchat.common.ChatServer;
import com.hotcoin.webchat.model.ChannelAttribute;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.service.ChannelSendService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static String getOnlineKey(String fid,String orderId,String clientType){
        if(StringUtils.isNotBlank(fid) && StringUtils.isNotBlank(orderId)&& StringUtil.isNotBlank(clientType)) {
            return fid + "_" + orderId;
        }else{
            return "";
        }
    }

    public static String getUserClientTypeKey(String fid,String clientType){
        if(StringUtils.isNotBlank(fid) && StringUtils.isNotBlank(clientType)) {
            return fid + "_" + clientType;
        }else{
            return "";
        }
    }

    public static void putUserOrderClientType(String fid,String orderId,String clientType,ChannelHandlerContext ctx){

        ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>> orderMap = ChatConstants.UserOrderClientTypeMap.get(fid);
        if(null == orderMap){
            orderMap = new ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>>();
            ConcurrentHashMap<String, ChannelHandlerContext> clientTypeMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
            clientTypeMap.put(clientType,ctx);
            orderMap.put(orderId,clientTypeMap);
        }else{
            ConcurrentHashMap<String, ChannelHandlerContext> clientTypeMap =orderMap.get(orderId);
            if(null == clientTypeMap){
                clientTypeMap = new ConcurrentHashMap<String, ChannelHandlerContext>();
                clientTypeMap.put(clientType,ctx);
                orderMap.put(orderId,clientTypeMap);
            }else{
                clientTypeMap.put(clientType,ctx);
            }
        }

        ChatConstants.UserOrderClientTypeMap.put(fid,orderMap);
    }

    public static void removeOnlineUser(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        ChannelAttribute channelAttribute = ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).get();
        logger.error("removeOnlineUser channelAttribute:{}", JSON.toJSONString(channelAttribute));
        if(null != channelAttribute) {
            String currentUserId = channelAttribute.getFid();
            String orderId = channelAttribute.getOrderId();
            String clientType = channelAttribute.getClientType();
            String onlineKey = Utils.getOnlineKey(currentUserId, orderId,clientType);
            UserInfo currenUser = ChatConstants.onlinesUsers.get(onlineKey);
            if(null == currenUser){
                logger.info("removeOnlineUser currenUser is null");
                ChatConstants.removeOnlines(currentUserId, orderId,clientType);
                ChatServer.getChannelGroup().remove(channel);
                ctx.close();
                return;
            }
            //ChatMessageVo message = new ChatMessageVo(currenUser,orderId,currenUser.getFnickname()+"用户已下线",MessageTypeEnum.NOTIC.getType(),clientType,new Date());
            ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>> orderMap = ChatConstants.UserOrderClientTypeMap.get(currentUserId);
            if(null == orderMap){
                logger.info("removeOnlineUser orderMap is null");
                ChatConstants.removeOnlines(currentUserId, orderId,clientType);
                ChatServer.getChannelGroup().remove(channel);
                ctx.close();
                return;
            }
            ConcurrentHashMap<String, ChannelHandlerContext> clientTypeMap = orderMap.get(orderId);
            if(null == clientTypeMap){
                logger.info("removeOnlineUser clientTypeMap is null");
                ChatConstants.removeOnlines(currentUserId, orderId,clientType);
                ChatServer.getChannelGroup().remove(channel);
                ctx.close();
                return;
            }
            ChannelHandlerContext removeCtx = clientTypeMap.get(clientType);
            if (null != removeCtx) {
                logger.info("removeOnlineUser clientTypeMap is not null");
                clientTypeMap.remove(clientType);
                ChatServer.getChannelGroup().remove(channel);
                ctx.close();
            }
            if (clientTypeMap.size() <= 0) {
                logger.info("removeOnlineUser clientTypeMap is not null");
                orderMap.remove(orderId);
            }
            if (orderMap.size() <= 0) {
                logger.info("removeOnlineUser orderMap is not null");
                ChatConstants.removeOnlines(currentUserId, orderId,clientType);
            }
        }else{
            logger.info("removeOnlineUser channelAttribute is null");
            ChatServer.getChannelGroup().remove(channel);
            ctx.close();
        }
    }
}