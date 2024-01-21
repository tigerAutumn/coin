package hotcoin.util;

import hotcoin.model.constant.KlineHistoryConstant;
import hotcoin.model.em.TimeTypeEnum;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class JodaTimeUtil {

    public static final String FORMATE_DATE = "yyyy-MM-dd";
    public static final String FORMATE_SECONDS = "HH:mm:ss";
    private static final String FORMATE_MINITER = FORMATE_DATE.concat(" ").concat("HH:mm:ss");
    private static final String FORMATE_FULL = FORMATE_DATE.concat(" ").concat(FORMATE_SECONDS);
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }


    public static String getTime(Date date, DateFormat formatter) {
        return formatter.format(date);
    }

    public static Date convertToUTC(Date date) {
        DateTimeZone tz = DateTimeZone.getDefault();
        Date utcDate = new Date(tz.convertLocalToUTC(date.getTime(), false));
        System.out.println("UTC Date : " + utcDate);
        Date utcDate2 = new Date(tz.convertLocalToUTC(utcDate.getTime(), false));
        System.out.println("UTC Date 2 : " + utcDate2);
        return utcDate;
    }


    public static DateTime getCurrentDateByTimeZone(String timeZone) {
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(zone);
        return dateTime;
    }

    public static DateTime getDateZeroByTimeZone(DateTime pdateTime, String timeZone) {
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(new DateTime(pdateTime.withMillisOfDay(0)), zone);
        return dateTime;
    }

    public static DateTime getCurrentDateZeroByTimeZone(String timeZone) {
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(zone).withMillisOfDay(0);
        return dateTime;
    }

    public static int getBetweenMonth(DateTime beginFtime, DateTime endFtime) {
        DateTime begin = beginFtime.dayOfMonth().withMinimumValue().withMillisOfDay(0).dayOfMonth().getDateTime();
        DateTime end = endFtime.dayOfMonth().withMinimumValue().withMillisOfDay(0).dayOfMonth().getDateTime();
        Period p = new Period(begin, end, PeriodType.months());
        return p.getMonths();
    }

    public static int getBetweenYear(DateTime beginFtime, DateTime endFtime) {
        DateTime begin = beginFtime.dayOfYear().withMinimumValue().withMillisOfDay(0);
        DateTime end = endFtime.dayOfYear().withMinimumValue().withMillisOfDay(0);
        Period p = new Period(begin, end, PeriodType.years());
        return p.getYears();
    }

    /**
     * 时间相差的天数（不包含开始日期）
     *
     * @param beginFtime
     * @param endFtime
     * @return
     */
    public static int getBetweenDays(DateTime beginFtime, DateTime endFtime) {
        return getBetweenDays(beginFtime, endFtime, false);
    }

    /**
     * 时间相差的天数（是否包括开始日期）
     *
     * @param beginFtime
     * @param endFtime
     * @param includeBeginDay 是否包括开始日期
     * @return
     */
    public static int getBetweenDays(DateTime beginFtime, DateTime endFtime, boolean includeBeginDay) {
        DateTime begin = beginFtime.withMillisOfDay(0);
        DateTime end = endFtime.withMillisOfDay(0);
        Period p = new Period(begin, end, PeriodType.days());
        int days = p.getDays();
        if (includeBeginDay) {
            days = days + 1;
        }
        return days;
    }

    public static DateTime getDateByTimeZone(DateTime pdateTime, String timeZone) {
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(pdateTime.getMillis(), zone);
        return dateTime;
    }

    public static DateTime getDateByTimeZone(Long mills, String timeZone) {
        DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
        DateTime dateTime = new DateTime(mills, zone);
        return dateTime;
    }

    /**
     * 获取当前时间戳,忽略分钟数(13位)
     *
     * @return
     */
    public static long getCurrentTimestampIgnoreSeconds() {
        return System.currentTimeMillis() / 1000 / 60 * 60 * 1000;
    }


    /**
     * 获取时间戳,忽略分钟数(13位)
     *
     * @return
     */
    public static long timestampIgnoreSecond(long time) {
        return time / 1000 / 60 * 60 * 1000;
    }

    /**
     * 获取上一分钟或者下一分钟的时间戳,
     *
     * @param time         13位
     * @param direction    1表示下一分钟时间,-1表示上一分钟时间,以此类推
     * @param timeTypeEnum 时间类型
     * @return
     */
    @Deprecated
    public static long getTimestampBeforeOrAfter(long time, int direction, TimeTypeEnum timeTypeEnum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        switch (timeTypeEnum) {
            case MIN:
                calendar.add(Calendar.MINUTE, direction);
                break;
            case HOUR:
                calendar.add(Calendar.HOUR, direction);
                break;
            case DAY:
                calendar.add(Calendar.DATE, direction);
                break;
            case WEEK:
                calendar.add(Calendar.DATE, direction * 7);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, direction);
                break;
            default:
                break;
        }

        return calendar.getTime().getTime();
    }

    /**
     * 获取上一分钟或者下一分钟的时间戳,
     *
     * @param time         13位
     * @param direction    1表示下一分钟时间,-1表示上一分钟时间,以此类推
     * @param timeTypeEnum 时间类型
     * @return
     */
    public static long getTimestampBeforeOrAfterByJdk8(long time, int direction, TimeTypeEnum timeTypeEnum) {
        LocalDateTime localDateTime = getDateTimeOfTimestamp(time);
        LocalDateTime result = null;
        switch (timeTypeEnum) {
            case MIN:
                result = localDateTime.plusMinutes(direction);
                break;
            case HOUR:
                result = localDateTime.plusHours(direction);
                break;
            case DAY:
                result = localDateTime.plusDays(direction);
                break;
            case WEEK:
                result = localDateTime.plusWeeks(direction);
                break;
            case MONTH:
                result = localDateTime.plusMonths(direction);
                break;
            default:
                break;
        }

        return getTimestampOfDateTime(result);
    }


    /**
     * long类型的timestamp转为LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转为long类型的timestamp
     *
     * @param localDateTime
     * @return
     */
    public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 从时间戳中得到当前年份,或者月份,小时,分钟
     *
     * @param time
     * @param timeTypeEnum
     * @return
     */
    public static int getPartTimeOfTimestamp(long time, TimeTypeEnum timeTypeEnum) {
        int result = 0;
        switch (timeTypeEnum) {
            case MIN:
                result = getDateTimeOfTimestamp(time).getMinute();
                break;
            case HOUR:
                result = getDateTimeOfTimestamp(time).getHour();
                break;
            case DAY:
                result = getDateTimeOfTimestamp(time).getDayOfMonth();
                break;
            case WEEK:
                result = getDateTimeOfTimestamp(time).getDayOfWeek().getValue();
                break;
            case MONTH:
                result = getDateTimeOfTimestamp(time).getMonthValue();
                break;
            case YEAR:
                result = getDateTimeOfTimestamp(time).getYear();
                break;
            default:
                break;
        }
        return result;
    }

    public static Map<String, Integer> getPartTimeOfTimestampMap(long time) {
        Map<String, Integer> map = new HashMap<>(4);
        LocalDateTime localDateTime = getDateTimeOfTimestamp(time);
        int minute = localDateTime.getMinute();
        int hour = localDateTime.getHour();
        int day = localDateTime.getDayOfMonth();
        int week = localDateTime.getDayOfWeek().getValue();
        map.put(KlineHistoryConstant.KLINE_HIS_MIN, minute);
        map.put(KlineHistoryConstant.KLINE_HIS_HOUR, hour);
        map.put(KlineHistoryConstant.KLINE_HIS_DAY, day);
        map.put(KlineHistoryConstant.KLINE_HIS_WEEK, week);
        return map;
    }

    /**
     * 获得某天最大时间 2019-09-17 23:59:59
     *
     * @param time
     * @return
     */
    public static long getEndOfDay(long time) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    /**
     * 获得某天最小时间 2019-09-17 00:00:00
     *
     * @param time
     * @return
     */
    public static long getStartOfDay(long time) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    /**
     * 获取某年的开始日期 eg: 2019-01-01 00:00
     *
     * @param offset 0今年，1明年，-1去年，
     * @return
     */
    public static long yearOfStart(long time, int offset) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return localDateTime.plusYears(offset).with(TemporalAdjusters.firstDayOfYear())
                .withMonth(01)
                .withDayOfMonth(01)
                .withHour(00)
                .withMinute(00)
                .withSecond(00)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    /**
     * 获取某年的开始日期 eg: 2019-12-31 59:59
     *
     * @param offset 0今年，1明年，-1去年，
     * @return
     */
    public static long yearOfEnd(long time, int offset) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        return localDateTime.plusYears(offset).with(TemporalAdjusters.lastDayOfYear())
                .withMonth(12)
                .withDayOfMonth(31)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }
}
