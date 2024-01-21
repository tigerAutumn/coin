package com.qkwl.service.admin.component;
import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.mq.MQEntrust;
import com.qkwl.common.framework.mq.dynamic.RabbitMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 从委托订单代码复制而来
 */
@Component
public class CancelOrderComponent {
    private static final Logger logger = LoggerFactory.getLogger(CancelOrderComponent.class);
    @Autowired
    private MessageProperties messageProperties;

    @Autowired
    private CancelOrderSender cancelOrderSender;

    /**
     * 撤单
     */

    public void cancleEntrust(MQEntrust mqEntrust) {
        int ftype = EntrustTypeEnum.CANCEL.getCode();
        long time = System.currentTimeMillis();
        String ftradeId = String.valueOf(mqEntrust.getFtradeid());
        mqEntrust.setFtype(ftype);
        mqEntrust.setCreatedate(time);
        String mqKey = mqEntrust.getFtradeid() + ftype + time + "";
        cancelOrderSender.sendAsync(new RabbitMQMessage(mqKey, mqEntrust, ftradeId, messageProperties));
        logger.error("mqEntrust:{}", JSON.toJSONString(mqEntrust));
    }

}
