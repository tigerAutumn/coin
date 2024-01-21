package com.qkwl.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConcurrentDateUtil {

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
    	
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        
    };

    private static ThreadLocal<DateFormat> threadLocal2 = new ThreadLocal<DateFormat>() {
    	
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
        
    };

    public static Date parse(String dateStr) throws ParseException {
    	Date date;
    	try {
    		date = threadLocal.get().parse(dateStr);
    	}catch(Exception e) {
    		date = threadLocal2.get().parse(dateStr);
    	}
        return date;
    }

    public static String format(Date date) {
        return threadLocal.get().format(date);
    }
    
}