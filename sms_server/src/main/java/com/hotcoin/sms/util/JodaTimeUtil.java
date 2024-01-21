package com.hotcoin.sms.util;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JodaTimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(JodaTimeUtil.class);
    private static final String FORMATE_DATE = "yyyy-MM-dd";
    private static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_MINITER = FORMATE_DATE.concat(" ").concat("HH:mm:ss");
    private static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

   // public static SimpleDateFormat SDF_FORMATE_DATE = new SimpleDateFormat(FORMATE_DATE);
   // public static SimpleDateFormat SDF_FORMATE_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   // public static SimpleDateFormat SDF_FORMATE_MINITER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Timestamp strToDateSql(String strDate,SimpleDateFormat sdf) {
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = sdf.parse(strDate, pos);
        return new Timestamp(strtodate.getTime());
    }

    public static String dateTransformBetweenTimeZone(Date sourceDate, DateFormat formatter,
                                                      TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
        return JodaTimeUtil.getTime(new Date(targetTime), formatter);
    }

    public static String getTime(Date date, DateFormat formatter){
        return formatter.format(date);
    }

    public static Date convertToUTC(Date date){
        DateTimeZone tz = DateTimeZone.getDefault();
        Date utcDate = new Date(tz.convertLocalToUTC(date.getTime(), false));
        System.out.println("UTC Date : " + utcDate);
        Date utcDate2 = new Date(tz.convertLocalToUTC(utcDate.getTime(), false));
        System.out.println("UTC Date 2 : " + utcDate2);
        return utcDate;
    }

    public static DateTime getYestday(String timeZone){
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone).minusDays(1);
        return  dateTime;
    }

    public static DateTime getCurrentDateByTimeZone(String timeZone){
        DateTime dateTime = getDateByTimeZone(new DateTime(),timeZone);
        return dateTime;
    }

    public static DateTime getDateTimeMinus(DateTime endFtime, int timeKind, int maxLen) {
        Long millis = endFtime.getMillis();
        millis = millis - millis % (timeKind * 60 * 1000) - timeKind * maxLen * 60 * 1000;
        return  new DateTime(millis);
    }

    public static int getBetweenMonth(DateTime beginFtime,DateTime endFtime){
        DateTime begin = beginFtime.dayOfMonth().withMinimumValue().withMillisOfDay(0).dayOfMonth().getDateTime();
        DateTime end = endFtime.dayOfMonth().withMinimumValue().withMillisOfDay(0).dayOfMonth().getDateTime();
        Period p = new Period(begin,end, PeriodType.months());
        return p.getMonths();
    }

    public static int getBetweenYear(DateTime beginFtime,DateTime endFtime){
        DateTime begin = beginFtime.dayOfYear().withMinimumValue().withMillisOfDay(0);
        DateTime end = endFtime.dayOfYear().withMinimumValue().withMillisOfDay(0);
        Period p = new Period(begin,end, PeriodType.years());
        return p.getYears();
    }

    /**
     *  时间相差的天数（不包含开始日期）
     * @param beginFtime
     * @param endFtime
     * @return
     */
    public static int getBetweenDays(DateTime beginFtime,DateTime endFtime){
      return getBetweenDays(beginFtime,endFtime,false);
    }

    /**
     * 时间相差的天数（是否包括开始日期）
     * @param beginFtime
     * @param endFtime
     * @param includeBeginDay  是否包括开始日期
     * @return
     */
    public static int getBetweenDays(DateTime beginFtime,DateTime endFtime,boolean includeBeginDay){
        DateTime begin = beginFtime.withMillisOfDay(0);
        DateTime end = endFtime.withMillisOfDay(0);
        Period p = new Period(begin,end, PeriodType.days());
        int days= p.getDays();
        if(includeBeginDay){
            days = days+1;
        }
        return days;
    }


    /**
     * 获取整数倍的时间线
     * @param timeZone
     * @param timeKind
     * @param maxLen
     * @return
     */
    public static DateTime getDateTimeMinutesByTimeZone(String timeZone,int timeKind, int maxLen){
        DateTimeZone zone = DateTimeZone.forID(timeZone);
        DateTime dateTime = new DateTime(zone);
        Long millis = dateTime.getMillis();
        millis = millis - millis % (timeKind * 60 * 1000) - timeKind * maxLen * 60 * 1000;
        return  new DateTime(millis);
    }

    public static DateTime getDateZeroByTimeZone(DateTime dateTime,String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime return_dateTime = new DateTime(dateTime.withMillisOfDay(0),zone);
        return return_dateTime;
    }

   /* *//**
     * 返回下个阶段的时间戳
     *
     * @param TimeKind      间隔
     * @param nowFtime      每分钟最新的K线时间
     * @param lastklineFtime    指定Kline最大时间
     *//*
    public static DateTime isOpenNewPeriod(long TimeKind, Long nowFtime,Long lastklineFtime, TimeZoneConfig timeZoneConfig) {//
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneConfig.getTimeZone()));
        DateTime startTime = getDateZeroByTimeZone(new DateTime(nowFtime),timeZoneConfig.getTimeZone());
        nowFtime = nowFtime/(60*1000)*(60*1000);
        // DateTime ftimeDateTime = new DateTime(new DateTime(nowFtime).toString("yyyy-MM-dd HH:mm:ss"));
        DateTime ftimeDateTime = getDateByTimeZone(new DateTime(nowFtime),timeZoneConfig.getTimeZone());
        // 委单时间 距离 本月开始时间 间隔
        long minus = (ftimeDateTime.getMillis() - startTime.getMillis()) / (60 * 1000L);
        if(null != lastklineFtime) {
            DateTime lastFTime = new DateTime(lastklineFtime, zone);
            DateTime nowTime = new DateTime(startTime.getMillis() + (minus / TimeKind) * TimeKind * 60 * 1000L);
            if (nowTime != lastFTime) {
                return nowTime;
            }
        }else{
            DateTime nowTime = new DateTime(startTime.getMillis() + (minus / TimeKind) * TimeKind * 60 * 1000L);
            return nowTime;
        }
        return null;
    }*/

    public static DateTime getDateByTimeZone(DateTime pdateTime,String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(pdateTime.getMillis(),zone);
        return dateTime;
    }


}
