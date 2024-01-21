package com.qkwl.service.push.run;

import static com.qkwl.common.redis.RedisConstant.KLINE_KEY;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("autoMarket")
public class AutoMarket {

    private static final Logger logger = LoggerFactory.getLogger(AutoMarket.class);
    @Autowired
    private RedisHelper redisHelper;

    /**
     * K线时间类型
     */
   /* public static final int[] TIME_KIND = {1, 3, 5, 15, 30, // 分钟
            60, 2 * 60, 4 * 60, 6 * 60, 12 * 60, // 小时
            24 * 60, 7 * 24 * 60,30 * 24 * 60 // 天
    };*/
    public static final int[] TIME_KIND = {
        1, 5, 15, 30, // 分钟
        60, // 小时
        24 * 60, 7 * 24 * 60 ,30 * 24 * 60 // 天
    };
    /**
     * K线最大存储数量
     */
    public static final int MAX_LEN = 300;
    /**
     * 实时数据
     */
    public final static ConcurrentHashMap<Integer, TickerData> TickerJson = new ConcurrentHashMap<Integer, TickerData>();

    /**
     * 买卖深度
     */
    public final static ConcurrentHashMap<Integer, JSONArray> BuyDepthJson = new ConcurrentHashMap<Integer, JSONArray>();
    public final static ConcurrentHashMap<Integer, JSONArray> SellDepthJson = new ConcurrentHashMap<Integer, JSONArray>();

    /**
     * K线数据
     */
    public final static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, JSONArray>> KlineJson = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, JSONArray>>();
    public final static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, JSONArray>> lastKlineJson = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, JSONArray>>();
    public final static ConcurrentHashMap<Integer, JSONArray> indexKlineJson = new ConcurrentHashMap<Integer, JSONArray>();

    /**
     * 已成交数据
     */
    public final static ConcurrentHashMap<Integer, JSONArray> SuccessJson = new ConcurrentHashMap<Integer, JSONArray>();

    public static boolean klineInitFlag = false;

    private static final String AUTOMARKET_CACHE = "AutoMarket";
    
    /**
     * 初始化线程
     */
   /* @PostConstruct
    public void init() throws InterruptedException{
    	 initKline();
		 BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(6);
	     //定义 线程执行过程中出现异常时的 异常处理器
	     MyExceptionHandler exceptionHandler = new MyExceptionHandler();
	     ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("test-%d")
	             .setUncaughtExceptionHandler(exceptionHandler).build();
	     CustomThreadPoolExecutor threadPoolExecutor = new CustomThreadPoolExecutor(1, 2, 1, TimeUnit.SECONDS, taskQueue,threadFactory);
	     threadPoolExecutor.execute(()->{
	            //提交的是一个while(true)任务,正常运行时这类任务不会结束
	            while (true) {
	                try {
	                	List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(0);
	                    if (tradeTypes != null) {
	                      for (SystemTradeType tradeType : tradeTypes) {
	                            int tradeId = tradeType.getId();
	                            // 实时行情
	                            initTicker(tradeId);
	                            // IndexKline
	                            initIndexKline(tradeId);
	                            // 平台撮合执行
	                            if (tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
	                                // 深度
	                                initDepth(tradeId);
	                                // 最新成交
	                                initSuccess(tradeId);
	                                // LastKline
	                                initLastKline(tradeId);
	                            }
	                        }
	                    }
	                    //执行耗时20ms
	                    Thread.sleep(20L);
	                } catch (Exception e) {
	                	logger.error("初始化线程出错",e);
	                	throw new RuntimeException("running encounter exception");
	                }
	            }
	        });
    }*/
    
    /*
    private class CustomThreadPoolExecutor extends ThreadPoolExecutor {

        public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            //若线程执行某任务失败了，重新提交该任务
            if (t != null) {
                Runnable task =  r;
                logger.info("restart task...");
                execute(task);
            }
        }
    }
    
    private static class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
        	logger.info(String.format("thread name %s, msg %s", t.getName(), e.getMessage()));
        }
    }
    */
    
    /**
     * 初始化线程
     */
    @PostConstruct
    public void init() {
        initKline();
        klineInitFlag= true;
    }

