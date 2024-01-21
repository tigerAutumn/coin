package com.hotcoin.increment.Enum;

public enum EntrustSideType {
	
	BUY(0,"买单"),
	SELL(1,"卖单");
	
	private int code;
	
	private String description;

	private EntrustSideType(int code, String description) {
		this.code = code;
		this.description = description;
	}


	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}
