package com.qkwl.common.framework.mq;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import com.qkwl.common.dto.mq.IMQEntrust;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.mq.MQCommission;
import com.qkwl.common.mq.MQConstant;
import com.qkwl.common.mq.MQTopic;

/**
 * 佣金队列发送公共接口
 */

public class CommissionHelper {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommissionHelper.class);
	
	/**
	 * 返佣队列
	 */
	private ConcurrentLinkedQueue<Message> commissionQueue = new ConcurrentLinkedQueue<Message>();
	
	private Producer commissionProducer;

	public void setCommissionHelperProducer(Producer commissionProducer) {
		this.commissionProducer = commissionProducer;
	}
	
	@PostConstruct
	public void init() {
		Thread thread = new Thread(new Work());
		thread.setName("CommissionHelper");
		thread.start();
	}
	
	public void SendAmountFee(BigDecimal sellAmountFee, IMQEntrust sellEntrust, boolean isSellFee,
							  BigDecimal buyCountFee, IMQEntrust buyEntrust, boolean isBuyFee){
		MQCommission mqCommission = new MQCommission();
		if (isSellFee) {
			mqCommission.setSellAmountFee(sellAmountFee);
			mqCommission.setSellInviteeId((int)sellEntrust.getFuid());
			mqCommission.setSellEntrustId(new BigInteger(sellEntrust.getFentrustid()));
			mqCommission.setSellStatus("Y");
		}
		if (isBuyFee) {
			mqCommission.setBuyCountFee(buyCountFee);
			mqCommission.setBuyInviteeId((int)buyEntrust.getFuid());
			mqCommission.setBuyEntrustId(new BigInteger(buyEntrust.getFentrustid()));
			mqCommission.setBuyStatus("Y");
		}
		Message message = new Message(MQTopic.COMMISSION, MQConstant.TAG_COMMISSION,
				JSON.toJSONBytes(mqCommission));
		message.setKey("COMMISSION_" + UUID.randomUUID().toString());
		try {
			logger.info("=====返佣队列发送key：" +message.getKey() + "=="+ "topic " + MQTopic.COMMISSION + "================");
			commissionProducer.send(message);
		} catch (ONSClientException e) {
			logger.error("CommissionMQ send failed " + e.getMessage());
		}
	}
    public void SendAmountFee(BigDecimal sellAmountFee, FEntrust sellEntrust,boolean isSellFee,
                              BigDecimal buyCountFee, FEntrust buyEntrust, boolean isBuyFee){
        MQCommission mqCommission = new MQCommission();
        if (isSellFee) {
            mqCommission.setSellAmountFee(sellAmountFee);
            mqCommission.setSellInviteeId(sellEntrust.getFuid());
            mqCommission.setSellEntrustId(sellEntrust.getFid());
            mqCommission.setSellStatus("Y");
        }
        if (isBuyFee) {
            mqCommission.setBuyCountFee(buyCountFee);
            mqCommission.setBuyInviteeId(buyEntrust.getFuid());
            mqCommission.setBuyEntrustId(buyEntrust.getFid());
            mqCommission.setBuyStatus("Y");
        }
        Message message = new Message(MQTopic.COMMISSION, MQConstant.TAG_COMMISSION,
                JSON.toJSONBytes(mqCommission));
        message.setKey("COMMISSION_" + UUID.randomUUID().toString());
        try {
            logger.info("==========================返佣队列发送key：" +message.getKey() + "==================");
            commissionProducer.send(message);
        } catch (ONSClientException e) {
            logger.error("CommissionMQ send failed");
        }
    }
    
    //异步方法
    public void OfferAmountFee(BigDecimal sellAmountFee, IMQEntrust sellEntrust, boolean isSellFee,
    		BigDecimal buyCountFee, IMQEntrust buyEntrust, boolean isBuyFee){
    	MQCommission mqCommission = new MQCommission();
    	if (isSellFee) {
    		mqCommission.setSellAmountFee(sellAmountFee);
    		mqCommission.setSellInviteeId((int)sellEntrust.getFuid());
    		mqCommission.setSellEntrustId(new BigInteger(sellEntrust.getFentrustid()));
    		mqCommission.setSellStatus("Y");
    	}
    	if (isBuyFee) {
    		mqCommission.setBuyCountFee(buyCountFee);
    		mqCommission.setBuyInviteeId((int)buyEntrust.getFuid());
    		mqCommission.setBuyEntrustId(new BigInteger(buyEntrust.getFentrustid()));
    		mqCommission.setBuyStatus("Y");
    	}
    	String key = "COMMISSION_" + UUID.randomUUID().toString();
		String topic = MQTopic.COMMISSION;
		String tags = MQConstant.TAG_COMMISSION;
		if(!commissionQueue.offer(new Message(topic, tags, key, JSON.toJSONString(mqCommission).getBytes()))) {
			logger.error("commissionQueue offer failed : " + topic);
		}
    }
    public void OfferAmountFee(BigDecimal sellAmountFee, FEntrust sellEntrust,boolean isSellFee,
    		BigDecimal buyCountFee, FEntrust buyEntrust, boolean isBuyFee){
    	MQCommission mqCommission = new MQCommission();
    	if (isSellFee) {
    		mqCommission.setSellAmountFee(sellAmountFee);
    		mqCommission.setSellInviteeId(sellEntrust.getFuid());
    		mqCommission.setSellEntrustId(sellEntrust.getFid());
    		mqCommission.setSellStatus("Y");
    	}
    	if (isBuyFee) {
    		mqCommission.setBuyCountFee(buyCountFee);
    		mqCommission.setBuyInviteeId(buyEntrust.getFuid());
    		mqCommission.setBuyEntrustId(buyEntrust.getFid());
    		mqCommission.setBuyStatus("Y");
    	}
    	String key = "COMMISSION_" + UUID.randomUUID().toString();
		String topic = MQTopic.COMMISSION;
		String tags = MQConstant.TAG_COMMISSION;
    	if(!commissionQueue.offer(new Message(topic, tags, key, JSON.toJSONString(mqCommission).getBytes()))) {
			logger.error("commissionQueue offer failed : " + topic);
		}
    }
    class Work implements Runnable {
		public void run() {
			while (true) {
				if (commissionQueue.isEmpty()) {
					try {
						Thread.sleep(100l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				final Message msg = commissionQueue.poll();
				if (msg == null) {
					continue;
				}
				commissionProducer.sendAsync(msg, new SendCallback() {
        			@Override
        			public void onSuccess(SendResult sendResult) {
        			}
        			@Override
        			public void onException(OnExceptionContext context) {
        				if (!commissionQueue.offer(msg)){
            				logger.error("commissionQueue onException failed : {}_{}", msg.getMsgID(), msg.getBody());
        				}
        			}
        		});
			}
		}
	}
    
}
