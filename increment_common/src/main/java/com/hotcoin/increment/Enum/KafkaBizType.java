package com.hotcoin.increment.Enum;

public enum KafkaBizType {
	
	ENTRUST_HISTORY("ENTRUST_HISTORY","历史委单");

	
	
	private String type;
	
	private String description;

	private KafkaBizType(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
