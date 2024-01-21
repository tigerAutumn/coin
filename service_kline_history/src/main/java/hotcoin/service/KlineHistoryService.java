package hotcoin.service;

import hotcoin.model.dto.KlineQueryDto;
import hotcoin.model.em.OptTypeEnum;
import hotcoin.model.vo.KlineHistoryVo;

import java.util.List;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.service
 * @ClassName: KlineHistoryService
 * @Author: hf
 * @Description:
 * @Date: 2019/9/5 17:53
 * @Version: 1.0
 */
public interface KlineHistoryService {
    /**
     * 查询kline历史数据
     *
     * @param klineQueryDto
     * @return
     */
    List<List<String>> query(KlineQueryDto klineQueryDto, OptTypeEnum optTypeEnum);

    KlineHistoryVo findMin(Integer tradeId, String period);

}
