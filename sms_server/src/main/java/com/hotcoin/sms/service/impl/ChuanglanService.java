package com.hotcoin.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.config.ChuanglanSMSConfig;
import com.hotcoin.sms.config.TimeZoneConfig;
import com.hotcoin.sms.model.ChuanglanSmsSendResponse;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service(value = "chuanglanService")
public class ChuanglanService implements SMSService {

    private static final Logger logger = LoggerFactory.getLogger(ChuanglanService.class);

    @Autowired
    private ChuanglanSMSConfig chuanglanSMSConfig;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

/*    *//**
     * 发送短信
     * @param areaCode 手机区号
     * @param mobile   手机号

     *//*
    public void sendSms(String areaCode,String mobile) {
        if(StringUtils.isBlank(mobile)){
            logger.error("手机号为空");
        }else {
            if (!areaCode.equals("86")) {
                if (mobile.charAt(0) == '0') {
                    mobile = mobile.substring(1, mobile.length());

                }
                mobile = areaCode + mobile;
                send253InternationalSms(mobile, "【HOTCOIN】the OTC order [" + orderNo + "] has a status update, please handle it in time!");
            } else {
                //send253NormalSMS(smsConfig, recvFuser, "您的OTC场外订单[" + orderNo + "]有状态更新，请及时处理！");
                send253InternationalSms(areaCode+mobile, "【HOTCOIN】您的OTC场外订单[" + orderNo + "]有状态更新，请及时处理！");
            }
        }
        //由于现在是走国际短信，所以如果是国内号码也要强制加上areacode
        logger.info("send sms mobile:{}", mobile);

    }*/

    /**
     * 国内短信发送
     * @param mobile
     * @param sendMessage
     * @return
     */
    private boolean send253NormalSMS(String mobile, String sendMessage,Long platform,Long sendtype){
        try {
            logger.info("send253NormalSMS send mobile:{}", mobile);
            Map map = new HashMap();
            map.put("account",chuanglanSMSConfig.getAccessKey());//API账号
            map.put("password",chuanglanSMSConfig.getSecretKey());//API密码
            map.put("msg","【HotcoinGlobal】"+sendMessage.trim());//短信内容
            mobile = mobile.substring(2);
            map.put("phone",mobile);//手机号
            map.put("report","true");//是否需要状态报告
            //map.put("extend","123");//自定义扩展码
            String requestJson = JSON.toJSONString(map);
            logger.info("send253NormalSMS before chuanglan send sms, mobile:{},msg:{}", mobile,sendMessage.trim());
            String response = sendSmsByPost(chuanglanSMSConfig.getUrl(), requestJson);
            logger.info("send253NormalSMS after chuanglan send253NormalSMS send sms, mobile:{},response:{}", mobile,response);
           /* // 创建StringBuffer对象用来操作字符串
            StringBuilder sb = new StringBuilder();
            sb.append(chuanglanSMSConfig.getInternationalUrl());
            sb.append("?");
            // APIKEY
            sb.append("un=");
            sb.append(chuanglanSMSConfig.getInternationalAccessKey());
            // 用户名
            sb.append("&pw=");
            sb.append(chuanglanSMSConfig.getInternationalSecretKey());
            // 向StringBuffer追加手机号码
            sb.append("&da=");
            sb.append(mobile);
            // 向StringBuffer追加消息内容转URL标准码
            sb.append("&sm=");
            sb.append(URLEncoder.encode(sendMessage, "UTF-8"));
            // 是否状态报告
            sb.append("&dc=15&rd=1&rf=2&tf=3");
            // 创建url对象
            URL url = new URL(sb.toString());
            // 打开url连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置url请求方式 ‘get’ 或者 ‘post’
            connection.setRequestMethod("POST");
            // 发送
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            // 返回发送结果
            String inputline = in.readLine();
            connection.disconnect();*/
            if(StringUtils.isNotBlank(response)){
                ChuanglanSmsSendResponse chuanglanSmsSendResponse = JSON.parseObject(response, ChuanglanSmsSendResponse.class);
                String code =  chuanglanSmsSendResponse.getCode();
                String msgId = chuanglanSmsSendResponse.getMsgId();
                if(code.equals("0")) {
                    saveSmsMessage(mobile, sendMessage, platform, Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()), sendtype, msgId);
                    return true;
                }else{
                    msgId=getTimeRandomNo(mobile);
                    saveSmsMessage(mobile,sendMessage,platform,Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()),sendtype,msgId);
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            logger.error("send253NormalSMS failed, mobile:{},sendMessage:{},except:{}", mobile,sendMessage, e);
            return false;
        }
    }

