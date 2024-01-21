package com.qkwl.common.dto.Enum;

public enum RedEnvelopeStatusEnum {

	NORECEIVE(1, "未领取完"), 
	RECEIVED(2, "已领取完"),
	OVERDUE(3, "已过期"),
	RETURN(4, "已退回");
	
	private Integer code;
	private Object value;

	private RedEnvelopeStatusEnum(Integer code, Object value) {
		this.code = code;
		this.value = value;
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
}
