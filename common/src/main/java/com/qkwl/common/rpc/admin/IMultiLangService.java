package com.qkwl.common.rpc.admin;

import java.util.Locale;

/**
 * 
 * @author huangjinfeng
 */
public interface IMultiLangService {


  String getMsg(String code);
  
  String getMsg(String code,Locale locale);
  
  void addOrUpdateMsg(Locale locale, String code, String msg);
  
  void deleteByTableNameAndCode(String... codes);
	
}
