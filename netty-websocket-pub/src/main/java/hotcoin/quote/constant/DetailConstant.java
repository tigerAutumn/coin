package hotcoin.quote.constant;

/**
 * @ProjectName: service_user
 * @Package: hotcoin.quote.constant
 * @ClassName: DetailConstant
 * @Author: hf
 * @Description: 交易详情(实时交易)
 * @Date: 2019/5/16 14:25
 * @Version: 1.0
 */
public interface DetailConstant {
    /**
     * 卖方订单号
     */
    String DETAIL_SELL_ORDER_ID = "sellOrderId";
    /**
     * 数量
     */
    String DETAIL_COUNT = "count";
    /**
     * 类型 0,1,2等
     * {@link com.qkwl.common.dto.Enum.EntrustChangeEnum}
     */
    String DETAIL_TYPE = "type";
    /**
     * 买入价格
     */
    String DETAIL_BUY_PRIZE = "buyPrize";
    /**
     * 卖出价格
     */
    String DETAIL_SELL_PRICE = "sellPrize";
    String DETAIL_NAME = "detail";
}
