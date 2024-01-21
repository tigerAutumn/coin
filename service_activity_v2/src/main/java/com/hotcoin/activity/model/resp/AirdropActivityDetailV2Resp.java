package com.hotcoin.activity.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
@Setter
@Getter
public class AirdropActivityDetailV2Resp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     */
    private Integer activityType;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 空投币种
     */
    private String airDropCoin;
    /**
     * 活动币种
     */
    private String activityCoin;
    /**
     * 空投数量
     */
    private String airDropTotal;
    /**
     * 空投时间
     */
    private Date airDropTime;
    /**
     * 发放状态:-1:发放失败,0:待发放,1发放成功
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 生成该条记录时间
     */
    private Date createTime;
}
