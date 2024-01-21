package com.qkwl.service.admin.bc.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Table;

import com.qkwl.service.admin.config.HBaseProperties;

public class HBaseUtils {
	
	public static Configuration conf = null;
	private static Connection conn;
	
	public static Configuration getHbaseConf(){
		return conf;
	}
	
	
	public HBaseUtils(HBaseProperties config) {
		conf = HBaseConfiguration.create();
		config.getConfig().forEach((key,value) -> {
			conf.set(key, value);
		});
		try {
			conn = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HBaseAdmin getHBaseAdmin() throws IOException{
		HBaseAdmin hbaseAdmin = null;
		try {
			hbaseAdmin = (HBaseAdmin)(conn.getAdmin());
		} catch (MasterNotRunningException e) {	
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		}
		return hbaseAdmin;
	}
	
	
	public static synchronized Table getHtable(String tableName) throws IOException{
		if(conn!=null){
			return conn.getTable(TableName.valueOf(tableName));
		}else{
			try {
				conn = ConnectionFactory.createConnection(conf);
				return conn.getTable(TableName.valueOf(tableName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;//正常情况下此处运行不到
	}	
	
	public static Connection getConnection(){
		return conn;
	}
	
	public static synchronized void closeConnection(){
		if(conn!=null){
			 try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
