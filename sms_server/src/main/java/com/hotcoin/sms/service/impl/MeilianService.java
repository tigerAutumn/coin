package com.hotcoin.sms.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.sms.Enum.SendStatusEnum;
import com.hotcoin.sms.config.MeilianSMSConfig;
import com.hotcoin.sms.config.TimeZoneConfig;
import com.hotcoin.sms.model.SmsMessage;
import com.hotcoin.sms.service.SMSService;
import com.hotcoin.sms.service.SmsMessageService;
import com.hotcoin.sms.util.JodaTimeUtil;
import com.hotcoin.sms.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service(value = "meilianService")
public class MeilianService implements SMSService {
    private static final Logger logger = LoggerFactory.getLogger(SMSService.class);

    @Autowired
    MeilianSMSConfig meilianSMSConfig;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    @Autowired
    private SmsMessageService smsMessageService;

    @Override
    public void sendSms(String mobile, String message,Long platform,Long sendType) {
//新建一个StringBuffer链接
        StringBuffer buffer = new StringBuffer();

        //String encode = "GBK"; //页面编码和短信内容编码为GBK。重要说明：如提交短信后收到乱码，请将GBK改为UTF-8测试。如本程序页面为编码格式为：ASCII/GB2312/GBK则该处为GBK。如本页面编码为UTF-8或需要支持繁体，阿拉伯文等Unicode，请将此处写为：UTF-8

        String encode = "UTF-8";

        String username = meilianSMSConfig.getUserName();  //用户名

        String password = meilianSMSConfig.getPassword();  //密码

        String apikey = meilianSMSConfig.getApikey();  //apikey秘钥（请登录 http://m.5c.com.cn 短信平台-->账号管理-->我的信息 中复制apikey）

       try {
            String password_md5 = MD5Utils.md5Password(password);
            String contentUrlEncode = URLEncoder.encode(message,encode);  //对短信内容做Urlencode编码操作。注意：如
            Map<String,String> data = new HashMap<String,String>();
            data.put("type","send");
            data.put("apikey",apikey);
            data.put("username",username);
            data.put("password_md5",password_md5);
            data.put("encode",encode);
            data.put("mobile",mobile);
            data.put("content",contentUrlEncode);


            //把发送链接存入buffer中，如连接超时，可能是您服务器不支持域名解析，请将下面连接中的：【m.5c.com.cn】修改为IP：【115.28.23.78】
           // buffer.append(meilianSMSConfig.getUrl()+"?username="+username+"&password_md5="+password_md5+"&mobile="+mobile+"&apikey="+apikey+"&content="+contentUrlEncode+"&encode="+encode);
            buffer.append(meilianSMSConfig.getUrl()+"&data="+ JSON.toJSONString(data));

            //System.out.println(buffer); //调试功能，输入完整的请求URL地址

            //把buffer链接存入新建的URL中
            URL url = new URL(buffer.toString());

            //打开URL链接
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            //使用POST方式发送
            connection.setRequestMethod("POST");

            //使用长链接方式
            connection.setRequestProperty("Connection", "Keep-Alive");

            //发送短信内容
           // BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
           try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
               //获取返回值
               String result = reader.readLine();
               //{"status":"success","msg":"73508727178536"}
               logger.info("MeilianService result：{}",result);
               JSONObject jsonObj = (JSONObject) JSON.parse(result);
               String status = jsonObj.getString("status");
               if("success".equalsIgnoreCase(status)){
                   String msg = jsonObj.getString("msg");
                   SmsMessage smsMessage = new SmsMessage();
                   smsMessage.setSendId(msg);
                   smsMessage.setMobile(mobile);
                   smsMessage.setContent(message);
                   smsMessage.setSendchannel(MeilianService.class.getSimpleName());
                   smsMessage.setPlatform(platform);
                   smsMessage.setSendtype(sendType);
                   smsMessage.setStatus(Long.valueOf(SendStatusEnum.SNED_PLATFORM_SUCCESS.getCode()));
                   smsMessage.setCreatetime(JodaTimeUtil.getCurrentDateByTimeZone(timeZoneConfig.getTimeZone()).toString("yyyy-MM-dd HH:mm:ss"));
                   smsMessageService.saveSmsMessage(smsMessage);
               }
               logger.info("MeilianService sendsms return :{} ",jsonObj);
               //输出result内容，查看返回值，成功为success，错误为error，详见该文档起始注释
           }


        } catch (Exception e) {
           logger.error("MeilianService sendsms exception :{} ",e);
        }
    }
}
