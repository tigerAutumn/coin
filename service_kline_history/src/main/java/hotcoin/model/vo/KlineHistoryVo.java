package hotcoin.model.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model
 * @ClassName: KlineHistoryVo
 * @Author: hf
 * @Description:
 * @Date: 2019/9/5 15:35
 * @Version: 1.0
 */
@Getter
@Setter
@Document
public class KlineHistoryVo {
    private BigDecimal high;
    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private long createTime;
    private int tradeId;
    private String period;

    public KlineHistoryVo() {
    }

}
