package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.dto.TradeTickersDTO;
import hotcoin.quote.model.wsquote.vo.TradeAmountRankVo;
import hotcoin.quote.model.wsquote.vo.TradeAreaTickersVo;
import hotcoin.quote.model.wsquote.vo.TradeChangeTickersVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.component
 * @ClassName: TickersComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/7/16 17:53
 * @Version: 1.0
 */
@Component
@Slf4j
public class TickersComponent {
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 用于缓存行情数据,初始化使用
     */
    private static ConcurrentHashMap<Integer, TradeTickersDTO> cacheTickerMap = new ConcurrentHashMap<>(256);

    /**
     * 获取缓存数据
     *
     * @return
     */
    public ConcurrentHashMap<Integer, TradeTickersDTO> getCacheTickerMap() {
        if (MapUtils.isEmpty(cacheTickerMap)) {
            cacheAllTickers4Redis();
        }
        return cacheTickerMap;
    }

    /**
     * 通过tradeId获取到缓存中的行情数据
     *
     * @param tradeId
     * @return
     */
    public TradeTickersDTO getCacheTickerByTradeId(Integer tradeId) {
        if (MapUtils.isEmpty(cacheTickerMap)) {
            cacheAllTickers4Redis();
        }
        TradeTickersDTO data = cacheTickerMap.get(tradeId);
        if (data == null) {
            cacheAllTickers4Redis();
        }
        return cacheTickerMap.get(tradeId);
    }

    /**
     * 返回指定条数的正序和倒序数据
     *
     * @param list   market推送过来的变化的数据
     * @param subNum subNum :截取条数
     * @return
     */
    public Map<String, List<TradeChangeTickersVo>> getChangeRateCompare2Cache(List<TradeTickersDTO> list, int subNum) {
        try {
            if (MapUtils.isEmpty(cacheTickerMap)) {
                cacheAllTickers4Redis();
            }
            List<Integer> changedTradeIdList = new ArrayList<>();
            for (TradeTickersDTO item : list) {
                Integer tradeId = item.getTradeId();
                // 过滤涨跌幅和价格不变化的数据
                if (isChangeTicker(cacheTickerMap, item)) {
                    cacheTickerMap.put(tradeId, item);
                    changedTradeIdList.add(tradeId);
                }
            }
            List<TradeTickersDTO> tradeTickersVoList = getAllTickersList4cacheMap();
            log.info("get data from all tickersCache->{}", JSON.toJSONString(tradeTickersVoList));
            if (subNum > tradeTickersVoList.size()) {
                subNum = tradeTickersVoList.size();
            }
            Map<String, List<TradeChangeTickersVo>> map = getDataSuit2Push(tradeTickersVoList, changedTradeIdList, subNum);
            log.info("get data about rate suit push->{}", JSON.toJSONString(map));
            return map;
        } catch (Exception e) {
            log.error("getChangeRateSortTickersCompare2Cache func ->{}", e);
            return null;
        }

    }

    /**
     * 判断是否是价格或者最新价变化的数据
     *
     * @param cacheMap
     * @param item
     * @return
     */
    private boolean isChangeTicker(Map<Integer, TradeTickersDTO> cacheMap, TradeTickersDTO item) {
        Integer tradeId = item.getTradeId();
        TradeTickersDTO ticker = cacheMap.get(tradeId);
        if (ticker == null) {
            return true;
        }
        String cacheChange = ticker.getChange();
        String cacheLast = ticker.getLast();
        String pushChange = item.getChange();
        String pushLast = item.getLast();
        if (!cacheChange.equals(pushChange) || !cacheLast.equals(pushLast)) {
            return true;
        }
        return false;
    }

    /**
     * 缓存所有币对行情信息
     */
    private void cacheAllTickers4Redis() {
        Map<Integer, String> map = redisComponent.getCacheTradeId4SymbolMap();
        for (Integer tradeId : map.keySet()) {
            // 获取最新价
            TickerData tickerData = redisComponent.getTickerDataByTradeId(tradeId);
            if (tickerData == null) {
                log.info("tickerData is null query by tradId->{}", tradeId);
                continue;
            }
            String symbol = map.get(tradeId);
            if (StringUtils.isEmpty(symbol)) {
                continue;
            }
            // 获取图片地址
            SystemTradeType systemTradeType = redisComponent.getCoinPairInfo(tradeId);
            if (systemTradeType == null) {
                continue;
            }
            String[] symbolArr = symbol.split(WsConstant.SLASH);
            String sellSymbol = symbolArr[0];
            String buySymbol = symbolArr[1];
            TradeTickersDTO tickersDTO = convertTickers(tradeId, systemTradeType, tickerData, sellSymbol, buySymbol);
            cacheTickerMap.put(tradeId, tickersDTO);
        }
    }

