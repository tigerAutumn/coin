package com.qkwl.common.dto.Enum;

public enum RedEnvelopeTypeEnum {

	NORMAL(1, "普通红包"), 
	RANDOM(2, "手气红包");
	
	private Integer code;
	private Object value;

	private RedEnvelopeTypeEnum(Integer code, Object value) {
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
