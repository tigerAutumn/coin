package com.qkwl.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;


/**
 * Created by hwj on 20180825
 */
public class CoinCommentUtils {

    private static final Logger logger = LoggerFactory.getLogger(CoinCommentUtils.class);

    /**
      * fod地址加密过程
      */
    public static String FODEncode(String address){
    	if(StringUtils.isEmpty(address) ) {
    		return null;
    	}
    	address = address.trim();
    	if(!address.startsWith("0x")) {
    		return null;
    	}
    	address = address.substring(2, address.length());
    	StringBuffer sb = new StringBuffer(address);
    	address = sb.reverse().toString().toUpperCase();
		return address;
    }
    /**
     * fod地址解密过程
     */
    public static String FODDecode(String address){
    	if(StringUtils.isEmpty(address)) {
    		return null;
    	}
    	address = address.trim();
    	StringBuffer sb = new StringBuffer(address);
    	address ="0x" + sb.reverse().toString().toLowerCase();
        return address;
    }
    
    public static Date formatDate(String time){
        SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    * 把字符串转换时间
    *
    * @param dateStr 2018-08-29T08:45:18
    * @return 时间
    */
    public static Date fromISODate(String time){
        if(!time.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")){
            return null;
        }
        time=time.replaceFirst("T", " ");
        Date date=formatDate(time);
         // 1、取得本地时间：
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, (zoneOffset + dstOffset));
        return cal.getTime();
    }


    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialCharExceptColons(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }


    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 全是数字
     * */
    public static boolean isAllNumeric(String str) {  
        Pattern pattern = Pattern.compile("[0-9]*");  
        Matcher isNum = pattern.matcher(str);  
        if (!isNum.matches()) {  
            return false;  
        }  
        return true;  
    }
    
    /**
     * 全是英文字符
     * */
    public static boolean isAllEnglishChar(String str) {  
        Pattern pattern = Pattern.compile("[a-z|A-Z]*");  
        Matcher isNum = pattern.matcher(str);  
        if (!isNum.matches()) {  
            return false;  
        }  
        return true;  
    }

    /**
     * 判断地址是否合法
     *
     * @param coinType 币种类型
     * @param address 地址
     * @return true为合法，false为不合法
     */
    public static boolean isLegitimateAddress(Integer coinType,String address) {
    	
    	//提币地址不能有中文和除 ：以外的特殊字符
		if(StringUtils.isEmpty(address) || isContainChinese(address)) {
			return false;
		}
		//eos和bts 不做判断
    	if(coinType.equals(SystemCoinSortEnum.EOS.getCode()) || coinType.equals(SystemCoinSortEnum.BTS.getCode())) {
    		return true;
    	}
    	
		//地址不能全是数字或者全是英文字符
		if(isAllEnglishChar(address) || isAllNumeric(address)) {
			return false;
		}
		
		if(coinType.equals(SystemCoinSortEnum.ETH.getCode())
					|| coinType.equals(SystemCoinSortEnum.MOAC.getCode())
					|| coinType.equals(SystemCoinSortEnum.ETC.getCode())
					|| coinType.equals(SystemCoinSortEnum.VCC.getCode())){
			if(!address.startsWith("0x") || isSpecialChar(address)  || address.length() != 42) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.FOD.getCode()) ) {
			if(address.startsWith("0x")  || isSpecialChar(address) || address.length() != 40) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.BTC.getCode()) 
				|| coinType.equals(SystemCoinSortEnum.USDT.getCode()) 
				|| coinType.equals(SystemCoinSortEnum.NEO.getCode())) {
			if(address.startsWith("0x")  || isSpecialChar(address.replace("bitcoincash:", ""))) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.BWT.getCode())) {
			if(!address.startsWith("b")  || isSpecialChar(address)) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.WICC.getCode())) {
			if(!address.startsWith("W")  || isSpecialChar(address)) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.RUBY.getCode())) {
			if(!address.startsWith("1")  || isSpecialChar(address) || address.length() != 38) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.HTDF.getCode())) {
			if(!address.startsWith("htdf1")) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.TCP.getCode())) {
			if(!address.startsWith("TCP")) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.TRX.getCode())) {
			if(!address.startsWith("T")) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.ONT.getCode())) {
			if(!address.startsWith("A")) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.XRP.getCode())) {
			if(!address.startsWith("r")) {
				return false;
			}
		}else if(coinType.equals(SystemCoinSortEnum.SERO.getCode())) {
			if(address.startsWith("0x")) {
				return false;
			}
		}
		
    	return true;
    }


/*    public static void main(String[] args) {
		System.out.println(isLegitimateAddress(2,"16iTi5my323GjzJP86NJiLQ76yMtvDr7Ki"));
		System.out.println(isLegitimateAddress(10,"16iTi5my323GjzJP86NJiLQ76yMtvDr7Ki"));
		System.out.println(isLegitimateAddress(2,"bitcoincash:qr7m09f40z5j8lhzgmtettf0rt65963q9555tr9g0n"));
		System.out.println(isLegitimateAddress(16,"AajQ7SBiVNE6cRTEg6HM7vZxd7U9VC2dLH"));
		System.out.println(isLegitimateAddress(3,"0xa8851bc691505425001b5ecf5037d670e0c0b515"));
	}*/
   
}
