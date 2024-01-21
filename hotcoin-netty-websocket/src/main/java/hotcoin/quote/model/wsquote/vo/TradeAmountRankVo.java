package hotcoin.quote.model.wsquote.vo;

import com.qkwl.common.dto.coin.SystemTradeType;
import com.qkwl.common.match.MathUtils;
import hotcoin.quote.constant.WsConstant;
import hotcoin.quote.model.wsquote.dto.TradeTickersDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.vo
 * @ClassName: TradeAmountRankVo
 * @Author: hf
 * @Description: 成交额排行对象
 * @Date: 2019/11/5 11:35
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeAmountRankVo {
    /**
     * 交易id
     */
    private Integer tradeId;

    /**
     * 折合人民币价格最新价
     */
    private String cny;

    /**
     * 币种简称:如(btc_eth) 则buyShortSymbol为eth
     */
    private String buyShortName;

    /**
     * 买方币种全称:eg:bitcoin
     */
    private String buySymbol;

    /**
     * 卖方币种简称:eg:BTC
     */
    private String sellShortName;
    /**
     * 卖方币种全称:eg:bitcoin
     */
    private String sellSymbol;

    /**
     * 成交额
     */
    private String totalAmount;

    /**
     * 最新价
     */
    private String last;

    /**
     * 币种图片地址
     */
    private String imageUrl;

    /**
     * 保留小数点
     */
    private String digit;

    public TradeAmountRankVo(SystemTradeType st, String cny, BigDecimal vol) {
        this.tradeId = st.getId();
        this.cny = cny;
        this.digit = st.getDigit();
        this.buyShortName = st.getBuyShortName();
        this.buySymbol = st.getBuySymbol();
        this.sellShortName = st.getSellShortName();
        this.sellSymbol = st.getSellSymbol();
        this.imageUrl = st.getSellWebLogo();
        this.totalAmount = MathUtils.mul(new BigDecimal(cny == null ? WsConstant.TRADE_CONSTANT_ZERO : cny),
                vol == null ? BigDecimal.ZERO : vol).toPlainString();
    }

    public TradeAmountRankVo() {
    }

    public TradeAmountRankVo(TradeTickersDTO dto) {
        this.tradeId = dto.getTradeId();
        this.cny = dto.getCny();
        this.last = dto.getLast();
        this.buyShortName = dto.getBuyShortName();
        this.buySymbol = dto.getBuySymbol();
        this.sellShortName = dto.getSellShortName();
        this.sellSymbol = dto.getSellSymbol();
        this.imageUrl = dto.getImageUrl();
        this.digit = dto.getDigit();
        this.totalAmount = MathUtils.mul(new BigDecimal(StringUtils.isEmpty(dto.getLast()) ? WsConstant.TRADE_CONSTANT_ZERO : dto.getLast()), new BigDecimal(StringUtils.isEmpty(dto.getVolume()) ? WsConstant.TRADE_CONSTANT_ZERO : dto.getVolume())).toPlainString();
    }
}
