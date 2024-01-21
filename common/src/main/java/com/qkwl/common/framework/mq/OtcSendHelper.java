package com.qkwl.common.framework.mq;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.qkwl.common.dto.mq.MQUmegOtc;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;

public class OtcSendHelper {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(OtcSendHelper.class);
	
	private Producer otcProducer;

	public void setOtcHelperProducer(Producer otcProducer) {
		this.otcProducer = otcProducer;
	}
	
	public void SendOtcOrder(Integer buyerId, Integer sellerId, Long orderId){
		MQUmegOtc mqUmegOtc = new MQUmegOtc();
		mqUmegOtc.setBuyerId(buyerId);
		mqUmegOtc.setSellerId(sellerId);
		mqUmegOtc.setOrderId(orderId);
		Message message = new Message(MQTopic.OTC, MQConstant.TAG_OTC,
				JSON.toJSONBytes(mqUmegOtc));
		message.setKey("OTC_" + UUID.randomUUID().toString());
		try {
			otcProducer.send(message);
		} catch (ONSClientException e) {
			logger.error("OtcMQ send failed");
		}
	}
}
