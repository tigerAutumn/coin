package com.qkwl.common.util;

import org.apache.commons.lang3.StringUtils;

import com.qkwl.common.dto.user.FUser;

public class SecurityUtil {

	public static FUser fuzzyUserData(FUser user) {
		user.setFgoogleauthenticator(""); 
		user.setFgoogleurl(""); 
		user.setIp("");
		user.setFlastip(0l);
		user.setFloginpassword(StringUtils.isNotBlank(user.getFloginpassword())?"true":"");
		user.setFtradepassword(StringUtils.isNotBlank(user.getFtradepassword())?"true":"");
		user.setFloginname(getLoginname(user.getFloginname()));
		user.setFtelephone(getStr(user.getFtelephone()));
		// 邮箱暂时不模糊，前端需要用来发邮件
//		user.setFemail(getEmail(user.getFemail()));
		user.setFidentityno(getStr(user.getFidentityno()));
		user.setFrealname(getName(user.getFrealname()));
		return user;
	}
	
	public static String getLoginname(String loginname) {
		if(loginname.indexOf("@")==-1){
			return getStr(loginname);
		} else {
//			return getEmail(loginname);
			return loginname;
		}
	}
	
	public static String getStr(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		if (str.length() < 8) {
			return str;
		}
		return str.substring(0,3) + "****" + str.substring(str.length()-4, str.length());
	}
	
	public static String  getEmail(String email){
		if (StringUtils.isBlank(email)) {
			return email;
		}
		if(email.indexOf("@")==-1){
			return  email;
		}
		String emailname=email.substring(0, email.indexOf("@"));
		String emailAddress=email.substring(email.indexOf("@"),email.length());
		if(emailname.length()>4){
			return emailname.substring(0, emailname.length()-4) +"****"+emailAddress;
		}else{
			  return  "****"+emailAddress;
		}
	} 
	
	public static String getName(String name) {
		if (StringUtils.isBlank(name)) {
			return name;
		}
		return "*" + name.substring(1, name.length());
	}
	
}
