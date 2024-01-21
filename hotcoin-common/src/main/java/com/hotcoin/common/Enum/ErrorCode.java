package com.hotcoin.common.Enum;

/**
 * 自定义异常枚举类
 * SYS_400   系统级全局错误
 * FINACE_01 个人钱包业务
 * FINACE_02 个人钱包币币业务
 */
public enum ErrorCode {

    TRADE_FREEZE_ERROR_01("TRADE_01_01","Insufficient frozen balance"),
    TRADE_UNFREEZE_ERROR_01("TRADE_02_01","UnFreeze error"),
    TRADE_PAY_ERROR_01("TRADE_03_1_01","Trade pay error"),
    TRADE_RECV_ERROR_01("TRADE_03_2_01","Trade recv error"),
    TRADE_SERVER_ERROR("TRADE_500","TRADE_SERVER_ERROR"),
    TRADE_UNFREEZE_SERVER_ERROR("TRADE_02_05",""),
    TRADE_PAY_SERVER_ERROR("TRADE_03_05",""),
    // 成功
    SUCCESS("200","SUCCESS"),

    // 失败
    FAIL("400","FAIL"),

    // 未认证（签名错误）
    UNAUTHORIZED("401","UNAUTHORIZED"),

    // 接口不存在
    NOT_FOUND("404","NOT_FOUND"),

    // 服务器内部错误
    INTERNAL_SERVER_ERROR("500","INTERNAL_SERVER_ERROR"),
	
	
	//无效请求参数
	INVALID_PARAM("1000", "INVALID_PARAM"),
	
	
	//杠杆钱包相关异常(2001-2100)
	//创建杠杆钱包失败
	CREATE_LEVER_WALLET_FAILED("2001","CREATE_LEVER_WALLET_FAILED"),
	//杠杆钱包不存在
	LEVER_WALLET_NO_EXIST("2002","LEVER_WALLET_NO_EXIST"),
	//可用资金不足
	INSUFFICIENT_FUND("2003","INSUFFICIENT_FUND"),
	//杠杆钱包状态异常
	ABNORMAL_LEVER_WALLET_STATUS("2004","ABNORMAL_LEVER_WALLET_STATUS"),
	//融币订单不存在
	MARGIN_ORDER_NO_EXIST("2005","MARGIN_ORDER_NO_EXIST"),
	//杠杆钱包解冻失败
	LEVER_WALLET_UNFREEZE_FAILED("2006","LEVER_WALLET_UNFREEZE_FAILED"),
	//融币失败
	MARGIN_FAILED("2007","MARGIN_FAILED"),
	//添加贷款金额失败
	INCREASE_LOAN_AMOUNT_FAILED("2008","increase_loan_amount_failed"),
	//还币清算异常
	REPAY_CLEAR_FAILED("2009","还币清算异常"),
	
	
	 //港盛服务异常(2101-2200)
	GS_MARGIN_ERROR("2101","融币异常"),
	
	
	
	//创建币币钱包失败
	CREATE_COIN_WALLET_FAILED("1002","CREATE_COIN_WALLET_FAILED"),

    CREATE_WALLET_ORDER_FAILED("1003","CREATE_WALLET_ORDER_FAILED"),
    DB_INSERT_EXCEPTION("9002","DB_INSERT_EXCEPTION"),
    DB_UPDATE_EXCEPTION("9003","DB_UPDATE_EXCEPTION"),
    DB_SELECT_EXCEPTION("9001","DB_SELECT_EXCEPTION")
    
	;


    private String name;
    private String descr;

    private ErrorCode(String name, String descr) {
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


    public static ErrorCode getErrorCode(String Name) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getName().equalsIgnoreCase(Name)) {
                return errorCode;
            }
        }
        return null;
    }
}
