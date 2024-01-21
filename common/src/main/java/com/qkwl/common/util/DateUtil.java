package com.qkwl.common.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
     * 获取某日期,几天前的日期
     * @param dayNum
     * @return
     */
    public static Date getDateByDayNum(Date date, int dayNum) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plusDays(dayNum);
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
}
