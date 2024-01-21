package hotcoin.component;

import hotcoin.model.bo.KlineQueryBo;
import hotcoin.model.constant.KlineHistoryConstant;
import hotcoin.model.dto.KlineQueryDto;
import hotcoin.model.em.TimeTypeEnum;
import hotcoin.util.JodaTimeUtil;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: service_kline_history
 * @Package: hotcoin.component
 * @ClassName: KlineParamComponent
 * @Author: hf
 * @Description:
 * @Date: 2019/9/19 10:12
 * @Version: 1.0
 */
@Component
public class KlineParamComponent {
    /**
     * @param klineQueryDto
     * @return
     */
    public KlineQueryBo convert2QueryParam(KlineQueryDto klineQueryDto) {
        Long from = klineQueryDto.getFrom();
        Integer limit = klineQueryDto.getLimit();
        String period = klineQueryDto.getPeriod();
        Integer tradeId = klineQueryDto.getTradeId();
        Integer direction = klineQueryDto.getDirection();
        if (limit == null || limit > KlineHistoryConstant.KLINE_HIS_MAX_LIMIT) {
            limit = KlineHistoryConstant.KLINE_HIS_DEFAULT_LIMIT;
        }
        Integer sort = klineQueryDto.getSort() == null ? KlineHistoryConstant.KLINE_HIS_ASC : KlineHistoryConstant.KLINE_HIS_DESC;
        KlineQueryBo bo = new KlineQueryBo(tradeId, period);
        if (direction == null || direction.equals(KlineHistoryConstant.KLINE_HIS_DESC)) {
            long startTime = getStartTime(from, limit, period);
            bo.setStartTime(startTime);
            bo.setEndTime(from);
        } else {
            long endTime = JodaTimeUtil.getCurrentTimestampIgnoreSeconds();
            // 不能以本地时间为准,所以取服务器时间
            if (from == null) {
                from = getStartTime(endTime, limit, period);
            }
            bo.setStartTime(from);
            bo.setEndTime(endTime);
        }
        bo.setSort(sort);
        return bo;
    }

    /**
     * 获取起始时间
     *
     * @param endTime
     * @param limit
     * @param period
     * @return
     */
    private long getStartTime(long endTime, int limit, String period) {
        long result = 0;
        switch (period) {
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -limit, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_5M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -5 * limit, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_15M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -15 * limit, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_30M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -30 * limit, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1H:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -limit, TimeTypeEnum.HOUR);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1D:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -limit, TimeTypeEnum.DAY);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1W:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -7 * limit, TimeTypeEnum.DAY);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_1MO:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(endTime, -limit, TimeTypeEnum.MONTH);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 获取结束时间
     *
     * @param startTime
     * @param period
     * @return
     */
    public long getFutureTime(long startTime, String period) {
        long result = 0;
        switch (period) {
            case KlineHistoryConstant.KLINE_HIS_PERIOD_5M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(startTime, 5, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_15M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(startTime, 15, TimeTypeEnum.MIN);
                break;
            case KlineHistoryConstant.KLINE_HIS_PERIOD_30M:
                result = JodaTimeUtil.getTimestampBeforeOrAfterByJdk8(startTime, 30, TimeTypeEnum.MIN);
                break;
            default:
                break;
        }
        return result;
    }
}
