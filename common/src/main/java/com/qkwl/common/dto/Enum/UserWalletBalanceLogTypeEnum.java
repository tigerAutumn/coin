package com.qkwl.common.dto.Enum;

/**
 * 用户钱包流水表类型
 * @author hwj
 */
public enum UserWalletBalanceLogTypeEnum {
	User_recharge(1, "用户充币"), 
	User_withdrawals(2, "用户提币"),
	Place_order(3, "下单"),
	Match_up(4, "撮合"),
	Admin_Balance_Adjustment(5, "管理员手工余额调整"),
	C2C(6, "C2C"),
	Return_commission(7, "返佣"),
	Recharge_freezing(8, "充值冻结"),
	Freezing_of_Innovation_Zone(9, "创新区解冻"),
	Dividend_of_Innovation_Zone(10, "创新区分红"),
	Activity_commission(11, "活动返佣"),
	Reward_of_Innovation_Zone(12, "创新区分红奖励"),
	Orepool_lock(13, "矿池锁定"),
	Orepool_unlock(14, "矿池解锁"),
	Orepool_income(15, "矿池收益"),
	Cancel_order(16,"撤单"),
	Innovation_unfrozen(17, "创新区存币解冻"),
	Innovation_lock(18, "创新区存币锁定"),
	Innovation_unlock(19, "创新区存币解锁"),
	Otc_deposit(20, "otc押金"),
	Airdrop_Candy(21, "空投糖果"),
	CLOSE_POSITION(22,"平仓"),
	SEND_RED_ENVELOPE(23, "发红包"),
	RECEIVE_RED_ENVELOPE(24, "收红包"),
	RETURN_RED_ENVELOPE(25, "退还红包"),
	DEDUCT_RED_ENVELOPE(26, "扣除红包");

	private Integer code;
	private Object value;

	private UserWalletBalanceLogTypeEnum(Integer code, Object value) {
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
		for (UserWalletBalanceLogTypeEnum coinType : UserWalletBalanceLogTypeEnum.values()) {
			if (coinType.getCode().equals(code)) {
				return coinType.getValue().toString();
			}
		}
		return null;
	}
}
