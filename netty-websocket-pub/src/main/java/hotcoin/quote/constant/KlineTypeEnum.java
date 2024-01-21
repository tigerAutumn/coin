package hotcoin.quote.constant;

/**
 * @ProjectName: finance-server
 * @Package: org.quote.model
 * @ClassName: KlineTypeEnum
 * @Author: hf
 * @Description: k线周期类型
 * @Date: 2019/4/30 13:42
 * @Version: 1.0
 */
public enum KlineTypeEnum {
    MIN1("1分钟", "1m"),
    MIN5("5分钟", "5m"),
    MIN15("15分钟", "15m"),
    MIN30("30分钟", "30m"),
    HOUR("1小时", "1h"),
    DAY("1天", "1d"),
    WEEK("一周", "1w"),
    MONTH("1个月", "1mo");

    /**
     * 周期类型名称
     */
    private String name;
    /**
     * 周期类型值
     */
    private String value;

    private KlineTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
