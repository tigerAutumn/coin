package com.qkwl.common.util;

import java.math.BigDecimal;

public class Constant {
	
	public static String GoogleAuthName = "hotcoin";//谷歌验证前缀

	public static String AesSecretKey="adD23FB9";
	
	public static String CNY_COIN_NAME = "GAVC";
	
	
	/*
	 * 分页数量
	 */
	public static int RewordCodePerPage=10;//激活码兑换记录分页
	public static int CapitalRecordPerPage = 10;// 充值记录分页
	public static int CapitalWithdrawPerPage = 12;// 充值记录分页
	public static int QuestionRecordPerPage = 10;// 问题记录分页
	public static int EntrustRecordPerPage = 10;// 委托分页
	public static int TradeRecordPerPage = 5;// 账单明细分页
	public static int VirtualCoinWithdrawTimes = 10;// 虚拟币每天提现次数
	public static int CnyWithdrawTimes = 10;// 人民币每天提现次数
	public static int webPageSize = 10; //web 分页显示条数
	public static int appPageSize = 5; //app 分页显示条数
	public static int appIcoRecordPageSize = 10; //app ICO分页显示条数
	public static int apiPageSize = 50; //app 分页显示条数
	
	public static int apinum = 1;//api申请上限
	public static int apilimit = 60;//api访问上限
	public static int adminPageSize=40;//后台每页容量
	
	public static String EmailReg = "^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";// 邮箱正则
	public static String PhoneReg = "^(1)\\d{10}$";
	public static String passwordReg = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";//必须包含字母和数字

	public static int ErrorCountLimit = 10;// 错误N次之后需要等待2小时才能重试
	public static int ErrorCountAdminLimit = 30;// 后台登录错误
	
	public static int MaxApiDepth = 10; //最大api市场深度
	public static int LastApiSuccess = 600; //api最新成交记录数
	
	public static int EXPIRETIME = 2 * 60 * 60;
//	public static int EXPIRETIME = 60;
	
	//每众筹一个btc对应的代币数量
	public static int ICO_GAIN = 1000;
	
	//每众筹一个btc对应的积分
	public static int ICO_SCORE = 1000;
	
	//众筹最小额度
	public static BigDecimal ICO_MIN = BigDecimal.valueOf(0.1);
	
	
	/**
	 * 找回密码，redis过期时间
	 */
	public static int RESTPASSEXPIRETIME = 30 * 60;
	
	/**
	 * 图片验证码，redis过期时间
	 */
	public static int IMAGESEXPIRETIME = 30 * 60;
	
	/**
	 * 访问限制时间，redis过期时间
	 */
	public static int limitTime = 60 * 60 * 1;
	
	/**
	 * 没有设置ip，密钥有效期(天)
	 */
	public static int REMAIN_PERIOD = 90;
	
	/**
	 * BTC网络手续费
	 */
	public static final BigDecimal[] BTC_FEES = {new BigDecimal("0.0001"),new BigDecimal("0.0002") , new BigDecimal("0.0003"),
			new BigDecimal("0.0004"), new BigDecimal("0.0005"), new BigDecimal("0.0006"), new BigDecimal("0.0007"),
			new BigDecimal("0.0008"), new BigDecimal("0.0009"), new BigDecimal("0.0010")};

	/**
	 * 
	 */
	public static final int BTC_FEES_MAX = 10;
	
	/**
	 * 活动开始时间
	 */
	public static final String ACTIVITYSTART = "2017-01-23";
	/**
	 * 活动结束时间
	 */
	public static final String ACTIVITYEND = "2017-02-02";

	/**
	 * VIP等级
	 */
	public static final Integer VIP_LEVEL[] = { 0, 1, 2, 3, 4, 5, 6 };
	
	/**
	 * 活动id
	 */
	public static final Integer COMMISSION_ACTIVITY = 1; //返佣活动id
	
	/**
	 * ebank交易排行榜活动开启
	 */
	public static final Integer EBANK_RANK_ACTIVITY_OPEN = 1;
	
	/**
	 * 系统参数
	 */
	public static final String COMMISSION_RATE = "commissionRate"; //返佣比例
	
