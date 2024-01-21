package com.hotcoin.sharding.conf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 多数据源配置
 */
@Configuration
@ConfigurationProperties("sharding.db")
@Setter
@Getter
public class DruidDataSourceConfig {
	
	private List<HotCoinDataSource> datasource = new ArrayList<HotCoinDataSource>();

    public List<DataSource> dataSource(){
    	List<DataSource> dataSources = new ArrayList<>();
    	for(HotCoinDataSource ds : datasource) {

            DruidDataSource datasource = new DruidDataSource();
            datasource.setUrl(ds.getUrl());
            datasource.setUsername(ds.getUsername());
            datasource.setPassword(ds.getPassword());
            datasource.setDriverClassName(ds.getDriverClassName());
            datasource.setName(ds.getName());

            //configuration
            datasource.setInitialSize(ds.getInitialSize());
            datasource.setMinIdle(ds.getMinIdle());
            datasource.setMaxActive(ds.getMaxActive());
            datasource.setMaxWait(ds.getMaxWait());
            datasource.setTimeBetweenEvictionRunsMillis(ds.getTimeBetweenEvictionRunsMillis());
            datasource.setMinEvictableIdleTimeMillis(ds.getMinEvictableIdleTimeMillis());
            datasource.setValidationQuery(ds.getValidationQuery());
            datasource.setTestWhileIdle(ds.getTestWhileIdle());
            datasource.setTestOnBorrow(ds.getTestOnBorrow());
            datasource.setTestOnReturn(ds.getTestOnReturn());
            datasource.setPoolPreparedStatements(ds.getPoolPreparedStatements());
            datasource.setMaxPoolPreparedStatementPerConnectionSize(ds.getMaxPoolPreparedStatementPerConnectionSize());
            try {
                datasource.setFilters(ds.getFilters());
            } catch (SQLException e) {
                System.err.println("druid configuration initialization filter: "+ e);
            }
            datasource.setConnectionProperties(ds.getConnectionProperties());
            dataSources.add(datasource);
    	}
        return dataSources;
    }

}
