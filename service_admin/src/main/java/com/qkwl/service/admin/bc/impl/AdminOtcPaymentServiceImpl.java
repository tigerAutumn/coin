package com.qkwl.service.admin.bc.impl;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.otc.GoPaymentJspResp;
import com.qkwl.common.dto.otc.OtcPayment;
import com.qkwl.common.rpc.admin.IAdminOtcPaymentService;
import com.qkwl.common.rpc.admin.IMultiLangService;
import com.qkwl.common.util.UUIDUtils;
import com.qkwl.service.admin.bc.comm.SystemRedisInit;
import com.qkwl.service.admin.bc.dao.OtcPaymentMapper;
import com.qkwl.service.admin.bc.utils.MQSend;

@Service("adminOtcPaymentService")
public class AdminOtcPaymentServiceImpl implements IAdminOtcPaymentService {

  private static final Logger logger = LoggerFactory.getLogger(AdminOtcPaymentServiceImpl.class);

  @Autowired
  private OtcPaymentMapper otcPaymentMapper;

  @Autowired
  private SystemRedisInit systemRedisInit;

  @Autowired
  private MQSend mqSend;
  
  @Autowired
  private IMultiLangService multiLangService;



  @Override
  public List<OtcPayment> selectOtcPayment() {
      List<OtcPayment> selectAll = otcPaymentMapper.selectAll();
      return selectAll;
  }



  @Override
  public OtcPayment selectPaymentByPrimaryKey(Integer id) {
      OtcPayment selectByPrimaryKey = otcPaymentMapper.selectByPrimaryKey(id);
      selectByPrimaryKey.setName(multiLangService.getMsg(selectByPrimaryKey.getNameCode()));
      return selectByPrimaryKey;
  }

  @Override
  public int savePayment(OtcPayment otcPayment) {   
    String nameCode=UUIDUtils.get32UUID();
    multiLangService.addOrUpdateMsg(Locale.CHINA, nameCode,  otcPayment.getChineseName());
    multiLangService.addOrUpdateMsg(Locale.US, nameCode,  otcPayment.getEnglishName());
    multiLangService.addOrUpdateMsg(Locale.TAIWAN, nameCode, otcPayment.getZhTWName());
    multiLangService.addOrUpdateMsg(Locale.KOREA, nameCode, otcPayment.getKoKRName());

    otcPayment.setStatus(OtcPaymentStatusEnum.Prohibit.getCode());
    otcPayment.setNameCode(nameCode);
    otcPayment.setCreateTime(new Date());
    int insert = otcPaymentMapper.insert(otcPayment);
    systemRedisInit.initOtcPayment();
    return insert;
  }

  @Override
  public int updatePaymentWithMultiLang(OtcPayment otcPayment) {
    
    OtcPayment dbOtcPayment = otcPaymentMapper.selectByPrimaryKey(otcPayment.getId());
    
    String nameCode=Optional.ofNullable(dbOtcPayment).map(OtcPayment::getNameCode).filter(StringUtils::isNotBlank).orElse(UUIDUtils.get32UUID());
    
    multiLangService.addOrUpdateMsg(Locale.CHINA, nameCode, otcPayment.getChineseName());
    multiLangService.addOrUpdateMsg(Locale.US, nameCode, otcPayment.getEnglishName());
    multiLangService.addOrUpdateMsg(Locale.TAIWAN, nameCode, otcPayment.getZhTWName());
    multiLangService.addOrUpdateMsg(Locale.KOREA, nameCode, otcPayment.getKoKRName());
    
      otcPayment.setNameCode(nameCode);
      int updateByPrimaryKey = updatePayment(dbOtcPayment);
      return updateByPrimaryKey;
  }
  
  
  @Override
  public int updatePayment(OtcPayment otcPayment) {
      int updateByPrimaryKey = otcPaymentMapper.updateByPrimaryKeySelective(otcPayment);
      systemRedisInit.initOtcPayment();
      return updateByPrimaryKey;
  }



  @Override
  public OtcPayment goPaymentJSP(Integer id) {
    OtcPayment otcPayment = selectPaymentByPrimaryKey(id);
    otcPayment.setChineseName(multiLangService.getMsg(otcPayment.getNameCode(),Locale.CHINA));
    otcPayment.setCreateTime(otcPayment.getCreateTime());
    otcPayment.setEnglishName(multiLangService.getMsg(otcPayment.getNameCode(), Locale.US));
    otcPayment.setZhTWName(multiLangService.getMsg(otcPayment.getNameCode(), Locale.TAIWAN));
    otcPayment.setKoKRName(multiLangService.getMsg(otcPayment.getNameCode(), Locale.KOREA));
    otcPayment.setId(otcPayment.getId());
    otcPayment.setPicture(otcPayment.getPicture());
    otcPayment.setNameCode(otcPayment.getNameCode());
    otcPayment.setStatus(otcPayment.getStatus());
    otcPayment.setType(otcPayment.getType());
    return otcPayment;
  }



  @Override
  public List<OtcPayment> listPayment() {
    List<OtcPayment> selectAll =selectOtcPayment();
    logger.info("支付方式:{}",JSON.toJSONString(selectAll));
    List<OtcPayment> list=selectAll.stream().map(e->otcPaymentWithMultiLang(e)).collect(Collectors.toList());
    return list;
  }
  
  
  private OtcPayment otcPaymentWithMultiLang(OtcPayment o) {
    o.setChineseName(multiLangService.getMsg(o.getNameCode(), Locale.CHINA));
    o.setCreateTime(o.getCreateTime());
    o.setEnglishName(multiLangService.getMsg(o.getNameCode(), Locale.US));
    o.setKoKRName(multiLangService.getMsg(o.getNameCode(), Locale.KOREA));
    o.setZhTWName(multiLangService.getMsg(o.getNameCode(), Locale.TAIWAN));
    o.setId(o.getId());
    o.setPicture(o.getPicture());
    o.setStatus(o.getStatus());
    o.setType(o.getType());
    return o;
  }

}