   /* *//**
     * 初始化线程
     *//*
    @Scheduled(fixedRate = 1*1000)
    public void job() {
        if(!klineInitFlag){
            logger.info("kline init not finish, please waiting ...");
            return ;
        }
       List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(0);
         if (tradeTypes != null) {
           for (SystemTradeType tradeType : tradeTypes) {
                 int tradeId = tradeType.getId();
                 // 实时行情
                 initTicker(tradeId);
                 // IndexKline
                 initIndexKline(tradeId);
                 // 平台撮合执行
                 if (tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                     // 深度
                     initDepth(tradeId);
                     // 最新成交
                     initSuccess(tradeId);
                     // LastKline
                     initLastKline(tradeId);
                 }
             }
         }
    }*/

    /**
     * 初始化k线
     */
    private void initKline() {
        List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(0);
        if (tradeTypes == null) {
            System.out.println("-->autoMarket initKline null");
            return;
        }
        for (SystemTradeType tradeType : tradeTypes) {
            int tradeId = tradeType.getId();
            for (int i : TIME_KIND) {
                // Kline
                String klineStr = redisHelper.getRedisData(KLINE_KEY + tradeId + "_" + i);
                ConcurrentHashMap<Integer, JSONArray> klineJsonMap = KlineJson.get(tradeId);

                if (klineJsonMap == null) {
                    klineJsonMap = new ConcurrentHashMap<Integer, JSONArray>();
                }
                if (klineStr == null || klineStr.isEmpty()) {
                    klineJsonMap.put(i, new JSONArray());
                } else {
                    klineJsonMap.put(i, JSON.parseArray(klineStr));
                }
//                if(400006 ==tradeId){
//                    logger.info("KlineJson tradeId:{},KlineJson:{}",tradeId, JSON.toJSONString(klineJsonMap));
//                }
                KlineJson.put(tradeId, klineJsonMap);
            }
        }
    }




    /**
     * 获取实时行情
     *
     * @param tradeId
     * @return
     */
    public TickerData getTickerData(Integer tradeId) {
        TickerData tickerData = TickerJson.get(tradeId);
        //TODO 获取买卖价格最新值
        if (null == tickerData) {
            String tickerStr = redisHelper.getRedisData(RedisConstant.TICKERE_KEY + tradeId);

            tickerData = new TickerData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            if (null != tickerStr && !tickerStr.isEmpty()) {
                tickerData = JSON.parseObject(tickerStr, TickerData.class);
            }
            String last_price_buy_sell = redisHelper.getRedisData(RedisConstant.LAST_PRICE_BUY_SELL+tradeId);
            if (null != last_price_buy_sell && !last_price_buy_sell.isEmpty()) {
                Map map = JSON.parseObject(last_price_buy_sell, Map.class);
                String buy = String.valueOf(null==map.get("b")?0:map.get("b"));
                String sell = String.valueOf(null==map.get("s")?0:map.get("s"));
                tickerData.setBuy(new BigDecimal(buy));
                tickerData.setSell(new BigDecimal(sell));
            }

            AutoMarket.TickerJson.put(tradeId, tickerData);
           /* if(tradeId.equals("900071")){
                tickerData.setKai(new BigDecimal("0.1519"));
                AutoMarket.TickerJson.put(tradeId, tickerData);
            }else{
                AutoMarket.TickerJson.put(tradeId, tickerData);
            }*/

            return tickerData;
        } else {
            return tickerData;
        }
    }

    /**
     * 获取买深度
     *
     * @param tradeId
     * @return
     */
    public JSONArray getBuyDepthJson(Integer tradeId) {
        return BuyDepthJson.get(tradeId);
    }

    /**
     * 获取买深度
     *
     * @param tradeId
     * @param num
     * @return
     */
    public JSONArray getBuyDepthJson(Integer tradeId, int num) {
        JSONArray jsonArray = BuyDepthJson.get(tradeId);
        JSONArray itemArray = new JSONArray();
        if (null == jsonArray) {
            return itemArray;
        }
        int forCount = jsonArray.size() <= num ? jsonArray.size() : num;
        for (int i = 0; i < forCount; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", i + 1);
            jsonObject.put("price", JSONArray.parseArray(jsonArray.get(i).toString()).get(0));
            jsonObject.put("amount", JSONArray.parseArray(jsonArray.get(i).toString()).get(1));
            itemArray.add(jsonObject);
        }
        return itemArray;
    }

    /**
     * 获取卖深度
     *
     * @param tradeId
     * @return
     */
    public JSONArray getSellDepthJson(Integer tradeId) {
        return SellDepthJson.get(tradeId);
    }

