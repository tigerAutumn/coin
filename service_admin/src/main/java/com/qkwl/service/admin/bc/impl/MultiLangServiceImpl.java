package com.qkwl.service.admin.bc.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qkwl.common.dto.multilang.MultiLang;
import com.qkwl.common.dto.multilang.MultiLangCriteria;
import com.qkwl.common.rpc.admin.IMultiLangService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.MultiLangMapper;


/**
 * 
 * @author huangjinfeng
 */
@Service("multiLangService")
public class MultiLangServiceImpl implements IMultiLangService {
  private static final Logger logger = LoggerFactory.getLogger(AdminOtcPaymentServiceImpl.class);
  
  @Autowired
  private SystemRedisInit systemRedisInit;

	@Autowired
	private MultiLangMapper multiLangMapper;

  @Override
  public String getMsg(String code) {
    Locale currentLocale = I18NUtils.getCurrentLocale();
    String msg = getMsg(code,I18NUtils.getCurrentLocale());
    logger.info("code:{},语言:{},msg:{},",code,currentLocale.toString(),msg);
    return msg;
  }
  
  @Override
  public String getMsg(String code, Locale locale) {
    String msg="";
    MultiLangCriteria example=new MultiLangCriteria();
    example.createCriteria().andCodeEqualTo(code).andLangEqualTo(locale.toString());
    List<MultiLang> selectByExample = multiLangMapper.selectByExample(example);
    if(CollectionUtils.isNotEmpty(selectByExample)) {
      msg=selectByExample.get(0).getMsg();
    }
    
    logger.info("code:{},语言:{},msg:{},",code,locale.toString(),msg);
    return msg;
  }
  
  
  
  @Override
  public void addOrUpdateMsg(Locale locale,String code,String msg) {
    MultiLangCriteria example=new MultiLangCriteria();
    example.createCriteria().andCodeEqualTo(code).andLangEqualTo(locale.toString());
    List<MultiLang> dbList = multiLangMapper.selectByExample(example);
    if(CollectionUtils.isEmpty(dbList)) {
      MultiLang record=new MultiLang();
      record.setCode(code);
      record.setLang(locale.toString());
      record.setMsg(msg);
      record.setCreateTime(new Date());
      multiLangMapper.insertSelective(record);
    }else {
      MultiLang record=new MultiLang();
      record.setMsg(msg);
      multiLangMapper.updateByExampleSelective(record, example);
    }
    
    systemRedisInit.initMultiLang();
    
  }

  @Override
  public void deleteByTableNameAndCode(String... codes) {
    MultiLangCriteria example=new MultiLangCriteria();
    example.createCriteria().andCodeIn(Arrays.asList(codes));
    multiLangMapper.deleteByExample(example);
    systemRedisInit.initMultiLang();
  }


  
}
