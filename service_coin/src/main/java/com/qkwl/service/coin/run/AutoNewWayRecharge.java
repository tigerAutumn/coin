package com.qkwl.service.coin.run;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.service.coin.service.RechargePushService;
import com.qkwl.service.coin.service.RechargeService;
import com.qkwl.service.coin.util.JobUtils;

@Component("autoNewWayRecharge")
public class AutoNewWayRecharge {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoNewWayRecharge.class);

    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private JobUtils jobUtils;
    @Autowired
    private RechargePushService rechargePushService;
    
    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work(1));
        thread.setName("autoNewWayRecharge");
        thread.start();
    }

    /**
     * Work
     * 与钱包机对接方式的区块确认和到账
     */
    class Work implements Runnable {
    	
    	private Integer i ;
        public Work(Integer i) {
			super();
			this.i = i;
		}
		public void run() {
			logger.info("===========>autoNewWayRecharge启动");
            while (true) {
                List<SystemCoinType> coinTypes = jobUtils.getCoinTypeList();
                if (coinTypes == null) {
                    continue;
                }
                List<SystemCoinType> collect = coinTypes.parallelStream().filter(p -> 
                		//虚拟币类
                		p.getType().equals(SystemCoinTypeEnum.COIN.getCode()) 
                		//状态正常
                		&& p.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())
                		//使用新方式同步
                		&& p.getUseNewWay()
                		
                		&& p.getIsRecharge()
                		
                		).collect(Collectors.toList());
                for (SystemCoinType coinType : collect) {
                	try {
                		boolean updateConfirm = rechargePushService.updateConfirm(coinType);
                    	//如果有更新确认数，就去确认到账
                    	if(updateConfirm) {
                    		rechargePushService.rechargeArrivals(coinType);
                    	}
					} catch (Exception e) {
						logger.error(coinType.getShortName() + "刷新确认数异常",e);
					}
                	
                }
            }
        }
    }
}
