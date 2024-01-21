package com.hotcoin.webchat.model;

import java.io.Serializable;

public class ChannelAttribute implements Serializable {

    private String fid;

    private String orderId;

    private String clientType;

    private int unRecPingTimes;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public int getUnRecPingTimes() {
        return unRecPingTimes;
    }

    public void setUnRecPingTimes(int unRecPingTimes) {
        this.unRecPingTimes = unRecPingTimes;
    }
}
