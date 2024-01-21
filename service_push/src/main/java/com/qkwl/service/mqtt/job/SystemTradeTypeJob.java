package com.qkwl.service.mqtt.job;


import static com.qkwl.common.redis.RedisConstant.KLINE_KEY;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.mq.MQTTHelper;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.MemCache;
import com.qkwl.common.redis.RedisConstant;
//import com.qkwl.service.event.MqttSendStringEvent;
//import com.qkwl.service.event.eventBus.MqttSendStringEventBus;
import com.qkwl.service.mqtt.config.MqttProperties;
import com.qkwl.service.push.run.AutoMarket;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SystemTradeTypeJob {

    private static final Logger logger = LoggerFactory.getLogger(SystemTradeTypeJob.class);

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    protected MemCache memCache;

//    @Autowired
//    private MQTTHelper mqttHelper;


    @Autowired
    private MqttProperties mqttProperties;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public static ConcurrentHashMap<String, String> coinMap = new ConcurrentHashMap<>();

    public final static ConcurrentHashMap<Integer,SystemTradeType> GAVC_Map = new ConcurrentHashMap<Integer,SystemTradeType>();
    public final static ConcurrentHashMap<Integer,SystemTradeType> BTC_MAP = new ConcurrentHashMap<Integer,SystemTradeType>();
    public final static ConcurrentHashMap<Integer,SystemTradeType> ETH_MAP = new ConcurrentHashMap<Integer,SystemTradeType>();
    public final static ConcurrentHashMap<Integer,SystemTradeType> USDT_MAP = new ConcurrentHashMap<Integer,SystemTradeType>();
    public final static ConcurrentHashMap<Integer,SystemTradeType> INNOVATE_MAP = new ConcurrentHashMap<Integer,SystemTradeType>();


    public static final int AGENTID = 0;
    public final static String TRADE_LIST_KEY = RedisConstant.TRADE_LIST_KEY + "_" + AGENTID;
    /**
     * redis数据更新本地缓存时间点
     */
    public final static ConcurrentHashMap<String, Long> RedisUpdateTimeMap = new ConcurrentHashMap<String, Long>();
    private static final String LAST_ACTIVE_DATE_TIME="lastActiveDateTime";

    /**
     * CNYTradeTypeMap  tradeId  cny_tradeid
     * TradeShortNameMap 本地缓存优化cny的查询
     * SystemTradeTypeMap 本地缓存避免查询多次查询redis
     *
     */
    public final static ConcurrentHashMap<Integer, Integer> CNYTradeTypeMap = new ConcurrentHashMap<Integer,Integer>();
    public final static ConcurrentHashMap<String, Integer> TradeShortNameMap = new ConcurrentHashMap<String,Integer>();

    // 交易信息
    public final static ConcurrentHashMap<Integer, SystemTradeType> SystemTradeTypeMap = new ConcurrentHashMap<Integer,SystemTradeType>();


    public final static int BTC_CONVERSION_ID = 8;

    public final static int GAVC_CONVERSION_ID = 9;

    public final static int ETH_CONVERSION_ID = 11;

    public final static int USDT_CONVERSION_ID = 57;



   /* @Scheduled(cron="0 0/3 * * * ? ")
    public void work() {
        initTradeMap();
    }*/
   @PostConstruct
   public void init() {
       //new Thread(new Work()).start();
       initTradeMap();
   }

    @Scheduled(cron="0 0/1 * * * ?")
    public void schedule() {
        if (!AutoMarket.klineInitFlag) {
            return;
        }
        initTradeMap();
        /*
        taskExecutor.execute(()->initKline(SystemTradeTypeJob.BTC_MAP));
        taskExecutor.execute(()->initKline(SystemTradeTypeJob.GAVC_Map));
        taskExecutor.execute(()->initKline(SystemTradeTypeJob.ETH_MAP));
        taskExecutor.execute(()->initKline(SystemTradeTypeJob.USDT_MAP));
        taskExecutor.execute(()->initKline(SystemTradeTypeJob.INNOVATE_MAP));
        */

    }

    public static void generateTradeShortNameMap(List<SystemTradeType> tradeTypes){
        for (SystemTradeType tradeType : tradeTypes) {
            if (tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
                if(SystemTradeTypeMap.containsKey(tradeType.getId())){
                  SystemTradeTypeMap.remove(tradeType.getId());
                  TradeShortNameMap.remove(tradeType.getSellShortName().toLowerCase() + "_" + tradeType.getBuyShortName().toLowerCase());
                }
                continue;
            }
            SystemTradeTypeMap.put(tradeType.getId(),tradeType);
            TradeShortNameMap.put(tradeType.getSellShortName().toLowerCase() + "_" + tradeType.getBuyShortName().toLowerCase(), tradeType.getId());
        }
    }

    public Long getTradeTypeListForLastActiveDateTime() {
        String coins = this.memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + AGENTID);
        if (StringUtils.isEmpty(coins)) {
            return null;
        } else {
            JSONObject obj = JSON.parseObject(coins);
            Long lastActiveDateTime =obj.getLong(LAST_ACTIVE_DATE_TIME);
            return lastActiveDateTime;
        }

    }


 private static AtomicBoolean coinMapFlag = new AtomicBoolean(true);

 public void initTradeIdMap(){
        int coinSize =  coinMap.size();
        if(coinSize<=0) {
            if(coinMapFlag.compareAndSet(true, false)){
                try {
                    if (coinMap.size() <= 0) {
                        initTradeMap();
                        String redisKey = RedisConstant.TRADE_LIST_KEY + "_" + 0;
                        coinMap.put(redisKey,redisKey);
                    }
                }finally {
                    coinMapFlag.set(true);
                }

            }

        }
    }

    //@Scheduled(fixedRate = 1*1000)
