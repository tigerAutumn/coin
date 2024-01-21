package com.hotcoin.increment.constant;

public class IncrementConstant {
	
	//hbase年表前缀
	public final static String YEAR_TABLE_NAME = "increment:year";
	//hbase月表前缀
	public final static String MONTH_TABLE_NAME_PREFIX = "increment:month_";
	//hbase天表前缀
	public final static String DAY_TABLE_NAME_PREFIX = "increment:day_";
	//增量数据用户分布式锁前缀
	public final static String INTREMENT_SPARK_LOCK_PREFIX = "INCREMENT_SPARK_lOCK_";
	//幂等
	public final static String INTREMENT_ENTRUST_MQ_PREFIX = "INTREMENT_ENTRUST_MQ_PREFIX";
	//hbase统计总计family
	public final static String TOTAL = "total";
	//hbase统计总计family前缀
	public final static String HBASE_FAMILY_PREFIX = "f";
	
	
	public final static String INTREMENT_TOPIC_TABLE = "increment:topic";

}
