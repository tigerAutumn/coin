package hotcoin.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.model.dto
 * @ClassName: KlineQueryDto
 * @Author: hf
 * @Description:
 * @Date: 2019/9/19 9:45
 * @Version: 1.0
 */
@Setter
@Getter
public class KlineQueryDto {
    private String req;
    /**
     * eg:btc_usdt ,暂时没实现,因为需要引入redis
     */
    private String symbol;
    /**
     * 粒度:1m,5m,15m,30m,1h,1w,1mo
     */
    private String period;
    /**
     * 时间戳 13位,精确到毫秒
     */
    private Long from;
    /**
     * 当direction 为 1时, from 为最小时间,当 direction为-1 时,from为最大时间
     */
    private Integer direction;
    /**
     * 当direction 为 1时,不需要传limit ,需要后端自己计算
     */
    private Integer limit;
    /**
     * 当用户传symbol时,不需要传tradeId
     */
    private Integer tradeId;
    /**
     * 不传时,默认正序,sort =1 正序,-1倒序
     */
    private Integer sort;

    public KlineQueryDto() {
    }

    public KlineQueryDto(String period, Integer direction, Integer limit, Integer tradeId) {
        this.period = period;
        this.direction = direction;
        this.limit = limit;
        this.tradeId = tradeId;
    }
}
