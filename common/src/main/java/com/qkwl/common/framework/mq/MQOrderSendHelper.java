package com.qkwl.common.framework.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MQOrderSendHelper {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(MQOrderSendHelper.class);

    /**
     * 委单队列
     */
    private ConcurrentLinkedQueue<Message> mqQueue = new ConcurrentLinkedQueue<Message>();

    /**
     * 发送
     */

    private OrderProducer producer;


    public void setProducer(OrderProducer producer) {
        this.producer = producer;
    }


    @PostConstruct
    public void init() {
        logger.error("Init mqEntrust Worker");
        Thread thread = new Thread(new MQOrderSendHelper.Work());
        thread.setName("MQOrderSendHelper");
        thread.start();
    }

    public void offer(String topic, String tradeid, String key, Object object) {
        if(!mqQueue.offer(new Message(topic, tradeid, key, JSON.toJSONString(object).getBytes()))) {
            logger.error("queue offer failed : " + topic);
        }
    }

    public boolean send(String topic, String tags, String key, Object object,String shardingkey) {
        Message message = new Message(topic, tags, key, JSON.toJSONString(object).getBytes());
        try {
            producer.send(message,shardingkey);
            return true;
        } catch (Exception e) {
            logger.error("message send failed : {}: exception:{}" , topic,e.getMessage());
            return false;
        }
    }
    class Work implements Runnable {
        public void run() {
            while (true) {
                if (mqQueue.isEmpty()) {
                    try {
                        Thread.sleep(100l);
                    } catch (InterruptedException e) {
                        logger.error("error:{}",e.getMessage());
                    }
                    continue;
                }
                final Message msg = mqQueue.poll();
                logger.error("send Message:{}",JSON.toJSONString(msg));
                if (msg == null) {
                    continue;
                }
                try {
                    SendResult result= producer.send(msg,msg.getTag());//getTag为交易对编号
                    logger.error("send result:{}",JSON.toJSONString(result));
                }
                catch (Exception e) {
                    logger.error("MQEntrust message:" + msg.toString()+ " send failed:" + e.getMessage() );
                    if (!MQOrderSendHelper.this.mqQueue.offer(msg)) {
                        MQOrderSendHelper.logger.error("queue onException failed : {}_{}", msg.getMsgID(), msg.getBody());
                    }

                }
            }
        }
    }
}
