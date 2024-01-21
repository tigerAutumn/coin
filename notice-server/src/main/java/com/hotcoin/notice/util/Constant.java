package com.hotcoin.notice.util;

public class Constant {

	private Constant() {
	}


	/**
	 * 分布式锁lease time(单位毫秒)
	 */
	public static final long LEASE_TIME = 2000;

	/**
	 * 登录密码输入多少次后限制登录
	 */
	public static final int MAX_ERROR_TIMES = 5;

	/**
	 * 分布式锁redis db
	 */
	public static final String REDIS_LOCK_DB = "NOTICE_LOCK:";
	
	/**
	 * 发送短信限制发送频率 db
	 */
	public static final String REDIS_SMS_LIMIT_DB = "NOTICE_SMS_LIMIT:";
	
	/**
	 * 发送邮件限制发送频率 db
	 */
	public static final String REDIS_EMAIL_LIMIT_DB = "NOTICE_EMAIL_LIMIT:";

	/**
	 * 分布式锁lease time(单位毫秒)
	 */
	public static final long WAIT_TIME = 100;
	
	/**
	 * 一分钟短信发送频率
	 */
	public static final int SMS_LIMIT_NUM = 300;
	
	/**
	 * 一分钟短信发送频率
	 */
	public static final int EMAIL_LIMIT_NUM = 300;

}
