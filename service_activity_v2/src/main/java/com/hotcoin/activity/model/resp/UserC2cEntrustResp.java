package com.hotcoin.activity.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *c2c充值提现表返回实体(部分字段)
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk1.8
 */
@Setter
@Getter
public class UserC2cEntrustResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;
    /**
     * 币种id
     */
    private Integer coinId;

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     *数量
     */
    private BigDecimal amount;
    /**
     * 类型
     * (1, "充值"),(2, "提现")
     */
    private Integer type;
    /**
     * 状态(1, "处理中"),
     * 	(2, "已完成"),
     * 	(3, "已撤销"),
     * 	(4,"已关闭"),
     * 	(5,"待确认");
     */
    private Integer status;
}
