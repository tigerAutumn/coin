package com.qkwl.web.utils;

import java.math.BigDecimal;

import com.qkwl.common.match.MathUtils;

public class CnyUtils {

  public static BigDecimal validateCny(BigDecimal p_new){
    String p_new_str = p_new.toPlainString();
    String tmp_p_new =p_new_str.split("\\.")[1];
    String newStr = tmp_p_new.replaceAll("^0*", "");
    if(org.apache.commons.lang3.StringUtils.isNotBlank(newStr)){
      int point = p_new_str.indexOf(newStr);
      if(point>2){
        return MathUtils.toScaleNum(p_new , point+1);
      }else {
        return MathUtils.toScaleNum(p_new, point);
      }
    }
    return p_new;
  }

}
