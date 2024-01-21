package com.hotcoin.webchat.common;


import com.alibaba.fastjson.JSON;
import com.hotcoin.webchat.Enum.ClientTypeEnum;
import com.hotcoin.webchat.Enum.MessageTypeEnum;
import com.hotcoin.webchat.conf.SMSConfig;
import com.hotcoin.webchat.model.*;
import com.hotcoin.webchat.service.ChannelSendService;
import com.hotcoin.webchat.service.ChatMessageService;
import com.hotcoin.webchat.service.SMSService;
import com.hotcoin.webchat.util.RequestParserUtils;
import com.hotcoin.webchat.util.TransformerUtils;
import com.hotcoin.webchat.util.Utils;
import com.hotcoin.webchat.vo.ChatMessageVo;
import com.hotcoin.webchat.vo.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.util.*;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private Logger loger = LoggerFactory.getLogger(HttpRequestHandler.class);

	private final String webUri;

	private ChatMessageService chatMessageService;

	private SMSConfig smsConfig;

	public HttpRequestHandler(String webUri, ChatMessageService chatMessageService, SMSConfig smsConfig) {
		this.webUri = webUri;
		this.chatMessageService = chatMessageService;
		this.smsConfig = smsConfig;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		loger.info("====HttpRequestHandler channelRead0=======> {}, {}", webUri, request.uri());
		
		String uri = StringUtils.substringBefore(request.uri(), "?");
		if ("/health".equalsIgnoreCase(uri)){
			// 健康检查
			ResponseJson(ctx, request, JSON.toJSONString("yes"));
			return;
		}
		if(webUri.equalsIgnoreCase(uri)) {//获取webSocket参数
			QueryStringDecoder query = new QueryStringDecoder(request.uri());
            Map<String, List<String>> map = query.parameters();
            String sender = "";
			String clientType = "";
            String receiver = "";
            String orderId="";
			loger.info("====ChatConstants.onlinesUsers size=======> {},ChatConstants.onlinesUsers:{}", ChatConstants.onlinesUsers.size(),JSON.toJSONString(ChatConstants.onlinesUsers));
			ClientTypeEnum clientTypeEnum = null;
            loger.info("====HttpRequestHandler channelRead0 map=======> {}", JSON.toJSONString(map));
            for (Map.Entry<String, List<String>> attr : map.entrySet()) {
                for (String attrVal : attr.getValue()) {
                   if("sender".equals(attr.getKey())){
                       sender = attrVal;
                   }else if("receiver".equals(attr.getKey())){
                       receiver = attrVal;
                    }else if("clientType".equals(attr.getKey())){
					   clientType = attrVal;
					   clientTypeEnum =  ClientTypeEnum.fromValue(clientType);
				   }else if("orderId".equals(attr.getKey())){
                       orderId = attrVal;
                   }
                }
            }
            if(StringUtils.isNotBlank(sender) &&StringUtils.isNotBlank(orderId) && StringUtils.isNotBlank(clientType)){
            	String senderNickName = "";
				String onlineKey = Utils.getOnlineKey(sender,orderId,clientType);
            	if(ChatConstants.onlinesUsers.containsKey(onlineKey)){
            		   senderNickName = ChatConstants.onlinesUsers.get(onlineKey).getFnickname();
				}else{
					FUser fUser = chatMessageService.selectByPrimaryKey(Long.valueOf(sender));
					if(null !=fUser){
						senderNickName = fUser.getFnickname();
					}else{
						senderNickName = String.valueOf(sender);
					}
					//senderNickName = String.valueOf(sender);
				}
				UserInfo sendUser = new UserInfo(Long.valueOf(sender),orderId,senderNickName,clientTypeEnum,ctx);
				ChatConstants.addOnlines(sendUser);
				Utils.putUserOrderClientType(sender,orderId,clientType,ctx);
			//	loger.info("http ChatConstants.UserOrderClientTypeMap:{}",JSON.toJSONString(ChatConstants.UserOrderClientTypeMap));
				ChannelAttribute channelAttribute = new ChannelAttribute();
				channelAttribute.setFid(sender);
				channelAttribute.setUnRecPingTimes(0);
				channelAttribute.setClientType(clientType);
				ctx.channel().attr(ChatConstants.CHANNEL_TOKEN_KEY).getAndSet(channelAttribute);
            }
			request.setUri(uri);
			ctx.fireChannelRead(request.retain());
		}else if(ChatConstants.CHATMESSAGE_URI.equalsIgnoreCase(uri.split("/")[2])){
			//http://ip:port/service_otc_chat/chatMessage/22/history/0001
			loger.info("=======HttpRequestHandler chatMessage=======");
			String unReadCount = uri.split("/")[4];
			String uriRecvId =uri.split("/")[3];
			String uriOrderId =uri.split("/")[5];
			ChatMessage qChatMessage = new ChatMessage();
			qChatMessage.setOrderId(uriOrderId);
			qChatMessage.setReceiver(uriRecvId);


			if(StringUtils.isNotBlank(unReadCount)){
				if(ChatConstants.CHATMESSAGE_UNREADCOUNT.equalsIgnoreCase(unReadCount)){
					unReadCount(uri,ctx,qChatMessage,request);
				}else if(ChatConstants.CHATMESSAGE_HISTORY.equalsIgnoreCase(unReadCount)){
					selectList(uri,ctx,qChatMessage,request);
				}
			}


		}else if(ChatConstants.ORDERDER_URI.equalsIgnoreCase(uri.split("/")[2])){
			order(uri,ctx,request);
		}else if(ChatConstants.SMS_URI.equalsIgnoreCase(uri.split("/")[2])){
			sendSMS(uri,ctx,request);
		}else{
			if(HttpUtil.is100ContinueExpected(request)) {
				send100ContinueExpected(ctx);
			}
			
			RandomAccessFile file = new RandomAccessFile("", "r");
			HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
			
			boolean keepAlive = HttpUtil.isKeepAlive(request);
			if(keepAlive) {
				response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			}
			ctx.write(response);
			
			if(ctx.pipeline().get(SslHandler.class) == null) {
				ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
			}else {
				ctx.write(new ChunkedNioFile(file.getChannel()));
			}
			
			ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			if(!keepAlive) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
			
			file.close();
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	private void send100ContinueExpected(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONFLICT);
		ctx.writeAndFlush(response);		
	}

	/**
	 * 获取请求的内容
	 * @param request
	 * @return
	 */
	private String parseJosnRequest(FullHttpRequest request) {
		ByteBuf jsonBuf = request.content();
		String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
		return jsonStr;
	}

	/**
	 * 响应HTTP的请求
	 * @param ctx
	 * @param req
	 * @param jsonStr
	 */
	private void ResponseJson(ChannelHandlerContext ctx, FullHttpRequest req ,String jsonStr)
	{

		boolean keepAlive = HttpUtil.isKeepAlive(req);
		byte[] jsonByteByte = jsonStr.getBytes();
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(jsonByteByte));

		//返回是字符串
		// response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
		// 返回值是json
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");

		//允许跨域访问
		 response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
		 response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Origin, X-Requested-With, Content-Type, Accept");
		 response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST, PUT,DELETE");

		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

		if (!keepAlive) {
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		}
	}

	private void unReadCount(String uri,ChannelHandlerContext ctx, ChatMessage qChatMessage, FullHttpRequest request){

		ResponseMessage<Integer> responseMessage = new ResponseMessage<>();
		//把客户端的请求数据格式化为Json对象
		loger.info("chatMessage request:{}",request);
		Map<String, String> requestMap = null;
		try {
			requestMap = new RequestParserUtils(request).parse();
			loger.info("chatMessage requestMap:{}",JSON.toJSONString(requestMap));
		} catch (Exception e) {
			loger.info("=======HttpRequestHandler requestMap error json:{}", requestMap);
			responseMessage = responseMessage.error(0,"请求参数格式不正确");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}

		loger.info("chatMessage requestMap:{}",requestMap);
		int unRead = chatMessageService.selectUnReadCount(qChatMessage);
		responseMessage = responseMessage.success(unRead);
		ResponseJson(ctx,request, JSON.toJSONString(responseMessage));

	}

	private void order(String uri,ChannelHandlerContext ctx, FullHttpRequest request){

		ResponseMessage<String> responseMessage = new ResponseMessage<String>();
		//把客户端的请求数据格式化为Json对象
		loger.info("chatMessage request:{}",request);
		Map<String, String> requestMap = null;
		try {
			requestMap = new RequestParserUtils(request).parse();
			loger.info("chatMessage requestMap:{}",JSON.toJSONString(requestMap));
		} catch (Exception e) {
			loger.info("=======HttpRequestHandler requestMap error json:{}", requestMap);
			responseMessage = responseMessage.error("fail","请求参数格式不正确");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}
		loger.info("chatMessage requestMap:{}",requestMap);

		String sendId =  requestMap.get("sender").toString();
		String recvId = requestMap.get("receiver").toString();
		String orderId =requestMap.get("orderId").toString();
		String clientType = requestMap.get("clientType").toString();
		String msgText = requestMap.get("message").toString();
		String messageType = requestMap.get("messageType").toString();
		if(StringUtils.isBlank(sendId)||StringUtils.isBlank(recvId)||StringUtils.isBlank(orderId)||StringUtils.isBlank(clientType)||StringUtils.isBlank(messageType)||StringUtils.isBlank(msgText)){
			responseMessage = responseMessage.error("fail","请求参数缺失");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}
		String onelineKey = Utils.getOnlineKey(sendId,orderId,clientType);
		Date sendTime = new Date();
		if(null != requestMap.get("time")){
		long ltime =Long.valueOf(String.valueOf(requestMap.get("time")));
			sendTime = new Date(ltime);
		}
		if(StringUtils.isNotBlank(onelineKey)) {
			UserInfo send = ChatConstants.onlinesUsers.get(onelineKey);
			FUser recvFuser = chatMessageService.selectByPrimaryKey(Long.valueOf(recvId));
			FUser sendFuser = chatMessageService.selectByPrimaryKey(Long.valueOf(sendId));
			if(null == recvFuser||null == sendFuser){
				responseMessage = responseMessage.error("fail","非法请求");
				ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
				return;
			}

			new ChannelSendService(messageType, recvId, orderId, sendTime, msgText, clientType, send, chatMessageService).send(sendFuser,recvFuser);
			//chatMessageService.insert(chatMessage);
			//int unRead = chatMessageService.selectUnReadCount(qChatMessage);
			responseMessage = responseMessage.success("OK");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
		}else{
			responseMessage = responseMessage.error("fail","请求参数格式不正确缺失");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}

	}


	private void sendSMS(String uri,ChannelHandlerContext ctx, FullHttpRequest request){
		ResponseMessage<String> responseMessage = new ResponseMessage<String>();
		//把客户端的请求数据格式化为Json对象
		loger.info("chatMessage request:{}",request);
		Map<String, String> requestMap = null;
		try {
			requestMap = new RequestParserUtils(request).parse();
			loger.info("chatMessage requestMap:{}",JSON.toJSONString(requestMap));
		} catch (Exception e) {
			loger.info("=======HttpRequestHandler requestMap error json:{}", requestMap);
			responseMessage = responseMessage.error("fail","请求参数格式不正确");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}
		loger.info("chatMessage requestMap:{}",requestMap);

		String recvId = requestMap.get("receiver").toString();
		String orderNo =requestMap.get("orderNo").toString();
		if(StringUtils.isBlank(recvId)||StringUtils.isBlank(orderNo)){
			responseMessage = responseMessage.error("fail","请求参数缺失");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}
		FUser recvFuser = chatMessageService.selectByPrimaryKey(Long.valueOf(recvId));
		if(null == recvFuser){
			responseMessage = responseMessage.error("fail","非法请求");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}
		try {
			new SMSService().sendSms(recvFuser, orderNo, smsConfig);
			loger.info("sendSms success recvId:{},orderNo:{} ", recvId,orderNo);
			responseMessage = responseMessage.success("OK");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
		} catch (Exception e) {
			loger.error("sendSms error recvId:{},orderNo:{},e:{}", recvId,orderNo,e);
			responseMessage = responseMessage.error("fail","sendSms fail");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}

	}
	private void selectList(String uri,ChannelHandlerContext ctx, ChatMessage qChatMessage, FullHttpRequest request){
		List<ChatMessageVo> chatMessageVoList = new ArrayList<>();
		ResponseMessage<List<ChatMessageVo>> responseMessage = new ResponseMessage<>();
		//把客户端的请求数据格式化为Json对象
		loger.info("chatMessage request:{}",request);
		Map<String, String> requestMap = null;
		try {
			requestMap = new RequestParserUtils(request).parse();
			loger.info("chatMessage requestMap:{}",JSON.toJSONString(requestMap));
		} catch (Exception e) {
			loger.info("=======HttpRequestHandler requestMap error json:{}", requestMap);
			responseMessage = responseMessage.error(chatMessageVoList,"请求参数格式不正确");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}

		loger.info("chatMessage requestMap:{}",requestMap);

		Integer pageNum = (null == requestMap || StringUtils.isBlank(requestMap.get("pageNum")))?1:Integer.parseInt(requestMap.get("pageNum"));
		Integer pageSize = (null == requestMap || StringUtils.isBlank(requestMap.get("pageSize")))?10:Integer.parseInt(requestMap.get("pageSize"));
		List<ChatMessage> chatMessageList = null;
		try {
			 chatMessageList = chatMessageService.selectList(qChatMessage, pageNum, pageSize);
		}catch (Exception ex){
			loger.info("=======HttpRequestHandler requestMap error json:{}", requestMap);
			responseMessage = responseMessage.error(chatMessageVoList,"网络链接异常，请联系系统管理员");
			ResponseJson(ctx, request, JSON.toJSONString(responseMessage));
			return;
		}

		for(ChatMessage chatMessage: chatMessageList){
			UserInfo sender = new UserInfo(chatMessage.getSender(),chatMessage.getOrderId(),chatMessage.getSenderFnickname(), ClientTypeEnum.PLATFORM,ctx);
			Map<String, UserInfo> receiverMap = new HashMap<>();
			String[] receivers = chatMessage.getReceiver().split(",");
			String[] receiverFnicknames = chatMessage.getReceiverFnickname().split(",");
			for(int i=0;i<receivers.length;i++){
				if(StringUtils.isNotBlank(receivers[i])){
					UserInfo receiver = new UserInfo(Long.valueOf(receivers[i]),chatMessage.getOrderId(),receiverFnicknames[i], ClientTypeEnum.PLATFORM,ctx);
					receiverMap.put(Utils.getUserClientTypeKey(chatMessage.getOrderId(),ClientTypeEnum.PLATFORM.getValue()),receiver);
				}
			}
			ChatMessageVo chatMessageVo = TransformerUtils.getChatMessageVo(chatMessage,sender, receiverMap, MessageTypeEnum.fromType(chatMessage.getMsgType()));
			chatMessageVoList.add(chatMessageVo);
		}
		responseMessage = responseMessage.success(chatMessageVoList);
		ResponseJson(ctx,request, JSON.toJSONString(responseMessage));

	}
}
