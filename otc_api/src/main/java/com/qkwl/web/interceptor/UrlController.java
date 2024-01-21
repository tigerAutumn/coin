package com.qkwl.web.interceptor;

public class UrlController {
    /**
     * 不需要验证url
     */
    private static String[] VALIDATIONURLS = {
            "/otc/otc_user_details",  //otc商户或otc个人详情
    		"/otc/login",//登录
            
            "/otc/common/systemArgs",//广告公共参数
            "/otc/advert/tradeIndex",//广告列表
            "/otc/currency/getDeposit",//查询商户押金
    };

    /**
     * AJAX请求url
     */
    private static String[] AJAXRLS = {"/deposit/alipay_manual", // 增加一条充值记录
            "/user/security_json",
            "/deposit/alipay_transfer", // 支付宝，微信转账
            "/deposit/bank_manual", // 人民币充值提交
            "/deposit/cny_cancel", // 人民币充值取消
            "/withdraw/cny_manual", // 人民币提现
            "/withdraw/coin_manual", // 虚拟币提现
            "/withdraw/cny_cancel", // 人民币提现取消
            "/withdraw/coin_cancel", // 虚拟币提现取消
            "/withdraw/coin_address", // 获取虚拟币充值地址
            "/deposit/cny_online", // 第三方充值
            "/activity/exchange", // 使用激活码
            "/submit_api", // 申请API
            "/delete_api", // 删除API
            "/assets/apply_submit", // 优质资产申请
            "/assets/apply_support", // 提交支持
            "/assets/apply_comment", // 提交评论
            "/online_help/help_submit", // 提交问题
            "/online_help/help_delete", // 删除提问
            "/online_help/index_json",//
            "/online_help/help_list_json",//
            "/trade/cny_buy", // 买单
            "/trade/cny_sell", // 卖单
            "/trade/cny_cancel", // 撤单
            "/trade/cny_entrustLog", // 委单交易明细
            "/user/bind_phone", // 綁定手機
            "/user/bind_google_device", // 添加谷歌设备
            "/user/google_auth", // 添加设备认证
            "/user/get_google_key", // 查看谷歌密匙
            "/user/modify_passwd", // 修改登录和交易密码
            "/real_name_auth", // 实名认证
            "/user/pay_vip", // 购买VIP
            "/user/save_bankinfo", // 新增银行卡
            "/user/save_alipay", // 支付宝帐号
            "/user/del_bankinfo", // 删除银行卡
            "/user/save_withdraw_address", // 添加提现地址
            "/user/del_withdraw_address", // 删除提现地址
            "/user/send_bank_sms",//发送绑定银行的验证码
            "/deposit/cny_deposit_json",
            "/withdraw/cny_withdraw_json",
            "/deposit/coin_deposit_json",
            "/withdraw/coin_withdraw_json",//获取用户虚拟币钱包，提现地址及提现设置
            "/withdraw/coin_withdraw_info_json.html",
            "/financial/record_json",
            "/financial/transfer_record_json",
            //"/user/user_favorite_json"//添加收藏
            "/v2/validateInfo",//验证实名认证
            "/v2/common/index",//首页轮播图
            "/v2/getSecuritySetting",//获取用户安全设置信息
            "/v2/sendPhone",//发送手机验证码
            "/v2/sendEmail",//发送邮件验证码
            "/v2/validateImage", // 图片验证码
            "/v2/send_email_bind_code", //发送绑定邮箱验证码
            "/v2/email_bind", //根据邮箱验证码绑定邮箱
            "/v2/update_nickname",//修改昵称
            "/v2/capital/save_withdraw_address",// 新增提现地址
            "/v2/capital/list_withdraw_address",// 查询提现地址
            "/v2/capital/remove_withdraw_address",// 删除提现地址
            "/v2/capital/coin_withdraw_info",//获取提币所需参数
            "/v2/capital/withdraw", //虚拟币提现
            "/v2/capital/check_tradePwd",//验证交易密码
    };

    /**
     * 开放的api
     */
    private static String[] APIURLS = new String[]{
            "/v1/order/place",
            "/v1/order/cancel",
            "/v1/order/detail",
            "/v1/order/detailById",
            "/v1/order/entrust",
            "/v1/order/entrustList",
            "/v1/ticker",
            "/v1/depth",
            "/v1/balance",
            "/v1/trade"
    };

