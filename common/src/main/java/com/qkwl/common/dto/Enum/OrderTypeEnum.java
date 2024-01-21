package com.qkwl.common.dto.Enum;


/**
 * 委托类型
 * @author huangjinfeng
 * 2019年5月20日
 */
public enum OrderTypeEnum {
	COIN(1, "币币"), 
	LEVER(2, "杠杆"), 
	;
	

	private Integer code;
	private String name;

	private OrderTypeEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public Integer getCode() {
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

	public static OrderTypeEnum getEnum(Integer code) {
		for (OrderTypeEnum orderStatusEnum : values()) {
			if (orderStatusEnum.getCode().equals(code)) {
				return orderStatusEnum;
			}
		}
		return null;
	}

}
