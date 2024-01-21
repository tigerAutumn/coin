package com.hotcoin.webchat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.webchat.Enum.PlatformEnum;
import com.hotcoin.webchat.conf.SMSConfig;
import com.hotcoin.webchat.model.FUser;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SMSService {
    private static final Logger logger = LoggerFactory.getLogger(SMSService.class);

    /**
     * 发送短信
     * @param recvFuser
     * @param orderNo
     * @param smsConfig
     */
    public void sendSms(FUser recvFuser,String orderNo, SMSConfig smsConfig) {
        String areaCode = recvFuser.getFareacode();
        String phone = recvFuser.getFtelephone();
        if(StringUtils.isBlank(phone)){
            logger.error("手机号为空");
        }else {
            if (!areaCode.equals("86")) {
                if (phone.charAt(0) == '0') {
                    phone = phone.substring(1, phone.length());

                }
                phone = areaCode + phone;

                send253InternationalSms(smsConfig, phone, "【HOTCOIN】the OTC order [" + orderNo + "] has a status update, please handle it in time!");
            } else {
                //send253NormalSMS(smsConfig, recvFuser, "您的OTC场外订单[" + orderNo + "]有状态更新，请及时处理！");
                send253InternationalSms(smsConfig, areaCode+recvFuser.getFtelephone(), "【HOTCOIN】您的OTC场外订单[" + orderNo + "]有状态更新，请及时处理！");
            }
        }
        //由于现在是走国际短信，所以如果是国内号码也要强制加上areacode
        logger.info("send sms orderNo:{},phone:{}", orderNo,phone);

    }

    /**
     * 国内短信发送
     * @param smsConfig
     * @param recvFuser
     * @param sendMessage
     * @return
     */
    private boolean send253NormalSMS(SMSConfig smsConfig, FUser recvFuser, String sendMessage){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", smsConfig.getAccessKey());
        jsonObject.put("password", smsConfig.getSecretKey());
        jsonObject.put("msg",sendMessage);
        jsonObject.put("phone",recvFuser.getFtelephone());

        String submitString = jsonObject.toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(smsConfig.getUrl())
                .post(RequestBody.create(MediaType.parse("application/json"), submitString))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            JSONObject jsonObj = (JSONObject) JSON.parse(execute.body().string());
            logger.info("send253NormalSMS return :{} ",jsonObj);
            return "1".equals(jsonObject.getString("code"));
        } catch (IOException e) {
            logger.error("send253NormalSMS recvFuser:{},sendMessage:{},except:{}" ,recvFuser,sendMessage, e);
        }
        return false;
    }


    private boolean send253InternationalSms(SMSConfig smsConfig,String phone, String sendMessage){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platform", Long.valueOf(PlatformEnum.OTC.getCode()));
        jsonObject.put("message",sendMessage);
        jsonObject.put("mobile",phone);

        String submitString = jsonObject.toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(smsConfig.getUrl())
                .post(RequestBody.create(MediaType.parse("application/json"), submitString))
                .build();
        try {
            Response execute = okHttpClient.newCall(request).execute();
            JSONObject jsonObj = (JSONObject) JSON.parse(execute.body().string());
            logger.info("send253NormalSMS return :{} ",jsonObj);
            return "200".equals(jsonObject.getString("code"));
        } catch (IOException e) {
            logger.error("send253NormalSMS phone:{},sendMessage:{},except:{}" ,phone,sendMessage, e);
        }
        return false;
    }

    /**
     * 国际短信发送
     * @param smsConfig
     * @param phone
     * @param sendMessage
     * @return
     */
    /*private boolean send253InternationalSms(SMSConfig smsConfig,String phone, String sendMessage) {
        try {
            // 创建StringBuffer对象用来操作字符串
            StringBuilder sb = new StringBuilder();
            sb.append(smsConfig.getInternationalUrl());
            sb.append("?");
            // APIKEY
            sb.append("un=");
            sb.append(smsConfig.getInternationalAccessKey());
            // 用户名
            sb.append("&pw=");
            sb.append(smsConfig.getInternationalSecretKey());
            // 向StringBuffer追加手机号码
            sb.append("&da=");
            sb.append(phone);
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
            connection.disconnect();
            // 输出结果
            JSONObject jsonObj = (JSONObject) JSON.parse(inputline);
            if("true".equals(jsonObj.getString("success"))){
                logger.info("send253InternationalSms success, phone:{}", phone);
                return true;
            }else{
                logger.info("send253InternationalSms fail, phone:{}", phone);
                return false;
            }
            *//*String result[] = inputline.split(",");
            logger.info("send253InternationalSms smsconfig:{},phone:{},",smsConfig,phone);
            if (result[1] != null && !result[1].equals("0")) {
                //logger.info("send253InternationalSms URL :{}",sb.toString());
                logger.info("send253InternationalSms fail, phone:{}, {}", phone, inputline);
                return false;
            }*//*
        } catch (Exception e) {
            logger.error("send253InternationalSms failed,info:{},phone:{},sendMessage:{},except:{}", smsConfig,phone,sendMessage, e);
            return false;
        }
    }*/

}
