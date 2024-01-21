package com.qkwl.common.dto.Enum;

/**
 * 用户钱包流水表方向
 * @author hwj
 */
public enum UserWalletBalanceLogFieldEnum {
	total("total","可用"), 
	frozen("frozen","冻结"), 
	borrow("borrow", "理财"),
	ico("ico", "ico"),
	deposit_frozen("deposit_frozen", "充值冻结"),
	deposit_frozen_total("deposit_frozen_total", "充值冻结总数");
	
	private String value;
	private String describe;

	private UserWalletBalanceLogFieldEnum(String value,String describe) {
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
