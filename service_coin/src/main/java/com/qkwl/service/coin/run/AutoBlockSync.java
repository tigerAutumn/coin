package com.qkwl.service.coin.run;

import static org.hamcrest.CoreMatchers.instanceOf;

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
import com.qkwl.service.coin.service.BlockService;
import com.qkwl.service.coin.service.RechargeService;
import com.qkwl.service.coin.util.JobUtils;

@Component("autoBlockSync")
public class AutoBlockSync {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoBlockSync.class);

    @Autowired
    private BlockService blockService;
    @Autowired
    private JobUtils jobUtils;
    

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work(1));
        thread.setName("autoBlockSync");
        thread.start();
    }

    /**
     * Work
     * 同步区块，查询充值交易
     */
    class Work implements Runnable {
    	
    	private Integer i ;
        public Work(Integer i) {
			super();
			this.i = i;
		}
		public void run() {
			logger.info("===========>autoBlockSync启动");
            while (true) {
        		List<SystemCoinType> coinTypes = jobUtils.getCoinTypeList();
                if (coinTypes == null) {
                    continue;
                }
                List<SystemCoinType> collect = coinTypes.stream().filter(p -> 
                		//虚拟币类
                		p.getType().equals(SystemCoinTypeEnum.COIN.getCode()) 
                		//状态正常
                		&& p.getStatus().equals(SystemCoinStatusEnum.NORMAL.getCode())
                		//CHAIN33类
                		&& p.getCoinType().equals(SystemCoinSortEnum.CHAIN33.getCode()) 
                		
                		&& p.getIsRecharge()
                		
                		&& !p.getUseNewWay()
                		
                		).collect(Collectors.toList());
                if(collect == null || collect.size() == 0) {
                	logger.info("币种为空");
                }
                try {
                	for (SystemCoinType systemCoinType : collect) {
                		blockService.syncBlock(systemCoinType);
					}
				} catch (Exception e) {
					logger.error("autoBlockSync执行异常",e);
				}
            	try {
            		Thread.sleep(30000);
				} catch (InterruptedException e) {
					logger.error("autoBlockSync执行异常",e);
					Thread.currentThread().interrupt();
				}
            	if(i < 0) {
            		break;
            	}
            }
        }
    }
}
