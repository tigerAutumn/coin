package com.hotcoin.activity.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * @author hf
 * @date 2019-06-10 09:20:43
 */
@Setter
@Getter
public class AdminActivityRegisterResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 空投数量
     */
    private String airDropAmount;
    /**
     * 空投总额度
     */
    private String airDropTotal;
    /**
     * 活动开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 状态:-1:禁用,0:结束1:开始
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;
}
