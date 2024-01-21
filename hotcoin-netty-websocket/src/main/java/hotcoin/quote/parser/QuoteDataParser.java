package hotcoin.quote.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hotcoin.quote.component.RedisComponent;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.vo.TradeAreaTickersVo;
import hotcoin.quote.model.wsquote.vo.TradeDepthVo;
import hotcoin.quote.model.wsquote.vo.TradeSimpleTickersVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: service_user
 * @Package: hotcoin.quote.parser
 * @ClassName: QuoteDataParser
 * @Author: hf
 * @Description: 解析kafka发送过来的行情数据
 * @Date: 2019/5/16 13:57
 * @Version: 1.0
 */
@Component
@Slf4j
public class QuoteDataParser {
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 解析深度数据
     * 加入cny价格
     *
     * @param tradeDepthVo
     * @param tradeId
     * @return
     */
    public void parserDepthData(Integer tradeId, TradeDepthVo tradeDepthVo) {
        try {
            log.info("query depth cnyPrice,tradeId is->{},start time->{}", tradeId, System.currentTimeMillis());
            String coinPair = redisComponent.getSymbol4CacheMap(tradeId);
            if (StringUtils.isEmpty(coinPair)) {
                return;
            }
            String cnyPrice = redisComponent.getCnyPrice(tradeId, new BigDecimal(tradeDepthVo.getLast()));
            tradeDepthVo.setCny(cnyPrice);
            log.info("query depth cnyPrice,tradeId is->{}cny is->{},,end time->{}", tradeId, cnyPrice, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("parse depthData fail,err is ->{},data is->{}", e, JSON.toJSONString(tradeDepthVo));
        }
    }

    /**
     * 解析深度信息
     *
     * @param jsonArray
     * @return
     */
    public List<List<String>> parserDepthData(JSONArray jsonArray) {
        List<List<String>> depthList = new ArrayList();
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                List item = (List) jsonArray.get(i);
                List<String> list = new ArrayList(2);
                list.add(0, item.get(0).toString());
                list.add(1, item.get(1).toString());
                depthList.add(list);
            }
        }
        return CollectionUtils.isEmpty(depthList) ? null : depthList;
    }

    public JSONArray parserTradeDetailData(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray(WsConstant.DATA);
        if (jsonArray == null || jsonArray.size() < 1) {
            return null;
        }
        try {
            return jsonArray;
        } catch (Exception e) {
            log.error("transfer trade detail 4 json 2 tradeDetailVo fail,err is ->{},json data is ->{}", e.getMessage(), jsonArray);
        }
        return jsonArray;
    }

    /**
     * 解析K线数据
     *
     * @param jsonObject
     * @return
     */
    public List<List<String>> parserTradeKlineData(JSONObject jsonObject) {
        List<List<String>> list = new ArrayList();
        JSONArray jsonArray = (JSONArray) jsonObject.get(WsConstant.DATA);
        if (jsonArray == null || jsonArray.size() < 1) {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            List<String> item = new ArrayList<>(6);
            List arr = (List) jsonArray.get(i);
            //[时间,开盘价,最高价,最低价,收盘价,成交量]
            item.add(arr.get(0).toString());
            //开
            item.add(arr.get(1).toString());
            //高
            item.add(arr.get(2).toString());
            //低
            item.add(arr.get(3).toString());
            //收
            item.add(arr.get(4).toString());
            //成交量
            item.add(arr.get(5).toString());
            list.add(item);
        }
        return CollectionUtils.isEmpty(list) ? null : list;
    }

    /**
     * 解析24小时涨跌幅信息
     *
     * @param jsonArray
     * @return
     */
    public List<TradeAreaTickersVo> parserTickerData(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        List<TradeAreaTickersVo> tickersVoList = new ArrayList<>();
        for (Object item : jsonArray) {
            tickersVoList.add((TradeAreaTickersVo) item);
        }
        return tickersVoList;
    }

    /**
     * 转换24小时涨跌幅信息
     *
     * @param list
     * @return
     */
    public List<TradeSimpleTickersVo> convertTickerData(List<TradeAreaTickersVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<TradeSimpleTickersVo> tickersVoList = new ArrayList<>();
        for (TradeAreaTickersVo item : list) {
            TradeSimpleTickersVo tradeSimpleTickersVo = new TradeSimpleTickersVo(item);
            tickersVoList.add(tradeSimpleTickersVo);
        }
        return tickersVoList;
    }

}
