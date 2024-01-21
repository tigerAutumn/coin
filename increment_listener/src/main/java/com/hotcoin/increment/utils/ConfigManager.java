package com.hotcoin.increment.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.hotcoin.increment.constant.IncrementConstant;

import redis.clients.jedis.Jedis;

public class ConfigManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	
	private static Connection conn = null;
	
	private static Table topicTable = null;
	
	
	private static Map<String,String> allMap=new HashMap<String,String>();
	
	static {
		Yaml yaml = new Yaml();
		InputStream inputStream = ConfigManager.class.getResourceAsStream("/application.yml");
		Iterator<Object> result = yaml.loadAll(inputStream).iterator();
		while(result.hasNext()){
			Map map=(Map)result.next();
			iteratorYml( map,null);
		}
	}

	
	public static void iteratorYml(Map map,String key){
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry entry = (Map.Entry) iterator.next(); 
			Object key2 = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof LinkedHashMap){
				if(key==null){
					iteratorYml((Map)value,key2.toString());
				}else{
					iteratorYml((Map)value,key+"."+key2.toString());
				}
			}else{
				if(key==null){
					if(value != null) {
						allMap.put(key2.toString(), value.toString());
					}
				}
				if(key!=null){
					if(value != null) {
						allMap.put(key+"."+key2.toString(), value.toString());
					}
				}
			}
		}
	
	}
	
	private static Configuration hbaseconfig;
	

	public static Configuration getHbaseconfig() {
		return hbaseconfig;
	}

	//TableName tname = TableName.valueOf(tableName);
	
	public static void setHbaseconfig(Configuration hb) {
		hbaseconfig = hb;
		try {
			conn = ConnectionFactory.createConnection(hb);
			topicTable = conn.getTable(TableName.valueOf(IncrementConstant.INTREMENT_TOPIC_TABLE));
		} catch (IOException e) {
			logger.error("get hbase conn errer");
		}
	}
	
	private ConfigManager() {

	}



	public static Connection getConn() {
		return conn;
	}

	public static Table getTopicTable() {
		return topicTable;
	}

	public static String getProperty(String key) {
		if(!StringUtils.isEmpty(key)) {
			return allMap.get(key);
		}
		return null;
	}

	/**
	 * @param key
	 *            属性key值
	 * @return
	 */
	public static Integer getIntProperty(String key) {
		if(!StringUtils.isEmpty(key)) {
			return Integer.valueOf(allMap.get(key));
		}
		return null;
	}

}
