package com.hotcoin.webchat.common;

import com.hotcoin.webchat.model.ChannelAttribute;
import com.hotcoin.webchat.util.Utils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);

	// 失败计数器：未收到client端发送的ping请求
	//private int unRecPingTimes = 0;

	// 定义服务端没有收到心跳消息的最大次数
	private static final int MAX_UN_REC_PING_TIMES = 3;

	/*@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
			logger.info("====>Heartbeat: greater than {}", 600);
			ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}*/


	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				logger.info("===服务端===(READER_IDLE 读超时)");
				// 失败计数器次数大于等于3次的时候，关闭链接，等待client重连
				ChannelAttribute channelAttribute = ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).get();
				int unRecPingTimes = channelAttribute.getUnRecPingTimes();
				if (unRecPingTimes >= MAX_UN_REC_PING_TIMES) {
					logger.info("===服务端===(读超时，关闭chanel),fid:{}",channelAttribute.getFid());
					logger.info("===服务端===(读超时，关闭chanel)");
					// 连续超过N次未收到client的ping消息，那么关闭该通道，等待client重连
					//String currentUserId = ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).get().getFid();
                    Utils.removeOnlineUser(ctx);
				} else {
					channelAttribute.setUnRecPingTimes(channelAttribute.getUnRecPingTimes() + 1);
					ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).getAndSet(channelAttribute);
				}
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
