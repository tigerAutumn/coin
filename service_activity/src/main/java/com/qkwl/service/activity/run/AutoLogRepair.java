package com.qkwl.service.activity.run;

import com.qkwl.common.util.DateUtil;
import com.qkwl.common.util.DateUtils;
import com.qkwl.service.activity.impl.UserLogService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 日志定时统计
 *
 * @author TT
 */
@Component("autoLogRepair")
public class AutoLogRepair {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoLogRepair.class);

    @Autowired
    private UserLogService userLogService;

    @PostConstruct
    public void init() {
    	Thread thread = new Thread(new Work());
		thread.setName("runSettlement");
		thread.start();
    }

    class Work implements Runnable{

    	@Override
    	public void run() {
    		 logger.info("--------> 开始统计用户交易量  <--------------");
    	        try {
    	        	Date start = DateUtils.parse("2019-11-06 00:00:00");
    	        	Date end = DateUtils.parse("2019-11-20 01:00:00");
    	        	
    	        	List<Date> dayList = getDayList(start, end);
    	        	
    	        	for (int i = 0; i < dayList.size() - 1; i++) {
    	        		// 定时器更新
    	                userLogService.upTradeJob(dayList.get(i),dayList.get(i+1));
    				}
    	        } catch (Exception e) {
    	            logger.info("----> autoclog failed upTradeJob",e);
    	        }
    	        logger.info("--------> 用户交易量统计结束  <--------------");
    	}
    	
    }
    
    /**
	 * 获取开始和结束时间内每一天的零点时间
	 * */
	public static List<Date> getDayList(Date start,Date end) {
		List<Date> dateList = new ArrayList<>();
		if(start == null || end == null || start.getTime() > end.getTime()) {
			return dateList;
		}
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(start);
		calendarStart.set(Calendar.HOUR_OF_DAY, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		calendarStart.set(Calendar.MILLISECOND,0);
		
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(end);
		
		while (calendarStart.before(calendarEnd)) {
			dateList.add(calendarStart.getTime());
			calendarStart.add(Calendar.DAY_OF_MONTH, 1);
		}
		return dateList;
	}
	
}



