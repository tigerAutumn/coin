package hotcoin.quote.controller.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.redis.RedisConstant;
import hotcoin.quote.component.RedisComponent;
import hotcoin.quote.constant.KlineConstant;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.resp.CodeMsg;
import hotcoin.quote.utils.MatchUtil;
import hotcoin.quote.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: TradeKlineHistoryController
 * @Author: hf
 * @Description: 获取最近kline历史数据
 * @Date: 2019/6/17 15:17
 * @Version: 1.0
 */
@RestController
@Slf4j
public class TradeKlineHistoryController {
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private RedisComponent redisComponent;

    /**
     * 获取kline数据
     * symbol:btc_usdt
     */
    @RequestMapping("/common/kline")
    public Result selectKline(@RequestParam String symbol, @RequestParam String period,
                              @RequestParam(required = false, defaultValue = "300") int limit) {

        Integer tradeId = redisComponent.getTradeId4Symbol(symbol);
        if (tradeId == null || !Arrays.asList(KlineConstant.KLINE_PERIOD_ARR).contains(period)) {
            return Result.error(CodeMsg.REQUEST_PARAM_ERROR);
        }
        String newPeriod = convertPeriod(period);
        String coinsObj = redisHelper.get(RedisConstant.KLINE_KEY + tradeId + WsConstant.SLASH + newPeriod);
        JSONObject obj = JSON.parseObject(coinsObj);
        JSONArray tradeTypeList = obj.getJSONArray(WsConstant.EXT_OBJECT);
        List<List> list = new ArrayList<>(16);
        for (Object tradeTypeTemp : tradeTypeList) {
            if (null == tradeTypeTemp) {
                continue;
            }
            JSONArray jsonArray = (JSONArray) tradeTypeTemp;
            // [时间,开盘价,最高价,最低价,收盘价,成交量]
            List<String> temp = feedData(jsonArray);
            list.add(temp);
        }
        if (!CollectionUtils.isEmpty(list)) {
            if (limit > 0 && limit < WsConstant.KLINE_DEFAULT_LIMIT) {
                list = list.subList(0, limit);
            }
        }
        String ch = String.format(WsConstant.TRADE_KLINE_STR, symbol, period);
        return Result.success(list, ch);
    }

    private String convertPeriod(String period) {
        String strNum = MatchUtil.getNumbers(period);
        if (StringUtils.isEmpty(strNum)) {
            return "";
        }
        int num = Integer.valueOf(strNum);
        if (period.contains(WsConstant.MIN_KEY)) {
            return strNum;
        } else if (period.contains(WsConstant.HOUR_KER)) {
            return String.valueOf(num * 60);
        } else if (period.contains(WsConstant.DAY_KEY)) {
            return String.valueOf(num * 60 * 24);
        } else if (period.contains(WsConstant.WEEK_KEY)) {
            return String.valueOf(num * 60 * 24 * 7);
        } else {
            return String.valueOf(num * 60 * 24 * 30);
        }
    }

    /**
     * 数据收集
     *
     * @param jsonArray
     * @return
     */
    public List<String> feedData(JSONArray jsonArray) {
        List<String> temp = new ArrayList<>(6);
        String ts = String.valueOf(jsonArray.get(0));
        String open = String.valueOf(jsonArray.get(1));
        String high = String.valueOf(jsonArray.get(2));
        String low = String.valueOf(jsonArray.get(3));
        String close = String.valueOf(jsonArray.get(4));
        String vol = String.valueOf(jsonArray.get(5));
        temp.add(ts);
        temp.add(open);
        temp.add(high);
        temp.add(low);
        temp.add(close);
        temp.add(vol);
        return temp;
    }
}

