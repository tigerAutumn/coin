package com.hotcoin.sms.service;

import com.hotcoin.sms.dao.SmsMessageMapper;
import com.hotcoin.sms.model.SmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsMessageService {

    @Autowired
    private SmsMessageMapper smsMessageMapper;

    @Transactional
    public int saveSmsMessage(SmsMessage smsMessage){
       return smsMessageMapper.save(smsMessage);
    }


    @Transactional
    public void update(SmsMessage smsMessage){
         smsMessageMapper.update(smsMessage);
    }

    public SmsMessage get(String sendId,String sendchannel){
        return  smsMessageMapper.get(sendId,sendchannel);
    }
}
