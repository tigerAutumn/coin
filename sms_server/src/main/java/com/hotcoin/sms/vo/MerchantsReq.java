package com.hotcoin.sms.vo;

import java.io.Serializable;

public class MerchantsReq implements Serializable {

    //手机号
    private String uid;

    //模板ID
    private String modelId;

    private String sendMsg;

    //平台
    private String platform;

    public MerchantsReq() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}