package hotcoin.service.impl;

import com.alibaba.fastjson.JSON;
import hotcoin.component.KlineParamComponent;
import hotcoin.component.RedisComponent;
import hotcoin.model.bo.KlineQueryBo;
import hotcoin.model.constant.KlineHistoryConstant;
import hotcoin.model.dto.KlineQueryDto;
import hotcoin.model.em.OptTypeEnum;
import hotcoin.model.vo.KlineHistoryVo;
import hotcoin.service.KlineHistoryService;
import hotcoin.util.JodaTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.service.impl
 * @ClassName: KlineHistoryServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/9/5 17:53
 * @Version: 1.0
 */
@Service
@Slf4j
public class KlineHistoryServiceImpl implements KlineHistoryService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private KlineParamComponent klineParamComponent;
    @Autowired
    private RedisComponent redisComponent;

    @Override
    public List<List<String>> query(KlineQueryDto klineQueryDto, OptTypeEnum optTypeEnum) {
        Integer tradeId = klineQueryDto.getTradeId();
        String period = klineQueryDto.getPeriod();
        Integer direction = klineQueryDto.getDirection();
        Integer limit = klineQueryDto.getLimit();

        KlineQueryBo klineQueryBo = klineParamComponent.convert2QueryParam(klineQueryDto);
        String collectionName = getCollectionName(tradeId, period);

        Query query = queryParamConvert(klineQueryBo);
        List<KlineHistoryVo> list = mongoTemplate.find(query, KlineHistoryVo.class, collectionName);

        if (!optTypeEnum.equals(OptTypeEnum.KEEPHISTORY)) {
            list = removeRepeatData(list);
        }
        List<List<String>> convertResultList = convert2Dto(list, klineQueryBo.getSort());

        if (limit == null || limit > KlineHistoryConstant.KLINE_HIS_MAX_LIMIT) {
            limit = KlineHistoryConstant.KLINE_HIS_DEFAULT_LIMIT;
        }
        // 需要从redis 补上最新的一条kline
        if (direction.equals(KlineHistoryConstant.KLINE_HIS_ASC)) {
            addLastKlineData(convertResultList, tradeId, period, limit);
        }
        return convertResultList;
    }

    /**
     * 移除重复数据
     *
     * @param list
     * @return
     */
    private List<KlineHistoryVo> removeRepeatData(List<KlineHistoryVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        Map<String, KlineHistoryVo> map = new HashMap<>(list.size());
        for (KlineHistoryVo item : list) {
            map.put(String.valueOf(item.getCreateTime()), item);
        }
        List<KlineHistoryVo> result = new ArrayList<>(map.size());
        result.addAll(map.values());
        result.sort(Comparator.comparing(KlineHistoryVo::getCreateTime));
        return result;
    }

    @Override
    public KlineHistoryVo findMin(Integer tradeId, String period) {
        String collectionName = getCollectionName(tradeId, period);
        Query query = fillQueryMinOneParam(period);
        return mongoTemplate.findOne(query, KlineHistoryVo.class, collectionName);
    }

    /**
     * 查询最小时间数据
     *
     * @param period
     * @return
     */
    private Query fillQueryMinOneParam(String period) {
        Criteria criteria = Criteria.where(KlineHistoryConstant.KLINE_HIS_PERIOD).is(period);
        return Query.query(criteria).with(new Sort(Sort.Direction.ASC, KlineHistoryConstant.KLINE_HIS_CREATE_TIME)).limit(1);
    }

    /**
     * 添加最新一条kline到结果集中
     *
     * @param tradeId
     * @param period
     * @param convertResultList
     * @param limit
     */
    private void addLastKlineData(List<List<String>> convertResultList, Integer tradeId, String period, Integer limit) {
        if (CollectionUtils.isEmpty(convertResultList)) {
            return;
        }
        List lastKlineData = redisComponent.getLastKlineData(tradeId, period);
        log.error("get lastKline data is->{}", JSON.toJSONString(lastKlineData));
        if (CollectionUtils.isEmpty(lastKlineData)) {
            return;
        }
        //todo 测试后可放开
        // tempAddMissData(convertResultList, lastKlineData, period, tradeId);
        convertResultList.add(lastKlineData);
        if (convertResultList.size() >= limit) {
            convertResultList.remove(0);
        }
    }

    /**
     * 数据生成有延迟时,通过临时生成实时数据的方式进行填充
     *
     * @param list
     * @param lastRedisKlineData
     * @param period
     * @param tradeId
     */
    private void tempAddMissData(List<List<String>> list, List<String> lastRedisKlineData, String period, Integer tradeId) {
        if (KlineHistoryConstant.KLINE_HIS_PERIOD_5M.equals(period) || KlineHistoryConstant.KLINE_HIS_PERIOD_15M.equals(period) || KlineHistoryConstant.KLINE_HIS_PERIOD_30M.equals(period)) {

            List<String> lastKlineHistoryList = list.get(list.size() - 1);
            long mongoLastTs = Long.valueOf(lastKlineHistoryList.get(0));
            long redisLastTs = JodaTimeUtil.timestampIgnoreSecond(Long.valueOf(lastRedisKlineData.get(0)));
            if (!isMissData(mongoLastTs, redisLastTs, period)) {
                return;
            }
            long startTime = klineParamComponent.getFutureTime(mongoLastTs, period);
            long endTime = klineParamComponent.getFutureTime(startTime, period);
            BigDecimal open = new BigDecimal(lastKlineHistoryList.get(4));
            BigDecimal close = new BigDecimal(lastRedisKlineData.get(1));

            Query query = queryParamConvert(new KlineQueryBo(startTime, endTime, 1, tradeId, KlineHistoryConstant.KLINE_HIS_PERIOD_1M));
            List<KlineHistoryVo> queryList = mongoTemplate.find(query, KlineHistoryVo.class, getCollectionName(tradeId, KlineHistoryConstant.KLINE_HIS_PERIOD_1M));
            if (CollectionUtils.isEmpty(queryList)) {
                log.error("data unusual,can not find time from->{},to endTime->{},tradeId is->{},period is 1m", startTime, endTime, tradeId);
                return;
            }
            KlineHistoryVo queryResult = combineKline(queryList);
            List<String> item = new ArrayList<>(6);
            item.add(String.valueOf(startTime));
            item.add(open.toPlainString());
            item.add(queryResult.getHigh().toPlainString());
            item.add(queryResult.getLow().toPlainString());
            item.add(close.toPlainString());
            item.add(queryResult.getVolume().toPlainString());
            list.add(item);
        }
    }

    /**
     * 合成数据
     *
     * @param klineHistoryVoList
     * @return
     */
    private KlineHistoryVo combineKline(List<KlineHistoryVo> klineHistoryVoList) {
        KlineHistoryVo calcResult = klineHistoryVoList.get(0);
        for (KlineHistoryVo item : klineHistoryVoList) {
            BigDecimal tempLow = item.getLow();
            BigDecimal tempHigh = item.getHigh();
            BigDecimal tempVol = item.getVolume();
            BigDecimal high = calcResult.getHigh();
            BigDecimal vol = calcResult.getVolume();
            BigDecimal low = calcResult.getLow();
            if (tempHigh.compareTo(high) > 0) {
                calcResult.setHigh(tempHigh);
            }
            if (tempLow.compareTo(low) < 0) {
                calcResult.setLow(tempLow);
            }
            calcResult.setVolume(vol.add(tempVol));
        }
        return calcResult;
    }

    /**
     * 判断数据是否丢失
     *
     * @param mongoLastTs
     * @param redisLastTs
     * @param period
     * @return
     */
    private boolean isMissData(long mongoLastTs, long redisLastTs, String period) {
        if (KlineHistoryConstant.KLINE_HIS_PERIOD_5M.equals(period)) {
            return redisLastTs > mongoLastTs + KlineHistoryConstant.KLINE_HIS_MILLIS * 60 * 5;
        } else if (KlineHistoryConstant.KLINE_HIS_PERIOD_15M.equals(period)) {
            return redisLastTs > mongoLastTs + KlineHistoryConstant.KLINE_HIS_MILLIS * 60 * 15;
        } else if (KlineHistoryConstant.KLINE_HIS_PERIOD_30M.equals(period)) {
            return redisLastTs > mongoLastTs + KlineHistoryConstant.KLINE_HIS_MILLIS * 60 * 30;
        } else {
            return false;
        }
    }

    /**
     * @param list
     * @param sort
     * @return
     */
    private List<List<String>> convert2Dto(List<KlineHistoryVo> list, Integer sort) {
        if (!CollectionUtils.isEmpty(list) && sort < 0) {
            list.sort(Comparator.comparing(KlineHistoryVo::getCreateTime));
            Comparator<KlineHistoryVo> comparator = Comparator.comparing(KlineHistoryVo::getCreateTime);
            list.sort(comparator.reversed());
        }

        List<List<String>> data = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                List<String> item = new ArrayList<>(6);
                KlineHistoryVo kv = list.get(i);
                item.add(String.valueOf(kv.getCreateTime()));
                item.add(kv.getOpen().toPlainString());
                item.add(kv.getHigh().toPlainString());
                item.add(kv.getLow().toPlainString());
                item.add(kv.getClose().toPlainString());
                item.add(kv.getVolume().toPlainString());
                data.add(item);
            }
        }
        return data;
    }

    /**
     * 填充查询参数
     *
     * @param klineQueryBo
     * @return
     */
    private Query queryParamConvert(KlineQueryBo klineQueryBo) {
        log.info("query param ->{}", JSON.toJSONString(klineQueryBo));
        Criteria criteria = new Criteria()
                .andOperator(Criteria.where(KlineHistoryConstant.KLINE_HIS_CREATE_TIME).gte(klineQueryBo.getStartTime()),
                        Criteria.where(KlineHistoryConstant.KLINE_HIS_CREATE_TIME).lt(klineQueryBo.getEndTime()),
                        Criteria.where(KlineHistoryConstant.KLINE_HIS_PERIOD).is(klineQueryBo.getPeriod()));

        return Query.query(criteria)
                .with(new Sort(klineQueryBo.getSort() > 0 ? Sort.Direction.ASC : Sort.Direction.DESC, KlineHistoryConstant.KLINE_HIS_CREATE_TIME));
    }

    /**
     * 路由策略
     * 获取集合名称
     * 文档结构: 每个币对,1min 放在一个集合,其他粒度(5min,15min,30min,1hour,1week,1month)公用一个集合,
     * 文档命名规则: tradeId_1m (一分钟文档,eg: 900003_1m,其他: 900003_mix)
     * 文档索引字段: period ,createTime
     *
     * @param tradeId
     * @param period
     * @return
     */
    private String getCollectionName(int tradeId, String period) {
        if (!period.equals(KlineHistoryConstant.KLINE_HIS_PERIOD_1M)) {
            period = KlineHistoryConstant.KLINE_HIS_MIX;
        }
        return tradeId + KlineHistoryConstant.KLINE_HIS_SLASH + period;
    }

}
