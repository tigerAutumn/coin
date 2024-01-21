package com.hotcoin.coin.enums;
/**
 * Description: 主链类型枚举
 *      每次新增主链在此处登记
 * Date: 2019/7/03 13:34
 * Created by luoyingxiong
 */
public enum CoinTypeEnum {

    BTC(1, "BTC"),
    USDT(2, "USDT"),
    ETH(3, "ETH"),
    LTC(4, "LTC"),
    DOGE(5, "DOGE"),
    DASH(6, "DASH"),
    SEC(7, "SEC"),
    BCHSV(8, "BCHSV"),
    BCH(9, "BCH"),
    GOD(10, "GOD"),
    ETC(11, "ETC"),
    MOAC(12, "MOAC"),
    EOS(13, "EOS"),
    NEO(14, "NEO"),
    NEOGAS(15, "NEOGAS"),
    NEX(16, "NEX"),
    BTS(17, "BTS"),
    BAR(18, "BAR"),
    HTDF(19, "HTDF"),
    TRX(20, "TRX"),
    CET(21, "CET"),
    SERO(22, "SERO"),
    VAS(23, "VAS"),
    ONT(24, "ONT"),
	XRP(25,"XRP"),
	QTUM(26,"QTUM")
	;

    private Integer code;
    private String name;

    CoinTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Integer getCoinType(String name) {
        for (CoinTypeEnum c : CoinTypeEnum.values()) {
            if (c.getName().equals(name)) {
                return c.getCode();
            }
        }
        return null;
    }
}
