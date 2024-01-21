package hotcoin.quote.utils;

import hotcoin.quote.config.TimeZoneConfig;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JodaTimeUtil {

    public static final String FORMATE_DATE = "yyyy-MM-dd";
    public static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_MINITER = FORMATE_DATE.concat(" ").concat("HH:mm:ss");
    private static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
     * 返回下个阶段的时间戳
     *
     * @param TimeKind      间隔
     * @param nowFtime      每分钟最新的K线时间
     * @param lastklineFtime    指定Kline最大时间
     */
    public static DateTime isOpenNewPeriod(long TimeKind, Long nowFtime,Long lastklineFtime, TimeZoneConfig timeZoneConfig) {
        String timeZone = TimeZoneConfig.timeZone;
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

}
