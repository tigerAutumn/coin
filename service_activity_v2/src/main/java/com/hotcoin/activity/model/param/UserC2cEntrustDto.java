package com.hotcoin.activity.model.param;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.model.param
 * @ClassName: UserC2cEntrustDto
 * @Author: hf
 * @Description:
 * @Date: 2019/6/28 14:07
 * @Version: 1.0
 */
@Setter
@Getter
public class UserC2cEntrustDto implements Serializable {

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
     * 数量
     */
    private BigDecimal amount;
    /**
     * 类型
     * (1, "充值"),(2, "提现")
     */
    private Integer type;
    /**
     * 状态(1, "处理中"),
     * (2, "已完成"),
     * (3, "已撤销"),
     * (4,"已关闭"),
     * (5,"待确认");
     */
    private Integer status;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     *
     * @param type  (1, "充值"),(2, "提现")
     * @param status * (2, "已完成"),
     * @param startTime
     * @param endTime
     */
    public UserC2cEntrustDto(Integer type, Integer status, Date startTime, Date endTime) {
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UserC2cEntrustDto() {
    }
}
