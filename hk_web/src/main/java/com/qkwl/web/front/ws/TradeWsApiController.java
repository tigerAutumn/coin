package com.qkwl.web.front.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeEnum;
import com.qkwl.common.dto.Enum.SystemTradeTypeNewEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.common.DeviceInfo;
import com.qkwl.common.dto.market.FPeriod;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.match.MathUtils;
import com.qkwl.common.redis.RedisConstant;
import com.qkwl.common.rpc.push.PushService;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.RespData;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.dto.GetOneTradeInfoReq;
import com.qkwl.web.dto.TradeDetailReq;
import com.qkwl.web.dto.TradeRankCoinReq;
import com.qkwl.web.elasticsearch.SystemTradeTypeRepository;
import com.qkwl.web.front.controller.base.JsonBaseController;
import com.qkwl.web.permission.annotation.PassToken;
import com.qkwl.web.utils.ApiUtils;
import com.qkwl.web.utils.CnyUtils;
import com.qkwl.web.utils.WebConstant;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName: hk_web
 * @Package: com.qkwl.web.front.ws
 * @ClassName: TradeWsApiController
 * @Author: hf
 * @Description:
 * @Date: 2019/11/11 14:55
 * @Version: 1.0
 */
@RestController
@Slf4j
public class TradeWsApiController extends JsonBaseController {
    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private PushService pushService;

    @Autowired
    @Qualifier("systemTradeTypeRepository")
    private SystemTradeTypeRepository systemTradeTypeRepository;

    @ApiOperation("获取本周明星币")
    @GetMapping("/v3/getStarCoinThisWeek")
    @PassToken
    public RespData<JSONObject> getStarCoinThisWeek() {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        List<TradeRankCoinReq> restList = new ArrayList<>(list.size());
        for (SystemTradeType systemTradeType : list) {
            if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            FPeriod fPeriod = redisHelper.getLastKline(systemTradeType.getId(), 10080);
            TradeRankCoinReq resp = convert2TradeInfoResp(systemTradeType);
            if (fPeriod == null || fPeriod.getFkai().compareTo(BigDecimal.ZERO) == 0) {
                resp.setChange("0");
            } else {
                BigDecimal change = fPeriod.getFshou().subtract(fPeriod.getFkai()).divide(fPeriod.getFkai(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                resp.setChange(MathUtils.toScaleNum(change, 2).toPlainString());
            }
            restList.add(resp);
        }
        List<TradeRankCoinReq> collect = restList.stream().sorted(Comparator.comparing(TradeRankCoinReq::getChange, Comparator.comparing(Double::valueOf)).reversed()).limit(10).collect(Collectors.toList());
        JSONObject json = new JSONObject();
        json.put("list", collect);
        json.put("topic", WebConstant.TRADE_STAR_COIN_STR);
        return RespData.ok(json);
    }


    @ApiOperation("获取成交额榜")
    @GetMapping("/v3/getTopTurnover")
    @PassToken
    public RespData<JSONObject> getTopTurnover() {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        List<TradeRankCoinReq> restList = new ArrayList<>(list.size());
        for (SystemTradeType systemTradeType : list) {
            if (!systemTradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            TradeRankCoinReq resp = convert2TradeInfoResp(systemTradeType);
            restList.add(resp);
        }
        List<TradeRankCoinReq> collect = restList.stream().sorted(Comparator.comparing(TradeRankCoinReq::getTotalAmount, Comparator.comparing(BigDecimal::new)).reversed()).limit(10).collect(Collectors.toList());
        JSONObject json = new JSONObject();
        json.put("list", collect);
        json.put("topic", WebConstant.TRADE_AMOUNT_RANK_STR);
        return RespData.ok(json);
    }

    @ApiOperation("获取涨幅或跌幅排行数据")
    @RequestMapping(value = "/v3/tradeInfoRankingList", method = {RequestMethod.GET, RequestMethod.POST})
    @PassToken
    public RespData tradeInfoRankingList(@RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortEnum", defaultValue = "1") int sortCode) {
        List<SystemTradeType> redisList = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        List<SystemTradeType> filterList = redisList.stream().filter(t -> t.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())).collect(Collectors.toList());
        JSONObject json = new JSONObject();
        List<JSONObject> arrayList = new ArrayList<JSONObject>(filterList.size());
        for (SystemTradeType systemTradeType : filterList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tradeId", systemTradeType.getId());
            jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
            jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
            jsonObject.put("sellShortName", systemTradeType.getSellShortName());
            jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
            jsonObject.put("imageUrl", systemTradeType.getSellWebLogo());
            jsonObject.put("type", systemTradeType.getType());
            // 最新价格
            TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
            if (tickerData == null) {
                jsonObject.put("last", "0");
                jsonObject.put("open", "0");
                jsonObject.put("volume", "0");
                jsonObject.put("buy", "0");
                jsonObject.put("sell", "0");
                jsonObject.put("change", "0");
            } else {
                jsonObject.put("last", tickerData.getLast().toPlainString());
                jsonObject.put("open", tickerData.getKai().toPlainString());
                jsonObject.put("volume", tickerData.getVol() == null ? "0" : tickerData.getVol().toPlainString());
                jsonObject.put("buy", tickerData.getBuy() == null ? "0" : tickerData.getLow().toPlainString());
                jsonObject.put("sell", tickerData.getSell() == null ? "0" : tickerData.getHigh().toPlainString());
                jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), 2).toPlainString());
            }
            jsonObject.put("digit", systemTradeType.getDigit());
            arrayList.add(jsonObject);
        }

