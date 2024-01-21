package hotcoin.config.mqconfig;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.config.mqconfig
 * @ClassName: RabbitMqTopic
 * @Author: hf
 * @Description:
 * @Date: 2019/8/22 11:34
 * @Version: 1.0
 */
public interface RabbitMqTopic {
    String RISK_MANAGEMENT_CLOSEOUT = "closeout";
    String RISK_MANAGEMENT_CANCEL_ORDERS = "cancelOrders";
}
