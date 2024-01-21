package com.hotcoin.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 上海助通信息科技有限公司
 * 北京链上科技
 */
@Data
@Configuration
public class ZTSMSConfig {

    /**
     *用户名（必填）
     */
    @Value("${sms.zt.userName}")
    private String username;

    /**
     * 密码（必填，MD5加密,32位小写）
     */
    @Value("${sms.zt.password}")
    private String password;
    /**
     * 提交url
     * http://www.ztsms.cn/sendsmsls.do
     */
    @Value("${sms.zt.url}")
    private String url;

    /**
     * 产品id(必填)
     */
    @Value("${sms.zt.productid}")
    private String productid;
}
