package com.hotcoin.webchat.common;

import com.hotcoin.webchat.conf.SMSConfig;
import com.hotcoin.webchat.initializer.ChatServerInitializer;
import com.hotcoin.webchat.service.ChatMessageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class ChatServer {
	private static final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workGroup = new NioEventLoopGroup();
	private Channel channel;

	@Autowired
	private ChatMessageService chatMessageService;

	@Autowired
	private SMSConfig smsConfig;

	public ChannelFuture start(InetSocketAddress address) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChatServerInitializer(chatMessageService,smsConfig))
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
		channel = future.channel();
		return future;
	}
	
	public void destroy() {
		if(channel != null) {
			channel.close();
		}
		
		channelGroup.close();
		workGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public static ChannelGroup getChannelGroup() {
		return channelGroup;
	}
}
