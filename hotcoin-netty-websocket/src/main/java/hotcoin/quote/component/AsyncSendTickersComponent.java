package hotcoin.quote.component;

import com.alibaba.fastjson.JSON;
import hotcoin.quote.constant.TickersConstant;
import hotcoin.quote.model.wsquote.dto.TradeTickersDTO;
import hotcoin.quote.model.wsquote.vo.TradeAreaTickersVo;
import hotcoin.quote.model.wsquote.vo.TradeChangeTickersVo;
import hotcoin.quote.service.GroupSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.component
 * @ClassName: AsyncTickersComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/11/18 10:34
 * @Version: 1.0
 */
@Component
@Slf4j
public class AsyncSendTickersComponent {
    @Autowired
    private GroupSendService groupSendService;
    @Autowired
    private TickersComponent tickersComponent;

    public void asyncGroupSendHandle(List<TradeTickersDTO> receiveList) {
        // 成交额排行榜
        asyncAmountRankTasks();
        // 交易区排行榜
        asyncAreaTickersTask(receiveList);
        // 涨跌幅排行榜
        asyncChangeRateTask(receiveList);
    }

    /**
     * 异步成交额榜
     */
    private void asyncAmountRankTasks() {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            groupSendService.groupSendAmountRankData();
            return "";
        });
        executor.submit(futureTask);
    }

    /**
     * 异步分区信息分发
     *
     * @param receiveList
     */
    private void asyncAreaTickersTask(List<TradeTickersDTO> receiveList) {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            //获取交易区数据
            Map<String, List<TradeAreaTickersVo>> areaTickersMap = tickersComponent.getArea4PushData(receiveList);
            if (!MapUtils.isEmpty(areaTickersMap)) {
                groupSendService.groupSendTradeAreaData(areaTickersMap);
            }
            return "";
        });
        executor.submit(futureTask);
    }

    /**
     * 异步涨跌幅信息分发
     *
     * @param receiveList
     */
    private void asyncChangeRateTask(List<TradeTickersDTO> receiveList) {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            // 获取涨跌幅数据
            Map<String, List<TradeChangeTickersVo>> changeRateTickersMap = tickersComponent.getChangeRateCompare2Cache(receiveList, TickersConstant.TICKERS_SUB_NUM);
            if (!MapUtils.isEmpty(changeRateTickersMap)) {
                groupSendService.groupSendChangeRateData(changeRateTickersMap);
            }
            return "";
        });
        executor.submit(futureTask);
    }

    private ExecutorService executor = Executors.newFixedThreadPool(6);
}
