package com.hotcoin.sms.controller;

import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.model.SmsMessage;
import com.hotcoin.sms.service.SmsMessageService;
import com.hotcoin.sms.service.impl.GlobalsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meilian")
public class MeilianController {
    @Autowired
    private SmsMessageService smsMessageService;

    @RequestMapping(value = "/smsCallBack", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String sendSMSCallBack(@RequestBody String data){
        String[] res = data.split(",");
        String sendId = res[1];
        String status = res[3];
        SmsMessage rstSmsMessage = smsMessageService.get(sendId, GlobalsentService.class.getName());
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSendId(sendId);
        smsMessage.setSendchannel(GlobalsentService.class.getName());
        smsMessage.setStatus(Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()));
        if(null!=rstSmsMessage) {
            smsMessage.setMobile(rstSmsMessage.getMobile());
            if ("DELIVRD".equalsIgnoreCase(status)) {
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()));
            }
        }
        smsMessageService.update(smsMessage);
        return "";
    }
}
