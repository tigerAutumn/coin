package com.qkwl.admin.layui.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author huangjinfeng
 * 2019年4月4日
 */
public class FormatUtils {

    public static String toString(BigDecimal bigDecimal, int precision) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(precision, BigDecimal.ROUND_FLOOR).toPlainString();
    }

    public static String toString(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.stripTrailingZeros().toPlainString();
    }

    public static String toPlainString(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.toPlainString();
    }

    public static String toString10(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            bigDecimal = BigDecimal.ZERO;
        }
        return bigDecimal.setScale(10, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
    
    public static String toString10AndstripTrailingZeros(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            bigDecimal = BigDecimal.ZERO;
        }
        return bigDecimal.setScale(10, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }
    

    public static BigDecimal toBigDecimal(String number) {
        return StringUtils.isBlank(number) ? BigDecimal.ZERO : new BigDecimal(number);
    }


}
