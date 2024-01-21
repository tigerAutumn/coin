package com.hotcoin.sms.vo;

import java.io.Serializable;
import java.util.Locale;

public class OTCMessageReq implements Serializable {

    //手机号
    private String uid;

    //币种
    private String coinName;

    //订单号
    private String orderNo;

    //模板ID
    private String modelId;

    private String coinCount;

    //平台
    private String platform;

    //语言 eg:Locale.CHINA.toString()
    private String locale;

    public OTCMessageReq() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(String coinCount) {
        this.coinCount = coinCount;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