    /**
     * App
     */
    private static String[] APP_URLS = new String[]{
            "/v1/user/balance",
            "/v1/validate/sign",//发送需要签名的短信
            "/v1/deposit/coin",//获取虚拟币充值地址和近十次充值记录
            "/v1/user/password",//登录密码和交易密码的修改
            "/v1/log/login",//
            "/v1/log/setting",//
            "/v1/user/google_device",//获取 Google key
            "/v1/user/google_auth",//添加 Google key
            "/v1/user/real_auth",//实名认证
            "/v1/user/bind_msg",//发送绑定手机的短信
            "/v1/user/bind_phone",//绑定手机
            "/v1/entrust/place",//下单
            "/v1/entrust/cancel",//取消订单
            "/v1/entrust/list",//获取订单
            "/v1/user/security",//获取安全设置详情
            "/v1/coin/list_address",//获取提现列表
            "/v1/coin/address",//添加提现地址
            "/v1/coin/withdraw",//虚拟币提现
            "/v1/entrust/place",//下单
            "/v1/entrust/cancel",//取消订单
            "/v1/entrust/list",//获取委托订单
            "/v1/activity/exchange",//充值码兑换
            "/v1/activity/index",//充值码兑换历史
            "/v1/market/userassets",//获取用户资产
            "/v1/email/send",//绑定邮件验证码
            "/v1/email/add",//绑定邮件
            "/v1/user/info",//用户信息
            "/v1/user/send_bank_sms",//用户信息
            "/v1/user/save_bankinfo",//添加银行卡
            "/v1/user/cny_manual",//人民币提现
            "/v1/user/bankinfo",//获取已经绑定的提现银行卡
            "/v1/user/cny_list",//人民币提现记录
            "/v1/user/send_bank_sms",//添加银行卡和人民币提现的短信接口
            "/v1/system/bankinfo",//系统银行卡
            "/v1/system/fee",//系统的提现手续费
            "/v1/user/qq",//
            "/v1/user/test",
            "/v1/user/user_favorite_json",//添加收藏
            "/v1/collect/list",//收藏列表,
            "/c2c/businessList",
            "/user/getUserInfo",
            "/c2c/orderList",
            "/c2c/order",
            "/c2c/orderDetail",
            "/c2c/setting",
            "/c2c/userBanklist",
            "/c2c/save_bankinfo",
            "/c2c/del_bankinfo",
            "/c2c/default_bankinfo"
    };

    /**
     * 需要签名验证的接口
     */
    private static String[] APP_SIGN_URLS = new String[]{
            "/v1/validate/sign",//发送需要签名的短信
            "/v1/user/password",//登录密码和交易密码的修改
            "/v1/user/google_auth",//添加 Google 授权
            "/v1/user/real_auth",//实名认证
            "/v1/entrust/place",//下单
            "/v1/entrust/cancel",//取消订单
            "/v1/coin/address",//添加提现地址
            "/v1/coin/withdraw",//虚拟币提现
            "/v1/entrust/place",//下单
            "/v1/entrust/cancel",//取消订单
            "/v1/user/test",
            
            "/v2/capital/save_withdraw_address_sign", //新增提币地址
            "/v2/capital/withdraw_sign", //提币
            "/v2/capital/check_tradePwd_sign", //校验交易密码
            "/v2/modifyPwdSign"//修改登录和交易密码
    };



    public static boolean isValidationUrls(String url) {
        boolean flag = false;
        for (String s : VALIDATIONURLS) {
            if (url.startsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean isAjaxUrls(String url) {
        boolean flag = false;
        for (String s : AJAXRLS) {
            if (url.startsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean isApiUrls(String url) {
        boolean flag = false;
        for (String s : APIURLS) {
            if (url.startsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean isAppUrls(String url) {
        boolean flag = false;
        for (String s : APP_URLS) {
            if (url.startsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean isAppSignUrls(String url) {
        boolean flag = false;
        for (String s : APP_SIGN_URLS) {
            if (url.startsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }



}
