package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.UserWalletBalanceLogDirectionEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogFieldEnum;
import com.qkwl.common.dto.Enum.UserWalletBalanceLogTypeEnum;
import com.qkwl.common.dto.wallet.UserCoinWalletSnapshot;
import com.qkwl.common.dto.wallet.UserWalletBalanceLog;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.UserCoinWalletMapper;
import com.qkwl.service.activity.dao.UserWalletBalanceLogMapper;
import com.qkwl.service.activity.utils.WalletUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("autoInnovationArea")
public class AutoInnovationArea {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoInnovationArea.class);
	
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	@Autowired
	private WalletUtils walletUtils;
	@Autowired
	private UserWalletBalanceLogMapper userWalletBalanceLogMapper;
	
	//创新区交易手续费分红，按照用户的平台币HTEC的持仓比例来分红，T+1日的12点发放分红收益
	@Scheduled(cron="0 0 12 * * ?")
	public void init() throws Exception {
		//快照开始
		logger.info("--------------分红HTEC币开始----------------");
		//获取redis中保存的HTEC分红总额，如果为0跳过
		String feeToal = redisHelper.get("innovation_area_htec_fee_total");
		if(StringUtils.isNotEmpty(feeToal)){
			BigDecimal fee = new BigDecimal(feeToal);
			//查询所有快照HTEC的用户
			String startTime = Utils.dateFormatYYYYMMDD(Utils.getCurTimestamp(0));
	        String endTime = Utils.dateFormatYYYYMMDD(Utils.getCurTimestamp(1));
			
	        BigDecimal totalAmount = userCoinWalletMapper.selectTotalAmount(10, startTime, endTime);
	        
	        List<UserCoinWalletSnapshot> list = userCoinWalletMapper.selectSnapshotByCoinId(10, startTime, endTime);
	        for(UserCoinWalletSnapshot userCoinWalletSnapshot : list) {
	        	//计算用户分红比例
	        	BigDecimal total = userCoinWalletSnapshot.getTotal();
	        	//得到用户分红比例
	        	BigDecimal rate = MathUtils.div(total, totalAmount);
	        	//得到用户分红
	        	BigDecimal dividend = MathUtils.mul(fee, rate);
	        	boolean result = walletUtils.change(userCoinWalletSnapshot.getUid(), 10, WalletUtils.total_add, dividend);
	        	if(result) {
	        		//记录钱包操作日志
        			UserWalletBalanceLog userWalletBalanceLog = new UserWalletBalanceLog();
        			userWalletBalanceLog.setCoinId(10);
        			userWalletBalanceLog.setChange(dividend);
        			userWalletBalanceLog.setCreatedatestamp(new Date().getTime());
        			userWalletBalanceLog.setCreatetime(new Date());
        			userWalletBalanceLog.setDirection(UserWalletBalanceLogDirectionEnum.in.getValue());
        			userWalletBalanceLog.setFieldId(UserWalletBalanceLogFieldEnum.total.getValue());
        			userWalletBalanceLog.setSrcId(userCoinWalletSnapshot.getId());
        			userWalletBalanceLog.setSrcType(UserWalletBalanceLogTypeEnum.Dividend_of_Innovation_Zone.getCode());
        			userWalletBalanceLog.setUid(userCoinWalletSnapshot.getUid());
        			
        			if (this.userWalletBalanceLogMapper.insert(userWalletBalanceLog)<= 0) {
        				logger.error("创新区分红----用户账户钱包新增日志出错uid = " + userCoinWalletSnapshot.getUid());
                    }
	        	}
	        }
		}
		logger.info("--------------分红HTEC币结束----------------");
	}
}
