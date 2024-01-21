/**
 * 
 */
package com.hotcoin.notice.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author huangjinfeng
 */
public class PhoneNumberUtils {
	
	private static final String  CHINA_MOBILE_PHONE_NUMBER_REGEX="^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4(?:[14]0\\d{3}|[68]\\d{4}|[579]\\d{2}))\\d{6}$";
	
	/**
	 * https://github.com/VincentSit/ChinaMobilePhoneNumberRegex
	 * 匹配所有号码（手机卡 + 数据卡 + 上网卡）
	 */
	public static Boolean isChinaPhoneNum(String phoneNum) {
		if(StringUtils.isBlank(phoneNum)) {
			return false;
		}
		return Pattern.matches(CHINA_MOBILE_PHONE_NUMBER_REGEX, phoneNum);
	}
	
	
	
	public static String chinaPhoneAdd86(String phoneNum) {
		if(isChinaPhoneNum(phoneNum)&&!Pattern.matches("^(?:\\+?86.*)", phoneNum)) {
			return String.format("86%s", phoneNum);
		}
		return phoneNum;
	}
	
	public static String internationalPhoneAddPlus(String phoneNum){
		if(!isChinaPhoneNum(phoneNum)&&!phoneNum.startsWith("+")) {
			return String.format("+%s", phoneNum);
		}
		return phoneNum;
	}
	
	
	public static String chinaPhoneDel86(String phoneNum) {
		if(isChinaPhoneNum(phoneNum)&&Pattern.matches("^(?:\\+?86.*)", phoneNum)) {
			return StringUtils.substringAfter(phoneNum, "86");
		}
		return phoneNum;
	}
	

}