	/**
	 * otc 购买
	 */
	public static final String OTC_BUY = "BUY";
	
	/**
	 * otc 出售
	 */
	public static final String OTC_SELL = "SELL";
	
	/**
	 * 广告上架
	 */
	public static final Integer OTC_ADVERT_ON = 1;
	
	/**
	 * 广告下架
	 */
	public static final Integer OTC_ADVERT_OFF = 2;
	
	/**
	 * 广告过期
	 */
	public static final Integer OTC_ADVERT_OVERDUE = 3;
	
	/**
	 * otc广告固定价格
	 */
	public static final Integer OTC_FLOAT_PRICE = 1;
	
	/**
	 * otc广告固定价格
	 */
	public static final Integer OTC_FIXED_PRICE = 2;
	
	/**
	 * otc市场平均价
	 */
	public static final Integer OTC_MARKET_AVERAGE = 1;
	
	/**
	 * otc 热币价
	 */
	public static final Integer OTC_MARKET_HOTCOIN = 2;
	
	/**
	 * 默认卷商ID
	 */
	public static int BCAgentId = 0;
	
	/**
	 * otc 广告冻结状态
	 */
	public static int OTC_ADVERT_FROZEN = 1;
	
	/**
	 * otc 广告解冻状态
	 */
	public static int OTC_ADVERT_UNFROZEN = 0;
	/**
	 * otc金额精度
	 */
	public static int OTC_AMOUNT_DIGIT = 2;
	/**
	 * otc金额精度
	 */
	public static int OTC_PRICE_DIGIT = 3;
	/**
	 * otc数量精度
	 */
	public static int OTC_COUNT_DIGIT = 6;
	
	/**
	 * 活动id
	 */
	public static final Integer EBANK_RANK_ACTIVITY = 1; //EBank交易排名赛活动id
	
	/**
	 * android平台标示
	 */
	public static final Integer PLATFORM_ANDROID = 2;
	
	/**
	 * ios平台标示
	 */
	public static final Integer PLATFORM_IOS = 3;
	
	/**
	 * 极验通过标示
	 */
	public static final String GEETEST_SUCC = "geetest_succ_";
	
	/**
	 * 资产平衡每日自增表名前缀
	 */
	public static final String USER_DAY_INCREMENT_TABLE_NAME = "user_day_increment_"; 
	
	/**
	 * 资产平衡每月自增表名前缀
	 */
	public static final String USER_MONTH_INCREMENT_TABLE_NAME = "user_month_increment_"; 
	
	
	/**
	 * 第一个用户uid
	 */
	public static final Integer FIRST_USER_ID = 1; 
	
    /**
	 * 杠杆机构编号
	 */
	public static final String LEVER_INSITUTION_NUM = "INSITUTION_NUM";
	
	/**
	 * 杠杆操作员编号
	 */
	public static final String LEVER_OPERATOR_NUM = "OPERATOR_NUM";
	
	/**
	 * 杠杆 港盛API-Key
	 */
	public static final String LEVER_API_KEY = "API_KEY";
	
	/**
	 * 杠杆 港盛API-Key
	 */
	public static final String LEVER_API_URL = "URL";
	
	/**
	 * 杠杆 港盛API-Key
	 */
	public static final String LEVER_API_PROT = "PROT";
	
	
	public static final String PROJECT_BRIEF="project_brief";
	
	
	
	public static final BigDecimal API_KEY_DEFAULT_RATE=BigDecimal.valueOf(0.002);
	
	
	public static final String PROJECT_WEBSITE="project_website";
	
	/**
	 * 分布式锁redis db
	 */
	public static final String DISTRIBUTED_LOCK_DB = "DISTRIBUTED_LOCK:";
	
	/**
	 * 隐私号绑定
	 */
	public static final String PRIVATE_CODE = "private_code_";
	
	/**
	 * user_coin_wallet表同步redis key
	 */
	public static final String USER_COIN_WALLET_SYNC_KEY="user_coin_wallet_sync";
	
	/**
	 * user_coin_wallet表同步redis key
	 */
	public static final String USER_COIN_WALLET_KEY="s_%s_%s";
}
