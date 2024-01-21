package com.hotcoin.common.Enum;

public enum OrderStatusEnum {
	INIT("C", "初始化"), 
	FAIELD("F", "交易失败"), 
	SUCCESS("S", "交易成功"), 
	EXCEPTION("E", "交易异常或者处理中"),
	INVILD("I", "无效交易(交易不存在)"),
    NO_EXIST("N","交易不存在");

	private String name;
	private String descr;

	private OrderStatusEnum(String name, String descr) {
		this.name = name;
		this.descr = descr;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public static OrderStatusEnum getOrderStatusEnum(String name) {
		for (OrderStatusEnum orderStatusEnum : values()) {
			if (orderStatusEnum.getName().equals(name)) {
				return orderStatusEnum;
			}
		}
		return null;
	}

}
