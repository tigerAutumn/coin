package com.qkwl.common.dto.activity_v2.bo;

import java.io.Serializable;

/**
 * @author hf
 * @date 2019-06-10 09:20:43
 */

public class AdminActivityRegisterBo implements Serializable {
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
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 状态:-1:禁用,0:结束1:开始
     */
    private Integer status;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 创建时间
     */
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAirDropCoin() {
        return airDropCoin;
    }

    public void setAirDropCoin(String airDropCoin) {
        this.airDropCoin = airDropCoin;
    }

    public String getAirDropAmount() {
        return airDropAmount;
    }

    public void setAirDropAmount(String airDropAmount) {
        this.airDropAmount = airDropAmount;
    }

    public String getAirDropTotal() {
        return airDropTotal;
    }

    public void setAirDropTotal(String airDropTotal) {
        this.airDropTotal = airDropTotal;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
