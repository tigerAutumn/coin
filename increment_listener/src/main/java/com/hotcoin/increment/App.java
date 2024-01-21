package com.hotcoin.increment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.CanCommitOffsets;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.ConsumerStrategy;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;
import org.apache.spark.streaming.scheduler.StreamingListener;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerBatchSubmitted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationCompleted;
import org.apache.spark.streaming.scheduler.StreamingListenerOutputOperationStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverError;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStarted;
import org.apache.spark.streaming.scheduler.StreamingListenerReceiverStopped;
import org.apache.spark.streaming.scheduler.StreamingListenerStreamingStarted;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotcoin.increment.Enum.EntrustSideType;
import com.hotcoin.increment.bean.IncrementBean;
import com.hotcoin.increment.bean.KafakaMqDto;
import com.hotcoin.increment.bean.KafkaMQEntrust;
import com.hotcoin.increment.constant.IncrementConstant;
import com.hotcoin.increment.listener.SparkMonitoringListener;
import com.hotcoin.increment.utils.ConfigManager;
import com.hotcoin.increment.utils.DateUtils;
import com.hotcoin.increment.utils.KafkaOffsetManager;
import com.hotcoin.increment.utils.MathUtils;
import com.hotcoin.increment.utils.RedisTool;

import kafka.common.TopicAndPartition;
import redis.clients.jedis.Jedis;



/***
 *                    _ooOoo_
 *                   o8888888o
 *                   88" . "88
 *                   (| -_- |)
 *                    O = /O
 *                ____/`---'____
 *              .   ' \| |// `.
 *               / \||| : |||// 
 *             / _||||| -:- |||||- 
 *               | | \ - /// | |
 *             | _| ''---/'' | |
 *               .-__ `-` ___/-. /
 *           ___`. .' /--.-- `. . __
 *        ."" '< `.____<|>_/___.' >'"".
 *       | | : `- `.;` _ /`;.`/ - ` : | |
 *           `-. _ __ /__ _/ .-` / /
 * ======`-.____`-.________/___.-`____.-'======
 *                    `=---='
 *
 * .............................................
 *          佛祖保佑             永无BUG
 *          
 * 通过kafka接收历史委单的变化更新平衡数据       
 * @author HWJ
 */
public class App implements Serializable{
    	
    	private static final long serialVersionUID = 4746372206301124368L;
    	
    	private static final Logger logger = Logger.getLogger(App.class);
    	
    	
    	private static final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<>();
    	
    	private static final List<String> list = new ArrayList<>();
    	
    	private static final Map<String, Object> kafkaConsumerParams = new HashMap<String, Object>();
    	
    	private static Map<TopicPartition,Long> fromOffsets;
    	
    	private static HashSet<String> topicSet;
    	
    	public static void main(String[] args) throws Exception {
    		Logger.getLogger("org").setLevel(Level.ERROR);
    		logger.info("程序启动");
    		long time = 0;
    		if(args.length > 0) {
    			logger.info("入参："+args[0]);
    			try {
    				Calendar calendar = Calendar.getInstance();
    				int year = calendar.get(Calendar.YEAR);
    				int month = calendar.get(Calendar.MONTH) + 1;
    				int day = calendar.get(Calendar.DAY_OF_MONTH);
    				String dateStr = year + ":" + month + ":" + day + " " + args[0];
    				time = DateUtils.parse(dateStr).getTime();
    				
    			} catch (Exception e) {
					logger.error("转译时间异常",e);
				}
    			
    		}
    		try {
				
			
    		JavaStreamingContext ssc = getJavaStreamingContext(time);
    		ssc.start();
    		ssc.awaitTermination();
    		} catch (Exception e) {
				logger.error("spark异常",e);
			}
    	}
    	
    	private static JavaStreamingContext getJavaStreamingContext(long time) {
    		//初始化配置参数
    		createParams();
    		
    	    SparkConf sc = new SparkConf().setAppName(ConfigManager.getProperty("steaming.appName")).setMaster("local[*]");
/*    	    sc.set("spark.speculation", "true");
    	    sc.set("spark.speculation.interval", "300s");
    	    sc.set("spark.speculation.quantile","0.5");*/
    		Duration seconds = Durations.seconds(Long.valueOf(ConfigManager.getProperty("steaming.interval")));
    		JavaStreamingContext ssc = new JavaStreamingContext(sc,seconds);
    		ssc.addStreamingListener(new SparkMonitoringListener());
    		
    		ConsumerStrategy<String, String> subscribe = null;
    		//判断是否存在kafka偏移量
    		if(fromOffsets == null || fromOffsets.isEmpty()) {
    			subscribe = ConsumerStrategies.Subscribe(topicSet, kafkaConsumerParams);
    		}else {
    			subscribe = ConsumerStrategies.Subscribe(topicSet, kafkaConsumerParams,fromOffsets);
    		}
    	    JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
    	            ssc,
    	            LocationStrategies.PreferConsistent(),
    	            subscribe);
    	    
