package com.hotcoin.sharding.algorithm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.hotcoin.sharding.conf.ShardingPropertyConfig;
import com.hotcoin.sharding.exception.ShardingException;
import com.qkwl.common.util.ConcurrentDateUtil;

import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.core.api.algorithm.sharding.standard.RangeShardingAlgorithm;

/**
 * f_entrust_history表分库逻辑
 * @author peiqin
 *
 */
public class FEntrustHistoryShardingDatabaseAlgorithm implements PreciseShardingAlgorithm<Date>, RangeShardingAlgorithm<Date> {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        
        log.info("PreciseShardingAlgorithm do resolving table: {}, shardingValue:{}, availableTargetNames: {}, ShardingPropertyConfig:{}", 
        		shardingValue.getLogicTableName(), shardingValue.getValue(), availableTargetNames, JSON.toJSONString(ShardingPropertyConfig.config));

        String databaseName = "";
        
        // 分库操作
        
        if ("f_entrust_history".equalsIgnoreCase(shardingValue.getLogicTableName())) {
        	Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - Integer.valueOf(ShardingPropertyConfig.config.get("f-entrust-history")));
			ca.set(Calendar.DAY_OF_MONTH, 1);
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			
			if(shardingValue.getValue().before(ca.getTime())) {
				if(availableTargetNames.size() > 1) {
					databaseName = availableTargetNames.toArray(new String[availableTargetNames.size()])[1];
				}else {
					databaseName = availableTargetNames.toArray(new String[availableTargetNames.size()])[0];
				}
			}else {
				databaseName = availableTargetNames.toArray(new String[availableTargetNames.size()])[0];
			}
        }else {
        	throw new ShardingException("暂不支持该表分片逻辑");
        }
        

        log.info("PreciseShardingAlgorithm resolved table: {}, shardingValue:{}, availableTargetNames: {}, ShardingPropertyConfig:{}, tagetDataBase:{}", 
        		shardingValue.getLogicTableName(), shardingValue.getValue(), availableTargetNames, JSON.toJSONString(ShardingPropertyConfig.config), databaseName);

        Preconditions.checkArgument(StringUtils.isNotBlank(databaseName), "无法找到对应数据库");
        
        return databaseName;
	}

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames,
			RangeShardingValue<Date> shardingValue) {

        log.info("RangeShardingAlgorithm do resolving table: {}, lowerEndpoint:{}, upperEndpoint:{}, availableTargetNames: {}, ShardingPropertyConfig:{}", 
        		shardingValue.getLogicTableName(), shardingValue.getValueRange().lowerEndpoint(), shardingValue.getValueRange().upperEndpoint(), availableTargetNames, JSON.toJSONString(ShardingPropertyConfig.config));

        Collection<String> collect = new ArrayList<>();
        Range<Date> valueRange = shardingValue.getValueRange();

        // 分库操作
		if ("f_entrust_history".equalsIgnoreCase(shardingValue.getLogicTableName())) {
			
        	Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - Integer.valueOf(ShardingPropertyConfig.config.get("f-entrust-history")));
			ca.set(Calendar.DAY_OF_MONTH, 1);
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			
			if(ca.getTime().after(valueRange.lowerEndpoint())) {
				if(availableTargetNames.size() > 1) {
					collect.add(availableTargetNames.toArray(new String[availableTargetNames.size()])[1]);
				}else {
					collect.add(availableTargetNames.toArray(new String[availableTargetNames.size()])[0]);
				}
			}
			
			if(ca.getTime().before(valueRange.upperEndpoint())) {
				if(!collect.contains(availableTargetNames.toArray(new String[availableTargetNames.size()])[0])) {
					collect.add(availableTargetNames.toArray(new String[availableTargetNames.size()])[0]);
				}
			}
		}
        
		log.info("RangeShardingAlgorithm resolved table: {}, lowerEndpoint:{}, upperEndpoint:{}, availableTargetNames: {},"
				+ " ShardingPropertyConfig:{}, collect: {}", 
				shardingValue.getLogicTableName(), shardingValue.getValueRange().lowerEndpoint(), 
				shardingValue.getValueRange().upperEndpoint(), availableTargetNames, 
				JSON.toJSONString(ShardingPropertyConfig.config), JSON.toJSONString(collect));

		Preconditions.checkArgument(collect != null && collect.size() > 0, "无法找到对应数据库");
		
        return collect;
	}

}
