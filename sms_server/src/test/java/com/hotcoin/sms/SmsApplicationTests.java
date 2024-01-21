package com.hotcoin.sms;


import com.hotcoin.sms.service.impl.ZTService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SmsApplication.class)
public class SmsApplicationTests {

    @Autowired
    ZTService ztService;

    @Test
    public void contextLoads() {
        ztService.sendSms("8613670162346","尊敬的14787184016用户，您发布的BTC购买广告，已生成新的订单，订单编号1139048879178780672，请您前往“ OTC—订单管理 ” 查看订单详情，并于15分钟内完成付款。",1L,3L);;
        //chuanglanService.sendSms("8613670162346","尊敬的14787184016用户，您发布的BTC购买广告，已生成新的订单，订单编号1139048879178780672，请您前往“ OTC—订单管理 ” 查看订单详情，并于15分钟内完成付款。",1L,3L);
    }

}

