package com.qkwl.common.dto.Enum;

/**
 * 虚拟币类型
 * @author LY
 *
 */
public enum SystemCoinSortEnum {
	CNY(1, "法币类","CNY"),
	BTC(2, "比特币类","BTC"), 
	ETH(3, "以太坊类","ETH"),
	ICS(4, "小企链资产","ICS"),
	ETC(5, "以太经典类","ETC"),
	ETP(6, "熵类","ETP"),
	GXS(7, "公信宝类","GXS"),
	MIC(8, "小米链资产","MIC"),
	WICC(9, "维基链","WICC"),
	USDT(10, "泰达币USDT","USDT"),
	MOAC(11, "墨客链","MOAC"),
	FOD(12, "发现链","FOD"),  //抄的eth
	EOS(13, "柚子币","EOS"),
	BWT(14, "商链","BWT"),
	RUBY(15, "路链","RUBY"),
	NEO(16, "小蚁链","NEO"),
	VCC(17, "VCC","VCC"),  //抄的eth
	BTS(18, "BTS","BTS"),
	MULTI(19, "MULTI","MULTI"),
	CHAIN33(20, "CHAIN33","CHAIN33"),
	HTDF(21, "华特东方","HTDF"),
	CET(22, "CET","CET"),  //抄的eth
	SERO(23, "SERO","SERO"),  
	TRX(24, "TRX","TRX") ,
	TCP(25, "TCP","TCP") ,
	ONT(26, "ONT","ONT") ,
	XRP(27, "XRP","XRP") ,
	;

	private Integer code;
	private Object value;
	private String shortName;

	private SystemCoinSortEnum(Integer code, Object value,String shortName) {
		this.code = code;
		this.value = value;
		this.shortName = shortName;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public static String getValueByCode(Integer code) {
		for (SystemCoinSortEnum coinType : SystemCoinSortEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
	
	public static String getShortNameCode(Integer code) {
		for (SystemCoinSortEnum coinType : SystemCoinSortEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getShortName();
			}
		}
		return null;
	}
}
