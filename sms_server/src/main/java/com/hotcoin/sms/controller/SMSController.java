package com.hotcoin.sms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.sms.Enum.PlatformEnum;
import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.Enum.SendTypeEnum;
import com.hotcoin.sms.config.SMSTemplateConfig;
import com.hotcoin.sms.dao.FUserMapper;
import com.hotcoin.sms.dao.OtcMerchantMapper;
import com.hotcoin.sms.model.FUser;
import com.hotcoin.sms.model.OtcMerchant;
import com.hotcoin.sms.service.SMSService;
import com.hotcoin.sms.service.SmsSendFactory;
import com.hotcoin.sms.vo.MerchantsReq;
import com.hotcoin.sms.vo.OTCMessageReq;
import com.hotcoin.sms.vo.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SMSController {
    private static final Logger logger = LoggerFactory.getLogger(SMSController.class);

    @Autowired
    private SmsSendFactory smsSendFactory;

    @Autowired
    private SMSTemplateConfig smsTemplateConfig;

    @Autowired
    private FUserMapper fUserMapper;

    @Autowired
    private OtcMerchantMapper otcMerchantMapper;

    @RequestMapping(value = "/sendSMSForOTC", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public String sendSMSForOTC(@RequestBody OTCMessageReq otcMessageReq){
        logger.info("OTCMessageReq:{}",JSON.toJSONString(otcMessageReq));

        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> sendMap = new HashMap<String,String>();
        String coinName = otcMessageReq.getCoinName().trim();

        String orderNo = otcMessageReq.getOrderNo().trim();

        String uid = otcMessageReq.getUid().trim();

        String coinCount = otcMessageReq.getCoinCount().trim();

        String modelId = otcMessageReq.getModelId().trim();

        String platform = PlatformEnum.OTC.getCode().toString();

        String locale = otcMessageReq.getLocale();

        if(StringUtils.isBlank(uid)||StringUtils.isBlank(coinName)||StringUtils.isBlank(orderNo)||StringUtils.isBlank(modelId)){
            sendMap.put("requestparamete",JSON.toJSONString(otcMessageReq));
            responseMessage = responseMessage.error(sendMap,"paramete error");
            return JSON.toJSONString(responseMessage);
        }
        sendMap.put("uid",otcMessageReq.getUid());
        sendMap.put("message","not found sms provider");
        sendMap.put("sendStatus", SendStatusEnum.SEND_NOT.getCode()+"");
        SMSService smsService = smsSendFactory.getSmsServer();
        if(null == smsService){
            responseMessage = responseMessage.error(sendMap,"not found sms provider");
            return JSON.toJSONString(responseMessage);
        }

        try {
            FUser fUser = fUserMapper.selectByPrimaryKey(Long.valueOf(uid));
            String areaCode = fUser.getFareacode();
            String phone = fUser.getFtelephone();
            Map<String, String> replaceMap = new HashMap<String, String>();
            if(StringUtils.isBlank(phone)){
                logger.error("uid:{} phone is null",uid);
                sendMap.put("sendStatus", SendStatusEnum.SEND_FAILURE.getCode()+"");
                sendMap.put("message","phone is null");
                return JSON.toJSONString(responseMessage);
            }
            replaceMap.put("mobile", phone);
            replaceMap.put("coinName", coinName);
            replaceMap.put("orderNo", orderNo);
            replaceMap.put("coinCount", coinCount);
            String sendMessage;
            if(null == locale){
                if (!areaCode.equals("86")) {
                    if (phone.charAt(0) == '0') {
                        phone = phone.substring(1, phone.length());
                    }
                    String otcEnMapStr = smsTemplateConfig.getOtcEnMapStr();
                    Map<String, String> otcEn = JSONObject.parseObject(otcEnMapStr,Map.class);
                    String messageTemplate = otcEn.get(modelId);
                    sendMessage = getContent(replaceMap, messageTemplate);
                } else {
                    String otcZhMapStr = smsTemplateConfig.getOtcZhMapStr();
                    Map<String, String> otcZh = JSONObject.parseObject(otcZhMapStr,Map.class);
                    String messageTemplate = otcZh.get(modelId);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }
            }else{
                if(Locale.CHINA.toString().equals(locale.toString())){
                    String otcZhMapStr = smsTemplateConfig.getOtcZhMapStr();
                    Map<String, String> otcZh = JSONObject.parseObject(otcZhMapStr,Map.class);
                    String messageTemplate = otcZh.get(modelId);
                    logger.error("replaceMap:{},messageTemplate:{}",JSON.toJSON(replaceMap),messageTemplate);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }else if(Locale.KOREA.toString().equals(locale.toString())){
                    String otcKOMapStr = smsTemplateConfig.getOtcKOMapStr();
                    Map<String, String> otcKO = JSONObject.parseObject(otcKOMapStr,Map.class);
                    String messageTemplate = otcKO.get(modelId);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }else{
                    String otcEnMapStr = smsTemplateConfig.getOtcEnMapStr();
                    Map<String, String> otcEn = JSONObject.parseObject(otcEnMapStr,Map.class);
                    String messageTemplate = otcEn.get(modelId);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }
            }
            phone = areaCode + phone;

            smsService.sendSms(phone,sendMessage,Long.valueOf(platform), Long.valueOf(SendTypeEnum.SMS_TEXT.getCode()));

            sendMap.put("message", "send success");
            sendMap.put("sendStatus", SendStatusEnum.SEND_SUCCESS.getCode() + "");
            logger.info("result:{}", JSON.toJSONString(responseMessage));
            return JSON.toJSONString(responseMessage);
        }catch (Exception ex){
            sendMap.put("requestparamete",JSON.toJSONString(otcMessageReq));
            responseMessage = responseMessage.error(sendMap,ex.getMessage());
            sendMap.put("sendStatus", SendStatusEnum.SEND_FAILURE.getCode()+"");
            logger.error("send OTC SMS error:{}",ex);
            return JSON.toJSONString(responseMessage);
        }
    }


    @RequestMapping(value = "/sendSMS", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public String sendSMSForAll(@RequestBody OTCMessageReq otcMessageReq){
        logger.info("OTCMessageReq:{}",JSON.toJSONString(otcMessageReq));

        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> sendMap = new HashMap<String,String>();
        String coinName = otcMessageReq.getCoinName().trim();

        String orderNo = otcMessageReq.getOrderNo().trim();

        String uid = otcMessageReq.getUid().trim();

        String coinCount = otcMessageReq.getCoinCount().trim();

        String modelId = otcMessageReq.getModelId().trim();

        String platform =otcMessageReq.getPlatform().trim();

        if(StringUtils.isBlank(uid)||StringUtils.isBlank(coinName)||StringUtils.isBlank(orderNo)||StringUtils.isBlank(modelId)){
            sendMap.put("requestparamete",JSON.toJSONString(otcMessageReq));
            responseMessage = responseMessage.error(sendMap,"paramete error");
            return JSON.toJSONString(responseMessage);
        }
        sendMap.put("uid",otcMessageReq.getUid());
        sendMap.put("message","not found sms provider");
        sendMap.put("sendStatus", SendStatusEnum.SEND_NOT.getCode()+"");
        SMSService smsService = smsSendFactory.getSmsServer();
        if(null == smsService){
            responseMessage = responseMessage.error(sendMap,"not found sms provider");
            return JSON.toJSONString(responseMessage);
        }

        try {
            FUser fUser = fUserMapper.selectByPrimaryKey(Long.valueOf(uid));
            String areaCode = fUser.getFareacode();
            String phone = fUser.getFtelephone();
            String sendMessage;
            if (!areaCode.equals("86")) {
                if (phone.charAt(0) == '0') {
                    phone = phone.substring(1, phone.length());
                }
                String otcEnMapStr = smsTemplateConfig.getOtcEnMapStr();
                Map<String, String> otcEn = JSONObject.parseObject(otcEnMapStr,Map.class);
                String messageTemplate = otcEn.get(modelId);
                Map<String, String> replaceMap = new HashMap<String, String>();
                replaceMap.put("mobile", phone);
                replaceMap.put("coinName", coinName);
                replaceMap.put("orderNo", orderNo);
                replaceMap.put("coinCount", coinCount);
                sendMessage = getContent(replaceMap, messageTemplate);
            } else {
                String otcZhMapStr = smsTemplateConfig.getOtcZhMapStr();
                Map<String, String> otcZh = JSONObject.parseObject(otcZhMapStr,Map.class);
                String messageTemplate = otcZh.get(modelId);
                Map<String, String> replaceMap = new HashMap<String, String>();
                replaceMap.put("mobile", phone);
                replaceMap.put("coinName", coinName);
                replaceMap.put("orderNo", orderNo);
                replaceMap.put("coinCount", coinCount);
                sendMessage = getContent(replaceMap, messageTemplate);
            }
            phone = areaCode + phone;

            smsService.sendSms(phone,sendMessage,Long.valueOf(platform), Long.valueOf(SendTypeEnum.SMS_TEXT.getCode()));

            sendMap.put("message", "send success");
            sendMap.put("sendStatus", SendStatusEnum.SEND_SUCCESS.getCode() + "");
            logger.info("result:{}", JSON.toJSONString(responseMessage));
            return JSON.toJSONString(responseMessage);
        }catch (Exception ex){
            sendMap.put("requestparamete",JSON.toJSONString(otcMessageReq));
            responseMessage = responseMessage.error(sendMap,ex.getMessage());
            sendMap.put("sendStatus", SendStatusEnum.SEND_FAILURE.getCode()+"");
            logger.error("send OTC SMS error:{}",ex);
            return JSON.toJSONString(responseMessage);
        }
    }


    @RequestMapping(value = "/sendSMSForMerchants", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public String sendSMSForMerchants(@RequestBody MerchantsReq merchantsReq){
        logger.info("merchantsReq:{}",JSON.toJSONString(merchantsReq));

        ResponseMessage<Map<String,String>> responseMessage = new ResponseMessage<>();
        Map<String,String> sendMap = new HashMap<String,String>();

        String uid = merchantsReq.getUid().trim();

        String modelId = merchantsReq.getModelId().trim();

        String platform = PlatformEnum.OTC.getCode().toString();

       // String sendMsg = merchantsReq.getSendMsg();

        if(StringUtils.isBlank(uid)||StringUtils.isBlank(modelId)){
            sendMap.put("requestparamete ",JSON.toJSONString(merchantsReq));
            responseMessage = responseMessage.error(sendMap,"paramete error");
            return JSON.toJSONString(responseMessage);
        }
        sendMap.put("uid",merchantsReq.getUid());
        sendMap.put("message","not found sms provider");
        sendMap.put("sendStatus", SendStatusEnum.SEND_NOT.getCode()+"");
        SMSService smsService = smsSendFactory.getSmsServer();
        if(null == smsService){
            responseMessage = responseMessage.error(sendMap,"not found sms provider");
            return JSON.toJSONString(responseMessage);
        }

        try {
            FUser fUser = fUserMapper.selectByPrimaryKey(Long.valueOf(uid));
            OtcMerchant otcMerchant = otcMerchantMapper.selectByUid(Long.valueOf(uid));
            String locale =  otcMerchant.getLanguage();

            String areaCode = fUser.getFareacode();
            String phone = otcMerchant.getPhone();
            String sendMessage;

            if(null == locale){
                if (!areaCode.equals("86")) {
                    if (phone.charAt(0) == '0') {
                        phone = phone.substring(1, phone.length());
                    }
                    String otcEnMapStr = smsTemplateConfig.getOtcEnMapStr();
                    Map<String, String> otcEn = JSONObject.parseObject(otcEnMapStr,Map.class);
                    String messageTemplate = otcEn.get(modelId);
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put("mobile", phone);
                    sendMessage = getContent(replaceMap, messageTemplate);
                } else {
                    String otcZhMapStr = smsTemplateConfig.getOtcZhMapStr();
                    Map<String, String> otcZh = JSONObject.parseObject(otcZhMapStr,Map.class);
                    String messageTemplate = otcZh.get(modelId);
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put("mobile", phone);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }
            }else{
                if(Locale.CHINA.toString().equals(locale)){
                    String otcZhMapStr = smsTemplateConfig.getOtcZhMapStr();
                    Map<String, String> otcZh = JSONObject.parseObject(otcZhMapStr,Map.class);
                    String messageTemplate = otcZh.get(modelId);
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put("mobile", phone);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }else if(Locale.KOREA.toString().equals(locale)){
                    String otcKOMapStr = smsTemplateConfig.getOtcKOMapStr();
                    Map<String, String> otcKO = JSONObject.parseObject(otcKOMapStr,Map.class);
                    String messageTemplate = otcKO.get(modelId);
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put("mobile", phone);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }else{
                    String otcEnMapStr = smsTemplateConfig.getOtcEnMapStr();
                    Map<String, String> otcEn = JSONObject.parseObject(otcEnMapStr,Map.class);
                    String messageTemplate = otcEn.get(modelId);
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put("mobile", phone);
                    sendMessage = getContent(replaceMap, messageTemplate);
                }
            }
            phone = areaCode + phone;

            smsService.sendSms(phone,sendMessage,Long.valueOf(platform), Long.valueOf(SendTypeEnum.SMS_TEXT.getCode()));

            sendMap.put("message", "send success");
            sendMap.put("sendStatus", SendStatusEnum.SEND_SUCCESS.getCode() + "");
            logger.info("result:{}", JSON.toJSONString(responseMessage));
            return JSON.toJSONString(responseMessage);
        }catch (Exception ex){
            sendMap.put("requestparamete",JSON.toJSONString(merchantsReq));
            responseMessage = responseMessage.error(sendMap,ex.getMessage());
            sendMap.put("sendStatus", SendStatusEnum.SEND_FAILURE.getCode()+"");
            logger.error("send OTC SMS error:{}",ex);
            return JSON.toJSONString(responseMessage);
        }
    }

    public static String getContent(Map<String, String> params,String content) throws Exception {
        String reg = "\\{\\w*}";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String group = matcher.group();//
            String key = group.substring(1, group.length() - 1);
            if (!params.containsKey(key)) {
                throw new Exception("not found replace key：" + key);
            }
            content = content.replace(group, params.get(key));
        }
        return content;
    }
}
