package com.hotcoin.activity.util;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * @ProjectName: service_activity_v2
 * @ClassName: DateUtil
 * @Author: hf
 * @Description:
 * @Date: 2019/4/23 14:24
 * @Version: 1.0
 */
public class DateUtil {
    public static final String FORMATE_DATE = "yyyy-MM-dd";
    public static final String FORMATE_SECONDS = "HH:mm:ss";
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /***
     * 获取某日期,几天前或者几天后的日期(dayNum为负数则为几天前)
     * @param dayNum
     * @return
     */
    public static Date getDateByDayNum(Date date, int dayNum) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusDays(dayNum);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
    /***
     * 获取某日期,几秒后的日期(dayNum为负数则为几秒前)
     * @param secondsNum
     * @return
     */
    public static Date getDateBySecondsNum(Date date, int secondsNum) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusSeconds(secondsNum);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
    /***
     * 获取某日期,几小时后的日期(hoursNum为负数则为几小时前)
     * @param hoursNum
     * @return
     */
    public static Date getDateByHoursNum(Date date, int hoursNum) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusHours(hoursNum);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
    /**
     * 获取当天0点的时间
     *
     * @return
     */
    public static Date getDayStartTime() {
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Instant instant = today_start.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date strToDate(String dateTimeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_FORMAT);
        try {
            return simpleDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return "";
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }
}
