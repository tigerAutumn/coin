package com.qkwl.service.util;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class JodaTimeUtil {

    private static final String FORMATE_DATE = "yyyy-MM-dd";
    private static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_MINITER = FORMATE_DATE.concat(" ").concat("HH:mm:ss");
    private static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";


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


    public static DateTime getCurrentDateByTimeZone(String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(zone);
        return dateTime;
    }

    public static DateTime getDateZeroByTimeZone(DateTime pdateTime,String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(new DateTime(pdateTime.withMillisOfDay(0)),zone);
        return dateTime;
    }

    public static DateTime getCurrentDateZeroByTimeZone(String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(zone).withMillisOfDay(0);
        return dateTime;
    }



    /*
     * 毫秒转化时分秒毫秒
     */

    public static KlineTime getMinuts(Long ms){
        DateTime dt = new DateTime(ms).withMillisOfSecond(0).withSecondOfMinute(0);
        long hours = dt.getHourOfDay();
        long minutes = dt.getMinuteOfDay();
        long seconds = dt.getSecondOfDay();
        long day = dt.getDayOfMonth();
        long ts = dt.getMillis();
        KlineTime klineTime = new KlineTime();
        klineTime.setHour(hours);
        klineTime.setMinute(minutes);
        klineTime.setTs(ts);
        klineTime.setDay(day);
        return klineTime;
    }

    public static int getLastDay(Long ms){
        DateTime dt = new DateTime(ms).withMillisOfSecond(0).withSecondOfMinute(0);
        return dt.minusDays(1).getDayOfMonth();
    }

    /**
     * 一天之内分钟级时间段查询
     * @param TimeKind
     * @param ftime
     * @param timeZone
     * @return
     */
    public static DateTime isOpenNewPeriodForDay(long TimeKind, Long ftime, String timeZone) {
        //避免当日
        DateTime startTime = getDateZeroByTimeZone(new DateTime(ftime).minusDays(1),timeZone);
        // DateTime ftimeDateTime = new DateTime(new DateTime(nowFtime).toString("yyyy-MM-dd HH:mm:ss"));
        DateTime nowftimeDateTime = getDateByTimeZone(new DateTime(ftime),timeZone);
        // 委单时间 距离 本月开始时间 间隔
        long minus = (nowftimeDateTime.getMillis() - startTime.getMillis()) / (60 * 1000L);
        DateTime nowTime = new DateTime(startTime.getMillis() + (minus / TimeKind) * TimeKind * 60 * 1000L);
        return nowTime;
    }

    public static DateTime getDateByTimeZone(DateTime pdateTime,String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(pdateTime.getMillis(),zone);
        return dateTime;
    }

    public static DateTime getDateByTimeZone(Long mills,String timeZone){
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(mills,zone);
        return dateTime;
    }

    /**
     * 返回下个阶段的时间戳
     *
     * @param TimeKind      间隔
     * @param nowFtime      每分钟最新的K线时间
     * @param lastklineFtime    指定Kline最大时间
     */
    public static DateTime isOpenNewPeriod(long TimeKind, Long nowFtime,Long lastklineFtime) {
        String timeZone = "GMT+8";
        DateTime ftimeDateTime = getDateByTimeZone(new DateTime(nowFtime),timeZone);
        //月
        if(TimeKind/(30 * 24 * 60)>=1) {
            DateTime newDateTime = ftimeDateTime.dayOfMonth().withMinimumValue().withMillisOfDay(0);
            if(null != lastklineFtime){
                DateTime lastFTime = getDateByTimeZone(new DateTime(lastklineFtime),timeZone);
                if(lastFTime.getMillis() - newDateTime.getMillis() == 0){
                    return null;
                }else{
                    return newDateTime;
                }
            }else{
                return newDateTime;
            }
        }else if(TimeKind/(7 * 24 * 60)>=1){
            //周
            DateTime newDateTime = ftimeDateTime.withDayOfWeek(1).withMillisOfDay(0);
            if(null != lastklineFtime){
                DateTime lastFTime = getDateByTimeZone(new DateTime(lastklineFtime),timeZone);
                if(lastFTime.getMillis() - newDateTime.getMillis() == 0){
                    return null;
                }else{
                    return newDateTime;
                }
            }else{
                return newDateTime;
            }
        }else {
            DateTime newDateTime = isOpenNewPeriodForDay(TimeKind, nowFtime, timeZone);

            if(null != lastklineFtime){
                DateTime lastFTime = getDateByTimeZone(new DateTime(lastklineFtime),timeZone);
                DateTime newlastFtime = isOpenNewPeriodForDay(TimeKind, lastFTime.getMillis(), timeZone);
                if(newlastFtime.getMillis() - newDateTime.getMillis() == 0){
                    return null;
                }else{
                    return newDateTime;
                }
            }else{
                return newDateTime;
            }
        }
    }

}
