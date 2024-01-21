package hotcoin.quote.model.wsquote.vo;

import com.qkwl.common.dto.coin.SystemTradeType;
import lombok.Getter;
import lombok.Setter;

/**
 * @ProjectName: hotcoin-netty-websocket
 * @Package: hotcoin.quote.model.wsquote.vo
 * @ClassName: TradeStarCoinVo
 * @Author: hf
 * @Description: 明星币结构
 * @Date: 2019/11/5 11:00
 * @Version: 1.0
 */
@Setter
@Getter
public class TradeStarCoinVo {
    /**
     * 交易id
     */
    private Integer tradeId;

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
     * 变化率(涨跌幅)(负数表示跌,正数表示涨)
     */
    private String change;

    /**
     * 最新价(原始价格)
     */
    private String last;
    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 保留小数点
     */
    private String digit;

    /**
     * @param st
     * @param last
     */
    public TradeStarCoinVo(SystemTradeType st, String last) {
        this.tradeId = st.getId();
        this.last = last;
        this.buyShortName = st.getBuyShortName();
        this.buySymbol = st.getBuyShortName();
        this.sellShortName = st.getSellShortName();
        this.sellSymbol = st.getSellShortName();
        this.imageUrl = st.getSellWebLogo();
        this.digit = st.getDigit();
    }

    public TradeStarCoinVo() {
    }
}