    /**
     * 获取卖深度
     *
     * @param tradeId
     * @param num
     * @return
     */
    public JSONArray getSellDepthJson(Integer tradeId, int num) {
        JSONArray jsonArray = SellDepthJson.get(tradeId);
        JSONArray itemArray = new JSONArray();
        if (null == jsonArray) {
            return itemArray;
        }
        int forCount = jsonArray.size() <= num ? jsonArray.size() : num;
        for (int i = 0; i < forCount; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", i + 1);
            jsonObject.put("price", JSONArray.parseArray(jsonArray.get(i).toString()).get(0));
            jsonObject.put("amount", JSONArray.parseArray(jsonArray.get(i).toString()).get(1));
            itemArray.add(jsonObject);
        }
        return itemArray;
    }

    /**
     * 获取最新成交
     *
     * @param tradeId
     * @return
     */
    public JSONArray getSuccessJson(Integer tradeId) {
        return SuccessJson.get(tradeId);
    }

    /**
     * 获取最新成交
     *
     * @param tradeId
     * @param num
     * @return
     */
    public JSONArray getSuccessJson(Integer tradeId, int num) {
        JSONArray jsonArray = SuccessJson.get(tradeId);
        JSONArray itemArray = new JSONArray();
        if (null == jsonArray) {
            return itemArray;
        }
        int forCount = jsonArray.size() <= num ? jsonArray.size() : num;
        for (int i = 0; i < forCount; i++) {
            JSONObject jsonObject = new JSONObject();
            JSONArray listArray = JSONArray.parseArray(jsonArray.get(i).toString());
            jsonObject.put("price", listArray.get(0));
            jsonObject.put("amount", listArray.get(1));
            jsonObject.put("id", i + 1);
            jsonObject.put("time", listArray.get(2));
            int type = (int) listArray.get(3);
            jsonObject.put("en_type", type == 0 ? "bid" : "ask");
            jsonObject.put("type", type == 0 ? "买入" : "卖出");
            itemArray.add(jsonObject);
        }
        return itemArray;
    }

    /**
     * 获取Kline
     *
     * @param tradeId
     * @param stepid
     * @return
     */
    public JSONArray getKlineJson(Integer tradeId, Integer stepid) {
        Map<Integer, JSONArray> tempMap = KlineJson.get(tradeId);
        if (null !=tempMap) {
            return tempMap.get(stepid);
        }
        return new JSONArray();
        /*
        DateTime dt = new DateTime();
        logger.error("tradeid:{},stepid:{},dt:{}",tradeId,stepid,new DateTime());
        String klineStr = redisHelper.getRedisData(KLINE_KEY + tradeId + "_" + stepid);
        if (klineStr == null || klineStr.isEmpty()) {
            return new JSONArray();
        } else {
            return JSON.parseArray(klineStr);
        }
        */
        /*ConcurrentHashMap<Integer, JSONArray> tempMap  = AutoMarket.KlineJson.get(tradeId);
        if (null !=tempMap) {
            JSONArray stepArray = tempMap.get(stepid);
            if(null == stepArray){
               return new JSONArray();
            }else {
                logger.error("tradeid:{},stepid:{},stepArray:{}",tradeId,stepid,JSON.toJSON(stepArray));
                logger.error("tradeid:{},stepid:{},lostdt:{}",tradeId,stepid,new DateTime().getMillis()-dt.getMillis());
                return stepArray;
            }
        }*/
       // return new JSONArray();
    }

    /**
     * 获取LastKline
     *
     * @param tradeId
     * @param stepid
     * @return
     */
    public JSONArray getLastKlineJson(Integer tradeId, Integer stepid) {
        Map<Integer, JSONArray> tempMap = lastKlineJson.get(tradeId);
        if (null != tempMap) {
            return tempMap.get(stepid);
        }
        return new JSONArray();
    }

    /**
     * 获取IndexKline 三天的行情
     *
     * @param tradeId
     * @return
     */
    public JSONArray getIndexKlineJson(Integer tradeId) {
        JSONArray jsonArray =  indexKlineJson.get(tradeId);
        if(null == jsonArray){
            return new JSONArray();
        }
        return jsonArray;
    }

    public String getCNYValue() {
        String cny_value = redisHelper.getRedisData("CNY_VALUE");
        if (TextUtils.isEmpty(cny_value)) {
            return null;
        }
        return cny_value;
    }

    public void setCNYValue(String cny) {
        if (TextUtils.isEmpty(cny)) {
            return;
        }
        redisHelper.setRedisData("CNY_VALUE", cny, 60 * 60);
    }

}
