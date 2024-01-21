package hotcoin.model.dto;

import hotcoin.model.vo.KlineHistoryVo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model.dto
 * @ClassName: KlineHistoryDto
 * @Author: hf
 * @Description: 返回客户端对象
 * @Date: 2019/9/20 16:56
 * @Version: 1.0
 */
@Getter
@Setter
public class KlineHistoryDto {
    /**
     * //[时间,开盘价,最高价,最低价,收盘价,成交量]
     */
    private List<List<String>> data;
    /**
     * 粒度 1m,5m,15m,1h,1d,1w,1mo
     */
    private String period;
    /**
     * 交易id
     */
    private Integer tradeId;

    public KlineHistoryDto() {
    }

    public KlineHistoryDto(KlineHistoryVo klineHistoryVo) {
        this.data = new ArrayList<>();
        this.tradeId = klineHistoryVo.getTradeId();
        this.period = klineHistoryVo.getPeriod();
    }
    public KlineHistoryDto(Integer tradeId,String period) {
        this.tradeId = tradeId;
        this.period = period;
    }
}
