package com.qkwl.common.Enum.redis;

import com.qkwl.common.Enum.validate.BusinessTypeEnum;

/**
 * Created by ZKF on 2017/9/4.
 */
public enum RedisTypeEnum {

    Article(1, "新闻"),
    AboutUs(2, "关于我们"),
    FriendshipLink(3, "友链"),
    Coin(4, "币种"),
    Trade(5, "交易"),
    Finance(6, "存币理财"),
    SystemArgs(7, "系统参数"),
    Language(8, "语言"),
    SystemBank(9, "系统银行"),
    Agent(10, "券商"),
    SmsEmail(13, "短信邮件配置"),
    Activity(15, "活动配置"),
    AuthApi(17,"授权api"),
    Payment(18,"otc支付类型"),
    Currency(19,"otc法币类型"),
    MatchGroup(20,"交易对组"),
    TradeRule(21,"交易对规则"),
    LEVER_ARGS(22,"杠杆参数"),
    MULTI_LANG(23, "多语言"),
    HISTORIC_VERSION(24, "历史版本");

    private Integer code;
    private String value;

    RedisTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValueByCode(Integer code) {
        for (RedisTypeEnum e : RedisTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
