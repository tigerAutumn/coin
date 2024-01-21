package com.qkwl.service.coin.run;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.service.coin.service.CoinService;
import com.qkwl.service.coin.service.RechargeService;
import com.qkwl.service.coin.util.JobUtils;

@Component("autoUSDTCoinCollect")
public class AutoUSDTCoinCollect {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoUSDTCoinCollect.class);
    @Autowired
    private CoinService coinService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private JobUtils jobUtils;

    /**
     * 每十分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    //@PostConstruct
    public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("autoUSDTCoinCollect");
        thread.start();
    }

    /**
     * Work
     * 将usdt的所有地址的币都集中到一个主账户
     */
    class Work implements Runnable {
        public void run() {
            SystemCoinType coinType = jobUtils.getCoinType("USDT(OMNI)");
            SystemCoinType coinTypebtc = jobUtils.getCoinType("BTC");
            if (coinType == null || coinTypebtc == null) {
                return;
            }
            
            if(coinType.getUseNewWay()) {
            	return;
            }
            	
            if(coinType.getCoinType() != SystemCoinSortEnum.USDT.getCode()) {
            	return;
            }
            if (!coinType.getType().equals(SystemCoinTypeEnum.COIN.getCode())) {
            	return;
            }
            try {
            	rechargeService.updateUSDTCoinCollect(coinType,coinTypebtc);
            } catch (Exception e) {
                logger.error("updateUSDTCoinCollect failed",e);
            }
        }
    }
}
