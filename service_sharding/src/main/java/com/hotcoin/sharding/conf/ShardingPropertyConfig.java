package com.hotcoin.sharding.conf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sharding")
public class ShardingPropertyConfig {

	public static Map<String, String> config = new HashMap<>();

	public Map<String, String> getConfig() {
		return config;
	}

	public void setConfig(Map<String, String> config) {
		ShardingPropertyConfig.config = config;
	}
	
	
}
