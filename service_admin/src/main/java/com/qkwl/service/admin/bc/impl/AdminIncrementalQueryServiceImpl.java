package com.qkwl.service.admin.bc.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.capital.IncrementBean;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.entrust.FEntrustHistory;
import com.qkwl.common.dto.statistic.UserDayIncrement;
import com.qkwl.common.dto.statistic.UserMonthIncrement;
import com.qkwl.common.dto.statistic.UserYearIncrement;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.rpc.admin.IAdminIncrementalQueryService;
import com.qkwl.common.util.Constant;
import com.qkwl.common.util.PojoConvertUtil;
import com.qkwl.service.admin.bc.dao.FEntrustHistoryMapper;
import com.qkwl.service.admin.bc.dao.UserDayIncrementMapper;
import com.qkwl.service.admin.bc.dao.UserMonthIncrementMapper;
import com.qkwl.service.admin.bc.dao.UserYearIncrementMapper;
import com.qkwl.service.admin.bc.utils.HBaseUtils;

@Service("adminIncrementalQueryService")
public class AdminIncrementalQueryServiceImpl implements IAdminIncrementalQueryService {

	private static final Logger logger = LoggerFactory.getLogger(AdminIncrementalQueryServiceImpl.class);
	@Autowired
	private UserYearIncrementMapper userYearIncrementMapper;
	@Autowired
	private UserDayIncrementMapper userDayIncrementMapper;
	@Autowired
	private UserMonthIncrementMapper userMonthIncrementMapper;
	@Autowired
	private RedisHelper redisHelper;
	
	
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

	
	@Override
	public Map<Integer, IncrementBean> selectUserIncrement(Integer uid) {
		try {
			
			Map<Integer, IncrementBean> returnMap = new HashMap<>();
			Calendar calendar = Calendar.getInstance();
			int nowYear = calendar.get(Calendar.YEAR);
			int nowMonth =  calendar.get(Calendar.MONTH) + 1;
			
			byte[] totalBytes = Bytes.toBytes(TOTAL);
			
			Get get = new Get(Bytes.toBytes(uid.toString()));
			
			get.addFamily(totalBytes);
			
			Table yearTable = HBaseUtils.getConnection().getTable(TableName.valueOf(YEAR_TABLE_NAME));
			
			Table monthTable = HBaseUtils.getConnection().getTable(TableName.valueOf(MONTH_TABLE_NAME_PREFIX + nowYear));
			
			Table dayTable = HBaseUtils.getConnection().getTable(TableName.valueOf(DAY_TABLE_NAME_PREFIX + nowYear + "_" + nowMonth));
			//查询年表
			Result yearResult = yearTable.get(get);
			//查询月表
			Result monthResult = monthTable.get(get);
			//查询天表
			Result dayResult = dayTable.get(get);
			
			if(yearResult.size() > 0) {
				KeyValue[] raw = yearResult.raw();
				for (KeyValue keyValue : raw) {
					IncrementBean bean = getBean(keyValue.getValue());
					returnMap.put(bean.getCoinId(), bean);
				}
			}
			if(monthResult.size() > 0) {
				KeyValue[] raw = monthResult.raw();
				for (KeyValue keyValue : raw) {
					IncrementBean bean = getBean(keyValue.getValue());
					IncrementBean incrementBean = returnMap.get(bean.getCoinId());
					if(incrementBean != null) {
						bean.setTradeCost(MathUtils.add(incrementBean.getTradeCost(), bean.getTradeCost()));
						bean.setTradeFee(MathUtils.add(incrementBean.getTradeFee(), bean.getTradeFee()));
						bean.setTradeIncome(MathUtils.add(incrementBean.getTradeIncome(), bean.getTradeIncome()));
					}
					returnMap.put(bean.getCoinId(), bean);
				}
			}
			if(dayResult.size() > 0) {
				KeyValue[] raw = dayResult.raw();
				for (KeyValue keyValue : raw) {
					IncrementBean bean = getBean(keyValue.getValue());
					IncrementBean incrementBean = returnMap.get(bean.getCoinId());
					if(incrementBean != null) {
						bean.setTradeCost(MathUtils.add(incrementBean.getTradeCost(), bean.getTradeCost()));
						bean.setTradeFee(MathUtils.add(incrementBean.getTradeFee(), bean.getTradeFee()));
						bean.setTradeIncome(MathUtils.add(incrementBean.getTradeIncome(), bean.getTradeIncome()));
					}
					returnMap.put(bean.getCoinId(), bean);
				}
			}
			return returnMap;
		} catch (Exception e) {
			logger.error("查询用户增量数据异常",e);
		}
		return null;
	}
	
	public static IncrementBean getBean(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		return JSON.parseObject(new String(bytes), IncrementBean.class);
	}

	
}
