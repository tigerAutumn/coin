package com.qkwl.common.dto.Enum.orepool;

/**
 * 矿池计划表锁仓类型
 * @author zhangpeng
 *
 */
public enum OrepoolPlanTypeEnum {

	fixed_plan(1, "定期"), 
	current_plan(2, "活期"),
	innovation_plan(3, "创新区锁仓");

	private Integer code;
	private Object value;

	private OrepoolPlanTypeEnum(Integer code, Object value) {
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
		for (OrepoolPlanTypeEnum coinType : OrepoolPlanTypeEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