    private TradeTickersDTO convertTickers(Integer tradeId, SystemTradeType systemTradeType, TickerData tickerData, String sellSymbol, String buySymbol) {
        BigDecimal last = tickerData.getLast();
        // 获取折合cny价格
        String cnyPrice = redisComponent.getCnyPrice(tradeId, last);
        TradeTickersDTO result = new TradeTickersDTO();
        result.setSell(tickerData.getSell().toPlainString());
        result.setSellSymbol(sellSymbol);
        result.setSellShortName(sellSymbol);
        result.setBuy(tickerData.getBuy().toPlainString());
        result.setBuySymbol(buySymbol);
        result.setBuyShortName(buySymbol);
        result.setChange(tickerData.getChg().toPlainString());
        result.setVolume(tickerData.getVol().toPlainString());
        result.setCny(StringUtils.isEmpty(cnyPrice) ? "" : cnyPrice);
        result.setTradeId(tradeId);
        result.setLast(last.toPlainString());
        result.setLever("");
        result.setImageUrl(systemTradeType.getSellWebLogo());
        result.setDigit(systemTradeType.getDigit());
        return result;
    }

    /**
     * 将分区的数据进行分区,并返回
     *
     * @param list
     * @return
     */
    public Map<String, List<TradeAreaTickersVo>> getArea4PushData(List<TradeTickersDTO> list) {
        Map<String, List<TradeAreaTickersVo>> areasDataMap = new HashMap<>(5);
        try {
            List<TradeAreaTickersVo> btcList = new ArrayList<>();
            List<TradeAreaTickersVo> ethList = new ArrayList<>();
            List<TradeAreaTickersVo> gavcList = new ArrayList<>();
            List<TradeAreaTickersVo> usdtList = new ArrayList<>();
            List<TradeAreaTickersVo> innoList = new ArrayList<>();
            for (TradeTickersDTO item : list) {
                Integer tradeId = item.getTradeId();
                if (tradeId == null) {
                    continue;
                }
                Integer type = item.getType();
                if (type.equals(SystemTradeTypeEnum.BTC.getCode())) {
                    btcList.add(convertDTO2AreaTickersVo(item));
                } else if (type.equals(SystemTradeTypeEnum.ETH.getCode())) {
                    ethList.add(convertDTO2AreaTickersVo(item));
                } else if (type.equals(SystemTradeTypeEnum.GAVC.getCode())) {
                    gavcList.add(convertDTO2AreaTickersVo(item));
                } else if (type.equals(SystemTradeTypeEnum.USDT.getCode())) {
                    usdtList.add(convertDTO2AreaTickersVo(item));
                } else if (type.equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
                    innoList.add(convertDTO2AreaTickersVo(item));
                }
            }
            setAreaData(TickersConstant.TICKERS_BTC_AREA, btcList, areasDataMap);
            setAreaData(TickersConstant.TICKERS_ETH_AREA, ethList, areasDataMap);
            setAreaData(TickersConstant.TICKERS_GAVC_AREA, gavcList, areasDataMap);
            setAreaData(TickersConstant.TICKERS_USDT_AREA, usdtList, areasDataMap);
            setAreaData(TickersConstant.TICKERS_INNOVATE_AREA, innoList, areasDataMap);
            return areasDataMap;
        } catch (Exception e) {
            log.error("getArea4PushData func fail->{}", e);
            return null;
        }


    }

    /**
     * 精简字段,只推送需要的字段,结构体转换
     *
     * @param data
     * @return
     */
    private TradeAreaTickersVo convertDTO2AreaTickersVo(TradeTickersDTO data) {
        TradeAreaTickersVo vo = new TradeAreaTickersVo();
        vo.setTradeId(data.getTradeId());
        vo.setSellShortName(data.getSellShortName());
        vo.setSellSymbol(data.getSellSymbol());
        vo.setSell(data.getSell());
        vo.setBuyShortName(data.getBuyShortName());
        vo.setBuySymbol(data.getBuySymbol());
        vo.setBuy(data.getBuy());
        vo.setLever(data.getLever());
        vo.setLast(data.getLast());
        vo.setCny(data.getCny());
        vo.setVolume(data.getVolume());
        vo.setChange(data.getChange());
        vo.setImageUrl(data.getImageUrl());
        vo.setDigit(data.getDigit());
        return vo;
    }

