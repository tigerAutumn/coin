package hotcoin.model.bo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model.bo
 * @ClassName: KlineHistoryBo
 * @Author: hf
 * @Description:
 * @Date: 2019/11/22 14:21
 * @Version: 1.0
 */
@Setter
@Getter
public class KlineHistoryBo {
    private String _id;
    private BigDecimal high;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private long createTime;
    private int tradeId;
    private String period;

    public KlineHistoryBo() {
    }
}
