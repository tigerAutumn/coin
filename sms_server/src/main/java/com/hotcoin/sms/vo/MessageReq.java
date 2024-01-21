package com.hotcoin.sms.vo;

import com.hotcoin.sms.Enum.SendTypeEnum;

import java.io.Serializable;

public class MessageReq implements Serializable {
    private String mobile;

    private String message;

    private Long platform;

    private Long sendType = Long.valueOf(SendTypeEnum.SMS_TEXT.getCode());

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getPlatform() {
        return platform;
    }

    public void setPlatform(Long platform) {
        this.platform = platform;
    }

    public Long getSendType() {
        return sendType;
    }

    public void setSendType(Long sendType) {
        this.sendType = sendType;
    }
}
