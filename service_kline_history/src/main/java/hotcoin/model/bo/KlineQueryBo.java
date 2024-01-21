package hotcoin.model.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model.bo
 * @ClassName: KlineQueryBo
 * @Author: hf
 * @Description:
 * @Date: 2019/9/5 17:58
 * @Version: 1.0
 */
@Setter
@Getter
public class KlineQueryBo {
    private long startTime;
    private long endTime;
    private Integer sort;
    private int tradeId;
    private String period;

    public KlineQueryBo() {
    }

    public KlineQueryBo(int tradeId, String period) {
        this.tradeId = tradeId;
        this.period = period;
    }

    public KlineQueryBo(long startTime, long endTime, Integer sort, int tradeId, String period) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.sort = sort;
        this.tradeId = tradeId;
        this.period = period;
    }
}
