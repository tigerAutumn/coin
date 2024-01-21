package com.hotcoin.notice.sms.provider;

import com.hotcoin.notice.entity.MessageTemplateEntity;

public interface SmsProvider {

    /**
     * 发送短信
     * @param mobile  手机号
     * @param message 消息
     * @param template 用于导入模版
     * @return 成功返回提供商发送id，失败返回null
     */
    public String sendSms(String mobile, String message, MessageTemplateEntity template);
    
    
    public String getName();
    
}