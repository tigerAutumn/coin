package com.qkwl.common.mq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.annotation.Order;

/**
 * @author tr
 * @date 17-5-27
 */
@ConfigurationProperties(prefix = "aliyun.mq.topic")
public class MQTopic {

    /**
     * TOPIC-委单状态
     */
    public static String ENTRUST_STATE;

    /**
     * TOPIC-委单请求
     */
    public static String ENTRUST_REQ;
    /**
     * TOPIC-用户行为
     */
    public static String USER_ACTION;

    /**
     * TOPIC-管理员行为
     */
    public static String ADMIN_ACTION;

    /**
     * TOPIC-验证相关
     */
    public static String VALIDATE;

    /**
     * TOPIC-积分相关
     */
    public static String SCORE;

    public static String c2cEntrustStatus;
    
    /**
     * TOPIC-返佣相关
     */
    public static String COMMISSION;
    
    /**
     * TOPIC-Otc相关
     */
    public static String OTC;
    
    public  void setC2cEntrustStatus(String c2cEntrustStatus) {
        MQTopic.c2cEntrustStatus = c2cEntrustStatus;
    }

    public void setEntrustReq(String entrustReq){
        ENTRUST_REQ = entrustReq;
    }
    
    public void setEntrustState(String entrustState) {
        ENTRUST_STATE = entrustState;
    }

    public void setUserAction(String userAction) {
        USER_ACTION = userAction;
    }

    public void setAdminAction(String adminAction) {
        ADMIN_ACTION = adminAction;
    }

    public void setVALIDATE(String VALIDATE) {
        MQTopic.VALIDATE = VALIDATE;
    }

    public void setSCORE(String SCORE) {
        MQTopic.SCORE = SCORE;
    }

    public void setCOMMISSION(String COMMISSION) {
        MQTopic.COMMISSION = COMMISSION;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        // 打印属性
        System.out.println("============= redisConnect ================");
        System.out.println(this.toString());
    }


    @Override
    public String toString() {
        return "MQConfiguration [ENTRUST_STATE=" + ENTRUST_STATE + ", ENTRUST_REQ=" + ENTRUST_REQ
                + "]";
    }
    public void setOTC(String OTC) {
        MQTopic.OTC = OTC;
    }
    
}
