package com.qkwl.common.dto.otc;

import java.util.Date;

public class OtcOrderLog {
    private Long id;

    private Long orderId;

    private Byte beforeStatus;

    private Byte afterStatus;

    private String beforeData;

    private String afterData;

    private Date createTime;

    private Long creator;

    private String creatorName;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Byte getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(Byte beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Byte getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Byte afterStatus) {
        this.afterStatus = afterStatus;
    }

    public String getBeforeData() {
        return beforeData;
    }

    public void setBeforeData(String beforeData) {
        this.beforeData = beforeData == null ? null : beforeData.trim();
    }

    public String getAfterData() {
        return afterData;
    }

    public void setAfterData(String afterData) {
        this.afterData = afterData == null ? null : afterData.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName == null ? null : creatorName.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}