        List<JSONObject> sortArrayList;
        if (1 == sortCode) {
            sortArrayList = arrayList.stream().sorted((s1, s2) -> compareByDigitThenBuyShortName(s1, s2, 1)).collect(Collectors.toList());
            json.put("topic", WebConstant.TRADE_TICKERS_UP_STR);
        } else {
            sortArrayList = arrayList.stream().sorted((s1, s2) -> compareByDigitThenBuyShortName(s1, s2, -1)).collect(Collectors.toList());
            json.put("topic", WebConstant.TRADE_TICKERS_DOWN_STR);
        }
        if (sortArrayList.size() > pageSize) {
            json.put("list", sortArrayList.subList(0, pageSize));
        } else {
            json.put("list", sortArrayList);
        }

        return RespData.ok(json);
    }

    @ApiOperation("根据交易区和交易类型获取交易对成交信息")
    @RequestMapping(value = "/v3/tradeInfo", method = {RequestMethod.GET, RequestMethod.POST})
    @PassToken
    public RespData<List<JSONObject>> tradeInfo(Integer id, String type) {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        List<JSONObject> restList = new ArrayList<>(list.size());
        for (SystemTradeType systemTradeType : list) {
            if ("level".equals(type) && !systemTradeType.isOpenLever()) {
                continue;
            }
            if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !systemTradeType.getType().equals(id)) {
                continue;
            }
            String[] digits = systemTradeType.getDigit().split("#");
            int coinDigit = Integer.valueOf(digits[1]);
            JSONObject jsonObject = convert2Json(convert2TradeInfoResp(systemTradeType));

            TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
            if (tickerData == null) {
                jsonObject.put("high", "0");
                jsonObject.put("low", "0");
                jsonObject.put("change", "0");
            } else {

                jsonObject.put("high", tickerData.getHigh() == null ? "0" : MathUtils.toScaleNum(tickerData.getHigh(), coinDigit).toPlainString());
                jsonObject.put("low", tickerData.getLow() == null ? "0" : MathUtils.toScaleNum(tickerData.getLow(), coinDigit).toPlainString());
                jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), 2).toPlainString());
            }
            restList.add(jsonObject);
        }
        return RespData.ok(restList);
    }

    @PassToken
    @ApiOperation("创新区列表")
    @RequestMapping(value = "/v3/innovationTradeInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public RespData<JSONObject> innovationTradeInfo() {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        JSONObject json = new JSONObject();
        json.put("topic", WebConstant.TRADE_TICKERS_INNOVATE_AREA_STR);
        List<TradeRankCoinReq> restList = new ArrayList<>(list.size());
        for (SystemTradeType systemTradeType : list) {
            if (systemTradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode()) || !systemTradeType.getType().equals(SystemTradeTypeNewEnum.INNOVATION_AREA.getCode())) {
                continue;
            }
            restList.add(convert2TradeInfoResp(systemTradeType));
        }
        json.put("list", restList);
        return RespData.ok(json);
    }

    /**
     * 主板交易区(WS版)
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PassToken
    @ApiOperation("主板交易区(app改版)")
    @RequestMapping(value = "/v3/market/areas", method = {RequestMethod.GET, RequestMethod.POST})
    public ReturnResult tradeMainMarkets() {
        try {
            String marketAreasOrder = redisHelper.getSystemArgs("market_areas_order");

            String[] marketAreasOrderArray = null;
            if (org.apache.commons.lang3.StringUtils.isBlank(marketAreasOrder)) {
                marketAreasOrderArray = Stream.of(SystemTradeTypeNewEnum.values())
                        .map(e -> String.valueOf(e.getCode())).toArray(String[]::new);
            } else {
                marketAreasOrderArray = marketAreasOrder.split(",");
            }

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < marketAreasOrderArray.length; i++) {
                SystemTradeTypeNewEnum trade =
                        SystemTradeTypeNewEnum.getEnum(Integer.valueOf(marketAreasOrderArray[i].trim()));
                if (trade == null) {
                    continue;
                }
                JSONObject jsonObject = null;
                switch (trade) {
                    case BTC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name", I18NUtils.getString("SystemTradeTypeNewEnum." + trade.getCode()));
                        jsonObject.put("topic", WebConstant.TRADE_TICKERS_BTC_AREA_STR);
                        jsonArray.add(jsonObject);
                        break;
                    case ETH:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name", I18NUtils.getString("SystemTradeTypeNewEnum." + trade.getCode()));
                        jsonObject.put("topic", WebConstant.TRADE_TICKERS_ETH_AREA_STR);
                        jsonArray.add(jsonObject);
                        break;
                    case GAVC:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name", I18NUtils.getString("SystemTradeTypeNewEnum." + trade.getCode()));
                        jsonObject.put("topic", WebConstant.TRADE_TICKERS_GAVC_AREA_STR);
                        jsonArray.add(jsonObject);
                        break;
                    case USDT:
                        jsonObject = new JSONObject();
                        jsonObject.put("code", trade.getCode());
                        jsonObject.put("name", I18NUtils.getString("SystemTradeTypeNewEnum." + trade.getCode()));
                        jsonObject.put("topic", WebConstant.TRADE_TICKERS_USDT_AREA_STR);
                        jsonArray.add(jsonObject);
                        break;
                    default:
                        break;
                }
            }
            return ReturnResult.SUCCESS(jsonArray);
        } catch (Exception e) {
            log.error("请求 /v3/market/areas 错误" + e);
            return ReturnResult.FAILUER(I18NUtils.getString("TradeApiController.24"));
        }

    }

    @ApiOperation("搜索所有交易区的所有币种")
    @RequestMapping(value = "/v3/getTradeTypeBySellName", method = {RequestMethod.GET, RequestMethod.POST})
    @PassToken
    public RespData getTradeTypeBySellName(String sellName) {
        try {
            BoolQueryBuilder query = null;
            if (StringUtils.isBlank(sellName)) {
                query = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status", SystemTradeStatusEnum.ABNORMAL.getCode()));
            } else {
                query = QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status", SystemTradeStatusEnum.ABNORMAL.getCode())).must(QueryBuilders.wildcardQuery("sellShortName", String.format("%s%s%s", "*", String.join("*", sellName.split("")), "*")));
            }
            Iterable<SystemTradeType> searchResult = systemTradeTypeRepository.search(query);
            JSONArray array = new JSONArray();
            Iterator<SystemTradeType> iterator = searchResult.iterator();
            while (iterator.hasNext()) {
                SystemTradeType systemTradeType = iterator.next();
                JSONObject jsonObject = new JSONObject();
                // 小数位处理(默认价格2位，数量4位)
                String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
                String[] digits = digit.split("#");
                int cnyDigit = Integer.valueOf(digits[0]);
                int coinDigit = Integer.valueOf(digits[1]);
                // 最新价格
                TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
                jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), 2).toPlainString());
                jsonObject.put("tradeId", systemTradeType.getId());
                jsonObject.put("imageUrl", systemTradeType.getSellWebLogo());
                jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
                jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
                jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
                jsonObject.put("sellShortName", systemTradeType.getSellShortName());
                jsonObject.put("volume", tickerData.getVol() == null ? "0" : tickerData.getVol().toPlainString());
                jsonObject.put("last", MathUtils.toScaleNum(tickerData.getLast(), cnyDigit).toPlainString());
                jsonObject.put("open", Optional.ofNullable(tickerData).map(TickerData::getKai).map(open -> MathUtils.toScaleNum(open, coinDigit)).map(BigDecimal::toPlainString).orElse("0"));
                jsonObject.put("digit", systemTradeType.getDigit());
                BigDecimal cny = getCnyPrice(systemTradeType, tickerData);
                jsonObject.put("cny", cny == null ? "0" : MathUtils.toScaleNum(cny, cnyDigit).toPlainString());
                array.add(jsonObject);
            }
            return RespData.ok(array);
        } catch (Exception e) {
            log.error("get all coinPairs fail->{}", e);
            return RespData.error(ErrorCodeEnum.DEFAULT, "network err,please try again");
        }
    }

    @ApiOperation("根据sellName和buyName获取一条交易对信息,默认获取交易区1中BTC/USDT的交易对信息,如果状态异常则随机取出一条正常的交易对信息")
    @GetMapping(value = "/v3/getOneTradeInfo")
    @PassToken
    public RespData<TradeRankCoinReq> getOneTradeInfo(@Valid GetOneTradeInfoReq req) {
        List<SystemTradeType> list = redisHelper.getAllTradeTypeList(WebConstant.BCAgentId);
        SystemTradeType systemTradeType = list.stream()
                .filter(e -> e.getSellShortName().equalsIgnoreCase(req.getSellShortName()))
                .filter(e -> e.getBuyShortName().equalsIgnoreCase(req.getBuyShortName()))
                .filter(e -> e.getType().equals(req.getType()))
                .filter(e -> e.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode()))
                .findFirst().orElse(list.stream().filter(e -> e.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())).findFirst().get());
        return RespData.ok(convert2TradeInfoResp(systemTradeType));
    }

    /**
     * 币币钱包交易区选择
     *
     * @param coinId
     * @return
     */
    @PassToken
    @ApiOperation("")
    @RequestMapping(value = "/v3/checkTradeArea", method = RequestMethod.POST)
    public RespData checkTradeArea(@RequestParam(required = true) Integer coinId) {
        List<SystemTradeType> tradeTypeShareList = redisHelper.getTradeTypeShare(0);
        JSONArray jsonArray = new JSONArray();
        for (SystemTradeType systemTradeType : tradeTypeShareList) {
            if (systemTradeType.getSellCoinId().equals(coinId)) {
                JSONObject jsonObject = new JSONObject();

                // 小数位处理(默认价格2位，数量4位)
                String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
                String[] digits = digit.split("#");
                int cnyDigit = Integer.valueOf(digits[0]);
                int coinDigit = Integer.valueOf(digits[1]);
                
                jsonObject.put("tradeId", systemTradeType.getId());
                jsonObject.put("imageUrl", systemTradeType.getSellWebLogo());
                jsonObject.put("digit", systemTradeType.getDigit());
                jsonObject.put("buySymbol", systemTradeType.getBuySymbol());
                jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
                jsonObject.put("sellSymbol", systemTradeType.getSellSymbol());
                jsonObject.put("sellShortName", systemTradeType.getSellShortName());

                if (ApiUtils.isTradeOpen(systemTradeType)) {
                    jsonObject.put("isOpen", "1");
                } else {
                    jsonObject.put("isOpen", "0");
                }
                
                // 最新价格
                TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
                if (tickerData == null) {
                    jsonObject.put("last", "0");
                    jsonObject.put("open", "0");
                    jsonObject.put("cny", "0");
                    jsonObject.put("volume", "0");
                    jsonObject.put("low", "0");
                    jsonObject.put("high", "0");
                    jsonObject.put("change", "0");
                    jsonObject.put("sell", "0");
                    jsonObject.put("buy", "0");
                } else {
                    jsonObject.put("last", tickerData.getLast().toPlainString());
                    jsonObject.put("open", tickerData.getKai().toPlainString());
                    jsonObject.put("low", tickerData.getLow() == null ? "0" : MathUtils.toScaleNum(tickerData.getLow(), cnyDigit).toPlainString());
                    jsonObject.put("volume", tickerData.getVol() == null ? "0" : MathUtils.toScaleNum(tickerData.getVol(), coinDigit).toPlainString());
                    jsonObject.put("sell", tickerData.getSell() == null ? "0" : MathUtils.toScaleNum(tickerData.getSell(), cnyDigit).toPlainString());
                    jsonObject.put("buy", tickerData.getBuy() == null ? "0" : MathUtils.toScaleNum(tickerData.getBuy(), coinDigit).toPlainString());
                    jsonObject.put("high", tickerData.getHigh() == null ? "0" : MathUtils.toScaleNum(tickerData.getHigh(), cnyDigit).toPlainString());
                    jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), cnyDigit).toPlainString());
                    BigDecimal cny = getCnyPrice(systemTradeType, tickerData);
                    jsonObject.put("cny", cny == null ? "0" : cny.toPlainString());
                }
                
                jsonArray.add(jsonObject);
            }
        }
        return RespData.ok(jsonArray);
    }


    // 实时交易
    @ApiOperation("")
    @RequestMapping(value = "/v3/realTimeTrade", method = {RequestMethod.GET, RequestMethod.POST})
    @PassToken
    public RespData MarketJson(@RequestParam(required = false, defaultValue = "0") Integer tradeId, @RequestParam(required = false, defaultValue = "100") Integer successCount) {
        if (tradeId == 0 || successCount < 0) {
            return RespData.error(ErrorCodeEnum.DEFAULT, "param is error");
        }
        if (successCount > 100) {
            successCount = 100;
        }
        // 获取虚拟币
        SystemTradeType tradeType = redisHelper.getTradeType(tradeId, WebConstant.BCAgentId);
        if (tradeType == null || tradeType.getStatus().equals(SystemTradeStatusEnum.ABNORMAL.getCode())) {
            return RespData.error(ErrorCodeEnum.DEFAULT, "param is error");
        }
        String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
        String[] digits = digit.split("#");
        int cnyDigit = Integer.valueOf(digits[0]);
        int coinDigit = Integer.valueOf(digits[1]);

        String arrayStr = redisHelper.getRedisData(RedisConstant.SUCCESSENTRUST_KEY + tradeId);
        JSONArray successArray = new JSONArray();
        if (StringUtils.isNotBlank(arrayStr)) {
            successArray = JSONArray.parseArray(arrayStr);
        }
        List<TradeDetailReq> data = new ArrayList<>();
        for (Object successObj : successArray) {
            JSONArray array = JSON.parseArray(successObj.toString());
            TradeDetailReq item = new TradeDetailReq();
            BigDecimal price = new BigDecimal(array.get(0).toString());
            BigDecimal count = new BigDecimal(array.get(1).toString());
            String direction = array.get(3).toString();
            item.setTradeId(tradeId);
            item.setAmount(MathUtils.toScaleNum(count, coinDigit).toPlainString());
            item.setPrice(MathUtils.toScaleNum(price, cnyDigit).toPlainString());
            item.setTs(array.get(5).toString());
            // 0 买 1卖
            item.setDirection(direction.equals("0") ? "buy" : "sell");
            data.add(item);
        }
        if (!CollectionUtils.isEmpty(data) && data.size() > successCount) {
            return RespData.ok(data.subList(0, successCount));
        }
        return RespData.ok(data);
    }

    /**
     * 获取深度数据
     *
     * @param tradeId
     * @return
     * @throws Exception
     */
    @PassToken
    @ApiOperation("")
    @RequestMapping(value = "/v3/fullDepth", method = {RequestMethod.GET, RequestMethod.POST})
    public RespData fullDepth(@RequestParam(required = false, defaultValue = "0") int tradeId) {
        JSONObject jsonObject = new JSONObject();
        if (tradeId == 0) {
            return RespData.error(ErrorCodeEnum.DEFAULT, I18NUtils.getString("TradeApiV2Controller.176"));
        }

        DeviceInfo deviceInfo = getDeviceInfo();
        SystemTradeType tradeType = pushService.getSystemTradeType(tradeId);
        if (null == tradeType || !tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
            return RespData.error(ErrorCodeEnum.DEFAULT, I18NUtils.getString("TradeApiV2Controller.177"));
        }

        // 小数位处理(默认价格2位，数量4位)
        String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
        String[] digits = digit.split("#");
        int cnyDigit = Integer.valueOf(digits[0]);
        int coinDigit = Integer.valueOf(digits[1]);
        JSONArray sellDepth = pushService.getSellDepthJson(tradeId);
        JSONArray buyDepth = pushService.getBuyDepthJson(tradeId);

        List<List<String>> buyDepthList = parseAsksOrBids(buyDepth, cnyDigit, coinDigit);
        List<List<String>> sellDepthList = parseAsksOrBids(sellDepth, cnyDigit, coinDigit);

        // web端需要反转
        if (deviceInfo.getPlatform() == 1) {
            sort(sellDepthList);
        }
        TickerData tickerData = redisHelper.getTickerData(tradeId);
        if (tickerData == null) {
            jsonObject.put("last", "0");
            jsonObject.put("open", "0");
            jsonObject.put("cny", "0");
        } else {
            jsonObject.put("last", MathUtils.toScaleNum(tickerData.getLast(), cnyDigit).toPlainString());
            jsonObject.put("open", MathUtils.toScaleNum(tickerData.getKai(), cnyDigit).toPlainString());
            BigDecimal cny = getCnyPrice(tradeType, tickerData);
            jsonObject.put("cny", cny == null ? "0" : cny.toPlainString());
        }

        jsonObject.put("bids", buyDepthList);
        jsonObject.put("asks", sellDepthList);
        return RespData.ok(jsonObject);
    }

    // web自选币种
    @ApiOperation("")
    @RequestMapping(value = "/v3/collect/list", method = {RequestMethod.GET, RequestMethod.POST})
    public RespData MarketJsons() {
        FUser user = getUser();
        if (user == null) {
            return RespData.error(ErrorCodeEnum.NOT_LOGGED_IN, I18NUtils.getString("com.public.error.10001"));
        }
        if (StringUtils.isEmpty(user.getfFavoriteTradeList())) {
            return RespData.ok();
        }
        JSONArray parseArray = JSON.parseArray(user.getfFavoriteTradeList());
        List<SystemTradeType> tradeTypeSort = redisHelper.getTradeTypeShareByTradeIds(parseArray, WebConstant.BCAgentId);
        JSONArray array = new JSONArray();
        for (SystemTradeType systemTradeType : tradeTypeSort) {
            JSONObject jsonObject = new JSONObject();


            // 小数位处理(默认价格2位，数量4位)
            String digit = StringUtils.isEmpty(systemTradeType.getDigit()) ? "2#4" : systemTradeType.getDigit();
            String[] digits = digit.split("#");
            int cnyDigit = Integer.valueOf(digits[0]);
            int coinDigit = Integer.valueOf(digits[1]);

            jsonObject.put("tradeId", systemTradeType.getId());
            jsonObject.put("imageUrl", systemTradeType.getSellWebLogo());
            jsonObject.put("digit", systemTradeType.getDigit());
            jsonObject.put("buySymbol", systemTradeType.getBuyShortName());
            jsonObject.put("buyShortName", systemTradeType.getBuyShortName());
            jsonObject.put("sellSymbol", systemTradeType.getSellShortName());
            jsonObject.put("sellShortName", systemTradeType.getSellShortName());
            if (ApiUtils.isTradeOpen(systemTradeType)) {
                jsonObject.put("isOpen", "1");
            } else {
                jsonObject.put("isOpen", "0");
            }
            // 最新价格
            TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
            if (tickerData == null) {
                jsonObject.put("last", "0");
                jsonObject.put("open", "0");
                jsonObject.put("cny", "0");
                jsonObject.put("volume", "0");
                jsonObject.put("low", "0");
                jsonObject.put("high", "0");
                jsonObject.put("change", "0");
                jsonObject.put("sell", "0");
                jsonObject.put("buy", "0");
            } else {
                jsonObject.put("last", tickerData.getLast().toPlainString());
                jsonObject.put("open", tickerData.getKai().toPlainString());
                jsonObject.put("low", tickerData.getLow() == null ? "0" : MathUtils.toScaleNum(tickerData.getLow(), cnyDigit).toPlainString());
                jsonObject.put("volume", tickerData.getVol() == null ? "0" : MathUtils.toScaleNum(tickerData.getVol(), coinDigit).toPlainString());
                jsonObject.put("sell", tickerData.getSell() == null ? "0" : MathUtils.toScaleNum(tickerData.getSell(), cnyDigit).toPlainString());
                jsonObject.put("buy", tickerData.getBuy() == null ? "0" : MathUtils.toScaleNum(tickerData.getBuy(), coinDigit).toPlainString());
                jsonObject.put("high", tickerData.getHigh() == null ? "0" : MathUtils.toScaleNum(tickerData.getHigh(), cnyDigit).toPlainString());
                jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), cnyDigit).toPlainString());
                BigDecimal cny = getCnyPrice(systemTradeType, tickerData);
                jsonObject.put("cny", cny == null ? "0" : cny.toPlainString());
            }
            array.add(jsonObject);
        }
        return RespData.ok(array);
    }

    // 买卖盘，最新成交
    @ApiOperation("")
    @RequestMapping(value = "/v3/real/markets", method = {RequestMethod.GET, RequestMethod.POST})
    @PassToken
    @ResponseBody
    public RespData MarketJsons(
            @RequestParam(required = false, defaultValue = "0") String symbol) {
        if ("0".equals(symbol)) {
            return RespData.error(ErrorCodeEnum.PARAM_ERROR, "");
        }
        String[] symbolArray = symbol.split(",");
        JSONArray jsonArray = new JSONArray();
        for (String tradeId : symbolArray) {
            //获取虚拟币
            SystemTradeType tradeType = redisHelper.getTradeType(Integer.parseInt(tradeId), WebConstant.BCAgentId);
            if (tradeType == null || !tradeType.getStatus().equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }

            // 小数位处理(默认价格2位，数量4位)
            String digit = StringUtils.isEmpty(tradeType.getDigit()) ? "2#4" : tradeType.getDigit();
            String[] digits = digit.split("#");
            int cnyDigit = Integer.valueOf(digits[0]);
            int coinDigit = Integer.valueOf(digits[1]);


            JSONObject jsonObject = new JSONObject();
            // 最新价格
            TickerData tickerData = pushService.getTickerData(tradeType.getId());
            if (tickerData == null) {
                jsonObject.put("last", "0");
                jsonObject.put("open", "0");
                jsonObject.put("volume", "0");
                jsonObject.put("low", "0");
                jsonObject.put("high", "0");
                jsonObject.put("buy", "0");
                jsonObject.put("sell", "0");
                jsonObject.put("cny", "0");
                jsonObject.put("change", "0");
            } else {
                jsonObject.put("last", tickerData.getLast() == null ? "0" : MathUtils.toScaleNum(tickerData.getLast(), cnyDigit).toPlainString());
                jsonObject.put("open", tickerData.getKai() == null ? "0" : MathUtils.toScaleNum(tickerData.getKai(), cnyDigit).toPlainString());
                jsonObject.put("volume", tickerData.getVol() == null ? "0" : MathUtils.toScaleNum(tickerData.getVol(), coinDigit).toPlainString());
                jsonObject.put("low", tickerData.getLow() == null ? "0" : MathUtils.toScaleNum(tickerData.getLow(), cnyDigit).toPlainString());
                jsonObject.put("high", tickerData.getHigh() == null ? "0" : MathUtils.toScaleNum(tickerData.getHigh(), cnyDigit).toPlainString());
                jsonObject.put("sell", tickerData.getSell() == null ? "0" : MathUtils.toScaleNum(tickerData.getSell(), cnyDigit).toPlainString());
                jsonObject.put("buy", tickerData.getBuy() == null ? "0" : MathUtils.toScaleNum(tickerData.getBuy(), cnyDigit).toPlainString());
                jsonObject.put("change", tickerData.getChg() == null ? "0" : MathUtils.toScaleNum(tickerData.getChg(), cnyDigit).toPlainString());
                BigDecimal cny = getCnyPrice(tradeType, tickerData);
                jsonObject.put("cny", cny == null ? "0" : cny.toPlainString());
            }
            jsonObject.put("sellSymbol", tradeType.getSellShortName());
            jsonObject.put("sellShortName", tradeType.getSellShortName());
            jsonObject.put("buyShortName", tradeType.getBuyShortName());
            jsonObject.put("buySymbol", tradeType.getBuyShortName());
            if (ApiUtils.isTradeOpen(tradeType)) {
                jsonObject.put("isOpen", "1");
            } else {
                jsonObject.put("isOpen", "0");
            }

            jsonArray.add(jsonObject);
        }
        return RespData.ok(jsonArray);
    }

    /**
     * 获取人民币价格
     *
     * @param systemTradeType
     * @param tickerData
     * @return
     */
    private BigDecimal getCnyPrice(SystemTradeType systemTradeType, TickerData tickerData) {
        if (tickerData == null) {
            return BigDecimal.ZERO;
        }

        // BTC交易区
        if (systemTradeType.getType().equals(1)) {
            return MathUtils.toScaleNum(tickerData.getLast(), 2);
        } else if (systemTradeType.getType().equals(2)) {
            return getCny(8, tickerData.getLast());
        }
        // ETH交易区
        else if (systemTradeType.getType().equals(3)) {
            return getCny(11, tickerData.getLast());
        } else if (systemTradeType.getType().equals(4)) {
            return getCny(57, tickerData.getLast());
        } else if (systemTradeType.getType().equals(5)) {
            return getCnyByCoinId(systemTradeType.getBuyCoinId(), systemTradeType.getSellCoinId(), tickerData.getLast());
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * @param list
     * @param cnyDigit
     * @param coinDigit
     * @return
     */
    private List<List<String>> parseAsksOrBids(JSONArray list, int cnyDigit, int coinDigit) {
        List<List<String>> resultList = new ArrayList<>();
        for (Object object : list) {
            JSONArray array = JSON.parseArray(object.toString());
            if (array == null || array.size() < 1) {
                continue;
            }
            List<String> item = new ArrayList<>();
            // 单价
            BigDecimal price = new BigDecimal(array.get(0).toString());
            // 数量
            BigDecimal count = new BigDecimal(array.get(1).toString());

            item.add(MathUtils.toScaleNum(price, cnyDigit).toPlainString());
            item.add(MathUtils.toScaleNum(count, coinDigit).toPlainString());
            resultList.add(item);
        }
        if (!CollectionUtils.isEmpty(resultList) && resultList.size() > 50) {
            return resultList.subList(0, 50);
        } else {
            return resultList;
        }
    }

    private void sort(List<List<String>> list) {
        Collections.sort(list, (o1, o2) -> {
            JSONArray o1Array = JSON.parseArray(o1.toString());
            JSONArray o2Array = JSON.parseArray(o2.toString());
            // o1单价
            BigDecimal o1Price = new BigDecimal(o1Array.get(0).toString());
            // o2单价
            BigDecimal o2Price = new BigDecimal(o2Array.get(0).toString());

            if (o1Price.compareTo(o2Price) < 0) {
                return 1;
            } else if (o1Price.compareTo(o2Price) == 0) {
                return 0;
            } else {
                return -1;
            }
        });
    }

    private static int compareByDigitThenBuyShortName(JSONObject lst, JSONObject rst, int sortCode) {
        BigDecimal lrose = new BigDecimal(lst.get("change").toString());
        BigDecimal rrose = new BigDecimal(rst.get("change").toString());
        if (1 == sortCode) {
            if (rrose.compareTo(lrose) == 0) {
                return rst.get("buyShortName").toString().compareTo(lst.get("buyShortName").toString());
            } else {
                return rrose.compareTo(lrose);
            }
        } else {
            if (lrose.compareTo(rrose) == 0) {
                return lst.get("buyShortName").toString().compareTo(rst.get("buyShortName").toString());
            } else {
                return lrose.compareTo(rrose);
            }
        }
    }

    /**
     * 新需求需要将多返回最高价和最低价字段,所以进行转换
     *
     * @param tradeRankCoinReq
     * @return
     */
    private JSONObject convert2Json(TradeRankCoinReq tradeRankCoinReq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeId", tradeRankCoinReq.getTradeId());
        jsonObject.put("cny", tradeRankCoinReq.getCny());
        jsonObject.put("buyShortName", tradeRankCoinReq.getBuyShortName());
        jsonObject.put("buySymbol", tradeRankCoinReq.getBuySymbol());
        jsonObject.put("sellShortName", tradeRankCoinReq.getSellShortName());
        jsonObject.put("sellSymbol", tradeRankCoinReq.getSellSymbol());
        jsonObject.put("change", tradeRankCoinReq.getChange());
        jsonObject.put("imageUrl", tradeRankCoinReq.getImageUrl());
        jsonObject.put("digit", tradeRankCoinReq.getDigit());
        jsonObject.put("isOpen", tradeRankCoinReq.getIsOpen());
        jsonObject.put("last", tradeRankCoinReq.getLast());
        jsonObject.put("totalAmount", tradeRankCoinReq.getTotalAmount());
        jsonObject.put("volume", tradeRankCoinReq.getVolume());
        return jsonObject;
    }


    /**
     * 数据转换
     *
     * @param systemTradeType
     * @return
     */
    private TradeRankCoinReq convert2TradeInfoResp(SystemTradeType systemTradeType) {
        TradeRankCoinReq tradeInfoResp = new TradeRankCoinReq();
        tradeInfoResp.setTradeId(systemTradeType.getId());
        tradeInfoResp.setBuySymbol(systemTradeType.getBuyShortName());
        tradeInfoResp.setSellSymbol(systemTradeType.getSellShortName());
        tradeInfoResp.setImageUrl(systemTradeType.getSellWebLogo());
        tradeInfoResp.setSellShortName(systemTradeType.getSellShortName());
        tradeInfoResp.setBuyShortName(systemTradeType.getBuyShortName());
        tradeInfoResp.setDigit(systemTradeType.getDigit());
        if (!ApiUtils.isTradeOpen(systemTradeType)) {
            tradeInfoResp.setIsOpen("0");
        } else {
            tradeInfoResp.setIsOpen("1");
        }
        // 最新价格
        TickerData tickerData = pushService.getTickerData(systemTradeType.getId());
        BigDecimal last = tickerData.getLast() == null ? BigDecimal.ZERO : tickerData.getLast();
        tradeInfoResp.setLast(last.toPlainString());
        if (systemTradeType.getType().equals(SystemTradeTypeEnum.GAVC.getCode())) {
            tradeInfoResp.setCny(MathUtils.toScaleNum(last, 2).toPlainString());
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.BTC.getCode())) {
            tradeInfoResp.setCny(getCny(8, last).toPlainString());
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.ETH.getCode())) {
            tradeInfoResp.setCny(getCny(11, last).toPlainString());
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.USDT.getCode())) {
            tradeInfoResp.setCny(getCny(57, last).toPlainString());
        } else if (systemTradeType.getType().equals(SystemTradeTypeEnum.INNOVATION_AREA.getCode())) {
            tradeInfoResp.setCny(getCnyByCoinId(systemTradeType.getBuyCoinId(), systemTradeType.getSellCoinId(), last).toPlainString());
        }
        BigDecimal vol = tickerData.getVol() == null ? BigDecimal.ZERO : tickerData.getVol();
        tradeInfoResp.setVolume(vol.toPlainString());
        tradeInfoResp.setTotalAmount(MathUtils.mul(new BigDecimal(tradeInfoResp.getCny()), vol).toPlainString());
        return tradeInfoResp;
    }

    public BigDecimal getCny(int tradeId, BigDecimal p_new) {
        // 取BTC/GSET交易对价格计算
        BigDecimal cny = redisHelper.getLastPrice(tradeId);

        // 当前交易对最新价格
        BigDecimal money = MathUtils.mul(p_new, cny);
        BigDecimal newMoney = MathUtils.toScaleNum(money, 2);
        if (newMoney.compareTo(BigDecimal.ZERO) <= 0) {
            return CnyUtils.validateCny(money);
        }
        return newMoney;
    }

}
