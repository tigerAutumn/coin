package com.hotcoin.common.Enum;

public enum GsMarginStatusEnum {
	DONE(1, "已融币"), 
	PENDING(2, "待审批"), 
	REJECT(3, "审批拒绝"), 
	DOING(4, "正审批"),
	FINISHED(5, "已结束"),
	CANCELED(6, "已取消"),
	DELAY(7, "延期中"),
	;
	

	private int code;
	private String name;

	private GsMarginStatusEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static GsMarginStatusEnum getEnum(int code) {
		for (GsMarginStatusEnum orderStatusEnum : values()) {
			if (orderStatusEnum.getCode()==code) {
				return orderStatusEnum;
			}
		}
		return null;
	}

}