    /**
     * 国际短信发送
     * @param mobile
     * @param sendMessage
     * @return
     */
    private boolean send253InternationalSms(String mobile, String sendMessage,Long platform,Long sendtype) {
        try {

            Map map = new HashMap();
            map.put("account",chuanglanSMSConfig.getInternationalAccessKey());//API账号
            map.put("password",chuanglanSMSConfig.getInternationalSecretKey());//API密码
            map.put("msg",sendMessage.trim());//短信内容
            map.put("mobile",mobile);//手机号
            map.put("report","true");//是否需要状态报告
            //map.put("extend","123");//自定义扩展码
            String requestJson = JSON.toJSONString(map);
            logger.info("send253InternationalSms before chuanglan send sms, mobile:{},sendMessage:{}", mobile,sendMessage.trim());
            String response = sendSmsByPost(chuanglanSMSConfig.getInternationalUrl(), requestJson);
            logger.info("send253InternationalSms after chuanglan send sms, mobile:{},response:{}", mobile,response);
           /* // 创建StringBuffer对象用来操作字符串
            StringBuilder sb = new StringBuilder();
            sb.append(chuanglanSMSConfig.getInternationalUrl());
            sb.append("?");
            // APIKEY
            sb.append("un=");
            sb.append(chuanglanSMSConfig.getInternationalAccessKey());
            // 用户名
            sb.append("&pw=");
            sb.append(chuanglanSMSConfig.getInternationalSecretKey());
            // 向StringBuffer追加手机号码
            sb.append("&da=");
            sb.append(mobile);
            // 向StringBuffer追加消息内容转URL标准码
            sb.append("&sm=");
            sb.append(URLEncoder.encode(sendMessage, "UTF-8"));
            // 是否状态报告
            sb.append("&dc=15&rd=1&rf=2&tf=3");
            // 创建url对象
            URL url = new URL(sb.toString());
            // 打开url连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置url请求方式 ‘get’ 或者 ‘post’
            connection.setRequestMethod("POST");
            // 发送
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            // 返回发送结果
            String inputline = in.readLine();
            connection.disconnect();*/
           if(StringUtils.isNotBlank(response)){
               ChuanglanSmsSendResponse chuanglanSmsSendResponse = JSON.parseObject(response, ChuanglanSmsSendResponse.class);
               String code =  chuanglanSmsSendResponse.getCode();
               String msgId = chuanglanSmsSendResponse.getMsgId();
               if(code.equals("0")) {
                   saveSmsMessage(mobile, sendMessage, platform, Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()), sendtype, msgId);
                   return true;
               }else{
                   msgId=getTimeRandomNo(mobile);
                   saveSmsMessage(mobile,sendMessage,platform,Long.valueOf(SendStatusEnum.SEND_FAILURE.getCode()),sendtype,msgId);
                   return false;
               }
           }
           return false;
        } catch (Exception e) {
            logger.error("send253InternationalSms failed, mobile:{},sendMessage:{},except:{}", mobile,sendMessage, e);
            return false;
        }
    }

    public static String getTimeRandomNo(String mobile){
        StringBuffer id=new StringBuffer();
        //获取当前时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String temp = sf.format(new Date());
        id.append(mobile).append(temp);
        return id.toString();
    }

    public static String sendSmsByPost(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();
            OutputStream os=httpURLConnection.getOutputStream();
            os.write(postContent.getBytes("UTF-8"));
            os.flush();
            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            logger.error("chuanglan sendSmsByPost failed, ex:{}", e);
        }
        return null;
    }

    @Override
    public void sendSms(String mobile, String message,Long platform,Long sendtype) {
        if (mobile.indexOf("86")==0) {
            this.send253NormalSMS(mobile,message,platform,sendtype);
        } else {
            //send253NormalSMS(smsConfig, recvFuser, "您的OTC场外订单[" + orderNo + "]有状态更新，请及时处理！");
            this.send253InternationalSms(mobile,message,platform,sendtype);
        }
        //由于现在是走国际短信，所以如果是国内号码也要强制加上areacode
        //logger.info("send sms mobile:{}", mobile);
       // this.send253InternationalSms(mobile,message,platform,sendtype);
    }

    private void saveSmsMessage(String mobile, String message,Long platform,Long status,Long sendtype,String msgId){
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSendId(msgId);
        smsMessage.setMobile(mobile);
        smsMessage.setContent(message);
        smsMessage.setSendchannel(ChuanglanService.class.getSimpleName());
        smsMessage.setPlatform(platform);
        smsMessage.setSendtype(sendtype);
        smsMessage.setStatus(status);
        smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
        smsMessageService.saveSmsMessage(smsMessage);
    }
}
