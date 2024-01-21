package com.qkwl.common.dto.Enum;

/**
 * 交易类型
 * @author hwj
 *
 */
public enum UserWhiteListTypeEnum {

	RECHARGE_OF_INNOVATION_ZONE(1, "创新区充值");
	
	private Integer code;
	private String describe;

	private UserWhiteListTypeEnum(Integer code, String describe) {
		this.code = code;
		this.describe = describe;
	}

	public Integer getCode() {
		return code;
	}

	public String getDescribe() {
		return describe;
	}

	public static String getValueByCode(Integer code) {
		for (UserWhiteListTypeEnum coinType : UserWhiteListTypeEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getDescribe().toString();
			}
		}
		return null;
	}
}
