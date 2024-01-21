package com.hotcoin.webchat.common;

import com.alibaba.fastjson.JSON;
import com.hotcoin.webchat.model.ChannelAttribute;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.util.Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatConstants {

	private static final Logger logger = LoggerFactory.getLogger(ChatConstants.class);

	public static final AttributeKey<ChannelAttribute> CHANNEL_TOKEN_KEY = AttributeKey.valueOf("netty.channel.token");

	public static final String CHATMESSAGE_URI = "chatMessage";

	public static final String ORDERDER_URI = "order";

	public static final String SMS_URI = "sms";

	public static final String CHATMESSAGE_UNREADCOUNT = "unReadCount";

	public static final String CHATMESSAGE_HISTORY = "history";
	/**用来保存当前在线人员*/
	public static Map<String,UserInfo> onlinesUsers = new ConcurrentHashMap<>();

	/**
	 * {fid:{orderid:{clientType:ctx}}}
	 */
	public static ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>>> UserOrderClientTypeMap = new ConcurrentHashMap<String,ConcurrentHashMap<String,ConcurrentHashMap<String, ChannelHandlerContext>>>();

	/**
	 * @param userInfo
	 */
	public static void addOnlines(UserInfo userInfo) {
		onlinesUsers.putIfAbsent(Utils.getOnlineKey(String.valueOf(userInfo.getFid()),userInfo.getOrderId(),userInfo.getClientType()),userInfo);
	}

	public static void removeOnlines(String userId,String orderId,String clientType) {

		String onlineKey = Utils.getOnlineKey(userId,orderId,clientType);
		if(StringUtils.isNotBlank(onlineKey) && onlinesUsers.containsKey(onlineKey)){
			//	onlines.remove(ctx);
			onlinesUsers.remove(onlineKey);
			logger.info("removeOnlines userId:{},orderId:{},clientType:{}", userId,orderId,clientType);
		}
	}

/*	public static void removeOnlines(String sessionId) {
		if(StringUtils.isNotBlank(sessionId) && onlines.containsKey(sessionId)){
			onlines.remove(sessionId);
		}
	}*/





}