    	    stream.transform(rdd -> {    
    	    	//获取偏移量
    	    	OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();    
    	    	offsetRanges.set(offsets);
    	    	return rdd;
    	    }).mapPartitions(new FlatMapFunction<Iterator<ConsumerRecord<String,String>>, String>() {
				private static final long serialVersionUID = 1L;

				
				@Override
				public Iterator<String> call(Iterator<ConsumerRecord<String, String>> t) throws Exception {
					if(t == null) {
				    	return list.iterator();
				    }
					Connection conn = null;
					Jedis jedis =null;
					try {
						//conn = ConnectionFactory.createConnection(ConfigManager.getHbaseconfig());
						conn = ConfigManager.getConn();
						jedis = new Jedis(ConfigManager.getProperty("redis.host"),ConfigManager.getIntProperty("redis.port"),ConfigManager.getIntProperty("redis.timeout"));
			    	    if(!StringUtils.isEmpty(ConfigManager.getProperty("redis.password"))) {
					    	jedis.auth(ConfigManager.getProperty("redis.password"));
					    }
					} catch (Exception e) {
						logger.error("建立链接异常",e);
					}
					Calendar calendar = Calendar.getInstance();
				    
					while(t.hasNext()) {
						String lockKey = null;
						String next = null;
						String uuid = RedisTool.getUUID();
						//BigInteger id = null;
						try {
							next = t.next().value();
							//logger.info(next);
							KafakaMqDto dto = JSON.parseObject(next, KafakaMqDto.class);
							
							JSONObject parseObject = JSON.parseObject(next);
							KafkaMQEntrust entrust = JSON.parseObject(parseObject.getString("extObj"), KafkaMQEntrust.class);
							
							if(entrust.getBizTime() < time) {
								continue;
							}
							
							calendar.setTime(new Date(entrust.getBizTime()));
							int year = calendar.get(Calendar.YEAR);
							int month = calendar.get(Calendar.MONTH) + 1;
							int day = calendar.get(Calendar.DAY_OF_MONTH);
							//id = entrust.getId();
							
							//幂等
							String entrustRedis = IncrementConstant.INTREMENT_ENTRUST_MQ_PREFIX +dto.getBizServiceName() + entrust.getId()  ;
							Long idempotent = jedis.setnx(entrustRedis , "1");
							if(idempotent.equals(1l)) {
								jedis.expire(entrustRedis, 10 * 60);
							}else {
								continue;
							}
							//加锁
							lockKey = IncrementConstant.INTREMENT_SPARK_LOCK_PREFIX + entrust.getUid();
							boolean isSecc = false;
							//logger.info(entrust.getId() + "获取锁");
							while (!isSecc) {
								if(RedisTool.tryGetDistributedLock(jedis, lockKey, uuid, 60 * 1000) ) {
									isSecc = true;
								}else {
									Thread.sleep(5);
								}
							}
							String tableName = IncrementConstant.DAY_TABLE_NAME_PREFIX + year + "_" + month;
							TableName tname = TableName.valueOf(tableName);
							Table table = conn.getTable(tname);
							byte[] dayBytes = Bytes.toBytes(IncrementConstant.HBASE_FAMILY_PREFIX + day);
							
							byte[] totalBytes = Bytes.toBytes(IncrementConstant.TOTAL);
							
							byte[] buyCoinIdBytes = Bytes.toBytes(entrust.getBuyCoinId().toString());
							
							byte[] sellCoinIdBytes = Bytes.toBytes(entrust.getSellCoinId().toString());
							
							byte[] uidBytes = Bytes.toBytes(entrust.getUid().toString());
							//logger.info(id + "查询数据");
							Get get = new Get(uidBytes);
							get.addColumn(dayBytes, buyCoinIdBytes);
							get.addColumn(totalBytes, buyCoinIdBytes);
							get.addColumn(dayBytes, sellCoinIdBytes);
							get.addColumn(totalBytes, sellCoinIdBytes);
							
							Result r = table.get(get);
							
							IncrementBean dayBuy = getBean(r.getValue(dayBytes, buyCoinIdBytes));
							
							IncrementBean dayBuyTotal = getBean(r.getValue(totalBytes, buyCoinIdBytes));
							
							IncrementBean daySell = getBean(r.getValue(dayBytes, sellCoinIdBytes));
							
							IncrementBean daySellTotal = getBean(r.getValue(totalBytes, sellCoinIdBytes));
							
							Put put = new Put(uidBytes);
							
							if(entrust.getSide() == EntrustSideType.BUY.getCode()) {
								//买币收币，卖币收钱
								//修改天表统计数据
								//买方币的交易花费
								if(dayBuy == null) {
									dayBuy = new IncrementBean();
									dayBuy.setCoinId(entrust.getBuyCoinId());
									dayBuy.setUserId(entrust.getUid());
									dayBuy.setTradeCost(entrust.getAmount());
									dayBuy.setTradeIncome(BigDecimal.ZERO);
									dayBuy.setTradeFee(BigDecimal.ZERO);
								}else {
									dayBuy.setTradeCost(MathUtils.add(dayBuy.getTradeCost(),entrust.getAmount()));
								}
								put.addColumn(dayBytes,buyCoinIdBytes, Bytes.toBytes(JSON.toJSONString(dayBuy)));
								//天表的总计
								if(dayBuyTotal == null) {
									dayBuyTotal = dayBuy;
								}else {
									dayBuyTotal.setTradeCost(MathUtils.add(dayBuyTotal.getTradeCost(),entrust.getAmount()));
								}
								put.addColumn(totalBytes,buyCoinIdBytes, Bytes.toBytes(JSON.toJSONString(dayBuyTotal)));
								
								//卖方币的
								if(daySell == null) {
									daySell = new IncrementBean();
									daySell.setCoinId(entrust.getSellCoinId());
									daySell.setUserId(entrust.getUid());
									daySell.setTradeCost(BigDecimal.ZERO);
									daySell.setTradeIncome(entrust.getCount());
									daySell.setTradeFee(entrust.getFee());
								}else {
									daySell.setTradeIncome(MathUtils.add(daySell.getTradeIncome(),entrust.getCount()));
									daySell.setTradeFee(MathUtils.add(daySell.getTradeFee(),entrust.getFee()));
								}
								put.addColumn(dayBytes,sellCoinIdBytes, Bytes.toBytes(JSON.toJSONString(daySell)));
								//天表的总计
								if(daySellTotal == null) {
									daySellTotal = daySell;
								}else {
									daySellTotal.setTradeIncome(MathUtils.add(daySellTotal.getTradeIncome(),entrust.getCount()));
									daySellTotal.setTradeFee(MathUtils.add(daySellTotal.getTradeFee(),entrust.getFee()));
								}
								put.addColumn(totalBytes,sellCoinIdBytes, Bytes.toBytes(JSON.toJSONString(daySellTotal)));
							}else if(entrust.getSide() == EntrustSideType.SELL.getCode()) {
								//买币收币，卖币收钱
								//修改天表统计数据
								//买方币的交易收入和手续费
								if(dayBuy == null) {
									dayBuy = new IncrementBean();
									dayBuy.setCoinId(entrust.getBuyCoinId());
									dayBuy.setUserId(entrust.getUid());
									dayBuy.setTradeCost(BigDecimal.ZERO);
									dayBuy.setTradeIncome(entrust.getAmount());
									dayBuy.setTradeFee(entrust.getFee());
								}else {
									dayBuy.setTradeIncome(MathUtils.add(dayBuy.getTradeIncome(),entrust.getAmount()));
									dayBuy.setTradeFee(MathUtils.add(dayBuy.getTradeFee(),entrust.getFee()));
								}
								put.addColumn(dayBytes,buyCoinIdBytes, Bytes.toBytes(JSON.toJSONString(dayBuy)));
								//天表的总计
								if(dayBuyTotal == null) {
									dayBuyTotal = dayBuy;
								}else {
									dayBuyTotal.setTradeIncome(MathUtils.add(dayBuyTotal.getTradeIncome(),entrust.getAmount()));
									dayBuyTotal.setTradeFee(MathUtils.add(dayBuyTotal.getTradeFee(),entrust.getFee()));
								}
								put.addColumn(totalBytes,buyCoinIdBytes, Bytes.toBytes(JSON.toJSONString(dayBuyTotal)));
								
								//卖方币的统计数据
								if(daySell == null) {
									daySell = new IncrementBean();
									daySell.setCoinId(entrust.getSellCoinId());
									daySell.setUserId(entrust.getUid());
									daySell.setTradeCost(entrust.getCount());
									daySell.setTradeIncome(BigDecimal.ZERO);
									daySell.setTradeFee(BigDecimal.ZERO);
								}else {
									daySell.setTradeCost(MathUtils.add(daySell.getTradeCost(),entrust.getCount()));
								}
								put.addColumn(dayBytes,sellCoinIdBytes, Bytes.toBytes(JSON.toJSONString(daySell)));
								//天表的总计
								if(daySellTotal == null) {
									daySellTotal = daySell;
								}else {
									daySellTotal.setTradeCost(MathUtils.add(daySellTotal.getTradeCost(),entrust.getCount()));
								}
								put.addColumn(totalBytes,sellCoinIdBytes, Bytes.toBytes(JSON.toJSONString(daySellTotal)));
							}
							
							//入库
							//logger.info(id + "入库");
							table.put(put);
							//logger.info(id + "入库结束");
						} catch (Exception e) {
							logger.error("执行异常，mq："+next,e);
						}finally {
							//解锁
							if(lockKey != null) {
								try {
									int count = 0;
									boolean releaseDistributedLock = RedisTool.releaseDistributedLock(jedis, lockKey, uuid);
									while(!releaseDistributedLock && count < 5) {
										count++;
										releaseDistributedLock = RedisTool.releaseDistributedLock(jedis, lockKey, uuid);
									}
									//logger.info(id + "解锁");
								} catch (Exception e2) {
									logger.error("解锁异常",e2);
								}
							}
						}
					}
					try {
						jedis.close();
						/*int i = 0;
						while(!conn.isClosed()) {
							conn.close();
							i++;
							if(i > 10) {
								logger.error("链接未成功关闭"+i);
							}
						}*/
						//logger.info("jedis.close");
					} catch (Exception e) {
						logger.error("jedis.close err",e);
					}
					return list.iterator();
				}
			}).foreachRDD(rdd -> {
				
				try {
					//算子，如果没有算子就会不会触发计算
					rdd.isEmpty();
					//提交kafka偏移量
					//logger.info("提交偏移量");
					((CanCommitOffsets) stream.inputDStream()).commitAsync(offsetRanges.get());
					//kafka偏移量入zookeeper
					//KafkaOffsetManager.writeOffset(zksever, zkpath, offsetRanges);
					KafkaOffsetManager.writeOffset(ConfigManager.getTopicTable(), offsetRanges);
					//logger.info("提交偏移量结束");
				} catch (Exception e) {
					logger.error("提交偏移量异常",e);
				}
				
				
			});
    	    
