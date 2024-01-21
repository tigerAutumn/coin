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
import com.qkwl.service.coin.service.RechargeService;
import com.qkwl.service.coin.util.JobUtils;

@Component("autoAddressRecharge")
public class AutoAddressRecharge {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(AutoAddressRecharge.class);

    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private JobUtils jobUtils;
    
    private ConcurrentHashMap<Integer, Boolean> coinMap = new  ConcurrentHashMap<Integer, Boolean>();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        Thread thread = new Thread(new Work(1));
        thread.setName("AutoCoinRecharge");
        thread.start();
    }

    /**
     * Work
     * 根据用户的充值地址查询充值记录，入库和充值到账
     */
    class Work implements Runnable {
    	
    	private Integer i ;
        public Work(Integer i) {
			super();
			this.i = i;
		}
		public void run() {
			logger.info("===========>autoAdressRecharge启动");
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
                		//商链类
                		&& (
                				p.getCoinType().equals(SystemCoinSortEnum.BWT.getCode()) 
                				//tcp
                				|| p.getCoinType().equals(SystemCoinSortEnum.TCP.getCode()) 
                		)
                		&& !p.getUseNewWay()
                		
                		&& p.getIsRecharge()
                		
                		).collect(Collectors.toList());
                for (SystemCoinType coinType : collect) {
                    if (coinType == null) {
                        continue;
                    }
                    //加锁
                   /* Boolean bool = coinMap.get(coinType.getId());
                    if(bool != null && bool) {
                    	continue;
                    }else {
                    	Boolean put = coinMap.put(coinType.getId(), true);
                    	if(put != null && put) {
                    		continue;
                    	}
                    }*/
			
                    try {
                    	//logger.info("线程"+i+" "+coinType.getShortName());
                    	rechargeService.updateRechargeByCoinAddress(coinType);
                    } catch (Exception e) {
                        logger.error("coin recharge failed",e);
                    }
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                    	logger.error("sleep导致线程异常中断",e);
                    	Thread.currentThread().interrupt();
                    }
                    //解锁
                    /*coinMap.put(coinType.getId(), false);*/
                }
            }
        }
    }
}
