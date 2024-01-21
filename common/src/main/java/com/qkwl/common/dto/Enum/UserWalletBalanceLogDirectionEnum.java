package com.qkwl.common.dto.Enum;

/**
 * 用户钱包流水表方向
 * @author hwj
 */
public enum UserWalletBalanceLogDirectionEnum {
	in("in","转入"), 
	out("out", "转出");
	
	private String value;
	private String describe;

	private UserWalletBalanceLogDirectionEnum(String value,String describe) {
		this.value = value;
		this.describe = describe;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	
}
