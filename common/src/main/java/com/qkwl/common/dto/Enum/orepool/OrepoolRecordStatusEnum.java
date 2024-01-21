package com.qkwl.common.dto.Enum.orepool;

public enum OrepoolRecordStatusEnum {

	lock(1, "锁仓成功"), 
	counting(2, "计息中"),
	unlock(3, "已解锁"),
	complete(4, "释放完成");

	private Integer code;
	private Object value;

	private OrepoolRecordStatusEnum(Integer code, Object value) {
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
		for (OrepoolRecordStatusEnum coinType : OrepoolRecordStatusEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
