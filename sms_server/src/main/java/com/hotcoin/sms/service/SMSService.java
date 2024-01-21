package com.hotcoin.sms.service;

public interface SMSService {

    /**
     * 发送短信
     * @param mobile  手机号
     * @param message 消息
     * @param platform 平台
     */
    public void sendSms(String mobile, String message,Long platform,Long sendType);
}