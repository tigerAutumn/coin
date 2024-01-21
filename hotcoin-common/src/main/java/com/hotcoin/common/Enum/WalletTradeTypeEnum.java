package com.hotcoin.common.Enum;

public enum WalletTradeTypeEnum {
    CLOSE_POSITION(3,"3","平仓"),
    LOCK_WALLET(91,"91","锁定交易币种"),
    FREEZE_WALLET(92,"92","冻结用户钱包"),
	UNFREEZE_WALLET(93,"93","解冻用户钱包"),
	CANCEL_ORDER(30,"30","取消订单"),

	COIN_RECHARGE(101,"COIN_RECHARGE","币币充值"),
	COIN_WITHDRAW(102,"COIN_WITHDRAW","币币提现"),
	COIN_2_LEVEL(103, "COIN_2_LEVEL", "币币转入杠杠"),
	COIN_2_COIN(104, "COIN_2_COIN", "币币转入币币"),
	COIN_ORDER_FREEZE(111, "COIN_ORDER_FREEZE", "币币委单冻结"),
	COIN_ORDER_UNFREEZE(112, "COIN_ORDER_UNFREEZE", "币币委单解冻"),

	LEVEL_RECHARGE(201,"LEVEL_RECHARGE","杠杆充值"),
	LEVEL_WITHDRAW(202,"LEVEL_WITHDRAW","杠杆提现"),
	LEVEL_2_COIN(203, "LEVEL_2_COIN", "杠杠转入币币"),
	LEVEL_2_LEVEL(204, "LEVEL_2_LEVEL", "杠杠转入杠杆"),
	LEVEL_LOAN(205, "LEVEL_LOAN", "杠杆贷款"),
    LEVEL_REPAY(206,"LEVEL_REPAY","杠杆还款"),
	LEVEL_REPAY_CAPITAL(206001, "REPAY_CAPITAL", "杠杆归还本金"),
	LEVEL_REPAY_INTEREST(206002, "REPAY_INTEREST", "杠杆归还利息"),
	LEVER_ORDER_FREEZE(211, "LEVEL_ORDER_FREEZE", "杠杆委单冻结"),
	LEVER_ORDER_UNFREEZE(212, "LEVEL_ORDER_UNFREEZE", "杠杆委单解冻");



	private int code;
	private String name;
	private String descr;

	private WalletTradeTypeEnum(int code, String name, String descr) {
		this.code = code;
		this.name = name;
		this.descr = descr;
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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public static WalletTradeTypeEnum getWalletTradeTypeEnum(int code) {
		for (WalletTradeTypeEnum walletTradeTypeEnum : values()) {
			if (walletTradeTypeEnum.getCode() == code) {
				return walletTradeTypeEnum;
			}
		}
		return null;
	}
}
