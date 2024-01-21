package com.hotcoin.webchat.initializer;

import com.hotcoin.webchat.common.HeartBeatServerHandler;
import com.hotcoin.webchat.common.HttpRequestHandler;
import com.hotcoin.webchat.common.TextWebSocketFrameHandler;
import com.hotcoin.webchat.conf.SMSConfig;
import com.hotcoin.webchat.service.ChatMessageService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ChatServerInitializer extends ChannelInitializer<Channel> {

	// 检测chanel是否接受过心跳数据时间间隔（单位秒）
	private static final int READ_WAIT_SECONDS = 10;

	private ChatMessageService chatMessageService;

	private SMSConfig smsConfig;

	public ChatServerInitializer(ChatMessageService chatMessageService, SMSConfig smsConfig) {
		this.chatMessageService = chatMessageService;
		this.smsConfig = smsConfig;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		//处理日志
		pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		
		//处理心跳
		pipeline.addLast(new IdleStateHandler(READ_WAIT_SECONDS, 0, 0, TimeUnit.SECONDS));
		pipeline.addLast(new HeartBeatServerHandler());

		//websocket协议本身是基于http协议的，所以这边也要使用http解编码器
		pipeline.addLast(new HttpServerCodec());
		//以块的方式来写的处理器
		pipeline.addLast(new ChunkedWriteHandler());
		//netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
		pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 10));
		pipeline.addLast(new HttpRequestHandler("/ws",chatMessageService, smsConfig));
		pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,1048576));
		//pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,65535 * 1024 * 2));
		pipeline.addLast(new TextWebSocketFrameHandler(chatMessageService));
	}
}
