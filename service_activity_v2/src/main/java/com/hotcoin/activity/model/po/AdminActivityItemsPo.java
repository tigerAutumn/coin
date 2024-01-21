package com.hotcoin.activity.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hf
 * @date 2019-06-10 09:20:40
 */
public class AdminActivityItemsPo implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 活动类型:0:注册空投,1:充值空投,2:交易空投,3:项目空投
     */
    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    /**
     * 活动类型:0:注册空投,1:充值空投,2:交易空投,3:项目空投
     */
    public Integer getActivityType() {
        return activityType;
    }

    /**
     * 活动名称
     */
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * 活动名称
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * 是否开启:0:未开启,1:启用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 是否开启:0:未开启,1:启用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    public AdminActivityItemsPo() {
    }
}
