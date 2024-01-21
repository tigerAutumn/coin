package com.qkwl.service.coin.run;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.service.coin.service.CoinService;
import com.qkwl.service.coin.service.RechargeService;
import com.qkwl.service.coin.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;


@Component("autoCoinCollect")
public class AutoCoinCollect {


    private static final Logger logger = LoggerFactory.getLogger(AutoCoinCollect.class);
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private JobUtils jobUtils;

    /**
     * 每五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    //@PostConstruct
    public void init() {
        Thread thread = new Thread(new Work());
        thread.setName("AutoCoinCollect");
        thread.start();
    }

    /**
     * Work
     * 将所有地址的币都集中到提现账户
     */
    class Work implements Runnable {
        public void run() {
        	List<SystemCoinType> coinTypeList = jobUtils.getCoinTypeList();
			List<SystemCoinType> collect = coinTypeList.stream().filter(p -> 
			    		//虚拟币类
			    		p.getType().equals(SystemCoinTypeEnum.COIN.getCode()) 
			    		//状态正常
			    		&& p.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())
			    		
			    		&& !p.getUseNewWay()
			    		
			    		//非以太类
			    		&& (
			    				p.getCoinType().equals(SystemCoinSortEnum.BWT.getCode())
			    			|| 	p.getCoinType().equals(SystemCoinSortEnum.NEO.getCode())
			    			|| 	p.getCoinType().equals(SystemCoinSortEnum.MULTI.getCode())
			    			|| 	p.getCoinType().equals(SystemCoinSortEnum.TCP.getCode())
			    			)
			    		).collect(Collectors.toList());
		    for (SystemCoinType coinType : collect) {
		        if (coinType == null) {
		            continue;
		        }
		        if(coinType.getUseNewWay()) {
	            	return;
	            }
		        try {
		        	logger.info("开始汇集"+coinType.getShortName());
		        	rechargeService.updateCollect(coinType);
		        } catch (Exception e) {
		            logger.error("coin collect failed",e);
		        }
		    }
        }
    }
}
