package com.hotcoin.sms.service.impl;

import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.config.TimeZoneConfig;
import com.hotcoin.sms.config.ZTSMSConfig;
import com.hotcoin.sms.model.SmsMessage;
import com.hotcoin.sms.service.SMSService;
import com.hotcoin.sms.service.SmsMessageService;
import com.hotcoin.sms.util.JodaTimeUtil;
import com.hotcoin.sms.util.MD5Utils;
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

@Service(value = "ztService")
public class ZTService implements SMSService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalsentService.class);

    @Autowired
    private ZTSMSConfig ztsmsConfig;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    @Override
    public void sendSms(String mobile, String message, Long platform, Long sendType) {
        //新建一个StringBuffer链接
        StringBuffer buffer = new StringBuffer();

        //String encode = "GBK"; //页面编码和短信内容编码为GBK。重要说明：如提交短信后收到乱码，请将GBK改为UTF-8测试。如本程序页面为编码格式为：ASCII/GB2312/GBK则该处为GBK。如本页面编码为UTF-8或需要支持繁体，阿拉伯文等Unicode，请将此处写为：UTF-8

        String encode = "UTF-8";

        String username = ztsmsConfig.getUsername();

        String password = ztsmsConfig.getPassword();

        String productid = ztsmsConfig.getProductid();
        String host = ztsmsConfig.getUrl();
        try {
            String password_md5 = MD5Utils.md5Password(password);
           // String contentUrlEncode = URLEncoder.encode(message, encode);
            Map<String, String> parmas = new HashMap<String, String>();
            parmas.put("username", username);
            parmas.put("password", password_md5);
            parmas.put("mobile", mobile);
            parmas.put("content", URLEncoder.encode("【Hotcoin Global】"+message, "UTF-8"));
            parmas.put("productid", productid);

            ArrayList request = new ArrayList();

            for (String st : parmas.keySet()) {
                request.add(URLEncoder.encode(st, "UTF-8") + "=" +parmas.get(st));
            }

            URL url = new URL(host+"?" + StringUtils.join(request, "&"));

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
          String sendId = "";
           String[] resultMsg = result.split(";");
           for(String smsRst : resultMsg){
               if("null".equalsIgnoreCase(smsRst)){
                   continue;
               }else{
                  if(smsRst.startsWith("1,")){
                      sendId = smsRst.split(",")[1];
                  }
               }
           }

            if (StringUtils.isNotBlank(sendId)) {
                SmsMessage smsMessage = new SmsMessage();
                smsMessage.setSendId("ztsms_"+sendId);
                smsMessage.setMobile(mobile);
                smsMessage.setContent(message);
                smsMessage.setSendchannel(GlobalsentService.class.getSimpleName());
                smsMessage.setSendtype(sendType);
                smsMessage.setPlatform(platform);
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()));
                smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
                smsMessageService.saveSmsMessage(smsMessage);
            } else {
                SmsMessage smsMessage = new SmsMessage();
                smsMessage.setSendId("ztsms_"+mobile);
                smsMessage.setMobile(mobile);
                smsMessage.setContent(message);
                smsMessage.setSendchannel(GlobalsentService.class.getSimpleName());
                smsMessage.setSendtype(sendType);
                smsMessage.setPlatform(platform);
                smsMessage.setStatus(Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()));
                smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
                smsMessageService.saveSmsMessage(smsMessage);
            }
        } catch (Exception e) {
            logger.info("ZTService sendsms exception e:{} ", e);
        }
    }
}
