package com.hotcoin.sms.service;

import com.hotcoin.sms.Enum.SendTypeEnum;
import com.hotcoin.sms.dao.SmsConfigMapper;
import com.hotcoin.sms.model.SmsConfig;
import com.hotcoin.sms.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class SmsSendFactory {

    @Resource
    private SmsConfigMapper smsConfigMapper;

    @Autowired
    private SpringUtil springUtil;

    public SmsConfig findSmsConfig(String sendType){
        SmsConfig querySmsconfig = new SmsConfig();
        querySmsconfig.setAction(sendType);
        querySmsconfig.setIsactivity(1L);
        List<SmsConfig> smsConfigList = smsConfigMapper.selectList(querySmsconfig);
        if(CollectionUtils.isEmpty(smsConfigList)){
            return null;
        }else{
          if(smsConfigList.size()>1){
              Collections.sort(smsConfigList);
              return smsConfigList.get(0);
          }else{
              return smsConfigList.get(0);
          }
        }
    }

    public SMSService getSmsServer(){
        SmsConfig smsConfig = findSmsConfig(SendTypeEnum.SMS_TEXT.getText());
        if(null == smsConfig){
            return null;
        }else{
            String smsClazz = smsConfig.getSmsclazz();
           return (SMSService) springUtil.getBean(smsClazz);
        }
    }
}
