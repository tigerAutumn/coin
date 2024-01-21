package com.qkwl.admin.layui.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.admin.layui.dto.CloseoutDto;
import com.qkwl.common.dto.mq.MQEntrust;
import com.qkwl.common.framework.mq.dynamic.RabbitMQMessage;
import com.qkwl.common.mq.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.component
 * @ClassName: RabbitMqSendComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/8/22 10:55
 * @Version: 1.0
 */
@Component
@Slf4j
public class RabbitMqSendComponent {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageProperties messageProperties;
    @Autowired
    @Qualifier("orderConnectionFactory")
    private ConnectionFactory orderConnectionFactory;
    /**
     * 发送 平仓消息
     */
    public boolean sendCloseoutMQAction(Integer userId, Integer coinId, String coinPair, Double debitAmount) {
        CloseoutDto closeoutDto = new CloseoutDto();
        closeoutDto.setSysCode(801);
        closeoutDto.setPayCoinId(coinId.toString());
        closeoutDto.setPayAccountNo(userId.toString());
        closeoutDto.setPayCoinAmt(debitAmount.toString());
        closeoutDto.setPayCoinName(coinPair);
        closeoutDto.setRecvCoinId(coinId.toString());
        closeoutDto.setRcvAccountNo("10000");
        closeoutDto.setBizOrderId(String.valueOf(System.currentTimeMillis()));
        String exchangeName = RabbitMQConstant.CLOSE_POSITION_EXCHANGE;
        String routingKey = RabbitMQConstant.CLOSE_POSITION_ROUTINGKEY;

        try {

            rabbitTemplate.convertAndSend(exchangeName, routingKey, closeoutDto);
            log.error("send message success");
            return true;
        } catch (Exception e) {
            log.error("send rocketMq fail->{}", e);
            return false;
        }
    }


    /**
     * 发送 撤单消息
     */
    public void sendCancelOrderAction(MQEntrust mqEntrust) {
        String exchangeName = RabbitMQConstant.ORDER_EXCHANGE_PRE + mqEntrust.getFtradeid();
        String routingKey = RabbitMQConstant.ORDER_ROUTINGKEY_PRE + mqEntrust.getFtradeid() + RabbitMQConstant.TOPIC_SUF;
        RabbitMQMessage message = createMqMessage(mqEntrust);

        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
            log.error("send cancel order message success,data is ->{}", JSON.toJSONString(mqEntrust));
        } catch (Exception e) {
            log.error("send cancel order rocketMq fail->{}", e);
        }
    }


    private RabbitMQMessage createMqMessage(MQEntrust mqEntrust) {
        String mqKey = mqEntrust.getFentrustid() + mqEntrust.getFtype();
        String ftradeId = String.valueOf(mqEntrust.getFtradeid());
        return new RabbitMQMessage(mqKey, mqEntrust, ftradeId, messageProperties);
    }
}
