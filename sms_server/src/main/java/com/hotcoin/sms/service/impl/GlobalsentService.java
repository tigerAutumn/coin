package com.hotcoin.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.Enum.SendTypeEnum;
import com.hotcoin.sms.config.GlobalsentSMSConfig;
import com.hotcoin.sms.config.TimeZoneConfig;
import com.hotcoin.sms.model.SmsMessage;
import com.hotcoin.sms.service.SMSService;
import com.hotcoin.sms.service.SmsMessageService;
import com.hotcoin.sms.util.JodaTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service(value = "globalsentService")
public class GlobalsentService implements SMSService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalsentService.class);

    @Autowired
    private GlobalsentSMSConfig globalsentSMSConfig;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    @Override
    public void sendSms(String mobile, String message,Long platform,Long sendType) {
        String host = globalsentSMSConfig.getUrl();

        Map<String,String> parmas = new HashMap();

        parmas.put("access_key",globalsentSMSConfig.getAccessKey());
        parmas.put("mobile",mobile);
        parmas.put("content",message);

       try {
            ArrayList request = new ArrayList();

            for (String st : parmas.keySet()) {
                request.add(URLEncoder.encode(st, "UTF-8") + "=" + URLEncoder.encode(parmas.get(st), "UTF-8"));
            }

            URL url = new URL(host + StringUtils.join(request, "&"));

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String lines;
            String result = "";

            while ((lines = reader.readLine()) != null) {
                result = result + lines;
            }

            reader.close();
            //{"code":"0000","data":{"send_id":"23408720","mobile":"8613670162346","messages":"\u3010Hotcoin\u3011test","count":1,"per_price":0.05,"count_price":0.05},"message":""}
           logger.info("GlobalsentService result：{}",result);
            JSONObject jsonObj = (JSONObject) JSON.parse(result);
            String code = jsonObj.getString("code");
            if("0000".equalsIgnoreCase(code)){
                String data =jsonObj.getString("data");;
                JSONObject dataJsonObj = (JSONObject) JSON.parse(data);
                SmsMessage smsMessage = new SmsMessage();
                smsMessage.setSendId(dataJsonObj.getString("send_id"));
                smsMessage.setMobile(mobile);
                smsMessage.setContent(message);
                smsMessage.setSendchannel(GlobalsentService.class.getSimpleName());
                smsMessage.setSendtype(sendType);
                smsMessage.setPlatform(platform);
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()));
                smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
                smsMessageService.saveSmsMessage(smsMessage);
            }else{
                String data =jsonObj.getString("data");;
                JSONObject dataJsonObj = (JSONObject) JSON.parse(data);
                SmsMessage smsMessage = new SmsMessage();
                smsMessage.setSendId(dataJsonObj.getString("send_id"));
                smsMessage.setMobile(mobile);
                smsMessage.setContent(message);
                smsMessage.setSendchannel(GlobalsentService.class.getSimpleName());
                smsMessage.setSendtype(sendType);
                smsMessage.setPlatform(platform);
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()));
                smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
                smsMessageService.saveSmsMessage(smsMessage);
            }


        }catch (Exception e){
            logger.info("GlobalsentService sendsms exception e:{} ",e);
        }

    }
}
