package com.hotcoin.sharding.conf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotCoinDataSource {

	private String name;
	private String url;
	private String username;
	private String password;
	private String driverClassName;
	private int initialSize;
	private int minIdle;
	private int maxActive;
	private int maxWait;
	private int timeBetweenEvictionRunsMillis;
	private int minEvictableIdleTimeMillis;
	private String validationQuery;
	private Boolean testWhileIdle;
	private Boolean testOnBorrow;
	private Boolean testOnReturn;
	private Boolean poolPreparedStatements;
	private int maxPoolPreparedStatementPerConnectionSize;
	private String filters;
	private String connectionProperties;

}
