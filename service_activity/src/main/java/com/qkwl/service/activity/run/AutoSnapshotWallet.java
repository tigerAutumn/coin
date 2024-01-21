package com.qkwl.service.activity.run;

import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.util.Utils;
import com.qkwl.service.activity.dao.FEntrustHistoryMapper;
import com.qkwl.service.activity.dao.UserCoinWalletMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("autoSnapshotWallet")
public class AutoSnapshotWallet {

	/**
	 * 日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(AutoSnapshotWallet.class);
	
	@Autowired
    private RedisHelper redisHelper;
	@Autowired
	private UserCoinWalletMapper userCoinWalletMapper;
	@Autowired
    private FEntrustHistoryMapper entrustHistoryMapper;
	
    //	//HTEC快照
	@Scheduled(cron = "0 0 0 * * ?")
	public void init() {
		//快照开始
		
		//快照开始
		SystemCoinType systemCoinType = redisHelper.getCoinType(10);
		
		//分红所需最低持仓数
		String limitTotalStr = redisHelper.getSystemArgs("limitTotal");
		//分红比例
		String rateStr = redisHelper.getSystemArgs("rate");
		BigDecimal limitTotal = BigDecimal.ZERO;
		BigDecimal rate = BigDecimal.ZERO;
		if(StringUtils.isNotEmpty(limitTotalStr)) {
			limitTotal = new BigDecimal(limitTotalStr);
		}
		
		if(StringUtils.isNotEmpty(rateStr)) {
			rate = new BigDecimal(rateStr);
		}
		
		logger.info("--------------快照HTEC币开始----------------");
		//快照大于等于最低持仓HTEC的用户钱包
		if(systemCoinType.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())) {
			userCoinWalletMapper.snapshotWallet(systemCoinType.getId(),limitTotal);
		}
		logger.info("--------------快照HTEC币结束----------------");
		
		//计算所有创新区币的手续费总额=查询今天所有历史委单，计算总额
		// 当前时间
        String pastTime = Utils.dateFormatYYYYMMDD(Utils.getCurTimestamp(-1));
        String nowTime = Utils.dateFormatYYYYMMDD(Utils.getCurTimestamp(0));
		
//        String pastTime = "2019-01-16 10:35:00";
//        String nowTime = "2019-01-16 10:55:00";
        
		//1、查询所有创新区币
		List<SystemTradeType> allList = redisHelper.getAllTradeTypeList(0);
		BigDecimal feeTotal = BigDecimal.ZERO;
		for(SystemTradeType param: allList) {
			//如果是创新币
			if(param.getType().equals(SystemTradeTypeNewEnum.INNOVATION_AREA.getCode())) {
				logger.info("开始查询今天所有历史委单，计算总额 交易对id:"+param.getId()+" 交易对："+param.getBuyShortName()+"_"+param.getSellShortName());
				Map<String, Object> total = entrustHistoryMapper.selectLogTotal(param.getId(), pastTime, nowTime);
				if(total != null) {
					String feesBuyStr = total.get("feesBuy")==null?"0":total.get("feesBuy").toString();
					String feesSellStr =  total.get("feesSell")==null?"0":total.get("feesSell").toString();
					//买币收币 卖币收钱
					BigDecimal feesBuy = StringUtils.isNotEmpty(feesBuyStr)?new BigDecimal(feesBuyStr):BigDecimal.ZERO;
					BigDecimal feesSell = StringUtils.isNotEmpty(feesSellStr)?new BigDecimal(feesSellStr):BigDecimal.ZERO;
					
					logger.info("feesBuy:"+feesBuy);
					logger.info("feesSell:"+feesSell);
					BigDecimal lastPrice = BigDecimal.ZERO;
					if(param.getBuyCoinId().equals(9)) {
						lastPrice = new BigDecimal("1"); 
					}else if(param.getBuyCoinId().equals(1)) {
						lastPrice = redisHelper.getLastPrice(8);
					}else if(param.getBuyCoinId().equals(4)) {
						lastPrice = redisHelper.getLastPrice(11);
					}else if(param.getBuyCoinId().equals(52)) {
						lastPrice = redisHelper.getLastPrice(57);
					}
					
					logger.info("lastPrice:"+lastPrice);
					
					//当前卖币的人民币价格
					BigDecimal cnyPrice = MathUtils.mul(feesBuy, lastPrice);;
					
					logger.info("cnyPrice:"+cnyPrice);
					BigDecimal buyTotal = cnyPrice;
					logger.info("buyTotal:"+buyTotal);
					BigDecimal sellTotal = MathUtils.mul(feesSell, lastPrice);
					logger.info("sellTotal:"+sellTotal);
					
					//折算成GAVC
					//查询创新币——GAVC交易区这个币种的价格
					feeTotal = MathUtils.add(feeTotal, buyTotal);
					feeTotal = MathUtils.add(feeTotal, sellTotal);
					logger.info("结束查询今天所有历史委单，当前币种 fee总额:"+MathUtils.add(buyTotal, sellTotal)+" 手续费总额："+feeTotal);
				}
			}
		}
		BigDecimal fee = MathUtils.mul(feeTotal, rate);
		//2、折算成HTEC
		if(fee.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal htecLastPrice = redisHelper.getLastPrice(21);
			BigDecimal htecTotal = MathUtils.div(fee, htecLastPrice);
			
			//3、保存到redis
			redisHelper.set("innovation_area_htec_fee_total",htecTotal.toPlainString(), 60*60*13);
			logger.info("所有手续费折合成HTEC："+htecTotal);
		}
	}
}
