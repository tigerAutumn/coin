package com.hotcoin.increment.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager2 implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ConfigManager2.class);
	private static ConfigManager2 configManger = new ConfigManager2();


	private volatile static Map<String, Properties> proMap = new ConcurrentHashMap<String, Properties>();
	
	private static Properties properties;
	
	
	private static Configuration hbaseconfig;
	

	public static Configuration getHbaseconfig() {
		return hbaseconfig;
	}

	public static void setHbaseconfig(Configuration hb) {
		hbaseconfig = hb;
	}

	private ConfigManager2() {

	}

	public static ConfigManager2 instance(String proFileName) {
		if(properties == null) {
			loadProps(proFileName);
		}
		return configManger;
	}


	public String getProperty(String key) {
		if(properties != null) {
			try {
				return properties.getProperty(key);
			} catch (Exception e) {
				logger.error("getIntProperty error",e);
				
			}
		}
		return null;
	}

	/**
	 * @param key
	 *            属性key值
	 * @return
	 */
	public Integer getIntProperty(String key) {
		if(properties != null) {
			try {
				String property = properties.getProperty(key);
				return Integer.valueOf(property);
			} catch (Exception e) {
				logger.error("getIntProperty error",e);
			}
		}
		return null;
	}

	private static Properties loadProps(String proFileName) {
		logger.debug("Getting Config");
		InputStream in = null;
		try {
			properties = new Properties();
			if (proFileName != null && proFileName.startsWith("http:")) {
				URL url = new URL(proFileName);
				in = url.openStream();
			} else {
				String fileName = "src/main/resources/" + proFileName;
				in = new FileInputStream(fileName);
				properties.load(in);
			}
			properties.load(in);
			logger.info("Successfully  proFileName=" + proFileName + " load Properties:" + properties);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error reading " + proFileName + " in loadProps() " + e.getMessage());
			logger.error("Ensure the " + proFileName + " file is readable and in your classpath.");
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return properties;
	}
}
