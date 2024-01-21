package com.hotcoin.sms.controller;

import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.model.SmsMessage;
import com.hotcoin.sms.service.SmsMessageService;
import com.hotcoin.sms.service.impl.GlobalsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/globalsent")
public class GlobalsentController {
    @Autowired
    private SmsMessageService smsMessageService;

    @GetMapping(value = "/smsCallBack")
    @ResponseBody
    public String sendSMSCallBack(@RequestParam("send_id")String sendId,@RequestParam("status")String status){
        SmsMessage rstSmsMessage = smsMessageService.get(sendId,GlobalsentService.class.getName());
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSendId(sendId);
        smsMessage.setSendchannel(GlobalsentService.class.getName());
        smsMessage.setStatus(Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()));
        if(null!=rstSmsMessage) {
            smsMessage.setMobile(rstSmsMessage.getMobile());
            if ("delivered".equalsIgnoreCase(status)) {
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()));
            }else if ("undelivered".equalsIgnoreCase(status)){
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()));
            }
        }
         smsMessageService.update(smsMessage);
        return "";
    }
}