//    public void ascTradeInfoRankingList() {
//        logger.info("ascTradeInfoRankingList start");
//        String coins = memCache.get(RedisConstant.TRADE_LIST_KEY + "_" + 0);
//        JSONObject obj = JSON.parseObject(coins);
//        List<SystemTradeType> redisList = JSONArray.parseArray(obj.getJSONArray("extObject").toString(), SystemTradeType.class);
//        Integer abnormalCode = SystemTradeStatusEnum.ABNORMAL.getCode();
//        List<SystemTradeType> filterList =redisList.stream().filter(t->(!t.getStatus().equals(abnormalCode))).collect(Collectors.toList());
//        logger.info("92 filterList.size :{}", filterList.size());
//        List<JSONObject> arrayList = new ArrayList<JSONObject>(filterList.size());
//        for(SystemTradeType systemTradeType:filterList){
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("tradeId", systemTradeType.getId());
//            jsonObject.put("id", systemTradeType.getId());
//            jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
//            jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
//            jsonObject.put("image", systemTradeType.getSellWebLogo());
//            jsonObject.put("sellShortName", systemTradeType.getSellShortName());
//            jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
//            jsonObject.put("type", systemTradeType.getType());
//            // 最新价格
//            TickerData tickerData =  AutoMarket.TickerJson.get(systemTradeType.getId());
//            if (null == tickerData) {
//                jsonObject.put("p_new", 0);
//                jsonObject.put("p_open", 0);
//                jsonObject.put("total",  0d);
//                jsonObject.put("buy", 0d);
//                jsonObject.put("sell",0d);
//                jsonObject.put("rose",0d);
//            } else {
//                jsonObject.put("p_new", tickerData.getLast());
//                jsonObject.put("p_open", tickerData.getKai());
//                jsonObject.put("total", null == tickerData.getVol() ? 0d : tickerData.getVol());
//                jsonObject.put("buy", null == tickerData.getBuy() ? 0d : tickerData.getLow());
//                jsonObject.put("sell", null == tickerData.getSell() ? 0d : tickerData.getHigh());
//                jsonObject.put("rose", null == tickerData.getChg() ? 0d : tickerData.getChg());
//            }
//            jsonObject.put("digit", systemTradeType.getDigit());
//            arrayList.add(jsonObject);
//        }
//         logger.info("121 ascTradeInfoRankingList :{}", arrayList.toString());
//        List<JSONObject> ascSortArrayList  = arrayList.stream().sorted((s1, s2) ->
//                compareByDigitThenBuyShortName(s1,s2,1)
//        ).collect(Collectors.toList());
//        List<JSONObject> subAscList = null;
//        if(ascSortArrayList.size()>10) {
//             subAscList = ascSortArrayList.subList(0, 10);
//        }else{
//            subAscList = ascSortArrayList;
//        }
//        JSONArray ascJsonArray = new JSONArray();
//        for(JSONObject jsonObject : subAscList){
//            ascJsonArray.add(jsonObject);
//        }
//        try {
//
//            logger.info("129 ascTradeInfoRankingList :{},topic:{}",
//                    ascJsonArray.toJSONString(),mqttProperties.getTopic().getTickerRankingAscTopic());
//             /*
//            mqttHelper.sendMqtt(mqttHelper.getTickerRankingAscTopicProducer(), ascJsonArray.toJSONString(),
//                    mqttProperties.getTopic().getTickerRankingAscTopic());
//            */
//
//            MqttSendStringEvent mqttSendStringEvent = new MqttSendStringEvent(ascJsonArray.toJSONString(),mqttHelper.getTickerRankingAscTopicProducer(),
//                    mqttProperties.getTopic().getTickerRankingAscTopic(),null, mqttHelper);
//            MqttSendStringEventBus.post(mqttSendStringEvent);
//
//        } catch (Exception e) {
//             logger.error("send TickerRankingAscTopic error e:{}",e);
//        }
//        List<JSONObject> descSortArrayList = arrayList.stream().sorted((s1, s2) ->
//                compareByDigitThenBuyShortName(s1, s2, -1)
//        ).collect(Collectors.toList());
//        List<JSONObject> subDescList = null;
//        if(descSortArrayList.size()>10) {
//            subDescList = descSortArrayList.subList(0, 10);
//        }else{
//            subDescList = descSortArrayList;
//        }
//        JSONArray descJsonArray = new JSONArray();
//        for(JSONObject jsonObject : subDescList){
//            descJsonArray.add(jsonObject);
//        }
//        try {
//           /* logger.info("146  descTradeInfoRankingList :{},topic:{}",
//                    descJsonArray.toJSONString(),mqttProperties.getTopic().getTickerRankingDescTopic());
//                    mqttHelper.sendMqtt(mqttHelper.getTickerRankingDescTopicProducer(), descJsonArray.toJSONString(),
//                    mqttProperties.getTopic().getTickerRankingDescTopic());*/
//
//
//            MqttSendStringEvent mqttSendStringEvent = new MqttSendStringEvent(descJsonArray.toJSONString(),mqttHelper.getTickerRankingDescTopicProducer(),
//                    mqttProperties.getTopic().getTickerRankingDescTopic(),null,mqttHelper);
//            MqttSendStringEventBus.post(mqttSendStringEvent);
//        } catch (Exception e) {
//            logger.error("send TickerRankingAscTopic error e:{}",e);
//        }
//    }


    private void initTradeMap(){
        Long lastActiveDateTime = RedisUpdateTimeMap.get(TRADE_LIST_KEY);
        if(null == lastActiveDateTime){
          try {
            List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(AGENTID);
            logger.error("KaiPrizeJob groupMarket tradeTypes.size: {}" , tradeTypes.size());
            generateTradeShortNameMap(tradeTypes);
            generateTradeTypeMap(tradeTypes);
            Long newLastActiveDateTime = getTradeTypeListForLastActiveDateTime();
            RedisUpdateTimeMap.put(TRADE_LIST_KEY,newLastActiveDateTime);
            generateCNYTradeTypeMap(tradeTypes);
          }catch (Exception ex){
            logger.error("281 initTradeMap ex :{}" ,ex);
          }
        }

        Long newLastActiveDateTime = getTradeTypeListForLastActiveDateTime();
        if(!newLastActiveDateTime.equals(lastActiveDateTime)){
            try {
                logger.error("KaiPrizeJob lastActiveDateTime: {}", lastActiveDateTime);
                long start = System.currentTimeMillis();
                List<SystemTradeType> tradeTypes = redisHelper.getTradeTypeList(AGENTID);
                generateTradeShortNameMap(tradeTypes);
                generateTradeTypeMap(tradeTypes);
                generateCNYTradeTypeMap(tradeTypes);
                RedisUpdateTimeMap.put(TRADE_LIST_KEY, newLastActiveDateTime);
                logger.error("lost time :{},tradeTypes:{}",System.currentTimeMillis()-start, JSON.toJSONString(tradeTypes));
            }catch (Exception ex){
                logger.error("initTradeMap ex :{}" ,ex);
            }

        }
    }

    private void initKline(ConcurrentHashMap<Integer,SystemTradeType> systemTradeTypeMap){
          for(Map.Entry<Integer,SystemTradeType> systemTradeTypeEntry :systemTradeTypeMap.entrySet()) {
          Integer tradeId = systemTradeTypeEntry.getKey();
          ConcurrentHashMap<Integer, JSONArray> klineJsonMap = AutoMarket.KlineJson.get(tradeId);
          if(null == klineJsonMap){
              klineJsonMap = new ConcurrentHashMap<Integer, JSONArray>();
          }
          for (int i : AutoMarket.TIME_KIND) {

              String klineStr = redisHelper.getRedisData(KLINE_KEY + tradeId + "_" + i);
              if (klineStr == null || klineStr.isEmpty()) {
                  klineJsonMap.put(i, new JSONArray());
              } else {
                  klineJsonMap.put(i, JSON.parseArray(klineStr));
              }

          }
          AutoMarket.KlineJson.put(tradeId, klineJsonMap);


              /*
              String klineStr = redisHelper.getRedisData(KLINE_KEY + tradeId + "_" + i);
              //logger.error("302 tradeId:{},klineStr:{}",tradeId, klineStr);
              JSONArray klineJson = null;

              if (StringUtils.isNotBlank(klineStr)) {
                  try {
                      klineJson = JSON.parseArray(klineStr);
                      klineJsonMap.put(i, klineJson);
                  } catch (Exception ex) {
                      logger.error(
                          "154 initLastKline KLINE_KEY parseArray error tradeId:{},i:{}, klineStr:{}, e:{}",
                          tradeId, i, klineStr, ex);
                      klineJsonMap.put(i, new JSONArray());
                  }
              } else {
                  klineJson = new JSONArray();
                  klineJsonMap.put(i, klineJson);
              }
              if(i==15) {
                  logger.error("318 tradeId:{},klineJsonMap:{}", tradeId,
                      JSON.toJSONString(klineJsonMap));
              }
              AutoMarket.KlineJson.put(tradeId, klineJsonMap);
              */

      }
    }

  /**
   * 当数据更新的时候
   * 1，CNYTradeTypeMap 没有数据
   * 2，CNYTradeTypeMap中存储的数据与之前的不相同时
   * @param tradeTypes
   */
  public static void generateTradeTypeMap(List<SystemTradeType> tradeTypes){
    for (SystemTradeType systemTradeType : tradeTypes) {
      Integer id = systemTradeType.getId();
      if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
        if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
          GAVC_Map.remove(id);
        } //BTC交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())) {
          BTC_MAP.remove(id);
        } //ETH交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode())) {
          ETH_MAP.remove(id);
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode())) {
          USDT_MAP.remove(id);
        }else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
          INNOVATE_MAP.remove(id);
        }
        continue;
      }


      if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
          GAVC_Map.put(id, systemTradeType);
      }
      //BTC交易区
      else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())) {
          BTC_MAP.put(id, systemTradeType);
      }
      //ETH交易区
      else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode())) {
          ETH_MAP.put(id, systemTradeType);
      } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode())) {
          USDT_MAP.put(id, systemTradeType);
      } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
        INNOVATE_MAP.put(id, systemTradeType);
      }
    }

  }

    /**
     * 当数据更新的时候
     * 1，CNYTradeTypeMap 没有数据
     * 2，CNYTradeTypeMap中存储的数据与之前的不相同时
     * @param tradeTypes
     */
    public static void generateCNYTradeTypeMap(List<SystemTradeType> tradeTypes){
      for (SystemTradeType systemTradeType : tradeTypes) {
        Integer id = systemTradeType.getId();
        Integer oldTradeId = CNYTradeTypeMap.get(id);

        if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
          if (null == oldTradeId || (!oldTradeId.equals(systemTradeType.getId()))) {
            CNYTradeTypeMap.put(systemTradeType.getId(), GAVC_CONVERSION_ID);
          }
        }
        //BTC交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode()) || systemTradeType.getBuyCoinId() == 1) {
          if (null == oldTradeId || (!oldTradeId.equals(BTC_CONVERSION_ID))) {
            CNYTradeTypeMap.put(systemTradeType.getId(), BTC_CONVERSION_ID);
          }
        }
        //ETH交易区
        else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode()) || systemTradeType.getBuyCoinId() == 4) {
          if (null == oldTradeId || (!oldTradeId.equals(ETH_CONVERSION_ID))) {
            CNYTradeTypeMap.put(systemTradeType.getId(), ETH_CONVERSION_ID);
          }
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode()) || systemTradeType.getBuyCoinId() == 52) {
          if (null == oldTradeId || (!oldTradeId.equals(USDT_CONVERSION_ID))) {
            CNYTradeTypeMap.put(systemTradeType.getId(), USDT_CONVERSION_ID);
          }
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
          int buyCoinId = systemTradeType.getBuyCoinId();
          if (buyCoinId == GAVC_CONVERSION_ID) {
            if (null == oldTradeId || (!oldTradeId.equals(systemTradeType.getId()))) {
              CNYTradeTypeMap.put(systemTradeType.getId(), GAVC_CONVERSION_ID);
            }
          } else {
            String symbol = systemTradeType.getBuyShortName().toUpperCase() + "_GAVC";
            Integer tradeId = TradeShortNameMap.get(symbol);
            if (null == oldTradeId || (!oldTradeId.equals(tradeId))) {
              CNYTradeTypeMap.put(systemTradeType.getId(), tradeId);
            }
          }
        }
      }

    }


  private static int compareByDigitThenBuyShortName(JSONObject lst, JSONObject rst,int sortCode) {
        BigDecimal lrose = new BigDecimal(lst.get("rose").toString());
        BigDecimal rrose = new BigDecimal(rst.get("rose").toString());
        if(1 == sortCode){
            if (rrose.compareTo(lrose) ==0) {
                return rst.get("buySymbol").toString().compareTo(lst.get("buySymbol").toString());
            } else {
                return rrose.compareTo(lrose);
            }
        }else{
            if (lrose.compareTo(rrose) ==0) {
                return lst.get("buySymbol").toString().compareTo(rst.get("buySymbol").toString());
            } else {
                return lrose.compareTo(rrose);
            }
        }
    }



}
