//package com.qkwl.service.umeng.mq;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.alibaba.fastjson.JSON;
//import com.aliyun.openservices.ons.api.Action;
//import com.aliyun.openservices.ons.api.ConsumeContext;
//import com.aliyun.openservices.ons.api.Message;
//import com.aliyun.openservices.ons.api.MessageListener;
//import com.qkwl.common.dto.mq.MQUmegOtc;
//import com.qkwl.common.dto.user.UserInfoExtend;
//import com.qkwl.common.mq.MQConstant;
//import com.qkwl.common.redis.RedisDBConstant;
//import com.qkwl.common.util.Constant;
//import com.qkwl.service.umeng.AndroidNotification;
//import com.qkwl.service.umeng.PushClient;
//import com.qkwl.service.umeng.android.AndroidUnicast;
//import com.qkwl.service.umeng.config.UmengProperties;
//import com.qkwl.service.umeng.dao.UserInfoExtendMapper;
//import com.qkwl.service.umeng.ios.IOSUnicast;
//import com.qkwl.service.umeng.util.JobUtils;
//
//public class MQUmengOtcListener implements MessageListener {
//
//	/**
//	 * 日志
//	 */
//	private static final Logger logger = LoggerFactory.getLogger(MQUmengOtcListener.class);
//
//	private PushClient client = new PushClient();
//	@Autowired
//	private JobUtils jobUtils;
//	@Autowired
//	UserInfoExtendMapper userInfoExtendMapper;
//	@Autowired
//	UmengProperties umengProperties;
//
//	@Override
//	public Action consume(Message message, ConsumeContext context) {
//		// body
//		String body = new String(message.getBody());
//		try {
//			// 幂等判断
//			String result = jobUtils.getRedisData(RedisDBConstant.REDIS_DB_MQ, message.getKey());
//			if (result != null && !"".equals(result)) {
//				return Action.CommitMessage;
//			}
//			// 序列号对象
//			MQUmegOtc mqUmegOtc = JSON.parseObject(body, MQUmegOtc.class);
//			//查询用户移动平台device token
//			List<UserInfoExtend> buyerList = userInfoExtendMapper.selectUserDeviceToken(mqUmegOtc.getBuyerId());
//			for (UserInfoExtend userInfoExtend : buyerList) {
//				if (userInfoExtend.getPlatform() == Constant.PLATFORM_ANDROID) {
//					sendAndroidUnicast(userInfoExtend.getDeviceToken(), mqUmegOtc.getOrderId(), "BUY", mqUmegOtc.getBuyerId());
//				} else if (userInfoExtend.getPlatform() == Constant.PLATFORM_IOS) {
//					sendIOSUnicast(userInfoExtend.getDeviceToken(), mqUmegOtc.getOrderId(), "BUY", mqUmegOtc.getBuyerId());
//				}
//			}
//			List<UserInfoExtend> sellerList = userInfoExtendMapper.selectUserDeviceToken(mqUmegOtc.getSellerId());
//			for (UserInfoExtend userInfoExtend : sellerList) {
//				if (userInfoExtend.getPlatform() == Constant.PLATFORM_ANDROID) {
//					sendAndroidUnicast(userInfoExtend.getDeviceToken(), mqUmegOtc.getOrderId(), "SELL", mqUmegOtc.getSellerId());
//				} else if (userInfoExtend.getPlatform() == Constant.PLATFORM_IOS) {
//					sendIOSUnicast(userInfoExtend.getDeviceToken(), mqUmegOtc.getOrderId(), "SELL", mqUmegOtc.getSellerId());
//				}
//			}
//			// 保存Redis
//			String uuid = message.getKey();
//			jobUtils.setRedisData(RedisDBConstant.REDIS_DB_MQ, uuid, uuid, MQConstant.MQ_EXPRIE_TIME);
//			return Action.CommitMessage;
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.debug("mq umeng otc failed : {} body : {}", message.getMsgID(), body);
//			return Action.ReconsumeLater;
//		}
//	}
//
//	private void sendAndroidUnicast(String deviceToken, Long orderId, String side, Integer userId) throws Exception {
//		AndroidUnicast unicast = new AndroidUnicast(umengProperties.getAndroidKey(), umengProperties.getAndroidSecret());
//		unicast.setDeviceToken(deviceToken);
//		unicast.setTicker("Android unicast ticker");
//		unicast.setTitle("热币网");
//		unicast.setText("您的OTC场外订单[" + orderId + "]有状态更新，请及时处理！");
//		unicast.goAppAfterOpen();
//		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
//		unicast.setProductionMode();
//		// Set customized fields
//		Map<String, Object> map = new HashMap<>();
//		map.put("event", 1);
//		Map<String, Object> param = new HashMap<>();
//		param.put("orderId", orderId);
//		param.put("side", side);
//		param.put("userId", userId);
//		map.put("param", param);
//		unicast.setCustomField(JSON.toJSONString(map));
//		client.send(unicast);
//	}
//
//	private void sendIOSUnicast(String deviceToken, Long orderId, String side, Integer userId) throws Exception {
//		IOSUnicast unicast = new IOSUnicast(umengProperties.getIosKey(), umengProperties.getIosSecret());
//		unicast.setDeviceToken(deviceToken);
//		unicast.setAlert("您的OTC场外订单[" + orderId + "]有状态更新，请及时处理！");
//		unicast.setBadge( 0);
//		unicast.setSound("default");
//		if (umengProperties.isIosIsProd()) {
//			unicast.setProductionMode();
//		} else {
//			unicast.setTestMode();
//		}
//		// Set customized fields
//		Map<String, Object> map = new HashMap<>();
//		map.put("event", 1);
//		Map<String, Object> param = new HashMap<>();
//		param.put("orderId", orderId);
//		param.put("side", side);
//		param.put("userId", userId);
//		map.put("param", param);
//		unicast.setCustomizedField("msg", JSON.toJSONString(map));
//		client.send(unicast);
//	}
//}
