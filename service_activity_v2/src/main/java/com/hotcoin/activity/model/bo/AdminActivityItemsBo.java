package com.hotcoin.activity.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:40
 */
@Setter
@Getter
public class AdminActivityItemsBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;
    /**
     * 活动类型:0:注册空投,1:充值空投,2:交易空投,3:项目空投
     */
    private Integer activityType;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 是否开启:0:未开启,1:启用
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
