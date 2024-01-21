package com.hotcoin.webchat.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hotcoin.webchat.Enum.ClientTypeEnum;
import com.hotcoin.webchat.Enum.MessageTypeEnum;
import com.hotcoin.webchat.model.ChannelAttribute;
import com.hotcoin.webchat.model.UserInfo;
import com.hotcoin.webchat.service.ChannelSendService;
import com.hotcoin.webchat.service.ChatMessageService;
import com.hotcoin.webchat.util.Utils;
import com.hotcoin.webchat.vo.ChatMessageVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

	private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

	private ChatMessageService chatMessageService;

	public TextWebSocketFrameHandler(ChatMessageService chatMessageService) {
		this.chatMessageService = chatMessageService;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("TextWebSocketFrameHandler Event====>{}", evt);
          /*
          if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
		    ctx.pipeline().remove(HttpRequestHandler.class);
			//加入当前, 上线人员推送前端，显示用户列表中去
			Channel channel = ctx.channel();
			ChatMessageVo message = new ChatMessageVo(null, "","", MessageTypeEnum.TEXT.getType(),"",new Date());
			logger.info("userEventTriggered message:{}",JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect));
			ChatServer.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect)));
			ChatServer.getChannelGroup().add(channel);
		}else {
			super.userEventTriggered(ctx, evt);
		}
           */
		if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
		    ctx.pipeline().remove(HttpRequestHandler.class);
			//加入当前, 上线人员推送前端，显示用户列表中去
			Channel channel = ctx.channel();
			/*ChatMessageVo message = new ChatMessageVo(null, "","", MessageTypeEnum.TEXT.getType(),"",new Date());
			logger.info("userEventTriggered message:{}",JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect));
			ChatServer.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect)));*/
			ChatServer.getChannelGroup().add(channel);
		}else {
			super.userEventTriggered(ctx, evt);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=6,rollbackFor=Exception.class)
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		logger.info("TextWebSocketFrameHandler channelRead0====>{}", JSON.toJSONString(msg.text()));
		JSONObject messageJson = JSON.parseObject(msg.text());
		String messageType = String.valueOf(messageJson.get("messageType"));
        String sendId =  String.valueOf(messageJson.get("sender"));
		String recvId = String.valueOf(messageJson.get("receiver"));
		String orderId =String.valueOf(messageJson.get("orderId"));
		long ltime =Long.valueOf(String.valueOf(messageJson.get("time")));

		if(messageType.equalsIgnoreCase(MessageTypeEnum.HEARTBEAT.getType())){
			ChannelAttribute channelAttribute = ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).get();
			channelAttribute.setUnRecPingTimes(0);
			ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).getAndSet(channelAttribute);
			return;
		}

		Date sendTime = new Date(ltime);
		String msgText = String.valueOf(messageJson.get("message"));
		String clientType = String.valueOf(messageJson.get("clientType"));

		logger.info("TextWebSocketFrame send0 clientType:{}", clientType);

		ClientTypeEnum clientTypeEnum =  ClientTypeEnum.fromValue(clientType);
        String onlineKey = Utils.getOnlineKey(sendId,orderId,clientType);
		UserInfo send = ChatConstants.onlinesUsers.get(onlineKey);

		if(null == clientTypeEnum){
			ChatMessageVo message = new ChatMessageVo(send, send,orderId, "对不起，非法请求！",MessageTypeEnum.WARN.getType(),clientType,sendTime);
			ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect)));
			return ;
		}
		logger.info("TextWebSocketFrame send0 message:{},clientTypeEnum:{}", clientType,clientTypeEnum.getValue());
		if(sendId == null) {
			ChatServer.getChannelGroup().writeAndFlush("OK");
			return;
		}

		new ChannelSendService(messageType, recvId, orderId, sendTime, msgText, clientType, send,chatMessageService).send();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Current channel channelInactive");
		Utils.removeOnlineUser(ctx);
	}


	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		logger.info("Current channel add");

	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		logger.info("Current channel handlerRemoved");
		offlines(ctx);
	}

	private void offlines(ChannelHandlerContext ctx) {
		Utils.removeOnlineUser(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ChannelAttribute channelAttribute = ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).get();
		logger.error("exceptionCaught channelAttribute:{},cause:{}", JSON.toJSONString(channelAttribute),cause);

		offlines(ctx);
	}
}