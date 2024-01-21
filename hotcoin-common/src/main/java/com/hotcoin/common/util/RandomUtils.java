package com.hotcoin.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RandomUtils {

    public static String getTimeRandomNo(String prefix, int digit){
        StringBuffer id=new StringBuffer();
        //获取当前时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String temp = sf.format(new Date());
        //获取6位随机数
        int random=(int) ((Math.random()+1)*10 * digit);
        id.append(prefix).append(temp).append(random);
        return id.toString();
    }

    public static String getefpCode(){
        StringBuffer id=new StringBuffer();
        //获取当前时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String temp = sf.format(new Date());
        //获取6位随机数
        int random=(int) ((Math.random()+1)*10 * 1);
        id.append(temp).append(random);
        return id.toString();

    }

    public static String getExternalNo(){
        return getTimeRandomNo("EXT-",5);
    }

    public static String getTradeOrderId(String sysName){
        return getTimeRandomNo("FINANCE-"+sysName.trim().toUpperCase()+"-",5);
    }

    public static String getExtenalPaymentFlowId(String sysName,String clientUID,String coinId,String tradeDirection){
        StringBuffer prefix=new StringBuffer();
        prefix = prefix.append("FINANCE_").append(sysName.trim()).append("_").append(clientUID.trim())
                .append(coinId.trim()).append(tradeDirection.trim()).append("-");
        return getTimeRandomNo(prefix.toString(),5);
    }


    public static String getPaymentFlowId(String sysName,String clientUID,String coinId,String tradeDirection){
        StringBuffer prefix=new StringBuffer();
        prefix = prefix.append("FINANCE_").append(sysName.trim()).append("_").append(clientUID.trim())
                .append(coinId.trim()).append(tradeDirection.trim()).append("-");
        return getTimeRandomNo(prefix.toString(),5);
    }

    public static String getSeqNo(Integer syscode,Long uid,Integer coinId,Integer tradeDirection){
        StringBuffer prefix=new StringBuffer();
        prefix = prefix.append(syscode).append(uid)
                .append(coinId).append(tradeDirection);
        return getTimeRandomNo(prefix.toString(),5);
    }

    public static String getTradeOrderId(){
        return getTimeRandomNo("FINANCE-",5);
    }

    public static String getSednGSTradeOrderId(){
        return getTimeRandomNo("S-GS-",5);
    }

}
