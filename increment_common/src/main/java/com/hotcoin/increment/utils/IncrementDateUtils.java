package com.hotcoin.increment.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IncrementDateUtils {

	//获取月份天数
	public static int GetMonthDayCount(int year,int month)
	{
		switch(month)
		{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				{
					return 31;
				}
			case 4:
			case 6:
			case 9:
			case 11:
				{
					return 30;
				}
			case 2:
				{
					if(year%4 != 0)
					{
						return 28;
					}
					else
					{
						if(year%100 != 0)
						{
							return 29;
						}
						else
						{
							if(year%400 != 0)
							{
								return 28;
							}
							else
							{
								return 29;
							}
						}
					}
				}
			default:
				{
					return 0;
				}
		}
	}
	
	public static Date parse(String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.parse(date);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
