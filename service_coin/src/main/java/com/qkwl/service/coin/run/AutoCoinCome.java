package com.qkwl.service.coin.run;

import com.qkwl.common.dto.Enum.SystemCoinSortEnum;
import com.qkwl.common.dto.Enum.SystemCoinStatusEnum;
import com.qkwl.common.dto.Enum.SystemCoinTypeEnum;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.service.coin.service.CoinService;
import com.qkwl.service.coin.util.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component("autoCoinCome")
public class AutoCoinCome {

    private static final Logger logger = LoggerFactory.getLogger(AutoCoinCome.class);
    @Autowired
    private CoinService coinService;
    @Autowired
    private JobUtils jobUtils;
    
    private ConcurrentHashMap<Integer, Boolean> coinMap = new  ConcurrentHashMap<Integer, Boolean>();

    /**
     * 初始化 ,刷新确认数及充值到账
     */
    @PostConstruct
    public void init() {
    	ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
        	Thread thread = new Thread(new Work(i));
        	thread.setName("AutoCoinCome"+i);
        	logger.info("启动线程"+i);
        	executorService.execute(thread);
		}
    }

    /**
     * Work
     */
    class Work implements Runnable {
    	private Integer i ;
        public Work(Integer i) {
			super();
			this.i = i;
		}
        public void run() {
        	logger.info("===========>AutoCoinCome启动");
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
        		
        		&& !p.getUseNewWay()
        		
        		//非bwt类
        		&& !p.getCoinType().equals(SystemCoinSortEnum.BWT.getCode())
        		&& !p.getCoinType().equals(SystemCoinSortEnum.HTDF.getCode())
        		&& !p.getCoinType().equals(SystemCoinSortEnum.TCP.getCode())
        		&& !p.getCoinType().equals(SystemCoinSortEnum.CET.getCode())
        		&& !p.getCoinType().equals(SystemCoinSortEnum.SERO.getCode())
        		&& !p.getCoinType().equals(SystemCoinSortEnum.TRX.getCode())
        		
                ).collect(Collectors.toList());
                for (SystemCoinType coinType : collect) {
                	if (coinType == null) {
                        continue;
                    }
                    //加锁
                    Boolean bool = coinMap.get(coinType.getId());
                    if(bool != null && bool) {
                    	continue;
                    }else {
                    	Boolean put = coinMap.put(coinType.getId(), true);
                    	if(put != null && put) {
                    		continue;
                    	}
                    }
                    
                    try {
                        coinService.updateCoinCome(coinType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("coincome failed");
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    	logger.error("sleep导致线程异常中断",e);
                    	Thread.currentThread().interrupt();
                    }
                  //解锁
                    coinMap.put(coinType.getId(), false);
                }
            }
        }
    }
}
