package com.hotcoin.notice;


import com.hotcoin.notice.NoticeApplication;
import com.hotcoin.notice.sms.provider.ChuanglanProvider;
import com.hotcoin.notice.sms.provider.GlobalsentProvider;
import com.hotcoin.notice.sms.provider.MeilianProvider;
import com.hotcoin.notice.sms.provider.YunpianProvider;
import com.hotcoin.notice.sms.provider.ZTProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NoticeApplication.class)
public class SmsApplicationTests {

    @Autowired
    ZTProvider ztProvider; //success
    @Autowired
    ChuanglanProvider chuanglanProvider; //success
    @Autowired
    GlobalsentProvider globalsentProvider;  
    @Autowired
    MeilianProvider meilianProvider;  //success ,不用
    @Autowired
    YunpianProvider yunpianProvider;

    @Test
    public void test() {
    	System.out.println(String.format("msgid:%s",yunpianProvider.sendSms("15711941299", "验证码：123456，您正在请求绑定手机号，请勿泄露！（热币）",null)));
    }

}

