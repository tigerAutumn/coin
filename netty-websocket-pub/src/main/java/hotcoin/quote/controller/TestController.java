package hotcoin.quote.controller;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.dto.market.TickerData;
import com.qkwl.common.framework.redis.RedisHelper;
import hotcoin.quote.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: service_user
 * @Package: hotcoin.quote.controller
 * @ClassName: TestKafkaProducerController
 * @Author: hf
 * @Description:
 * @Date: 2019/5/15 14:40
 * @Version: 1.0
 */
@RestController
@Slf4j
public class TestController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping("/kafka/send")
    public String send(String symbol) {
        ThreadLocal<Map> local = new ThreadLocal<>();
        local.get();
        // 每个线程中有一个静态内部类ThreadLocalMap,该map以Entry的方式存储,存储key为Thread对象,值为存储对象.
        //ThreadLocalMap 与Entry之间为弱引用
        local.set(new HashMap(1));
        Integer tradeId = redisComponent.getTradeId4Symbol(symbol);
        TickerData data = redisComponent.getTickerDataByTradeId(tradeId);
        SystemTradeType systemTradeType = redisComponent.getCoinPairInfo(tradeId);
        String result = String.format("symbol is %s and tradeId is %d and query last price from redis result is %s,query imageUrl result is %s", symbol, tradeId, data.getLast(),
                systemTradeType.getSellWebLogo());
        return result;
    }

    @RequestMapping("/kafka/test")
    public List<SystemTradeType> find() {
        List<SystemTradeType> coinsList = redisHelper.getAllTradeTypeList(0);
        return coinsList;
    }
}
