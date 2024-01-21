package com.qkwl.common.dto.Enum.orepool;

public enum OrepoolPlanStatusEnum {

	forbid(1, "已禁用"), 
	not_start(2, "未开始"),
	start(3, "已开始"),
	end(4, "已结束"),
	unlock(5, "已解锁");

	private Integer code;
	private Object value;

	private OrepoolPlanStatusEnum(Integer code, Object value) {
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

	public static String getValueByCode(Integer code) {
		for (OrepoolPlanStatusEnum coinType : OrepoolPlanStatusEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
