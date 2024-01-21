package hotcoin.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.mq.MQEntrust;
import com.qkwl.common.mq.RabbitMQConstant;
import hotcoin.model.po.CloseoutPo;
import hotcoin.model.po.SystemRiskManagementPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 发送 平仓消息
     */
    public void sendCloseoutMQAction(SystemRiskManagementPo po, Integer coinId) {
        Double rechargeFunds = po.getRechargeFunds();
        Double debitTimes = po.getDebitTimes();
        Double debitAmount = rechargeFunds * debitTimes;
        String coinPair = po.getRechargeCoin();
        CloseoutPo closeoutDto = new CloseoutPo();
        closeoutDto.setSysCode(801);
        closeoutDto.setPayCoinId(coinId.toString());
        closeoutDto.setPayAccountNo(po.getUserId().toString());
        closeoutDto.setPayCoinAmt(debitAmount.toString());
        closeoutDto.setPayCoinName(coinPair);
        closeoutDto.setRecvCoinId(coinId.toString());
        closeoutDto.setRcvAccountNo("10000");
        closeoutDto.setBizOrderId(po.getId().toString());
        String exchangeName = RabbitMQConstant.CLOSE_POSITION_EXCHANGE;
        String routingKey = RabbitMQConstant.CLOSE_POSITION_ROUTINGKEY;
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, closeoutDto);
            log.error("send message success");
        } catch (Exception e) {
            log.error("send rocketMq fail->{}", e);
        }
    }

    /**
     * 发送 撤单消息
     */
    public void sendCancelOrderAction(Integer coinId, MQEntrust mqEntrust) {

    }
}
