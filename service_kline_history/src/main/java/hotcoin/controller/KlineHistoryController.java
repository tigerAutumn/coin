package hotcoin.controller;

import hotcoin.component.RedisComponent;
import hotcoin.model.constant.KlineHistoryConstant;
import hotcoin.model.dto.KlineQueryDto;
import hotcoin.model.em.OptTypeEnum;
import hotcoin.model.resp.CodeMsg;
import hotcoin.model.resp.Result;
import hotcoin.service.KlineHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.controller
 * @ClassName: KlineHistoryController
 * @Author: hf
 * @Description:
 * @Date: 2019/9/5 17:46
 * @Version: 1.0
 */
@RestController
public class KlineHistoryController {
    @Autowired
    private KlineHistoryService klineHistoryService;
    @Autowired
    private RedisComponent redisComponent;

    @CrossOrigin
    @RequestMapping("/kline/history")
    public Result getKlineHistory(KlineQueryDto klineQueryDto) {
        List<List<String>> result = klineHistoryService.query(klineQueryDto, OptTypeEnum.KEEPHISTORY);
        return Result.httpSuccess(result);
    }

    /**
     * 健康检查
     *
     * @return
     */
    @GetMapping("/health")
    public Result<Void> health() {
        return new Result<>();
    }

    /**
     * 提供给openApi使用
     *
     * @param step   秒,如传入60,表示获取一分钟数据
     * @param symbol btc_gavc
     * @param limit  默认300,可不传,最多不超过1000
     * @return
     */
    @CrossOrigin
    @RequestMapping("/v1/ticker")
    public Result getKlineForApi(@RequestParam(required = false, defaultValue = "0") int step,
                                 @RequestParam String symbol, @RequestParam(required = false, defaultValue = "300") Integer limit) {
        if (step == 0) {
            return Result.error(CodeMsg.REQUEST_ERROR);
        }
        String[] symbols = symbol.split("_");
        if (symbols == null || symbols.length != KlineHistoryConstant.KLINE_HIS_SECOND) {
            return Result.error(CodeMsg.REQUEST_ERROR);
        }
        Integer tradeId = redisComponent.getTradeId4Symbol(symbol.toUpperCase());
        if (tradeId == null) {
            return Result.error(CodeMsg.REQUEST_ERROR);
        }
        String period = stepConvert2Period(step);
        if (StringUtils.isEmpty(period)) {
            return Result.error(CodeMsg.REQUEST_ERROR);
        }
        if (limit > KlineHistoryConstant.KLINE_HIS_MAX_LIMIT) {
            limit = KlineHistoryConstant.KLINE_HIS_DEFAULT_LIMIT;
        }
        KlineQueryDto klineQueryDto = new KlineQueryDto(period, 1, limit, tradeId);
        List<List<String>> result = klineHistoryService.query(klineQueryDto, OptTypeEnum.REMOVEREPEAT);
        return Result.httpSuccess(result);
    }

    /**
     * step转换为period
     *
     * @param step
     * @return
     */
    private String stepConvert2Period(int step) {
        switch (step) {
            case 60:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_1M;
            case 300:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_5M;
            case 900:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_15M;
            case 1800:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_30M;
            case 3600:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_1H;
            case 86400:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_1D;
            case 604800:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_1W;
            case 2592000:
                return KlineHistoryConstant.KLINE_HIS_PERIOD_1MO;
            default:
                return "";
        }
    }

    /**
     * 组装参数
     *
     * @return
     */
    private KlineQueryDto assembleParams() {
        return null;
    }
}
