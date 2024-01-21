/**
 * 
 */
package com.qkwl.web.permission.Enum;

/**
 * @author huangjinfeng
 */
public enum ExchangeRateEnum {
	
	CNY("CNY","人民币"),
	USD("USD","美元"),
	KRW("KRW","韩元"),
	EUR("EUR","欧元"),
	GBP("GBP","英镑"),
	HKD("HKD","港元");
	
	private String code;
	private String desc;
	
	ExchangeRateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

}