    /**
     * 存储到结果集中
     *
     * @param key
     * @param data
     * @param areasDataMap
     */
    private void setAreaData(String key, List<TradeAreaTickersVo> data, Map<String, List<TradeAreaTickersVo>> areasDataMap) {
        if (!CollectionUtils.isEmpty(data)) {
            areasDataMap.put(key, data);
        }
    }


    /**
     * 是否需要推送该次更新的数据
     *
     * @param tradeTickersVoList
     * @param changedTradeIdList
     * @param subNum
     * @return
     */
    private Map<String, List<TradeChangeTickersVo>> getDataSuit2Push(List<TradeTickersDTO> tradeTickersVoList, List<Integer> changedTradeIdList, int subNum) {
        Map<String, List<TradeChangeTickersVo>> map = new HashMap<>(2);
        Comparator<TradeTickersDTO> comparator = Comparator.comparing(t -> Double.valueOf(t.getChange()));
        tradeTickersVoList.sort(comparator);
        try {
            List<TradeTickersDTO> downData = new ArrayList<>(tradeTickersVoList.subList(0, subNum));
            log.info("get sort list down data :{}", JSON.toJSONString(downData));

            tradeTickersVoList.sort(comparator.reversed());
            List<TradeTickersDTO> upData = new ArrayList<>(tradeTickersVoList.subList(0, subNum));
            log.info("get sort list upData  :{}", JSON.toJSONString(upData));

            if (!CollectionUtils.isEmpty(upData) && containsPushData(upData, changedTradeIdList)) {
                map.put(TickersConstant.TICKERS_UP_KEY, convertDTO2ChangeTickersVo(upData));
            }
            if (!CollectionUtils.isEmpty(downData) && containsPushData(downData, changedTradeIdList)) {
                map.put(TickersConstant.TICKERS_DOWN_KEY, convertDTO2ChangeTickersVo(downData));
            }
        } catch (Exception e) {
            log.error("func getDataSuit2Push fail ->{}", e);
        }
        return map;
    }

    /**
     * 精简字段,只推送需要的字段,结构体转换
     *
     * @return
     */
    private List<TradeChangeTickersVo> convertDTO2ChangeTickersVo(List<TradeTickersDTO> data) {
        List<TradeChangeTickersVo> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TradeChangeTickersVo vo = new TradeChangeTickersVo();
            TradeTickersDTO item = data.get(i);
            vo.setTradeId(item.getTradeId());
            vo.setBuyShortName(item.getBuyShortName());
            vo.setBuySymbol(item.getBuyShortName());
            vo.setChange(item.getChange());
            vo.setDigit(item.getDigit());
            vo.setImageUrl(item.getImageUrl());
            vo.setCny(item.getCny());
            vo.setSellShortName(item.getSellShortName());
            vo.setSellSymbol(item.getSellShortName());
            vo.setType(item.getType());
            vo.setLast(item.getLast());
            result.add(vo);
        }
        return result;
    }


    /**
     * 是否存在需要推送的数据
     *
     * @param subPartList
     * @param changedTradeIdList
     * @return
     */
    private boolean containsPushData(List<TradeTickersDTO> subPartList, List<Integer> changedTradeIdList) {
        boolean flag = false;
        if (CollectionUtils.isEmpty(subPartList) || CollectionUtils.isEmpty(changedTradeIdList)) {
            return false;
        }
        for (TradeTickersDTO item : subPartList) {
            Integer tradeId = item.getTradeId();
            if (changedTradeIdList.contains(tradeId)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 从缓存获取到所有交易对list
     *
     * @return
     */
    private List<TradeTickersDTO> getAllTickersList4cacheMap() {
        List<TradeTickersDTO> list = new ArrayList<>(256);
        Map<Integer, TradeTickersDTO> map = getCacheTickerMap();
        map.values().forEach(item -> list.add(item));
        return list;
    }

    /**
     * 获取成交额排行榜
     *
     * @return
     */
    public List<TradeAmountRankVo> getAmountRankList() {
        ConcurrentHashMap<Integer, TradeTickersDTO> map = getCacheTickerMap();
        List<TradeAmountRankVo> resultList = new ArrayList<>(map.size());
        for (TradeTickersDTO dto : map.values()) {
            resultList.add(new TradeAmountRankVo(dto));
        }
        return resultList.stream().sorted(Comparator.comparing(TradeAmountRankVo::getTotalAmount, Comparator.comparing(BigDecimal::new)).reversed()).limit(10).collect(Collectors.toList());

    }
}