    	    return ssc;
    	}
    	
    	private static void createParams() {
    		//hbase配置
    		Configuration hbaseconfig = HBaseConfiguration.create();
    	    hbaseconfig.set("hbase.zookeeper.quorum",ConfigManager.getProperty("hbase.zookeeper.quorum"));
    	    hbaseconfig.set("hbase.zookeeper.property.clientPort",ConfigManager.getProperty("hbase.zookeeper.property.clientPort"));
    	    hbaseconfig.set("hbase.client.retries.number",ConfigManager.getProperty("hbase.client.retries.number"));
    	    hbaseconfig.set("zookeeper.recovery.retry",ConfigManager.getProperty("hbase.zookeeper.recovery.retry"));
    	    hbaseconfig.set("hbase.master",ConfigManager.getProperty("hbase.master"));
    	    ConfigManager.setHbaseconfig(hbaseconfig);
    	    
    	    
    	    /*Jedis jedis = new Jedis(ConfigManager.getProperty("redis.host"),ConfigManager.getIntProperty("redis.port"),ConfigManager.getIntProperty("redis.timeout"));
    	    if(!StringUtils.isEmpty(ConfigManager.getProperty("redis.password"))) {
		    	jedis.auth(ConfigManager.getProperty("redis.password"));
		    }
    	    ConfigManager.setJedis(jedis);*/
    	    
    	    //kafka配置
    	    kafkaConsumerParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigManager.getProperty("kafka.servers"));
    		kafkaConsumerParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    		kafkaConsumerParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    		kafkaConsumerParams.put(ConsumerConfig.GROUP_ID_CONFIG, ConfigManager.getProperty("kafka.group"));
    		kafkaConsumerParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, ConfigManager.getProperty("kafka.offset"));
    		kafkaConsumerParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    		
    		topicSet = new HashSet<String>(Arrays.asList(ConfigManager.getProperty("kafka.topics").split(",")));
    		
        	//获取kafka偏移量
    		//int countChildren = KafkaOffsetManager.countTopicPath(zksever,zkpath);
    		/*if(countChildren > 0) {
    			//fromOffsets = KafkaOffsetManager.readOffsets(zksever,zkpath,countChildren,topicSet.iterator().next());
    			
    		}*/
        	
        	fromOffsets = KafkaOffsetManager.readOffsets(ConfigManager.getTopicTable(), topicSet.iterator().next());
    	}
    	
    	
    	public static IncrementBean getBean(byte[] bytes) {
    		if(bytes == null) {
    			return null;
    		}
    		return JSON.parseObject(new String(bytes), IncrementBean.class);
    	}
    	
    	
}
