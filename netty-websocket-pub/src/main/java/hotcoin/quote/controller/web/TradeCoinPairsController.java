package hotcoin.quote.controller.web;

import com.qkwl.common.dto.Enum.SystemTradeStatusEnum;
import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.framework.redis.RedisHelper;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.CodeMsg;
import hotcoin.quote.resp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.controller
 * @ClassName: TradeCoinPairsController
 * @Author: hf
 * @Description: 查询所有交易对
 * @Date: 2019/6/17 15:16
 * @Version: 1.0
 */
@RestController
public class TradeCoinPairsController {
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 获取所有币对
     *
     * @return
     */
    @RequestMapping("/common/symbols")
    public Result getCoinPairs() {
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        if (CollectionUtils.isEmpty(coinsList)) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        List<Map<String, String>> list = new ArrayList<>(16);
        for (SystemTradeType tradeTypeTemp : coinsList) {
            if (null == tradeTypeTemp) {
                continue;
            }
            Integer status = tradeTypeTemp.getStatus();
            if (!status.equals(SystemTradeStatusEnum.NORMAL.getCode())) {
                continue;
            }
            Map<String, String> map = new HashMap<>(3);
            String buyShortName = tradeTypeTemp.getBuyShortName().toLowerCase();
            String sellShortName = tradeTypeTemp.getSellShortName().toLowerCase();
            map.put(WsConstant.BASE_CURRENCY, sellShortName);
            map.put(WsConstant.QUOTE_CURRENCY, buyShortName);
            map.put(WsConstant.SYMBOL, (sellShortName + WsConstant.SLASH + buyShortName));
            list.add(map);
        }
        return Result.httpSuccess(list);
    }
}
